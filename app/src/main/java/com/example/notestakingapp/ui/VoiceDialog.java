package com.example.notestakingapp.ui;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.example.notestakingapp.R;
import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.shared.SharedViewModel;
import com.example.notestakingapp.utils.AudioUtils;

import java.io.IOException;

public class VoiceDialog {
    public static LottieAnimationView playBtn;
    public static LottieAnimationView playingBar;
    private static MediaPlayer player = null;
    private static MediaRecorder recorder = null;
    private static String fileName = null;
    public static int noteId = -1;
    static boolean mStartPlaying = true;
    private static SharedViewModel tempShared;

    public static void showVoiceDialog(Context context, int noteID, SharedViewModel sharedViewModel) {
        tempShared = sharedViewModel;
        noteId = noteID;
        fileName = context.getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_voice_press);
        playBtn = dialog.findViewById(R.id.play_button);
        playingBar = dialog.findViewById(R.id.playing_bar);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DiaLogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        LinearLayout linearLayoutXButton = dialog.findViewById(R.id.layout_x_button);
        linearLayoutXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recorder!=null) {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                    int audioId =(int) DatabaseHandler.insertAudio(context, noteId, AudioUtils.convertFileToByteArray(fileName));

                    Log.d("playingInsert", "OK");
                    if (tempShared != null) {
                        tempShared.setAudioId(audioId);
                        tempShared.setNoteEditChangeInsertAudio(true);
                    }
                }
                dialog.dismiss();
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(context);
            }
        });


        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(recorder!=null) {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                    int audioId =(int) DatabaseHandler.insertAudio(context, noteId, AudioUtils.convertFileToByteArray(fileName));

                    Log.d("playingInsert", "OK");
                    if (tempShared != null) {
                        tempShared.setAudioId(audioId);
                        tempShared.setNoteEditChangeInsertAudio(true);
                    }
                }

            }
        });

    }

    public static void stopAnim(Context context) {
        ValueAnimator animator = ValueAnimator.ofFloat(0.5f, 1f);
        animator.addUpdateListener(animation -> playBtn.setProgress((Float) animation.getAnimatedValue()));

        animator.setDuration(500);
        animator.start();

        playingBar.pauseAnimation();
        playBtn.setOnClickListener(v -> play(context));
    }

    public static void stopAnim() {
        ValueAnimator animator = ValueAnimator.ofFloat(0.5f, 1f);
        animator.addUpdateListener(animation -> playBtn.setProgress((Float) animation.getAnimatedValue()));

        animator.setDuration(500);
        animator.start();

        playingBar.pauseAnimation();
    }

    public static void play(Context context) {
        if (mStartPlaying) {
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 0.5f);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                    playBtn.setProgress((Float) animation.getAnimatedValue());
                }
            });
            animator.setDuration(500);
            playingBar.setRepeatCount(ValueAnimator.INFINITE);
            animator.start();
            playingBar.playAnimation();
            startRecording();
        } else {
            //ui
            stopAnim(context);
            //db
            stopRecording(context, noteId);
        }
        mStartPlaying = !mStartPlaying;

    }

    private static void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("Recorder error", "prepare() failed");
        }
        recorder.start();
    }

    private static void stopRecording(Context context, int noteId) {
        if(recorder!= null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            int audioId =(int) DatabaseHandler.insertAudio(context, noteId, AudioUtils.convertFileToByteArray(fileName));

            Log.d("playingInsert", "OK");
            if (tempShared != null) {
                tempShared.setAudioId(audioId);
                tempShared.setNoteEditChangeInsertAudio(true);
            }
        }

    }
}
