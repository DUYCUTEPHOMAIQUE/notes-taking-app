package com.example.notestakingapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notestakingapp.R;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {
    List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    TagListener tagListener;
    public TagListener getTagListener() {
        return tagListener;
    }

    public void setTagListener(TagListener tagListener) {
        this.tagListener = tagListener;
    }


    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag_main, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        String tagName = list.get(position);
        holder.setTagView(tagName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tagListener!=null) {
                    tagListener.onTagClick(position, tagName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list == null) return 0;
        return list.size();
    }

    static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tagView;
        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagView = itemView.findViewById(R.id.tag_main);
        }
        public void setTagView(String tagName) {
            tagView.setText(tagName);
        }
    }
    public interface TagListener {
        void onTagClick(int position, String tagName);
    }
}
