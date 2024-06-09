package com.example.notestakingapp.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notestakingapp.R;
import com.example.notestakingapp.adapter.TodoAdapter;
import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.database.NoteComponent.ToDo;
import com.example.notestakingapp.notification.AlarmScheduler;
import com.example.notestakingapp.shared.Item;
import com.example.notestakingapp.ui.TodoFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class SwiperUtils {
    public static ItemTouchHelper handleSwiper(Context context, List<ToDo> list, TodoAdapter adapter, RecyclerView recyclerView) {
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ToDo task = list.get(position);
                //ui
                list.remove(position);
                adapter.notifyItemRemoved(position);
                Log.d("duycheck", list.toString());

                Snackbar snackbar = Snackbar.make(recyclerView, "Delete Task completed", Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ui
                        list.add(position, task);
                        adapter.notifyItemInserted(position);
                    }

                });
                snackbar.show();
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                            // Perform the deletion from the database after the Snackbar is dismissed and not due to an "Undo" action
                            //delete to-do alarm
                            AlarmScheduler.cancelTaskAlarm(context, task.getId());
                            //todo: db --OK
                            DatabaseHandler.deleteTodo(context, task.getId());
                            TodoFragment.sharedViewModel.notifyDataChanged();
                        }
                    }
                });
            }

            //gpt draw canvas view on RecyclerView
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float itemHeight = itemView.getBottom() - itemView.getTop();
                    boolean isCancelled = dX == 0 && !isCurrentlyActive;

                    if (!isCancelled) {
                        Paint paint = new Paint();
                        int colorError = ContextCompat.getColor(recyclerView.getContext(), R.color.colorError);
                        paint.setColor(colorError);

                        // Create a path with rounded corners
                        Path path = new Path();
                        float radius = 24; // Radius for the corners, you can adjust this value
                        RectF background = new RectF(
                                itemView.getRight() + dX, itemView.getTop(),
                                itemView.getRight(), itemView.getBottom());
                        path.addRoundRect(background, radius, radius, Path.Direction.CW);

                        // Clip the canvas to the rounded rectangle
                        c.save();
                        c.clipPath(path);

                        // Draw the red background
                        c.drawRect(background, paint);

                        // Draw the trash icon
                        Drawable icon = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.trash);
                        int colorTrash = ContextCompat.getColor(recyclerView.getContext(), R.color.colorWhite);

                        if (icon != null) {
                            icon.setTint(colorTrash);
                            int intrinsicWidth = icon.getIntrinsicWidth();
                            int intrinsicHeight = icon.getIntrinsicHeight();
                            int iconMargin = (int) ((itemHeight - intrinsicHeight) / 2);
                            int iconTop = (int) (itemView.getTop() + (itemHeight - intrinsicHeight) / 2);
                            int iconLeft = itemView.getRight() - iconMargin - intrinsicWidth;
                            int iconRight = itemView.getRight() - iconMargin;
                            int iconBottom = iconTop + intrinsicHeight;

                            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                            icon.draw(c);
                        }

                        // Restore the canvas to remove the clipping
                        c.restore();
                    }
                }

                // Ensure the item is still drawn in its new position
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        });
        return touchHelper;
    }
}
