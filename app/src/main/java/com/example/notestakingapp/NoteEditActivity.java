package com.example.notestakingapp;

import static com.example.notestakingapp.adapter.NoteDetailsAdapter.mStartPlaying;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import android.os.Handler;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notestakingapp.adapter.NoteDetailsAdapter;
import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.database.NoteComponent.Audio;
import com.example.notestakingapp.database.NoteComponent.Component;
import com.example.notestakingapp.database.NoteComponent.Image;
import com.example.notestakingapp.database.NoteComponent.Note;
import com.example.notestakingapp.database.NoteComponent.TextSegment;
import com.example.notestakingapp.database.NoteTakingDatabaseHelper;
import com.example.notestakingapp.shared.SharedViewModel;
import com.example.notestakingapp.ui.BottomDialog;
import com.example.notestakingapp.ui.VoiceDiaLog;
import com.example.notestakingapp.utils.AudioUtils;
import com.example.notestakingapp.utils.HideKeyBoard;
import com.example.notestakingapp.utils.ImageUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NoteEditActivity extends AppCompatActivity {
    public static final int REQ_CODE_MICRO = 99;
    private ImageView backImage, voiceImage, imageImage, scribbleImage, cameraImage, shirtImage, saveImage;
    private LinearLayout layoutBack;
    private TextView textBack;
    private RecyclerView recyclerViewDetails;
    private NoteDetailsAdapter noteDetailsAdapter;
    public SharedViewModel sharedViewModel;
    private List<Item> mItemList;
    private MediaPlayer player = null;
    private static final String IMAGE_PROP = "image";
    private static final String VOICE_PROP = "voice";

    View activityRootView;
    MediaRecorder mediaRecorder;


    private int textSegmentId = -1;
    public static int noteId = -1;
    private int voiceId = -1;
    private int imageId = -1;
    private LinearLayout toolNavigation;
    private SQLiteDatabase db;
    DatabaseHandler databaseHandler;

    //    private EditText titleEditText;
    String titleText = null;
    boolean isTheFirst = true;
    String noteColor = "#FFFFFF";
    private SharedFunc sharedFunc;


    public void setSharedFunc(SharedFunc sharedFunc) {
        this.sharedFunc = sharedFunc;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_edit);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        noteId = getIntent().getIntExtra("note_id", -1);
        Log.d("noteId...", String.valueOf(noteId));

        NoteTakingDatabaseHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(getApplicationContext());
        db = noteTakingDatabaseHelper.getReadableDatabase();
        databaseHandler = new DatabaseHandler();

        //check keyboard state changed
//        https://stackoverflow.com/questions/4745988/how-do-i-detect-if-software-keyboard-is-visible-on-android-device-or-not
        View rootView = getWindow().getDecorView().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Determine if the keyboard is visible
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getHeight();
                int keypadHeight = screenHeight - r.bottom;

                // If the keypad height is greater than a threshold, assume the keyboard is visible
                boolean isVisible = keypadHeight > screenHeight * 0.15;

                if (isVisible) {
                    // Keyboard is visible
                    toolNavigation.setVisibility(View.VISIBLE);
                } else {
                    // Keyboard is not visible
                    toolNavigation.setVisibility(View.GONE);
                }
            }
        });


        // khoi chay ui
        initUi();

        //todo: phai gan lai gia tri cho textSegmentId va noteId

        //xu li recycler view
        noteDetailsAdapter = new NoteDetailsAdapter(NoteEditActivity.this, this);
        mItemList = new ArrayList<>();
        //tao Item ui

        if (noteId == -1) {
            noteId = (int) databaseHandler.insertNote(NoteEditActivity.this, "", "#FFFFFF");
            textSegmentId = (int) databaseHandler.insertTextSegment(NoteEditActivity.this, noteId, "");
            //ui
            mItemList.add(new Item(Item.TYPE_EDIT_TEXT_TITLE, noteId));
            mItemList.add(new Item(Item.TYPE_EDIT_TEXT, textSegmentId));
        } else {
            Note note = databaseHandler.getNoteById(this, noteId);
            noteColor = note.getColor();
            ArrayList<Component> list = databaseHandler.getAllComponentByCreateAt(this, noteId, "ASC");
            mItemList.add(new Item(Item.TYPE_EDIT_TEXT_TITLE,note.getTitle(), noteId));
            mItemList.addAll(convertComponentToItem(convertComponentToProps(list)));
            Log.d("updateDuynote", String.valueOf(mItemList.get(mItemList.size()-1).getType()));
        }

        //ui

        noteDetailsAdapter.setData(mItemList);
        recyclerViewDetails.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDetails.setAdapter(noteDetailsAdapter);
        //todo: gan noteId please Duy oi, tao note moi
        //        noteId = (int) AppDatabase.getInstance(AddNoteActivity.this).noteDao().insert(new Note(""));

        noteDetailsAdapter.setNoteId(noteId);
        noteDetailsAdapter.setOnEditTextChangedListener(new NoteDetailsAdapter.OnEditTextChangedListener() {

            @Override
            public void onTextChanged(int position, String text) {
                Log.d("duyText", "textSegment  changed noteedit");
                mItemList.get(position).setText(text);
                if (isTheFirst) {
                    isTheFirst = false;
                    return;
                } else if (!isTheFirst) {
                    Item textSegment = mItemList.get(position);
                    textSegmentId = textSegment.getTextSegmentId();
                    databaseHandler.updateTextSegment(NoteEditActivity.this, textSegmentId, text);
                }

            }
        });
        noteDetailsAdapter.setAudioListener(new NoteDetailsAdapter.AudioListener() {
            @Override
            public void onPlayBtnClick(int position) {
                Item audioItem = mItemList.get(position);
                boolean fixTempTest = true;
                byte[] audioData = audioItem.getAudioData();
                Log.d("audioDuyT", "isPlaying="+sharedViewModel.isPlaying().getValue());
                if(sharedViewModel.isPlaying().getValue() && audioData!= null) {
                    startPlaying(audioData);
                    sharedViewModel.setPlaying(false);
                }
                else {
                    stopPlaying();
                }
            }

            @Override
            public void onTrashClick(int position) {
                Log.d("audioDuyT", "clicked");
                databaseHandler.deleteAudio(NoteEditActivity.this, mItemList.get(position).getVoiceId());
                mItemList.remove(position);
                noteDetailsAdapter.notifyItemRemoved(position);
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

        saveImage.setOnClickListener(v -> {
            if (!deleteNoteIsEmpty(noteId)) {
                titleText = NoteDetailsAdapter.title;

                databaseHandler.updateNote(NoteEditActivity.this, noteId, titleText, noteColor);
            }
            finish();
        });
        cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpenCamera();
            }
        });

        shirtImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showToolDialog();
                HideKeyBoard.hideKeyboard(NoteEditActivity.this);

                BottomDialog.showToolDialog(NoteEditActivity.this);
            }
        });

        imageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOpenGallery();
            }
        });
        voiceImage.setOnClickListener(new View.OnClickListener() {

            //            https://www.youtube.com/watch?v=3ffs2VbJ9JY
//            https://github.com/MoamenHassaballah/PlayPauseAnimationTutorial
            @Override
            public void onClick(View v) {
                if (!getMicroPermission()) {
                    //hide keyboard
                    HideKeyBoard.hideKeyboard(NoteEditActivity.this);
//                    btnRecordPressed();
                    VoiceDiaLog.showVoiceDialog(NoteEditActivity.this, noteId, sharedViewModel);

                } else {
                    getMicroPermission();
                }
            }
        });

        sharedViewModel.getNoteEditChangeInsertAudio().observe(this, aBoolean -> {
            //ui
            int audioId = sharedViewModel.getAudioId();
            mItemList.add(new Item(Item.TYPE_VOICE_VIEW, databaseHandler.getAudioByAudioId(NoteEditActivity.this, audioId).getAudioData(), audioId ));
            //todo: db --OK su dung Item temp
            noteDetailsAdapter.notifyItemInserted(mItemList.size()-1);
        });

        //xu li su kien khi back quay ve activity truoc cua dien thoai
        getOnBackPressedDispatcher().addCallback(NoteEditActivity.this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!deleteNoteIsEmpty(noteId)) {
                    titleText = NoteDetailsAdapter.title;

                    databaseHandler.updateNote(NoteEditActivity.this, noteId, titleText, noteColor);
                }
                finish();
            }
        });
    }

