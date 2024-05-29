package com.example.notestakingapp.adapter;


import static com.example.notestakingapp.ui.VoiceDiaLog.playBtn;
import static com.example.notestakingapp.ui.VoiceDiaLog.stopAnim;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.notestakingapp.Item;
import com.example.notestakingapp.NoteEditActivity;
import com.example.notestakingapp.R;
import com.example.notestakingapp.shared.SharedViewModel;
import com.example.notestakingapp.utils.CurrentTime;
import com.example.notestakingapp.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class NoteDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static boolean mStartPlaying = true;
    public static LifecycleOwner lifecycleOwner;

    public int getNoteId() {
        return noteId;
    }

    public NoteDetailsAdapter(Context context, LifecycleOwner u) {
        this.mContext = context;
        sharedViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(SharedViewModel.class);
        lifecycleOwner = u;
    }

    public Context mContext;
    public static String title;
    private int noteId;
    public static SharedViewModel sharedViewModel;

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    private List<Item> itemList;
    public static OnEditTextChangedListener editTextChangedListener;
    public static EditTextTitleListener editTextTitleListener;

    public void setOnEditTextTitleListener(EditTextTitleListener listener) {
        this.editTextTitleListener = listener;
    }

    public void setOnEditTextChangedListener(OnEditTextChangedListener listener) {
        this.editTextChangedListener = listener;
    }

    private static AudioListener audioListener;

    public AudioListener getAudioListener() {
        return audioListener;
    }

    public void setAudioListener(AudioListener audioListener) {
        NoteDetailsAdapter.audioListener = audioListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;
        EditText layout;
        switch (viewType) {
            case Item.TYPE_EDIT_TEXT_TITLE:
                itemView = inflater.inflate(R.layout.item_edit_text_title, parent, false);
                TextView textViewCurrentTime = itemView.findViewById(R.id.textview_current_time);
                String timeText = CurrentTime.getCurrentTimeText();
                textViewCurrentTime.setText(timeText);
                EditText titleEditText = itemView.findViewById(R.id.edit_text_title);

                return new EditTextTitleViewHolder(itemView);
            case Item.TYPE_EDIT_TEXT:
                itemView = inflater.inflate(R.layout.item_edit_text, parent, false);
                EditText editTextSegment = itemView.findViewById(R.id.edit_text_details);
                editTextSegment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                        }
                        if (!hasFocus) {
                            // EditText không còn focus, thực hiện xử lý ở đây
                            String text = editTextSegment.getText().toString();
                            List<String> urls = new ArrayList<>();
                            urls = TextUtils.linkDetectFromText(text);
                            SpannableString spannableString = new SpannableString(text);
                            int colorAccent = ContextCompat.getColor(mContext, R.color.colorAccent);

                            for (String url : urls) {
                                int startIndex = text.indexOf(url);
                                while (startIndex != -1) {
                                    int endIndex = startIndex + url.length();
                                    spannableString.setSpan(new UnderlineSpan(), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    spannableString.setSpan(new ForegroundColorSpan(colorAccent), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    startIndex = text.indexOf(url, endIndex);
                                }
                            }

                            editTextSegment.setText(spannableString);
                        }
                    }
                });
                return new EditTextViewHolder(itemView);
            case Item.TYPE_IMAGE_VIEW:
                itemView = inflater.inflate(R.layout.item_image_view, parent, false);
                return new ImageViewHolder(itemView);
            case Item.TYPE_VOICE_VIEW:
                itemView = inflater.inflate(R.layout.item_audio, parent, false);
                return new AudioViewHolder(itemView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    private int getString(int noteTitle) {
        return 0;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = itemList.get(position);
        switch (item.getType()) {
            case Item.TYPE_EDIT_TEXT:
                EditTextViewHolder editTextViewHolder = (EditTextViewHolder) holder;
                editTextViewHolder.editText.setText(item.getText());
                break;
            case Item.TYPE_IMAGE_VIEW:
                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                if (item.getImageBitmap() != null) {
                    imageViewHolder.imageView.setImageBitmap(item.getImageBitmap());
                } else {
                    imageViewHolder.imageView.setImageURI(item.getImageUri());
                }
                break;

            case Item.TYPE_EDIT_TEXT_TITLE:
                EditTextTitleViewHolder editTextTitleViewHolder = (EditTextTitleViewHolder) holder;
                editTextTitleViewHolder.editText.setText(item.getText());
                break;
            case Item.TYPE_VOICE_VIEW:
                AudioViewHolder audioViewHolder = (AudioViewHolder) holder;
                break;
        }
    }

    public void setData(List<Item> list) {
        this.itemList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getType();
    }

    static class EditTextViewHolder extends RecyclerView.ViewHolder {
        EditText editText;

        public EditTextViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.edit_text_details);
            // lang nghe su kien nhap van ban trong edittext
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (editTextChangedListener != null) {
                        //check position ép kiểu sai
                        if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                            // Sử dụng listener để thông báo văn bản đã thay đổi
                            editTextChangedListener.onTextChanged(getAdapterPosition(), s.toString());
                        }
                        //gui su kien cho activity va truyen van ban moi
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
//                    editText.setText(s);
                }
            });
        }
    }

    static class EditTextTitleViewHolder extends RecyclerView.ViewHolder {
        EditText editText;
        EditTextTitleListener listener;

        public EditTextTitleViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.edit_text_title);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    title = s.toString();
                    if (editTextTitleListener != null) {
                        editTextTitleListener.onTitleTextChanged(s.toString().trim());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            // lang nghe su kien nhap van ban trong edittext

        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_details);
        }
    }

    static class AudioViewHolder extends RecyclerView.ViewHolder {
        LottieAnimationView playBtn;
        LottieAnimationView playingBar;
        ImageView trashImage;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            playBtn = itemView.findViewById(R.id.play_button_item);
            playingBar = itemView.findViewById(R.id.playing_bar_item);
            trashImage = itemView.findViewById(R.id.image_trash_audio);

            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sharedViewModel.setPlaying(true);
                    sharedViewModel.isPlaying().observe(lifecycleOwner, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean startPlaying) {
                            Log.d("audioDuyTest", "bool = "+ startPlaying);

                            if (startPlaying) {
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
                            } else {
                                ValueAnimator animator = ValueAnimator.ofFloat(0.5f, 1f);
                                animator.addUpdateListener(animation -> playBtn.setProgress((Float) animation.getAnimatedValue()));

                                animator.setDuration(500);
                                animator.start();

                                playingBar.pauseAnimation();
                            }
                        }
                    });
                    int adapterPosition = getAdapterPosition();
                    if (audioListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                        audioListener.onPlayBtnClick(adapterPosition);
                    }
                }
            });

        }
    }

    public interface OnEditTextChangedListener {
        void onTextChanged(int position, String text);
    }

    public interface EditTextTitleListener {
        void onTitleTextChanged(String text);
    }

    public interface AudioListener {
        void onPlayBtnClick(int position);

        void onTrashClick(int position);
    }

}
