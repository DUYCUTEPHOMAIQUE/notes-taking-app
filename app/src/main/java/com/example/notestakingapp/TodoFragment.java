package com.example.notestakingapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.notestakingapp.adapter.TodoAdapter;
import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.database.NoteComponent.ToDo;
import com.example.notestakingapp.database.NoteTakingDatabaseHelper;
import com.example.notestakingapp.ui.BottomDialog;
import com.example.notestakingapp.ui.ToDoTest;
import com.factor.bouncy.BouncyRecyclerView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public TodoAdapter todoAdapter;
    public TodoAdapter completedTodoAdapter;
    private SQLiteDatabase db;
    private DatabaseHandler databaseHandler;
    private NoteTakingDatabaseHelper noteTakingDatabaseHelper;
    public RecyclerView recyclerView, completedRecyclerView;
    public List<ToDo> list;
    public List<ToDoTest> mList = new ArrayList<>();
    public List<ToDoTest> completedMList = new ArrayList<>();

    public TodoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodoFragment newInstance(String param1, String param2) {
        TodoFragment fragment = new TodoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //test
        mList.add(new ToDoTest(1, "Task 1", 1716601500000L , false));
        mList.add(new ToDoTest(2, "Task 2", 1716601500000L , false));
        mList.add(new ToDoTest(3, "Task 3", 1716601500000L , false));
        mList.add(new ToDoTest(4, "Task 4", 1716601500000L , false));

        completedMList.add(new ToDoTest(6, "Task 6", 1716601500000L , true));
        completedMList.add(new ToDoTest(7, "Task 7", 1716601500000L , true));
        completedMList.add(new ToDoTest(8, "Task 8", 1716601500000L , true));
        completedMList.add(new ToDoTest(9, "Task 9", 1716601500000L, true));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(getActivity());
        db = noteTakingDatabaseHelper.getReadableDatabase();
        databaseHandler = new DatabaseHandler();
        //
        recyclerView = view.findViewById(R.id.task_recycler);
        completedRecyclerView = view.findViewById(R.id.completed_task_recycler);
        //
        todoAdapter = new TodoAdapter();
        completedTodoAdapter = new TodoAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        completedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateView();
        completedTodoAdapter.setTodoListener(new TodoAdapter.TodoListener() {
            @Override
            public void onItemClick(View v, int position, ToDoTest todo) {
                routeToTodoEditUpdate(getActivity(), todo);
            }
        });
        todoAdapter.setTodoListener(new TodoAdapter.TodoListener() {
            @Override
            public void onItemClick(View v, int position, ToDoTest todo) {
                routeToTodoEditUpdate(getActivity(), todo);
            }
        });
        ItemTouchHelper incompleteTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ToDoTest task = mList.get(position);
                task.setCompleted(true);
                mList.remove(position);
                todoAdapter.notifyItemRemoved(position);
                completedMList.add(0, task);
                completedTodoAdapter.notifyItemInserted(0);

                Snackbar snackbar = Snackbar.make(recyclerView, "Task completed", Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ui
                        task.setCompleted(false);
                        mList.add(position, task);
                        todoAdapter.notifyItemInserted(position);
                        completedMList.remove(task);
                        completedTodoAdapter.notifyDataSetChanged();

                        //todo: db
                    }

                });
                snackbar.show();
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
                        float radius = 16; // Radius for the corners, you can adjust this value
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

        ItemTouchHelper completeTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
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
                        float radius = 16; // Radius for the corners, you can adjust this value
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

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ToDoTest task = completedMList.get(position);
                task.setCompleted(false);
                completedMList.remove(position);
                completedTodoAdapter.notifyItemRemoved(position);
                mList.add(task);
                todoAdapter.notifyItemInserted(mList.size() - 1 );

                Snackbar snackbar = Snackbar.make(completedRecyclerView, "Task completed", Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ui
                        task.setCompleted(false);
                        completedMList.add(position, task);
                        completedTodoAdapter.notifyItemInserted(position);
                        mList.remove(task);
                        todoAdapter.notifyDataSetChanged();

                        //todo: db
                    }

                });
                snackbar.show();
            }
        });

        incompleteTouchHelper.attachToRecyclerView(recyclerView);
        completeTouchHelper.attachToRecyclerView(completedRecyclerView);
    }

    private void updateView() {
        list = databaseHandler.getAllToDo(getActivity());

        //todo: test
        if(mList!= null && completedMList != null) {
            Log.d("todoDuy", mList.toString()+" ** "+completedMList.toString());
            todoAdapter.setTodos(mList);
            recyclerView.setAdapter(todoAdapter);

            completedTodoAdapter.setTodos(completedMList);
            completedRecyclerView.setAdapter(completedTodoAdapter);
        }
    }

    private void routeToTodoEdit(Context context) {
        BottomDialog.showToDoDiaLog(context, null );
    }
    private void routeToTodoEditUpdate(Context context, ToDoTest todo) {
        BottomDialog.showToDoDiaLog(context, todo);
    }
}