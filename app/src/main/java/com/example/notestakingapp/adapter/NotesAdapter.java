package com.example.notestakingapp.adapter;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notestakingapp.R;
import com.example.notestakingapp.database.NoteComponent.Audio;
import com.example.notestakingapp.database.NoteComponent.Image;
import com.example.notestakingapp.database.NoteComponent.Note;
import com.example.notestakingapp.database.NoteComponent.Tag;
import com.example.notestakingapp.database.NoteComponent.TextSegment;
import com.example.notestakingapp.utils.CurrentTime;
import com.example.notestakingapp.utils.ImageUtils;
import com.example.notestakingapp.utils.NoteDetailsComponent;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.Inflater;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<NoteDetailsComponent> noteDetailsComponentList;
    private NoteListener noteListener;
    private int currentPosition = -1;
    private boolean isChecked = false;
    public static boolean showCheckboxes = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isLongClick() {
        return isLongClick;
    }

    public void setLongClick(boolean longClick) {
        isLongClick = longClick;
    }

    public static boolean isLongClick = false;
    public static List<Integer> listNoteIdChecked;
    public static int numberNoteChecked;

    public void setNoteListener(NoteListener noteListener) {
        this.noteListener = noteListener;
    }

    public void setNotes(List<NoteDetailsComponent> noteDetailsComponentList) {
        this.noteDetailsComponentList = noteDetailsComponentList;
        notifyDataSetChanged();
    }

    public List<Integer> getCheckboxStates() {
        List<Boolean> states = new ArrayList<>();
        List<Integer> listNoteId = new ArrayList<>();
        for (NoteDetailsComponent component : noteDetailsComponentList) {
            states.add(component.isChecked());
            if (component.isChecked()) {
                listNoteId.add(component.getNote().getNoteId());
            }
        }
        listNoteIdChecked = listNoteId;
        return listNoteId;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_main, parent, false);
        return new NoteViewHolder(view);
    }

    public interface NoteListener {
        void onItemClick(View view, int position, Note note);

        void onItemLongPress(View view, int position, Note note);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NoteDetailsComponent noteDetailsComponent = noteDetailsComponentList.get(position);
        holder.setNote(noteDetailsComponent, showCheckboxes, isChecked, currentPosition, position);
        holder.checkBox.setChecked(noteDetailsComponent.isChecked());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                noteDetailsComponent.setChecked(isChecked);
                listNoteIdChecked = getCheckboxStates();
                numberNoteChecked = listNoteIdChecked.size();
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isLongClick = true;
                currentPosition = position;
                AnimatorSet animatorSet = new AnimatorSet();
                ObjectAnimator scaleXDown = ObjectAnimator.ofFloat(v, "scaleX", 0.95f);
                ObjectAnimator scaleYDown = ObjectAnimator.ofFloat(v, "scaleY", 0.95f);
                ObjectAnimator scaleXUp = ObjectAnimator.ofFloat(v, "scaleX", 1f);
                ObjectAnimator scaleYUp = ObjectAnimator.ofFloat(v, "scaleY", 1f);
                ObjectAnimator alphaDown = ObjectAnimator.ofFloat(v, "alpha", 0.8f);
                ObjectAnimator alphaUp = ObjectAnimator.ofFloat(v, "alpha", 1f);
                animatorSet.play(scaleXDown).with(scaleYDown).with(alphaDown);
                animatorSet.play(scaleXUp).with(scaleYUp).with(alphaUp).after(scaleXDown);
                animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                animatorSet.setDuration(100);
                animatorSet.start();
                noteListener.onItemLongPress(v, position, noteDetailsComponent.getNote());
                showCheckboxes = true;
                notifyItemChanged(position);
                return true;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLongClick) {
                    if (noteListener != null) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        ObjectAnimator scaleXDown = ObjectAnimator.ofFloat(v, "scaleX", 0.95f);
                        ObjectAnimator scaleYDown = ObjectAnimator.ofFloat(v, "scaleY", 0.95f);
                        ObjectAnimator scaleXUp = ObjectAnimator.ofFloat(v, "scaleX", 1f);
                        ObjectAnimator scaleYUp = ObjectAnimator.ofFloat(v, "scaleY", 1f);
                        ObjectAnimator alphaDown = ObjectAnimator.ofFloat(v, "alpha", 0.8f);
                        ObjectAnimator alphaUp = ObjectAnimator.ofFloat(v, "alpha", 1f);

                        animatorSet.play(scaleXDown).with(scaleYDown).with(alphaDown);
                        animatorSet.play(scaleXUp).with(scaleYUp).with(alphaUp).after(scaleXDown);
                        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
                        animatorSet.setDuration(100);
                        animatorSet.start();
                        animatorSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                noteListener.onItemClick(v, position, noteDetailsComponent.getNote());
                            }
                        });
                    }
                } else {


                    //reset flag
                    isLongClick = false;
                }
                isLongClick = false;

            }
        });
    }

    @Override
    public int getItemCount() {
        return noteDetailsComponentList.size();
    }


    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        TextView textTextSegment;
        TextView textDateTime;
        TextView textTag;
        RoundedImageView imageView;
        CardView cardView;
        CheckBox checkBox;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title_main);
            textTextSegment = itemView.findViewById(R.id.text_segment_main);
            textDateTime = itemView.findViewById(R.id.text_date_time_main);
            textTag = itemView.findViewById(R.id.text_tag_main);
            imageView = itemView.findViewById(R.id.image_main);
            cardView = itemView.findViewById(R.id.card_note_main);
            checkBox = itemView.findViewById(R.id.checkbox);
        }

        public void setNote(NoteDetailsComponent noteDetailsComponent, boolean showCheckbox, boolean isChecked, int currentPosition, int position) {
            if (noteDetailsComponent != null) {
                Note note = noteDetailsComponent.getNote();
                Tag tag = noteDetailsComponent.getTag();
                List<TextSegment> textSegmentList = noteDetailsComponent.getTextSegmentList();
                List<Image> imageList = noteDetailsComponent.getImageList();
                List<Audio> audioList = noteDetailsComponent.getAudioList();

                // Set title
                if (note != null && note.getTitle() != null && !note.getTitle().isEmpty()) {
                    textTitle.setText(note.getTitle());
                } else {
                    textTitle.setText(R.string.none_title);
                }

                // Set tag name
                if (tag != null && tag.getTagName() != null && !tag.getTagName().isEmpty()) {
                    textTag.setText(tag.getTagName());
                    textTag.setVisibility(View.VISIBLE);
                } else {
                    textTag.setVisibility(View.GONE);
                }

                // Set text segment
                if (textSegmentList != null && !textSegmentList.isEmpty() && textSegmentList.get(0).getText() != null && !textSegmentList.get(0).getText().isEmpty()) {
                    textTextSegment.setText(textSegmentList.get(0).getText());
                    textTextSegment.setVisibility(View.VISIBLE);
                } else {
                    textTextSegment.setText(R.string.none_text);
                    textTextSegment.setVisibility(View.VISIBLE);
                }

                // Set image
                if (imageList != null && !imageList.isEmpty() && imageList.get(0) != null && imageList.get(0).getImageData() != null) {
                    imageView.setImageBitmap(ImageUtils.byteToBitmap(imageList.get(0).getImageData()));
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
                //Set date time
                textDateTime.setText(CurrentTime.convertTimeFromMiliSecond(note.getCreateAt()));
                //setColor
                if (note.getColor() != null) {
                    cardView.setCardBackgroundColor(Color.parseColor(note.getColor()));
                }

                if (currentPosition != -1) {
                    checkBox.setVisibility(showCheckbox && currentPosition == position ? View.VISIBLE : View.INVISIBLE);
                    checkBox.setChecked(isChecked);
                }
                // Todo: Set UI for audio
            }

        }

    }
}
