package com.example.notestakingapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class NoteTakingDatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "note.db";
    public static final int DB_VERSION = 5;

    //NOTE
    public static final String COLUMN_NOTE_TITLE = "NOTE_TITLE";
    public static final String COLUMN_NOTE_ID = "NOTE_ID";
    public static final String COLUMN_NOTE_CREATEAT = "CREATE_AT";
    public static final String COLUMN_NOTE_COLOR = "COLOR";
	//user_id == 0 means local user
	public static final String COLUMN_USER_ID = "USER_ID";


    //TEXT SEGMENT
    public static final String COLUMN_TEXT_ID = "TEXT_ID";
    public static final String COLUMN_TEXT = "TEXT";
    public static final String COLUMN_TEXT_CREATEAT = "CREATE_AT";


    //IMAGE
    public static final String COLUMN_IMAGE_ID = "IMAGE_ID";
    public static final String COLUMN_IMAGE_DATA = "IMAGE_DATA";
    public static final String COLUMN_IMAGE_CREATEAT = "CREATE_AT";

    //AUDIO
    public static final String COLUMN_AUDIO_ID = "AUDIO_ID";
    public static final String COLUMN_AUDIO_DATA = "AUDIO_DATA";
    public static final String COLUMN_AUDIO_CREATEAT = "CREATE_AT";

    //TAG
    public static final String COLUMN_TAG_ID = "TAG_ID";
    public static final String COLUMN_TAG_NAME = "TAG_NAME";

    //TO-DO
    public static final String COLUMN_TODO_CONTENT = "TODO_CONTENT";
    public static final String COLUMN_TODO_ID = "TODO_ID";
    public static final String COLUMN_TODO_CREATEAT = "CREATE_AT";
    public static final String COLUMN_TODO_DURATION = "DURATION";
    public static final String COLUMN_TODO_COMPLETE = "COMPLETE";


    //COMPONENT
    public static final String COLUMN_COMPONENT_ID = "COMPONENT_ID";
    public static final String COLUMN_COMPONENT_TYPE = "TYPE";
    public static final String COLUMN_COMPONENT_CREATEAT = "CREATE_AT";

    //TABLE NAME
    public static final String TAG_TABLE = "TAG";
    public static final String NOTE_TABLE = "NOTE";
    public static final String TEXTSEGMENT_TABLE = "TEXTSEGMENT";
    public static final String IMAGE_TABLE = "IMAGE";
    public static final String AUDIO_TABLE = "AUDIO";
    public static final String TODO_TABLE = "TODO";
    public static final String NOTE_TAG_TABLE = "NOTE_TAG";
    public static final String COMPONENT = "COMPONENT";


    public NoteTakingDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateOrCreateDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateOrCreateDatabase(db, oldVersion, DB_VERSION);
    }

    public static void updateOrCreateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {

            //TAG
            db.execSQL("CREATE TABLE TAG(" +
                    COLUMN_TAG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TAG_NAME + " TEXT );");

            //NOTE
            db.execSQL("CREATE TABLE NOTE ( " +
                    COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NOTE_TITLE + " TEXT," +
                    COLUMN_NOTE_CREATEAT + " INTEGER," +
                    COLUMN_NOTE_COLOR + " TEXT);"
            );

//            db.execSQL("CREATE TABLE NOTE_TAG ( " +
//                    COLUMN_NOTE_ID + " INTEGER" +
//
//
//                    );
            //TEXTSEGMENT
            db.execSQL("CREATE TABLE TEXTSEGMENT (" +
                    COLUMN_TEXT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NOTE_ID + " INTEGER," +
                    COLUMN_TEXT + " TEXT," +
                    "FOREIGN KEY (" + COLUMN_NOTE_ID + ") REFERENCES NOTE(" + COLUMN_NOTE_ID + "));"
            );


            //IMAGE
            db.execSQL("CREATE TABLE IMAGE(" +
                    COLUMN_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_IMAGE_DATA + " BLOB," +
                    COLUMN_NOTE_ID + " INTEGER," +
                    "FOREIGN KEY (" + COLUMN_NOTE_ID + ") REFERENCES NOTE(" + COLUMN_NOTE_ID + "));"
            );


            //AUDIO
            db.execSQL("CREATE TABLE AUDIO(" +
                    COLUMN_AUDIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_AUDIO_DATA + " BLOB," +
                    COLUMN_NOTE_ID + " INTEGER," +
                    "FOREIGN KEY (" + COLUMN_NOTE_ID + ") REFERENCES NOTE(" + COLUMN_NOTE_ID + "));"
            );

            //TODO
            db.execSQL("CREATE TABLE TODO(" +
                    COLUMN_TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TODO_CONTENT + " TEXT," +
                    COLUMN_TODO_CREATEAT + " INTEGER," +
                    COLUMN_TODO_DURATION + " INTEGER);"
            );
        }

        if (oldVersion < 2) {
            //tao bang note_tag
            db.execSQL("CREATE TABLE NOTE_TAG(" +
                    COLUMN_NOTE_ID + " INTEGER, " +
                    COLUMN_TAG_ID + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_NOTE_ID + ") REFERENCES NOTE(" + COLUMN_NOTE_ID + ")," +
                    "FOREIGN KEY (" + COLUMN_TAG_ID + ") REFERENCES TAG(" + COLUMN_TAG_ID + "));"
            );

            //tao bang component, chứa noteid, componentId( là textid, audioid, hoặc imageid), createat, type(text, audio, image)
            db.execSQL("CREATE TABLE COMPONENT(" +
                    COLUMN_NOTE_ID + " INTEGER, " +
                    COLUMN_COMPONENT_ID + " INTEGER, " +
                    COLUMN_COMPONENT_CREATEAT + " INTEGER, " +
                    COLUMN_COMPONENT_TYPE + " INTEGER, " +
                    "FOREIGN KEY (" + COLUMN_NOTE_ID + ") REFERENCES NOTE(" + COLUMN_NOTE_ID + "));"
            );

            //thêm cột createat cho bảng text
            db.execSQL("ALTER TABLE " + TEXTSEGMENT_TABLE +
                    " ADD COLUMN " + COLUMN_TEXT_CREATEAT + " INTEGER"
            );

            //thêm cột createat cho bảng image
            db.execSQL("ALTER TABLE " + IMAGE_TABLE +
                    " ADD COLUMN " + COLUMN_TEXT_CREATEAT + " INTEGER"
            );

            //thêm cột createat cho bảng audio
            db.execSQL("ALTER TABLE " + AUDIO_TABLE +
                    " ADD COLUMN " + COLUMN_TEXT_CREATEAT + " INTEGER"
            );
        }

        if (oldVersion < 3){
            db.execSQL("ALTER TABLE " + TODO_TABLE + " ADD COLUMN " + COLUMN_TODO_COMPLETE + " NUMERIC;");
        }
        if (oldVersion < 4){
            ContentValues ct = new ContentValues();

            ct.put(COLUMN_TODO_COMPLETE, 0);

            db.update(TODO_TABLE,ct, COLUMN_TODO_COMPLETE + " IS NULL", null);
        }
		if (oldVersion < 5) {
			db.execSQL("ALTER TABLE " + NOTE_TABLE +
					" ADD COLUMN " + COLUMN_USER_ID + " TEXT");
			db.execSQL("ALTER TABLE " + TODO_TABLE +
					" ADD COLUMN " + COLUMN_USER_ID + " TEXT");
		}
    }

}
