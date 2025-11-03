package com.example.notestakingapp.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notestakingapp.shared.Item;
import com.example.notestakingapp.R;
import com.example.notestakingapp.adapter.NoteDetailsAdapter;
import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.database.NoteComponent.Audio;
import com.example.notestakingapp.database.NoteComponent.Component;
import com.example.notestakingapp.database.NoteComponent.Image;
import com.example.notestakingapp.database.NoteComponent.Note;
import com.example.notestakingapp.database.NoteComponent.TextSegment;
import com.example.notestakingapp.database.NoteTakingDatabaseHelper;
import com.example.notestakingapp.shared.SharedViewModel;
import com.example.notestakingapp.utils.AudioUtils;
import com.example.notestakingapp.utils.HideKeyBoard;
import com.example.notestakingapp.utils.ImageUtils;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class NoteEditActivity extends AppCompatActivity {
    public static final int REQ_CODE_MICRO = 99;
    private ImageView backImage, voiceImage, imageImage, scribbleImage, cameraImage, shirtImage, saveImage, addItemImage;
    private TextView backButton;
    private TextView textBack;
    private RecyclerView recyclerViewDetails;
    private NoteDetailsAdapter noteDetailsAdapter;
    public static SharedViewModel sharedViewModel;
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
    String noteColor = String.valueOf(R.color.colorNoteDefault);
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

        //check keyboard state changed --> đã loại bỏ layout add và đã thay thế
//        https://stackoverflow.com/questions/4745988/how-do-i-detect-if-software-keyboard-is-visible-on-android-device-or-not
//        View rootView = getWindow().getDecorView().getRootView();
//        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                // Determine if the keyboard is visible
//                Rect r = new Rect();
//                rootView.getWindowVisibleDisplayFrame(r);
//                int screenHeight = rootView.getHeight();
//                int keypadHeight = screenHeight - r.bottom;
//
//                // If the keypad height is greater than a threshold, assume the keyboard is visible
//                boolean isVisible = keypadHeight > screenHeight * 0.15;
//
//                if (isVisible) {
//                    // Keyboard is visible
//                    toolNavigation.setVisibility(View.VISIBLE);
//                } else {
//                    // Keyboard is not visible
//                    toolNavigation.setVisibility(View.GONE);
//                }
//            }
//        });

        // khoi chay ui
        initUi();

        //todo: phai gan lai gia tri cho textSegmentId va noteId

        //xu li recycler view
        noteDetailsAdapter = new NoteDetailsAdapter(NoteEditActivity.this, this);
        mItemList = new ArrayList<>();
        //tao Item ui

        if (noteId == -1) {
            noteId = (int) databaseHandler.insertNote(NoteEditActivity.this, "", noteColor);
            NotesFragment.sharedViewModel.notifyDataChanged();
            textSegmentId = (int) databaseHandler.insertTextSegment(NoteEditActivity.this, noteId, "");
            //ui
            mItemList.add(new Item(Item.TYPE_EDIT_TEXT_TITLE, noteId));
            mItemList.add(new Item(Item.TYPE_EDIT_TEXT, textSegmentId));
        } else {
            Note note = databaseHandler.getNoteById(this, noteId);
            noteColor = note.getColor();
            RelativeLayout relativeLayout = findViewById(R.id.main);
            GradientDrawable gradientDrawable = new GradientDrawable();
            Log.d("duyColor", String.valueOf(relativeLayout));
            int colorInt = ContextCompat.getColor(this, Integer.parseInt(note.getColor())); // Lấy mã màu từ resources
            String temp = String.format("#%06X", (0xFFFFFF & colorInt));
            gradientDrawable.setColor(Color.parseColor(temp));
            Log.d("duyColor111", String.valueOf(relativeLayout));
            relativeLayout.setBackground(gradientDrawable);
            ArrayList<Component> list = databaseHandler.getAllComponentByCreateAt(this, noteId, "ASC");
            mItemList.add(new Item(Item.TYPE_EDIT_TEXT_TITLE, note.getTitle(), noteId));
            mItemList.addAll(convertComponentToItem(convertComponentToProps(list)));
        }

        //ui

        noteDetailsAdapter.setData(mItemList);
        recyclerViewDetails.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDetails.setAdapter(noteDetailsAdapter);
        //todo: gan noteId please Duy oi, tao note moi
        //        noteId = (int) AppDatabase.getInstance(AddNoteActivity.this).noteDao().insert(new Note(""));

        noteDetailsAdapter.setNoteId(noteId);
        //image handle
        noteDetailsAdapter.setImageListener(new NoteDetailsAdapter.ImageListener() {
            @Override
            public void onImageClick(int position) {
                Item image = mItemList.get(position);
                int _imageID = image.getImageId();
                Log.d("imageID", "id="+_imageID);
                routetoViewImageFull(_imageID);
            }

            @Override
            public void onTrashClick(int position) {
                if (mItemList.size() > 2) {
                    Item image = mItemList.get(position);
                    int imageId = image.getImageId();
                    mItemList.remove(position);
                    noteDetailsAdapter.notifyItemRemoved(position);
                    databaseHandler.deleteImage(NoteEditActivity.this, imageId);
                } else {
                    Toast.makeText(NoteEditActivity.this, "Can not delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //text handle
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

            @Override
            public void onTrashClick(int position) {
                //ui
                if (mItemList.size() > 2) {
                    Item textSegment = mItemList.remove(position);
                    textSegmentId = textSegment.getTextSegmentId();
                    databaseHandler.deleteTextSegment(NoteEditActivity.this, textSegmentId);
                    noteDetailsAdapter.notifyItemRemoved(position);
                } else {
                    Toast.makeText(NoteEditActivity.this, "Can not delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
        noteDetailsAdapter.setAudioListener(new NoteDetailsAdapter.AudioListener() {
            @Override
            public void onPlayBtnClick(int position) {
                Item audioItem = mItemList.get(position);
                boolean fixTempTest = true;
                byte[] audioData = audioItem.getAudioData();
                Log.d("audioDuyT", "isPlaying=" + sharedViewModel.isPlaying().getValue());
                if (sharedViewModel.isPlaying().getValue() && audioData != null) {
                    startPlaying(audioData);
                    sharedViewModel.setPlaying(false);
                } else {
                    stopPlaying();
                }
            }

            @Override
            public void onTrashClick(int position) {
                if (mItemList.size() > 2) {
                    databaseHandler.deleteAudio(NoteEditActivity.this, mItemList.get(position).getVoiceId());
                    mItemList.remove(position);
                    noteDetailsAdapter.notifyItemRemoved(position);
                } else {
                    Toast.makeText(NoteEditActivity.this, "Can not delete", Toast.LENGTH_SHORT).show();
                }
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
        backButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        saveImage.setOnClickListener(v -> {
            if (!deleteNoteIsEmpty(noteId)) {
                titleText = NoteDetailsAdapter.title;

                databaseHandler.updateNote(NoteEditActivity.this, noteId, titleText, noteColor);
            }
            Log.d("testDUY", String.valueOf(sharedViewModel.getNoteEditChangeInsertDraw().getValue()));
            finish();
        });
//        cameraImage.setOnClickListener(v -> onOpenCamera());

        //addItemImage

//

//
//        imageImage.setOnClickListener(v -> onOpenGallery());
        //            https://www.youtube.com/watch?v=3ffs2VbJ9JY
//            https://github.com/MoamenHassaballah/PlayPauseAnimationTutorial
//        voiceImage.setOnClickListener(v -> {
//            if (!getMicroPermission()) {
//                //hide keyboard
//                HideKeyBoard.hideKeyboard(NoteEditActivity.this);
////                    btnRecordPressed();
//                VoiceDialog.showVoiceDialog(NoteEditActivity.this, noteId, sharedViewModel);
//
//            } else {
//                getMicroPermission();
//            }
//        });
        shirtImage.setOnClickListener(v -> {
            HideKeyBoard.hideKeyboard(NoteEditActivity.this);

            BottomDialog.showToolDialog(NoteEditActivity.this, noteId);
        });
        addItemImage.setOnClickListener(v -> {
            showPopupMenu(v);
        });
        sharedViewModel.getNoteEditChangeInsertAudio().observe(this, aBoolean -> {
            //ui
            int audioId = sharedViewModel.getAudioId();
            mItemList.add(new Item(Item.TYPE_VOICE_VIEW, databaseHandler.getAudioByAudioId(NoteEditActivity.this, audioId).getAudioData(), audioId));
            //todo: db --OK su dung Item temp
            noteDetailsAdapter.notifyItemInserted(mItemList.size() - 1);
        });

        sharedViewModel.getTest().observe(this, aBoolean -> {
            //ui
            mItemList.add(new Item(Item.TYPE_IMAGE_VIEW, "", ImageUtils.byteToBitmap(sharedViewModel.getImageData()), sharedViewModel.getImageId(), IMAGE_PROP));
            noteDetailsAdapter.notifyItemInserted(mItemList.size()-1);
        });

        sharedViewModel.getNoteEditChangeInsertDraw().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

            }
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

    private void routetoViewImageFull(int imageId) {
        Intent intent = new Intent(NoteEditActivity.this, ViewImageActivity.class);
        intent.putExtra("image_id", imageId);
        startActivity(intent);
    }

    private void onOpenDraw() {
        Intent intent = new Intent(NoteEditActivity.this, DrawingActivity.class);
        intent.putExtra("note_id", noteId);
        startActivity(intent);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu_add_item, popupMenu.getMenu());

        //https://stackoverflow.com/questions/20836385/popup-menu-with-icon-on-android
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        popupMenu.setOnMenuItemClickListener(item -> onPopupItemClick(item));
        popupMenu.show();
    }

    //popup item click
    private boolean onPopupItemClick(MenuItem item) {
        int name = item.getItemId();
        if (name == R.id.add_text_popup) {
            onAddText();
            Toast.makeText(this, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        } else if (name == R.id.add_image_popup) {
            onOpenGallery();
            Toast.makeText(this, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        } else if (name == R.id.add_camera_popup) {
            onOpenCamera();
            Toast.makeText(this, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        } else if (name == R.id.add_draw_popup) {
            onOpenDraw();
            Toast.makeText(this, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        } else if (name == R.id.add_voice_popup) {
            opOpenVoice();
            Toast.makeText(this, "" + item.getTitle(), Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    private void opOpenVoice() {
        if (!getMicroPermission()) {
            //hide keyboard
            HideKeyBoard.hideKeyboard(NoteEditActivity.this);
//                    btnRecordPressed();
            VoiceDialog.showVoiceDialog(NoteEditActivity.this, noteId, sharedViewModel);

        } else {
            getMicroPermission();
        }
    }

    private void onAddText() {
        //db
        textSegmentId = (int) databaseHandler.insertTextSegment(NoteEditActivity.this, noteId, "");
        //ui
        mItemList.add(new Item(Item.TYPE_EDIT_TEXT, textSegmentId));
        noteDetailsAdapter.notifyItemInserted(mItemList.size() - 1);
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
        if (player == null) return;
        player.release();
        player = null;
    }

    private void btnRecordPressed() {
        mediaRecorder = new MediaRecorder();

    }


    private void initUi() {
        saveImage = findViewById(R.id.image_save);
        shirtImage = findViewById(R.id.image_shirt);
        backButton = findViewById(R.id.back_button);
        voiceImage = findViewById(R.id.image_voice);
        imageImage = findViewById(R.id.image_image);
        scribbleImage = findViewById(R.id.image_scribble);
        recyclerViewDetails = findViewById(R.id.recycler_view_details);
        cameraImage = findViewById(R.id.image_camera);
        activityRootView = findViewById(R.id.main);
        addItemImage = findViewById(R.id.image_add_item);
    }


    private boolean deleteNoteIsEmpty(int noteId) {
        titleText = NoteDetailsAdapter.title;
        if (mItemList.size() > 2) {
            Log.d("update!!", String.valueOf(mItemList.size()));
            return false;
        }
        if (!mItemList.get(0).getText().isEmpty()) {
            Log.d("update!!", String.valueOf(mItemList.get(0).getText()));
            return false;
        }
        if (!mItemList.get(1).getText().isEmpty()) {
            Log.d("update!!", String.valueOf(mItemList.get(1).getText().isEmpty()));
            return false;
        }
        if (!titleText.isEmpty()) {
            return false;
        }
        if (mItemList.size() == 2 && mItemList.get(1).getType()==Item.TYPE_EDIT_TEXT && mItemList.get(0).getText().isEmpty() && mItemList.get(1).getText().isEmpty() && titleText.isEmpty()) {
            //todo: xoa note neu note do la empty cho nay xu li hoi ngu
            databaseHandler.deleteNote(this, noteId);
            NotesFragment.sharedViewModel.notifyDataChanged();
            NotesFragment.sharedViewModel.setTagChanged();
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

                    } catch (Exception e) {
                        Toast.makeText(this, "Image add into db error", Toast.LENGTH_SHORT).show();
                    }
                    //hien thi hinh anh ra noteEdit
                    mItemList.add(new Item(Item.TYPE_IMAGE_VIEW, selectedImageUri, imageId, IMAGE_PROP));
                    noteDetailsAdapter.notifyItemInserted(mItemList.size() - 1);
                    //todo: ... OK
                    EditText titleEditText = findViewById(R.id.edit_text_title);
                    mItemList.get(0).setText(titleEditText.getText().toString());
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
        if (player != null) {
            player.release();
            player = null;
        }
        super.onDestroy();
    }

    public String setColorBackgroundNoteEdit(int colorId) {
        noteColor = String.valueOf(colorId);
        RelativeLayout relativeLayout = findViewById(R.id.main);
        Log.d("color2222", String.valueOf(colorId));
        int colorInt = ContextCompat.getColor(this, colorId); // Lấy mã màu từ resources
        String temp = String.format("#%06X", (0xFFFFFF & colorInt));
        if (temp == "#FFFFFF") {
        } else {
            //todo: add color vao db
            titleText = NoteDetailsAdapter.title;
            databaseHandler.updateNote(NoteEditActivity.this, noteId, titleText, noteColor);
            GradientDrawable gradientDrawable = new GradientDrawable();
            Log.d("duyColor", String.valueOf(relativeLayout));
            gradientDrawable.setColor(Color.parseColor(temp));
            Log.d("duyColor111", String.valueOf(relativeLayout));
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