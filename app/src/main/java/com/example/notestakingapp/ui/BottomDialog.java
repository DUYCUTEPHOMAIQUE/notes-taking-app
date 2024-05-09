package com.example.notestakingapp.ui;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.notestakingapp.MainActivity;
import com.example.notestakingapp.NoteEditActivity;
import com.example.notestakingapp.R;

public class BottomDialog {
    public static String selectedNoteColor = "#FFFFFF";
    private static Context mContext;
    public static void setColor(String color) {

        if(mContext instanceof NoteEditActivity) {
            ((NoteEditActivity) mContext).setColorBackgroundNoteEdit(color);
        }
    }
    public static void showToolDialog(Context context) {
        mContext = context;
        Log.d("duyColor", String.valueOf(mContext instanceof NoteEditActivity));
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.tool_dialog_layout);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DiaLogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        LinearLayout linearLayoutXButton = dialog.findViewById(R.id.layout_x_button);
        LinearLayout linearLayoutFinishedNote = dialog.findViewById(R.id.layout_mask_as_finished);
        LinearLayout linearLayoutDeleteNote = dialog.findViewById(R.id.layout_delete_note);

        final ImageView colorNoteDefault = dialog.findViewById(R.id.color_note_default);
        final ImageView colorNoteVariant1 = dialog.findViewById(R.id.color_note_1);
        final ImageView colorNoteVariant2 = dialog.findViewById(R.id.color_note_2);
        final ImageView colorNoteVariant3 = dialog.findViewById(R.id.color_note_3);
        final ImageView colorNoteVariant4 = dialog.findViewById(R.id.color_note_4);
        final ImageView colorNoteVariant5 = dialog.findViewById(R.id.color_note_5);

        dialog.findViewById(R.id.view_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FFFFFF";
                colorNoteDefault.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(selectedNoteColor);
            }
        });
        dialog.findViewById(R.id.view_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#F7F6D4";
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(selectedNoteColor);
            }
        });
        dialog.findViewById(R.id.view_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FDEBAB";
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(selectedNoteColor);

            }
        });
        dialog.findViewById(R.id.view_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#DAF6E4";
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(selectedNoteColor);
            }
        });
        dialog.findViewById(R.id.view_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#EFE9F7";
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(selectedNoteColor);
            }
        });
        dialog.findViewById(R.id.view_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#F7DEE3";
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(R.drawable.round_done_24);
                setColor(selectedNoteColor);

            }
        });

        linearLayoutFinishedNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: them note vao db

                dialog.dismiss();
                context.startActivity(new Intent(context, MainActivity.class));

            }
        });
        linearLayoutDeleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showConfirmDeleteNote(context);
            }
        });
        linearLayoutXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private static void showConfirmDeleteNote(Context context) {
        Log.d("duy123456", "clicked");
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_confirm_delete_dialog);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DiaLogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        LinearLayout linearLayoutXButton = dialog.findViewById(R.id.layout_x_button);
        LinearLayout linearLayoutRestore = dialog.findViewById(R.id.layout_restore_note);
        LinearLayout linearLayoutConfirmDelete = dialog.findViewById(R.id.layout_confirm_delete_note);

        linearLayoutRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showToolDialog(context);
            }
        });
        linearLayoutConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: handle delete note
                dialog.dismiss();
                context.startActivity(new Intent(context, MainActivity.class));
            }
        });
        linearLayoutXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public static void showVoiceDialog(Context context) {
        Log.d("duy123456", "clicked");
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_voice_press);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DiaLogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        LinearLayout linearLayoutXButton = dialog.findViewById(R.id.layout_x_button);
        linearLayoutXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
