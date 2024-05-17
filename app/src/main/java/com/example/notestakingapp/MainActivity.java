package com.example.notestakingapp;
import com.example.notestakingapp.database.NoteComponent.Note;
import com.example.notestakingapp.database.TempDatabaseHelper;
import com.example.notestakingapp.firebase.FirebaseHandler;
import static com.example.notestakingapp.database.NoteTakingDatabaseHelper.DB_NAME;

import com.example.notestakingapp.R.id;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notestakingapp.adapter.ViewPagerAdapter;
import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.database.NoteTakingDatabaseHelper;
import com.example.notestakingapp.ui.BottomDialog;
import com.example.notestakingapp.ui.DrawingView;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private ImageView imageViewAdd;
    private BottomNavigationView bottomNavigationView;
    private EditText inputSearch;

    private ViewPagerAdapter mViewPagerAdapter;
    private BottomNavigationView mBottomNavigationView;
    private ViewPager2 mViewPager2;
    int currentPage;

    private SQLiteDatabase db;

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
                Toast.makeText(MainActivity.this, "Item = " + id.home_main, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(MainActivity.this, "clicked, going to todo page", Toast.LENGTH_SHORT).show();
    }

    private void routeToNoteEdit() {
        Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
        startActivity(intent);
    }

    private void initUi() {
        imageViewAdd = findViewById(R.id.imageAddNoteMain);
        mViewPager2 = findViewById(R.id.view_pager2);
        mBottomNavigationView = findViewById(R.id.layout_nav);
        inputSearch = findViewById(R.id.inputSearch);
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

}