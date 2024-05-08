package com.example.notestakingapp.database;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.notestakingapp.database.NoteComponent.Note;
import com.example.notestakingapp.database.NoteComponent.ToDo;

import java.util.ArrayList;

public class DatabaseHandler {
    public static final int TAG_ID_DEFAULT = 1;
    public static final String TAG_NAME_DEFAULT = "Default";

    public static final String  COLUMN_NOTE_TITLE = "NOTE_TITLE";

    public static final String COLUMN_NOTE_ID = "NOTE_ID";
    public static final String COLUMN_NOTE_CREATEAT = "CREATE_AT";
    public static final String COLUMN_NOTE_COLOR = "COLOR";


    //TEXT SEGMENT
    public static final String COLUMN_TEXT_ID = "TEXT_ID";
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

    //NOTE_TAG
    public static final String COLUMN_NOTA_NOTE_ID = "NOTE_ID";
    public static final String COLUMN_NOTA_TAG_ID = "TAG_ID";

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


    //insert : trả lại khóa chính của hàng được insert
    public long insertNote(Context context,@Nullable String title,@NonNull String createAt,@Nullable String color,@Nullable Integer tagId) {

        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_NOTE_TITLE, title);
        ct.put(COLUMN_NOTE_CREATEAT, createAt);
        ct.put(COLUMN_NOTE_COLOR, color);
        if (tagId != null) ct.put(COLUMN_TAG_ID, tagId);

        return db.insert(NOTE_TABLE, null, ct);
    }


    public long insertTextSegment(Context context, int noteId, String text) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_NOTE_ID, noteId);
        ct.put(COLUMN_TEXT, text);

        return db.insert(TEXTSEGMENT_TABLE, null, ct);
    }


    public long insertImage(Context context, int noteId,byte[] imageData) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_NOTE_ID, noteId);
        ct.put(COLUMN_IMAGE_DATA, imageData);

        return db.insert(IMAGE_TABLE, null, ct);
    }


    public long insertAudio(Context context, int noteId,byte[] audioData) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_NOTE_ID, noteId);
        ct.put(COLUMN_AUDIO_DATA, audioData);

        return db.insert(AUDIO_TABLE, null, ct);
    }


    public long insertTag(Context context, String tagName) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_TAG_NAME, tagName.trim());

        return db.insert(TAG_TABLE, null, ct);
    }


    //delete : các hàm trả về số row bị ảnh hưởng bởi lệnh xóa
    public int deleteNote(Context context, int noteId) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        return db.delete(NOTE_TABLE, COLUMN_NOTE_ID + " = ?", new String[]{Integer.toString(noteId)});
    }


    public int deleteTextSegment(Context context, int textId) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        return db.delete(TEXTSEGMENT_TABLE, COLUMN_TEXT_ID + " = ?", new String[]{Integer.toString(textId)});
    }


    public int deleteImage(Context context, int imageId) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        return db.delete(IMAGE_TABLE, COLUMN_IMAGE_ID + " = ?", new String[]{Integer.toString(imageId)});
    }


    public int deleteAudio(Context context, int audioId) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        return db.delete(AUDIO_TABLE, COLUMN_AUDIO_ID + " = ?", new String[]{Integer.toString(audioId)});
    }


