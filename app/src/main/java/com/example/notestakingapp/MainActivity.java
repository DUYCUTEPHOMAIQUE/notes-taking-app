package com.example.notestakingapp;

import com.example.notestakingapp.database.NoteComponent.Note;
import com.example.notestakingapp.database.TempDatabaseHelper;


import com.example.notestakingapp.database.NoteComponent.Audio;
import com.example.notestakingapp.database.NoteComponent.Component;
import com.example.notestakingapp.database.NoteComponent.Image;
import com.example.notestakingapp.database.NoteComponent.Note;
import com.example.notestakingapp.database.NoteComponent.TextSegment;

import com.example.notestakingapp.firebase.FirebaseHandler;

import static com.example.notestakingapp.adapter.NotesAdapter.listNoteIdChecked;
import static com.example.notestakingapp.database.NoteTakingDatabaseHelper.DB_NAME;

import com.example.notestakingapp.R.id;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;


import com.example.notestakingapp.adapter.ViewPagerAdapter;
import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.database.NoteTakingDatabaseHelper;
import com.example.notestakingapp.shared.SharedViewModel;
import com.example.notestakingapp.ui.BottomDialog;
import com.example.notestakingapp.ui.DrawingView;

import com.example.notestakingapp.utils.NoteDetailsComponent;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public ImageView imageViewAdd;
    private BottomNavigationView bottomNavigationView;
    private EditText inputSearch;
    private LinearLayout layoutDelete;
    private ImageView imageViewXButton, imageSettings;
    private ImageView imageTrashButton;

    private ViewPagerAdapter mViewPagerAdapter;
    private BottomNavigationView mBottomNavigationView;
    private ViewPager2 mViewPager2;
    int currentPage;

    private SQLiteDatabase db;
    public static ActivityResultLauncher<Intent> noteEditLauncher;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//	    Intent drawingIntent = new Intent(MainActivity.this, DrawingActivity.class);
//	    drawingIntent.putExtra("imageId", 0);
//	    startActivity(drawingIntent);

        NoteTakingDatabaseHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(getApplicationContext());

        db = noteTakingDatabaseHelper.getReadableDatabase();

        DatabaseHandler databaseHandler = new DatabaseHandler();

