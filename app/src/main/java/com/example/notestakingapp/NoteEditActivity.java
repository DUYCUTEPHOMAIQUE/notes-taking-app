package com.example.notestakingapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notestakingapp.adapter.NoteDetailsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NoteEditActivity extends AppCompatActivity {
    private ImageView backImage, voiceImage, imageImage, scribbleImage;
    private LinearLayout layoutBack;
    private TextView textBack;
    private RecyclerView recyclerViewDetails;
    private NoteDetailsAdapter noteDetailsAdapter;
    private List<Item> mItemList;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    private static final String IMAGE_PROP = "image";
    private static final String VOICE_PROP = "voice";

    private int textSegmentId = -1;
    private boolean isTheFirst = true;
    private int noteId = -1;
    private  int voiceId = -1;
    private int imageId = -1;
    private LinearLayout toolNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUi();

        //todo: phai gan lai gia tri cho textSegmentId va noteId

        //xu li recycler view
        noteDetailsAdapter = new NoteDetailsAdapter();
        mItemList = new ArrayList<>();
        //tao Item ui
        mItemList.add(new Item(Item.TYPE_EDIT_TEXT, textSegmentId));
        noteDetailsAdapter.setData(mItemList);
        recyclerViewDetails.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDetails.setAdapter(noteDetailsAdapter);

        //todo: gan noteId please Duy oi, tao note moi
        //        noteId = (int) AppDatabase.getInstance(AddNoteActivity.this).noteDao().insert(new Note(""));

        noteDetailsAdapter.setNoteId(noteId);
        noteDetailsAdapter.setOnEditTextChangedListener(new NoteDetailsAdapter.OnEditTextChangedListener() {
            @Override
            public void onTextChanged(int position, String text) {
                mItemList.get(position).setText(text);
                if (isTheFirst) {
                  //todo: insert 1 text segment vao db theo noteId pls
                    // idText = (int) AppDatabase.getInstance(AddNoteActivity.this).textSegmentDao().insert(new TextSegment(noteId, mlist.get(position).getText().toString().trim()));
                    isTheFirst = false;
                }

                //todo: Update textSegment trong db
                //AppDatabase.getInstance(AddNoteActivity.this).textSegmentDao().updateTextSegment(idText, text);
            }
        });

        //su kien scroll de an tool
        recyclerViewDetails.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        //back button
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_animation);
//                imageViewAdd.startAnimation(animation);

                Animation animation = AnimationUtils.loadAnimation(NoteEditActivity.this, R.anim.fade_out);
                backImage.startAnimation(animation);
                textBack.setAnimation(animation);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        imageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(NoteEditActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION);
                }
                else {
                    selectImage();
                }
            }
        });
    }

    private void initUi() {

        layoutBack = findViewById(R.id.layout_back);
        backImage = findViewById(R.id.image_back);
        textBack = findViewById(R.id.text_back);
        voiceImage = findViewById(R.id.image_voice);
        imageImage = findViewById(R.id.image_image);
        scribbleImage = findViewById(R.id.image_scribble);
        recyclerViewDetails =findViewById(R.id.recycler_view_details);
        toolNavigation =findViewById(R.id.tool_navigation);
//        imageSelected = findViewById(R.id.image_selected);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println(grantResults[1]);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void deleteNoteIsEmpty() {
        if (mItemList.size()==1 && mItemList.get(0).getText().isEmpty()) {
            //todo: xoa note neu note do la empty cho nay xu li hoi ngu
//            AppDatabase.getInstance(AddNoteActivity.this).noteDao().deleteNoteById(noteId);
//            System.out.println("deleted note!!!!!!!!!!!!!!!!!!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode ==RESULT_OK) {
            if(data != null) {
                Uri selectedImageUri = data.getData();
                if(selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        //todo: luu anh vao db nho them try catch
                        //ex: AppDatabase.getInstance(AddNoteActivity.this).imageDao().insert(new Image(noteId, ImageUtils.uriToBytes(imageUri, AddNoteActivity.this)));
//                        imageSelected.setImageBitmap(bitmap);
//                        imageSelected.setVisibility(View.VISIBLE);

                        //hien thi hinh anh ra noteEdit
                        mItemList.add(new Item(Item.TYPE_IMAGE_VIEW, selectedImageUri, imageId, IMAGE_PROP));
                        //xu li inputtext is empty se bi xoa di neu anh dc them vao
                        int size = mItemList.size();
                        if(mItemList.get(size-2).getType() == Item.TYPE_EDIT_TEXT && mItemList.get(size-2).getText().isEmpty()){
                            mItemList.remove(size-2);
                            Toast.makeText(NoteEditActivity.this, "Remove editText sucess!", Toast.LENGTH_SHORT).show();
                        }
                        noteDetailsAdapter.setData(mItemList);
                        //todo: cap nhat lai textSegmentId va them textsegment vao db
                        //textSegmentId = appdb.insert();
                        mItemList.add(new Item(Item.TYPE_EDIT_TEXT, textSegmentId));
                    }catch (Exception exception) {
                        Toast.makeText(this, "selected image error!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}