//    public int deleteTag(Context context,int noteId) {
//        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
//        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();
//
//        ContentValues ct = new ContentValues();
//
//        ct.put(COLUMN_TAG_ID , (String) null);
//
//        return  db.update(NOTE_TABLE, ct, )
//    }//dua tag ve default



    //update
    public int updateNote(Context context, int noteId,@Nullable String title,@Nullable String color,@Nullable Integer tagId) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_NOTE_TITLE, title);
        ct.put(COLUMN_NOTE_COLOR, color);
        if (tagId != null) ct.put(COLUMN_TAG_ID, tagId);

        return db.update(NOTE_TABLE, ct, COLUMN_NOTE_ID+" = ?", new String[]{Integer.toString(noteId)});
    }


    public int updateTextSegment(Context context,int textId, String text){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_TEXT, text);

        return db.update(TEXTSEGMENT_TABLE, ct, COLUMN_TEXT_ID + " = ?", new String[]{Integer.toString(textId)});
    }


    public int updateImage(Context context,int imageId, byte[] imageData) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_IMAGE_ID, imageData);

        return db.update(IMAGE_TABLE, ct, COLUMN_IMAGE_ID +" = ?", new String[]{Integer.toString(imageId)});
    }


    public int updateAudio(Context context,int audioId, byte[] audioData) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_AUDIO_DATA , audioData);

        return db.update(AUDIO_TABLE , ct, COLUMN_AUDIO_ID  +" = ?", new String[]{Integer.toString(audioId)});
    }


    //query
    public Note getNoteById(Context context, int noteId) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        String query = "SELECT * FROM "+ NOTE_TABLE+" WHERE " + COLUMN_NOTE_ID +" = ?";

        Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(noteId)});

        if (cursor.moveToFirst()){
            return new Note(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getInt(4));
        }
        else return null;
    }


    public ArrayList<Note> getNoteByTag(Context context, String tagName) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        String query = "SELECT * FROM "+ NOTE_TABLE+" WHERE " + COLUMN_TAG_NAME  +" = ?";

        Cursor cursor = db.rawQuery(query, new String[]{tagName});

        if (cursor.moveToFirst()){
            ArrayList<Note> listNote = new ArrayList<Note>();
            do{
                listNote.add(new Note(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4) ) );
            } while (cursor.moveToNext());
            return listNote;
        }
        else return null;
    }

    public ArrayList<Note> searchNote(Context context, String searchText){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        String query = "SELECT * FROM "+ NOTE_TABLE +" WHERE " + COLUMN_TODO_CONTENT + " LIKE ? ";

        Cursor cursor = db.rawQuery(query, new String[]{"'%" + searchText + "%'"});

        if (cursor.moveToFirst()){
            ArrayList<Note> listNote = new ArrayList<Note>();
            do{
                listNote.add(new Note(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4) ) );
            } while (cursor.moveToNext());
            return listNote;
        }
        else return null;
    }

    public ArrayList<Note> getAllNote(Context context) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        String query = "SELECT * FROM "+ NOTE_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            ArrayList<Note> listNote = new ArrayList<Note>();
            do{
                listNote.add(new Note(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4) ) );
            } while (cursor.moveToNext());
            return listNote;
        }
        else return null;
    }


    public ArrayList<Note> getNoteByCreateAt(Context context, String order) {    //truyền giá trị biến order là  "desc" hoặc "asc"
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        String query = "SELECT * FROM "+ NOTE_TABLE +" ORDER BY "+COLUMN_NOTE_CREATEAT + " "+order;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            ArrayList<Note> listNote = new ArrayList<Note>();
            do{
                listNote.add(new Note(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4) ) );
            } while (cursor.moveToNext());
            return listNote;
        }
        else return null;
    }

    public void getImageById() {}

    //todo: defaultTag="All"
    //to_do
    //todo: defaultTime="none"
    public long insertTodo(Context context, int todoId, @Nullable String content,@NonNull String createAt,@Nullable String duration ) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_TODO_ID , todoId);
        ct.put(COLUMN_TODO_CONTENT, content);
        ct.put(COLUMN_TODO_CREATEAT , createAt);
        ct.put(COLUMN_TODO_DURATION, duration);

        return db.insert(TODO_TABLE, null, ct);
    }

    public int updateTodo(Context context, int todoId,@Nullable String content,@NonNull String createAt,@Nullable String duration) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_TODO_ID , todoId);
        ct.put(COLUMN_TODO_CONTENT, content);
        ct.put(COLUMN_TODO_CREATEAT , createAt);
        ct.put(COLUMN_TODO_DURATION, duration);

        return  db.update(TODO_TABLE, ct, COLUMN_TODO_ID +" = ?", new String[]{Integer.toString(todoId)});
    }



    public int deleteTodo(Context context, int todoId) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        return db.delete(TODO_TABLE, COLUMN_TODO_ID + " = ? ", new String[]{Integer.toString(todoId)});
    }


    public ArrayList<ToDo> getAllToDo(Context context){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        String query = "SELECT * FROM " + TODO_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            ArrayList<ToDo> listToDo = new ArrayList<ToDo>();
            do{
                listToDo.add(new ToDo(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3) ) );
            } while (cursor.moveToNext());
            return listToDo;
        }
        else return null;
    }


    public int getTagByName(Context context, String tagName){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        Cursor cursor = db.query(TAG_TABLE,new String[]{COLUMN_TAG_ID, COLUMN_TAG_NAME}, COLUMN_TAG_NAME+" = ?", new String[]{tagName.trim()},
                null, null, null);
        if (cursor.moveToFirst()){
            return cursor.getInt(0);
        }
        else return -1;
    }

}
