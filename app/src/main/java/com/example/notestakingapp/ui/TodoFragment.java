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

import com.example.notestakingapp.R;
import com.example.notestakingapp.adapter.TodoAdapter;
import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.database.NoteComponent.ToDo;
import com.example.notestakingapp.database.NoteTakingDatabaseHelper;
import com.example.notestakingapp.shared.SharedViewModel;
import com.example.notestakingapp.utils.SwiperUtils;
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

    private SharedViewModel sharedViewModel;
    private String mParam1;
    private String mParam2;
    public static TodoAdapter todoAdapter;
    public static TodoAdapter completedTodoAdapter;
    private SQLiteDatabase db;
    private DatabaseHandler databaseHandler;
    private NoteTakingDatabaseHelper noteTakingDatabaseHelper;
    public RecyclerView recyclerView, completedRecyclerView;
    public List<ToDo> list;
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
                } else {
                    completedMList.remove(position);
                    completedTodoAdapter.notifyItemRemoved(position);
                    mList.add(0, toDo);
                    todoAdapter.notifyItemInserted(0);
                }
            }
        });


        sharedViewModel.getIsTodoChange().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                updateViewWhenInsertOrUpdate();
                Log.d("updateView", "upppp!!");
            }
        });
        NotesFragment.sharedViewModel.getDataChanged().observe(getViewLifecycleOwner(), u -> {
            updateViewWhenInsertOrUpdate();
        });

        //handle swiper to delete
        SwiperUtils.handleSwiper(getActivity(), mList, todoAdapter, recyclerView).attachToRecyclerView(recyclerView);
        SwiperUtils.handleSwiper(getActivity(), completedMList, completedTodoAdapter, completedRecyclerView).attachToRecyclerView(completedRecyclerView);
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
        }
        if (completedMList != null) {
            completedTodoAdapter.setTodos(completedMList);
            completedRecyclerView.setAdapter(completedTodoAdapter);
        }
    }

    private void updateView() {
        todoAdapter.notifyItemInserted(0);
        completedTodoAdapter.notifyItemInserted(0);
        //todo: test
        if (mList != null) {
            todoAdapter.setTodos(mList);
            recyclerView.setAdapter(todoAdapter);
        }
        if (completedMList != null) {
            completedTodoAdapter.setTodos(completedMList);
            completedRecyclerView.setAdapter(completedTodoAdapter);
        }
    }

    private void routeToTodoEdit(Context context) {
        BottomDialog.showToDoDiaLog(context, null);
    }

    private void routeToTodoEditUpdate(Context context, ToDo todo) {
        BottomDialog.showToDoDiaLog(context, todo);
    }
}