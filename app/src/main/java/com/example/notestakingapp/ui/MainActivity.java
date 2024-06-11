package com.example.notestakingapp.ui;

import com.example.notestakingapp.authentication.SettingsActivity;
import com.example.notestakingapp.database.NoteComponent.Tag;
import com.example.notestakingapp.shared.Item;
import com.example.notestakingapp.R;
import com.example.notestakingapp.database.NoteComponent.Note;


import com.example.notestakingapp.database.NoteComponent.Audio;
import com.example.notestakingapp.database.NoteComponent.Component;
import com.example.notestakingapp.database.NoteComponent.Image;
import com.example.notestakingapp.database.NoteComponent.TextSegment;

import static com.example.notestakingapp.adapter.NotesAdapter.listNoteIdChecked;

import com.example.notestakingapp.R.id;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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

import com.example.notestakingapp.utils.LanguageUtils;
import com.example.notestakingapp.utils.NoteDetailsComponent;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
    private LinearLayout layoutBackDelete, layoutDoDelete;
    boolean isNightModeOn;
    SharedPreferences sharedThemePreferences;

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

        // set theme mode when initialize app
        sharedThemePreferences = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        isNightModeOn = sharedThemePreferences.getBoolean("night", false);
        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

	    // Intent drawingIntent = new Intent(MainActivity.this, DrawingActivity.class);
        // drawingIntent.putExtra("imageId", 0);
	    // startActivity(drawingIntent);

        //khoi tao khi vao app chon ngon ngu

        //db
        NoteTakingDatabaseHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(getApplicationContext());

        db = noteTakingDatabaseHelper.getReadableDatabase();

        DatabaseHandler databaseHandler = new DatabaseHandler();

        // Kiểm tra và tạo kênh thông báo
        createNotificationChannel();

        //  khoi chay ui
        initUi();
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        //test
        noteEditLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult o) {
                updateNotes();
            }
        });
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
                        inputSearch.setHint(getString(R.string.search_notes));
                        inputSearch.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    String query = s.toString().trim();
                                    searchNotes(query);
                            }
                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 1:
                        mBottomNavigationView.getMenu().findItem(R.id.todo_main).setChecked(true);
                        inputSearch.setHint(getString(R.string.search_todo));
                        inputSearch.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    String query = s.toString().trim();
                                    Log.d("todoFrg", query);
                                    searchToDo(query);
                            }
                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });


        inputSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                sharedViewModel.setInputFocus(hasFocus);
            }else {
                sharedViewModel.setInputFocus(hasFocus);
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
                if (isLongPressed) {
                    if (layoutDelete.getVisibility() == View.GONE && listNoteIdChecked != null) {
                        listNoteIdChecked.clear();
                    }
                    layoutDelete.setVisibility(View.VISIBLE);
                    mBottomNavigationView.setVisibility(View.GONE);
                    imageViewAdd.setVisibility(View.GONE);
                } else {
                    layoutDelete.setVisibility(View.GONE);
                    mBottomNavigationView.setVisibility(View.VISIBLE);
                    imageViewAdd.setVisibility(View.VISIBLE);
                }
            }
        });

        //click x button
        layoutBackDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedViewModel.setItemLongPressed(false);
                sharedViewModel.triggerClearUiEvent();
            }
        });
        layoutDoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomDialog.showConfirmDeleteNote(MainActivity.this);
            }
            //todo: chinh sua lai de xoa note va cap nhat giao dien -- OK
        });
        imageSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    // DuongTestingFunction();
    }

    private void DuongTestingFunction() {
        DatabaseHandler.deleteAllTodo(this);
    }

    private void searchToDo(String query) {
        TodoFragment.performSearch(query);
    }

    private void searchNotes(String query) {
        NotesFragment.performSearch(query);
    }

    private void routeToTodoEdit() {
        BottomDialog.showToDoDiaLog(MainActivity.this, null);
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
        layoutBackDelete = findViewById(id.layout_back_delete);
        layoutDoDelete = findViewById(id.layout_do_delete);
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
            Tag tag = new Tag(DatabaseHandler.getTagIdByNoteId(this, note.getNoteId()),  DatabaseHandler.getTagNameByTagId(this, DatabaseHandler.getTagIdByNoteId(this, note.getNoteId())));
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
            noteDetailsComponentList.add(new NoteDetailsComponent(note, textSegmentList, imageList, audioList, tag));
        }
        return noteDetailsComponentList;
    }
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    getString(R.string.app_name),
                    "Task Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for task notifications");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


}