//    https://developer.android.com/media/platform/mediarecorder#java
    private void startPlaying(byte[] audioData) {
        player = new MediaPlayer();
        try {
            Log.d("audioFix", String.valueOf(audioData.length));
            player.setDataSource(AudioUtils.byteToFile(NoteEditActivity.this, audioData).getPath());
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    sharedViewModel.setPlaying(true);
                }
            });
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e("loi o noteedit player", "prepare() failed");
        }
    }

    private void stopPlaying() {
        sharedViewModel.setPlaying(true);
        if(player == null) return;
        player.release();
        player = null;
    }

    private void btnRecordPressed() {
        mediaRecorder = new MediaRecorder();

    }


    private void initUi() {
        saveImage =findViewById(R.id.image_save);
        shirtImage = findViewById(R.id.image_shirt);
        layoutBack = findViewById(R.id.layout_back);
        backImage = findViewById(R.id.image_back);
        textBack = findViewById(R.id.text_back);
        voiceImage = findViewById(R.id.image_voice);
        imageImage = findViewById(R.id.image_image);
        scribbleImage = findViewById(R.id.image_scribble);
        recyclerViewDetails = findViewById(R.id.recycler_view_details);
        toolNavigation = findViewById(R.id.tool_navigation);
        cameraImage = findViewById(R.id.image_camera);
        activityRootView = findViewById(R.id.main);
    }


    private boolean deleteNoteIsEmpty(int noteId) {
        titleText = NoteDetailsAdapter.title;
        if(mItemList.size()>2) {
            Log.d("update!!", String.valueOf(mItemList.size()));
            return false;
        }
        if(!mItemList.get(0).getText().isEmpty() ) {
            Log.d("update!!", String.valueOf(mItemList.get(0).getText()));
            return false;
        }
        if (!mItemList.get(1).getText().isEmpty()) {
            Log.d("update!!", String.valueOf(mItemList.get(1).getText().isEmpty() ));
            return false;
        }
        if(!titleText.isEmpty()) {
            return false;
        }
        if (mItemList.size() == 2 && mItemList.get(0).getText().isEmpty() && mItemList.get(1).getText().isEmpty() && titleText.isEmpty()) {
            //todo: xoa note neu note do la empty cho nay xu li hoi ngu
            databaseHandler.deleteNote(this, noteId);
            Log.d("update!!", String.valueOf(mItemList.get(1).getText()));
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);

                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    //todo: luu anh vao db nho them try catch
                    try {
                        imageId = (int) databaseHandler.insertImage(NoteEditActivity.this, noteId, ImageUtils.uriToBytes(selectedImageUri, NoteEditActivity.this));
                        Toast.makeText(this, "Image add into db success" + imageId, Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
//                        Toast.makeText(this, "Image add into db error", Toast.LENGTH_SHORT).show();
                    }


                    //hien thi hinh anh ra noteEdit
                    mItemList.add(new Item(Item.TYPE_IMAGE_VIEW, selectedImageUri, imageId, IMAGE_PROP));
                    //xu li inputtext is empty se bi xoa di neu anh dc them vao
                    int size = mItemList.size();
                    if (mItemList.get(size - 2).getType() == Item.TYPE_EDIT_TEXT && mItemList.get(size - 2).getText().isEmpty()) {
                        //xoa trong db
                        databaseHandler.deleteTextSegment(NoteEditActivity.this, textSegmentId);
                        //xoa o giao dien
                        mItemList.remove(size - 2);
                        Toast.makeText(NoteEditActivity.this, "Remove editText sucess!", Toast.LENGTH_SHORT).show();
                    }
                    //todo: cap nhat lai textSegmentId va them textsegment vao db
                    textSegmentId = (int) databaseHandler.insertTextSegment(NoteEditActivity.this, noteId, "");
                    Log.d("duytest", "tao textSegment id = " + textSegmentId + " noteId = " + noteId);

                    //textSegmentId = appdb.insert();
                    //todo: ... OK
                    EditText titleEditText = findViewById(R.id.edit_text_title);
                    mItemList.get(0).setText(titleEditText.getText().toString());
                    mItemList.add(new Item(Item.TYPE_EDIT_TEXT, textSegmentId));
                    noteDetailsAdapter.setData(mItemList);
                } catch (Exception exception) {
                    Toast.makeText(this, "selected image error!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void onOpenCamera() {
        ImagePicker.with(this).cameraOnly().crop().compress(1024).maxResultSize(1080, 1080)//User can only capture image using Camera
                .start();
    }

    private void onOpenGallery() {
        ImagePicker.with(this).galleryOnly().crop().compress(1024).maxResultSize(1080, 1080).start();
    }

    private boolean getMicroPermission() {
        if (ContextCompat.checkSelfPermission(NoteEditActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQ_CODE_MICRO);
            return true;
        } else {
            return false;
        }
    }

    private boolean isMicroPresent() {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        titleText = NoteDetailsAdapter.title;
        if(player!=null) {
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    public String setColorBackgroundNoteEdit(String color) {
        noteColor = color;
        RelativeLayout relativeLayout = findViewById(R.id.main);
        if (color == "#FFFFFF") {
        } else {
            Log.d("color2222", color);
            //todo: add color vao db
            titleText = NoteDetailsAdapter.title;
            databaseHandler.updateNote(NoteEditActivity.this, noteId, titleText, color);
            GradientDrawable gradientDrawable = new GradientDrawable();
            Log.d("duyColor", String.valueOf(relativeLayout));
            gradientDrawable.setColor(Color.parseColor(color));
//        gradientDrawable.set
            relativeLayout.setBackground(gradientDrawable);
        }
        return noteColor;
    }

    public List<Object> convertComponentToProps(ArrayList<Component> input) {
        List<Object> output = new ArrayList<>();
        for (Component i : input) {
            switch (i.getType()) {
                case Item.TYPE_EDIT_TEXT:
                    output.add(databaseHandler.getTextSegment(this, i));
                    break;
                case Item.TYPE_IMAGE_VIEW:
                    output.add(databaseHandler.getImage(this, i));
                    break;
                case Item.TYPE_VOICE_VIEW:
                    output.add(databaseHandler.getAudio(this, i));
                    break;
            }
        }
        return output;
    }

    public List<Item> convertComponentToItem(List<Object> input) {
        List<Item> output = new ArrayList<>();
        for (Object i : input) {
            Class<?> aClass = i.getClass();
            if (aClass.equals(TextSegment.class)) {
                output.add(new Item(Item.TYPE_EDIT_TEXT, ((TextSegment) i).getText(), ((TextSegment) i).getTextId()));
            } else if (aClass.equals(Image.class)) {
                output.add(new Item(Item.TYPE_IMAGE_VIEW, "", ImageUtils.byteToBitmap(((Image) i).getImageData()), ((Image) i).getImageId(), IMAGE_PROP));
            } else if (aClass.equals(Audio.class)) {//todo: chưa check lai --check OK
                output.add(new Item(Item.TYPE_VOICE_VIEW, ((Audio) i).getAudioData(), ((Audio) i).getAudioId()));
            }
        }

//        for(Item i: output) {
//            Log.d("updateDuy++", "type="+i.getType());
//        }
        return output;
    }
    interface SharedFunc {
        void deleteNote();
    }
}