//        databaseHandler.insertNote(this, "duong", "1223443", "red", null);

        //khoi chay ui
        initUi();
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        //test
        noteEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                updateNotes();
            }
        });
        //anim popup hehehe T_T
        animButton(imageViewAdd);

        //xu li click button
        imageViewAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_animation);
                imageViewAdd.startAnimation(animation);
                currentPage = mViewPager2.getCurrentItem();
                if (currentPage == 0) {
                    routeToNoteEdit();
                } else if (currentPage == 1) {
                    routeToTodoEdit();
                }
            }
        });

        //setup viewpage switch fragment
        mViewPagerAdapter = new ViewPagerAdapter(this);
        mViewPager2.setAdapter(mViewPagerAdapter);

        //su kien fragment thay doi
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mBottomNavigationView.getMenu().findItem(R.id.home_main).setChecked(true);
                        inputSearch.setHint("Search Notes..");
                        break;
                    case 1:
                        mBottomNavigationView.getMenu().findItem(R.id.todo_main).setChecked(true);
                        inputSearch.setHint("Search To do..");
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        //su kien item in menu thay doi
        mBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == id.home_main) {
                    mViewPager2.setCurrentItem(0);
                } else if (itemId == id.todo_main) {
                    mViewPager2.setCurrentItem(1);
                } else {
                    mViewPager2.setCurrentItem(0);
                }
                return true;
            }
        });

        sharedViewModel.getItemLongPressed().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLongPressed) {
                if(isLongPressed) {
                    layoutDelete.setVisibility(View.VISIBLE);
                    mBottomNavigationView.setVisibility(View.GONE);
                    imageViewAdd.setVisibility(View.GONE);
                }
                else {
                    layoutDelete.setVisibility(View.GONE);
                    mBottomNavigationView.setVisibility(View.VISIBLE);
                    imageViewAdd.setVisibility(View.VISIBLE);
                }
            }
        });

        //click x button
        imageViewXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedViewModel.setItemLongPressed(false);
                sharedViewModel.triggerClearUiEvent();
            }
        });
        imageTrashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listNoteIdChecked!=null && !listNoteIdChecked.isEmpty())
                    BottomDialog.showConfirmDeleteNote(MainActivity.this);
                }
                //todo: chinh sua lai de xoa note va cap nhat giao dien OK
        });
        imageSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

		huyTestingFunction();

    }

	public void huyTestingFunction() {


		//Huy's database sync test code
		//insert a test note
//	    DatabaseHandler.insertNote(this, "Test Note 2", "#FFFFFF");
//	    DatabaseHandler.insertNote(this, "Test Note 3", "#FFFFFF");

//	    DatabaseHandler.deleteAllNote(this);
//		DatabaseHandler.deleteAllTextSegment(this);
//		DatabaseHandler.deleteAllTag(this);
//		DatabaseHandler.deleteAllNoteTag(this);
//		DatabaseHandler.deleteNote(this, 7);
//	    FirebaseHandler.syncToFirebase(this);
//	    FirebaseHandler.syncFromFirebase(this);
//		DatabaseHandler.deleteAllTodo(this);
//		TempDatabaseHelper.mergeTodoTable(this);
//	    TempDatabaseHelper.mergeNoteTable(this);


//	    Log.d("Check exist by created at", Boolean.toString(TempDatabaseHelper.checkExistByCreateAt(this,
//			    DatabaseHandler.NOTE_TABLE,
//			    "1715798015609")));
//	    DatabaseHandler.insertTextSegment(this, 11, "Local Text Segment");
//	    DatabaseHandler.resetAllAutoincrement(this);
//	    long noteid = DatabaseHandler.insertNote(this, "Local Note", "#FFFFFD");
//		long tagid = DatabaseHandler.createNewTag(this, "VU ANH HUY");
//		DatabaseHandler.setTagForNote(this, (int)noteid, (int)tagid);

//		DatabaseHandler.insertTodo(this, 0, "Buy Milk", Long.toString(System.currentTimeMillis()), "0");
	}

    private void routeToTodoEdit() {
        BottomDialog.showToDoDiaLog(MainActivity.this);
    }

    private void routeToNoteEdit() {
        Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
        if (noteEditLauncher != null) {
            noteEditLauncher.launch(intent);
        }
    }

    private void initUi() {
        imageViewAdd = findViewById(R.id.imageAddNoteMain);
        mViewPager2 = findViewById(R.id.view_pager2);
        mBottomNavigationView = findViewById(R.id.layout_nav);
        inputSearch = findViewById(R.id.inputSearch);
        layoutDelete = findViewById(id.layout_delete_some_note);
        imageViewXButton = findViewById(id.x_icon_main_image);
        imageTrashButton = findViewById(id.delete_some_note);
        imageSettings = findViewById(id.image_settings);
    }

    private void animButton(ImageView imageView) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationSet animationSet = new AnimationSet(true);
                Animation zoomInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_in);
                Animation fadeInAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);

                Animation zoomOutAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.zoom_out);

                animationSet.addAnimation(fadeInAnimation);
                animationSet.addAnimation(zoomInAnimation);
                zoomOutAnimation.setStartOffset(250);
                animationSet.addAnimation(zoomOutAnimation);
                imageView.startAnimation(animationSet);
                // Hiển thị button
                imageView.setVisibility(View.VISIBLE);
            }
        }, 600);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    private void updateNotes() {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        List<Note> noteList = databaseHandler.getNoteByCreateAt(this, "desc");
        LinkedHashMap<Integer, ArrayList<Component>> hashMap = new LinkedHashMap<>();
        if (noteList != null) {
            for (Note note : noteList) {
                hashMap.put(note.getNoteId(), databaseHandler.getAllComponent(this, note.getNoteId()));
            }
        }

        List<NoteDetailsComponent> list = componentToProps(hashMap);
        sharedViewModel.setNotes(list);
    }
    private List<NoteDetailsComponent> componentToProps(LinkedHashMap<Integer, ArrayList<Component>> input) {
        List<NoteDetailsComponent> noteDetailsComponentList = new ArrayList<>();
        DatabaseHandler databaseHandler = new DatabaseHandler();

        for (Map.Entry<Integer, ArrayList<Component>> entry : input.entrySet()) {
            Integer key = entry.getKey();
            ArrayList<Component> value = entry.getValue();

            List<Object> temp = new ArrayList<>();
            for (Component i : value) {
                switch (i.getType()) {
                    case Item.TYPE_EDIT_TEXT:
                        temp.add(databaseHandler.getTextSegment(this, i));
                        break;
                    case Item.TYPE_IMAGE_VIEW:
                        temp.add(databaseHandler.getImage(this, i));
                        break;
                    case Item.TYPE_VOICE_VIEW:
                        temp.add(databaseHandler.getAudio(this, i));
                        break;
                }
            }

            Note note = databaseHandler.getNoteById(this, key);
            List<TextSegment> textSegmentList = new ArrayList<>();
            List<Image> imageList = new ArrayList<>();
            List<Audio> audioList = new ArrayList<>();

            for (Object i : temp) {
                if (i instanceof TextSegment) {
                    textSegmentList.add((TextSegment) i);
                } else if (i instanceof Image) {
                    imageList.add((Image) i);
                } else if (i instanceof Audio) {
                    audioList.add((Audio) i);
                }
            }
            //todo: tag dang la null
            noteDetailsComponentList.add(new NoteDetailsComponent(note, textSegmentList, imageList, audioList, null));
        }
        return noteDetailsComponentList;
    }

}