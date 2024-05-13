package com.example.notestakingapp.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notestakingapp.R;
import com.example.notestakingapp.database.NoteComponent.Audio;
import com.example.notestakingapp.database.NoteComponent.Image;
import com.example.notestakingapp.database.NoteComponent.Note;
import com.example.notestakingapp.database.NoteComponent.Tag;
import com.example.notestakingapp.database.NoteComponent.TextSegment;
import com.example.notestakingapp.utils.ImageUtils;
import com.example.notestakingapp.utils.NoteDetailsComponent;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;
import java.util.zip.Inflater;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<NoteDetailsComponent> noteDetailsComponentList;
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

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NoteDetailsComponent noteDetailsComponent = noteDetailsComponentList.get(position);
        holder.textTitle.setText(noteDetailsComponent.getNote().getTitle());
    }

    @Override
    public int getItemCount() {
        return 0;
    }


    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        TextView textTextSegment;
        TextView textDateTime;
        TextView textTag;
        RoundedImageView imageView;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title_main);
            textTextSegment = itemView.findViewById(R.id.text_segment_main);
            textDateTime = itemView.findViewById(R.id.text_date_time_main);
            textTag = itemView.findViewById(R.id.text_tag_main);
            imageView = itemView.findViewById(R.id.image_main);
        }
        public void setNote(Note note, @Nullable List<TextSegment> textSegmentList, @Nullable List<Image> imageList, @Nullable List<Audio> audioList,@Nullable Tag tag) {
            textTitle.setText(note.getTitle());
            if (note != null) {
                if(note.getTitle()!=null) {
                    textTitle.setText(note.getTitle());
                }
                else {
                    textTitle.setText(R.string.none_title);
                }
            }
            else {
                textTitle.setText(R.string.none_title);
            }
            //setTagName
            if (tag!=null) {
                if(!tag.getTagName().isEmpty() && tag.getTagName() != null) {
                    textTag.setText(tag.getTagName());
                    textTag.setVisibility(View.VISIBLE);
                }
                else {
                    textTag.setVisibility(View.GONE);
                }
            }
            else {
                textTag.setVisibility(View.GONE);
            }

            //setText
            if (textSegmentList!=null) {
                if(!textSegmentList.get(0).getText().isEmpty() && textSegmentList.get(0).getText() != null) {
                    textTextSegment.setText(textSegmentList.get(0).getText());
                    textTextSegment.setVisibility(View.VISIBLE);
                }
                else {
                    textTextSegment.setVisibility(View.GONE);
                }
            }
            else {
                textTextSegment.setVisibility(View.GONE);
            }

            //setImage
            if (imageList!=null || imageList.get(0)!= null) {
                    imageView.setImageBitmap(ImageUtils.byteToBitmap(imageList.get(0).getImageData()));
                    imageView.setVisibility(View.VISIBLE);

            }else {
                imageView.setVisibility(View.GONE);
            }
            //todo: set ui audio
            if(audioList.isEmpty()) {

            }else {

            }
        }

    }

}
