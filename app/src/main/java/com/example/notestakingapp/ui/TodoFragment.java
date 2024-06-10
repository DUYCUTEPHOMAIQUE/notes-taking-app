package com.example.notestakingapp.ui;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.notestakingapp.R;
import com.example.notestakingapp.adapter.TodoAdapter;
import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.database.NoteComponent.ToDo;
import com.example.notestakingapp.database.NoteTakingDatabaseHelper;
import com.example.notestakingapp.notification.AlarmScheduler;
import com.example.notestakingapp.shared.SharedViewModel;
import com.example.notestakingapp.utils.SwiperUtils;
import com.factor.bouncy.BouncyNestedScrollView;
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

    public static SharedViewModel sharedViewModel;
    private String mParam1;
    private String mParam2;
    public static TodoAdapter todoAdapter;
    public static TodoAdapter completedTodoAdapter;
    private SQLiteDatabase db;
    private DatabaseHandler databaseHandler;
    private NoteTakingDatabaseHelper noteTakingDatabaseHelper;
    public RecyclerView recyclerView, completedRecyclerView;
    LinearLayout linearLayoutTodoEmpty, textTask;
    BouncyNestedScrollView layoutToDo;

    public List<ToDo> mList = new ArrayList<>();
    public List<ToDo> completedMList = new ArrayList<>();

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
        noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(getActivity());
        db = noteTakingDatabaseHelper.getReadableDatabase();
        databaseHandler = new DatabaseHandler();
        mList = databaseHandler.getToDoListCompletedOrNot(getActivity(), false, "DESC");
        completedMList = databaseHandler.getToDoListCompletedOrNot(getActivity(), true, "DESC");
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
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
        //
        recyclerView = view.findViewById(R.id.task_recycler);
        completedRecyclerView = view.findViewById(R.id.completed_task_recycler);
        linearLayoutTodoEmpty = view.findViewById(R.id.layout_todo_empty);
        layoutToDo = view.findViewById(R.id.layout_todo);
        textTask = view.findViewById(R.id.text_task);
        //
        todoAdapter = new TodoAdapter();
        completedTodoAdapter = new TodoAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        completedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateView();
        completedTodoAdapter.setTodoListener(new TodoAdapter.TodoListener() {
            @Override
            public void onItemClick(View v, int position, ToDo todo) {
                routeToTodoEditUpdate(getActivity(), todo);
            }

            @Override
            public void onCheckBoxClick(int position, ToDo toDo, boolean isChecked) {
                toDo.setCompleted(isChecked);
                databaseHandler.updateTodo(getActivity(), toDo.getId(), isChecked);
                sharedViewModel.setIsTodoChange(true);
                sharedViewModel.notifyDataChanged();
            }
        });
        todoAdapter.setTodoListener(new TodoAdapter.TodoListener() {
            @Override
            public void onItemClick(View v, int position, ToDo todo) {
                routeToTodoEditUpdate(getActivity(), todo);
            }

            @Override
            public void onCheckBoxClick(int position, ToDo toDo, boolean isChecked) {
                toDo.setCompleted(isChecked);
                databaseHandler.updateTodo(getActivity(), toDo.getId(), isChecked);
                if (toDo == null)
                    return;
                if (isChecked) {
                    mList.remove(position);
                    todoAdapter.notifyItemRemoved(position);
                    completedMList.add(0, toDo);
                    completedTodoAdapter.notifyItemInserted(0);
                    Log.d("checkDuy", "checked");
                } else {
                    completedMList.remove(position);
                    completedTodoAdapter.notifyItemRemoved(position);
                    mList.add(0, toDo);
                    todoAdapter.notifyItemInserted(0);
                    Log.d("checkDuy", "UNchecked");
                }

            }
        });


        sharedViewModel.getDataChanged().observe(getViewLifecycleOwner(), u -> {
            updateViewWhenInsertOrUpdate();
        });

        //handle swiper to delete
        ItemTouchHelper touchHelperForIncompleted = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ToDo task = mList.get(position);
                //ui
                mList.remove(position);
                todoAdapter.notifyItemRemoved(position);
                Log.d("duycheck", mList.toString());

                Snackbar snackbar = Snackbar.make(recyclerView, "Delete Task completed", Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ui
                        mList.add(position, task);
                        todoAdapter.notifyItemInserted(position);
                    }

                });
                snackbar.show();
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                            // Perform the deletion from the database after the Snackbar is dismissed and not due to an "Undo" action
                            //delete to-do alarm
                            AlarmScheduler.cancelTaskAlarm(getActivity(), task.getId());
                            //todo: db --OK
                            DatabaseHandler.deleteTodo(getActivity(), task.getId());
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

        });ItemTouchHelper touchHelperForCompleted = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ToDo task = completedMList.get(position);
                //ui
                completedMList.remove(position);
                completedTodoAdapter.notifyItemRemoved(position);
                Log.d("duycheck", completedMList.toString());

                Snackbar snackbar = Snackbar.make(completedRecyclerView, "Delete Task completed", Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ui
                        completedMList.add(position, task);
                        completedTodoAdapter.notifyItemInserted(position);
                    }

                });
                snackbar.show();
                snackbar.addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event != Snackbar.Callback.DISMISS_EVENT_ACTION) {
                            // Perform the deletion from the database after the Snackbar is dismissed and not due to an "Undo" action
                            //delete to-do alarm
                            AlarmScheduler.cancelTaskAlarm(getActivity(), task.getId());
                            //todo: db --OK
                            DatabaseHandler.deleteTodo(getActivity(), task.getId());
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
        touchHelperForIncompleted.attachToRecyclerView(recyclerView);
        touchHelperForCompleted.attachToRecyclerView(completedRecyclerView);
    }

    public static void performSearch(String query) {
        todoAdapter.getFilter().filter(query);
        completedTodoAdapter.getFilter().filter(query);
    }

    private void updateViewWhenInsertOrUpdate() {
        mList = databaseHandler.getToDoListCompletedOrNot(getActivity(), false, "DESC");
        completedMList = databaseHandler.getToDoListCompletedOrNot(getActivity(), true, "DESC");
        //todo: test
        if (mList != null) {
            todoAdapter.setTodos(mList);
            recyclerView.setAdapter(todoAdapter);
            layoutToDo.setVisibility(View.VISIBLE);
            linearLayoutTodoEmpty.setVisibility(View.GONE);

        }
        if (completedMList != null) {
            completedTodoAdapter.setTodos(completedMList);
            completedRecyclerView.setAdapter(completedTodoAdapter);
            layoutToDo.setVisibility(View.VISIBLE);
            linearLayoutTodoEmpty.setVisibility(View.GONE);
        }
        if ((mList == null || mList.isEmpty()) && (completedMList == null || completedMList.isEmpty())) {
            layoutToDo.setVisibility(View.GONE);
            linearLayoutTodoEmpty.setVisibility(View.VISIBLE);
        }


    }

    private void updateView() {
        todoAdapter.notifyItemInserted(0);
        completedTodoAdapter.notifyItemInserted(0);
        //todo: test
        if (mList != null) {
            todoAdapter.setTodos(mList);
            recyclerView.setAdapter(todoAdapter);
            layoutToDo.setVisibility(View.VISIBLE);
            linearLayoutTodoEmpty.setVisibility(View.GONE);
        }
        if (completedMList != null) {
            completedTodoAdapter.setTodos(completedMList);
            completedRecyclerView.setAdapter(completedTodoAdapter);
            layoutToDo.setVisibility(View.VISIBLE);
            linearLayoutTodoEmpty.setVisibility(View.GONE);
        }
        if ((mList == null || mList.isEmpty()) && (completedMList == null || completedMList.isEmpty())) {
            layoutToDo.setVisibility(View.GONE);
            linearLayoutTodoEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void routeToTodoEdit(Context context) {
        BottomDialog.showToDoDiaLog(context, null);
    }

    private void routeToTodoEditUpdate(Context context, ToDo todo) {
        BottomDialog.showToDoDiaLog(context, todo);
    }
}