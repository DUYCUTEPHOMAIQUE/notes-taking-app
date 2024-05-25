package com.example.notestakingapp.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notestakingapp.R;
import com.example.notestakingapp.database.NoteComponent.ToDo;
import com.example.notestakingapp.ui.ToDoTest;
import com.example.notestakingapp.utils.AnimUtils;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    List<ToDoTest> listTodo;
    TodoListener todoListener;
    public void setTodoListener(TodoListener todoListener) {
        this.todoListener = todoListener;
    }

    public void setTodos(List<ToDoTest> list) {
        this.listTodo = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo_main, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        ToDoTest todo = listTodo.get(position);
        holder.setTodo(todo);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatorSet animatorSet = AnimUtils.setAnim(v);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                        super.onAnimationEnd(animation);
                        if(todo != null && todoListener!=null)
                            todoListener.onItemClick(v,position, todo);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listTodo != null)
            return listTodo.size();
        return 0;
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView todoContent;
        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.is_completed);
            todoContent = itemView.findViewById(R.id.todo_content);
        }

        public void setTodo(ToDoTest todo) {
            if(todo!=null) {
                //todo: setcheckbox nx
                todoContent.setText(todo.getContent());
            }
        }
    }
    public interface TodoListener {
        void onItemClick(View v, int position, ToDoTest todo);
    }
}
