package com.example.notestakingapp.adapter;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import java.util.List;
import java.util.zip.Inflater;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<NoteDetailsComponent> noteDetailsComponentList;
    private NoteListener noteListener;
    public void setNoteListener(NoteListener noteListener) {
        this.noteListener = noteListener;
    }

    public void setNotes(List<NoteDetailsComponent> noteDetailsComponentList) {
        this.noteDetailsComponentList = noteDetailsComponentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_main, parent, false);
        return new NoteViewHolder(view);
    }
    public interface NoteListener {
        void onItemClick(View view, int position, Note note);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        NoteDetailsComponent noteDetailsComponent = noteDetailsComponentList.get(position);
        holder.setNote(noteDetailsComponent);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noteListener!=null) {
                    noteListener.onItemClick(v, position, noteDetailsComponent.getNote());
                }
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

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title_main);
            textTextSegment = itemView.findViewById(R.id.text_segment_main);
            textDateTime = itemView.findViewById(R.id.text_date_time_main);
            textTag = itemView.findViewById(R.id.text_tag_main);
            imageView = itemView.findViewById(R.id.image_main);
            cardView = itemView.findViewById(R.id.card_note_main);
        }

        public void setNote(NoteDetailsComponent noteDetailsComponent) {
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

                // Todo: Set UI for audio
            }

        }

    }
}
