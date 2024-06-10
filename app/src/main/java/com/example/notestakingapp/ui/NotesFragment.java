package com.example.notestakingapp.ui;

import static com.example.notestakingapp.adapter.NotesAdapter.listNoteIdChecked;
import static com.example.notestakingapp.adapter.NotesAdapter.showCheckboxes;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.notestakingapp.adapter.TagAdapter;
import com.example.notestakingapp.database.NoteComponent.Tag;
import com.example.notestakingapp.shared.Item;
import com.example.notestakingapp.R;
import com.example.notestakingapp.adapter.NotesAdapter;
import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.database.NoteComponent.Audio;
import com.example.notestakingapp.database.NoteComponent.Component;
import com.example.notestakingapp.database.NoteComponent.Image;
import com.example.notestakingapp.database.NoteComponent.Note;
import com.example.notestakingapp.database.NoteComponent.TextSegment;
import com.example.notestakingapp.database.NoteTakingDatabaseHelper;
import com.example.notestakingapp.shared.SharedViewModel;
import com.example.notestakingapp.utils.NoteDetailsComponent;
import com.factor.bouncy.BouncyRecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    public List<NoteDetailsComponent> list;
    private String mParam2;
    public BouncyRecyclerView recyclerView;
    public static NotesAdapter notesAdapter;
    public static TagAdapter tagAdapter;
    private SQLiteDatabase db;
    private DatabaseHandler databaseHandler;
    private NoteTakingDatabaseHelper noteTakingDatabaseHelper;
    public static SharedViewModel sharedViewModel;
    private List<Integer> listNoteIDChecked;
    public static ActivityResultLauncher<Intent> noteEditLauncher;
    FloatingActionButton exitButton;
    LinearLayout linearLayoutNotesEmpty;
    RecyclerView recyclerViewTag;
    List<String> listTagName;


    public NotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotesFragment newInstance(String param1, String param2) {
        NotesFragment fragment = new NotesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        noteEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                updateView();
            }
        });
        sharedViewModel.getClearUiEvent().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean clearUi) {
                try {
                    if (clearUi != null && clearUi) {
                        clearUiAndStateCheck();
                        notesAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {

                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //khoi tao db
        noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(getActivity());
        db = noteTakingDatabaseHelper.getReadableDatabase();
        databaseHandler = new DatabaseHandler();
        recyclerView = view.findViewById(R.id.recycler_view_notes);
        linearLayoutNotesEmpty = view.findViewById(R.id.layout_notes_empty);
        recyclerViewTag = view.findViewById(R.id.recycler_view_tag);
         listTagName = new ArrayList<>();
        initTagView();
        tagAdapter.setTagListener(new TagAdapter.TagListener() {
            @Override
            public void onTagClick(int position, String tagName) {
                String query = "#"+tagName.toLowerCase();
                notesAdapter.getFilter().filter(query);
            }
        });

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        notesAdapter = new NotesAdapter(getActivity(), getString(R.string.all));

        notesAdapter.setNoteListener(new NotesAdapter.NoteListener() {
            @Override
            public void onItemClick(View view, int position, Note note) {
                Intent intent = new Intent(getActivity(), NoteEditActivity.class);
                intent.putExtra("note_id", note.getNoteId());
                noteEditLauncher.launch(intent);
            }

            @Override
            public void onItemLongPress(View view, int position, Note note) {
                // Cập nhật shared view model
                if (listNoteIdChecked != null) {

                }
                sharedViewModel.setItemLongPressed(true);
            }
        });
        updateView();

        sharedViewModel.getNotes().observe(getViewLifecycleOwner(), new Observer<List<NoteDetailsComponent>>() {
            @Override
            public void onChanged(List<NoteDetailsComponent> noteDetailsComponents) {
                notesAdapter.setNotes(noteDetailsComponents);
                notesAdapter.notifyDataSetChanged();
            }
        });
        sharedViewModel.getTagChanged().observe(getViewLifecycleOwner(), isTagChanged -> {
            updateTagView();
        });
        sharedViewModel.getDataChanged().observe(getViewLifecycleOwner(), isDataChanged -> {
            try {
                notesAdapter.notifyDataSetChanged();
                updateView();
            } catch (Exception e) {

            }
        });
        sharedViewModel.getIsDelete().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isDeletedOK) {
                if (isDeletedOK != null && isDeletedOK) {
                    try {
                        //
                    } catch (Exception e) {

                    }
                }
                notesAdapter.notifyDataSetChanged();
                updateView();
            }
        });
    }

    private void initTagView() {
        List<Tag> tagList = DatabaseHandler.getAllTags(getActivity());
        listTagName.add(getString(R.string.all));
        Set<String> uniqueTags = new HashSet<>();
        for (Tag i : tagList) {
            uniqueTags.add(i.getTagName().trim());
        }
        listTagName.addAll(new ArrayList<>(uniqueTags));
        Log.d("listTagDuy", listTagName.toString());
        tagAdapter = new TagAdapter();
        tagAdapter.setList(listTagName);
        recyclerViewTag.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewTag.setAdapter(tagAdapter);
    }
    private void updateTagView() {
        List<Tag> tagList = DatabaseHandler.getAllTags(getActivity());
        Set<String> uniqueTags = new HashSet<>();
        for (Tag i : tagList) {
            uniqueTags.add(i.getTagName().trim());
        }
        List<String> temp = new ArrayList<>(uniqueTags);
        listTagName.clear();
        listTagName.add(getString(R.string.all));
        listTagName.addAll(temp);
        tagAdapter.notifyDataSetChanged();
    }

    public static void performSearch(String query) {
        Log.d("filterDuy", "query = " + query);
        notesAdapter.getFilter().filter(query);
    }

    public void updateView() {

        List<Note> noteList = DatabaseHandler.getNoteByCreateAt(getActivity(), "desc");
        LinkedHashMap<Integer, ArrayList<Component>> hashMap = new LinkedHashMap<>();
        if (noteList != null && !noteList.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            linearLayoutNotesEmpty.setVisibility(View.GONE);
            for (Note note : noteList) {
                hashMap.put(note.getNoteId(), databaseHandler.getAllComponent(getActivity(), note.getNoteId()));
            }
        } else {
            recyclerView.setVisibility(View.GONE);
            linearLayoutNotesEmpty.setVisibility(View.VISIBLE);
        }

        list = componentToProps(hashMap);
        notesAdapter.setNotes(list);
        recyclerView.setAdapter(notesAdapter);
    }

    public List<NoteDetailsComponent> componentToProps(HashMap<Integer, ArrayList<Component>> input) {
        if (input == null) return new ArrayList<>();
        List<NoteDetailsComponent> noteDetailsComponentList = new ArrayList<>();
        LinkedHashMap<Integer, List<Object>> output = new LinkedHashMap<>();
        for (Map.Entry<Integer, ArrayList<Component>> entry : input.entrySet()) {
            Integer key = entry.getKey();
            ArrayList<Component> value = entry.getValue();
            List<Object> temp = new ArrayList<>();
            for (Component i : value) {
                switch (i.getType()) {
                    case Item.TYPE_EDIT_TEXT:
                        temp.add(databaseHandler.getTextSegment(getActivity(), i));
                        break;
                    case Item.TYPE_IMAGE_VIEW:
                        temp.add(databaseHandler.getImage(getActivity(), i));
                        break;
                    case Item.TYPE_VOICE_VIEW:
                        temp.add(databaseHandler.getAudio(getActivity(), i));
                        break;
                }
            }
            output.put(key, temp);
        }

        for (Map.Entry<Integer, List<Object>> entry : output.entrySet()) {
            Note note;
            List<TextSegment> textSegmentList = new ArrayList<>();
            List<Image> imageList = new ArrayList<>();
            List<Audio> audioList = new ArrayList<>();
            List<Object> value = entry.getValue();
            note = databaseHandler.getNoteById(getActivity(), entry.getKey());
            assert note != null;
            Tag tag = new Tag(DatabaseHandler.getTagIdByNoteId(getActivity(), note.getNoteId()),  DatabaseHandler.getTagNameByTagId(getActivity(), DatabaseHandler.getTagIdByNoteId(getActivity(), note.getNoteId())));
            for (Object i : value) {
                if (i instanceof TextSegment) {
                    textSegmentList.add((TextSegment) i);
                } else if (i instanceof Image) {
                    imageList.add((Image) i);
                } else if (i instanceof Audio) {
                    audioList.add((Audio) i);
                } else {
                    //loi
                }
            }
            noteDetailsComponentList.add(new NoteDetailsComponent(note, textSegmentList, imageList, audioList, tag));
        }
        return noteDetailsComponentList;
    }

    public void clearUiAndStateCheck() {
        showCheckboxes = false;
        for (NoteDetailsComponent i :
                list) {
            i.setChecked(false);
        }
    }
}