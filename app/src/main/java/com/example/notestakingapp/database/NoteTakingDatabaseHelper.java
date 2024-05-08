package com.example.notestakingapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class NoteTakingDatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "note.db";
    public static final int DB_VERSION = 1;

    //NOTE
    public static final String  COLUMN_NOTE_TITLE = "NOTE_TITLE";

    public static final String COLUMN_NOTE_ID = "NOTE_ID";
    public static final String COLUMN_NOTE_CREATEAT = "CREATE_AT";
    public static final String COLUMN_NOTE_COLOR = "COLOR";


    //TEXT SEGMENT
    public static final String COLUMN_TEXT_ID = "IMAGE_ID";
    public static final String COLUMN_TEXT = "TEXT";


    //IMAGE
    public static final String COLUMN_IMAGE_ID = "IMAGE_ID";
    public static final String COLUMN_IMAGE_DATA = "IMAGE_DATA";

    //AUDIO
    public static final String COLUMN_AUDIO_ID = "AUDIO_ID";
    public static final String COLUMN_AUDIO_DATA = "AUDIO_DATA";

    //TAG
    public static final String COLUMN_TAG_ID = "TAG_ID";
    public static final String COLUMN_TAG_NAME = "TAG_NAME";

    //TODO
    public static final String COLUMN_TODO_CONTENT = "TODO_CONTENT";
    public static final String COLUMN_TODO_ID = "TODO_ID";
    public static final String COLUMN_TODO_CREATEAT = "CREATE_AT";
    public static final String COLUMN_TODO_DURATION = "DURATION";

    //TABLE NAME
    public static final String TAG_TABLE = "TAG";
    public static final String NOTE_TABLE = "NOTE";
    public static final String TEXTSEGMENT_TABLE = "TEXTSEGMENT";
    public static final String IMAGE_TABLE = "IMAGE";
    public static final String AUDIO_TABLE = "AUDIO";
    public static final String TODO_TABLE= "TODO";
    public static final String  NOTE_TAG_TABLE = "NOTE_TAG";



    public NoteTakingDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void updateOrCreateDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        if (oldVersion < 1){

            //TAG
            db.execSQL(     "CREATE TABLE TAG("+
                            COLUMN_TAG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                            COLUMN_TAG_NAME +" TEXT );");

            //NOTE
            db.execSQL("CREATE TABLE NOTE ( "+
                    COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NOTE_TITLE + " TEXT," +
                    COLUMN_NOTE_CREATEAT + " TEXT," +
                    COLUMN_NOTE_COLOR + " TEXT,"+
                    COLUMN_TAG_ID + " INTEGER," +
                    "FOREIGN KEY ("+COLUMN_TAG_ID+") REFERENCES TAG("+COLUMN_TAG_ID +"));"
                    );

//            db.execSQL("CREATE TABLE ");


            //TEXTSEGMENT
            db.execSQL(     "CREATE TABLE TEXTSEGMENT ("+
                            COLUMN_TEXT_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                            COLUMN_NOTE_ID + " INTEGER,"+
                            COLUMN_TEXT+" TEXT,"+
                            "FOREIGN KEY ("+COLUMN_NOTE_ID+") REFERENCES NOTE("+COLUMN_NOTE_ID+"));"
            );


            //IMAGE
            db.execSQL(     "CREATE TABLE IMAGE("+
                            COLUMN_IMAGE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                            COLUMN_IMAGE_DATA+" BLOB,"+
                            COLUMN_NOTE_ID+" INTEGER,"+
                            "FOREIGN KEY ("+COLUMN_NOTE_ID+") REFERENCES NOTE("+COLUMN_NOTE_ID+"));"
            );


            //AUDIO
            db.execSQL(     "CREATE TABLE AUDIO("+
                            COLUMN_AUDIO_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                            COLUMN_AUDIO_DATA+" BLOB,"+
                            COLUMN_NOTE_ID +" INTEGER,"+
                            "FOREIGN KEY ("+COLUMN_NOTE_ID+") REFERENCES NOTE("+COLUMN_NOTE_ID+"));"
            );

            //TODO
            db.execSQL(     "CREATE TABLE TODO("+
                            COLUMN_TODO_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                            COLUMN_TODO_CONTENT +" TEXT,"+
                            COLUMN_TODO_CREATEAT+" TEXT," +
                            COLUMN_TODO_DURATION+" TEXT);"
            );
        }


    }
}
