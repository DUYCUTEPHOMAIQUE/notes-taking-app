package com.example.notestakingapp.database;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.notestakingapp.Item;
import com.example.notestakingapp.database.NoteComponent.Audio;
import com.example.notestakingapp.database.NoteComponent.Component;
import com.example.notestakingapp.database.NoteComponent.Image;
import com.example.notestakingapp.database.NoteComponent.Note;
import com.example.notestakingapp.database.NoteComponent.TextSegment;
import com.example.notestakingapp.database.NoteComponent.ToDo;

import java.util.ArrayList;
import java.util.Date;

public class DatabaseHandler {
    public static final int TAG_ID_DEFAULT = 1;
    public static final String TAG_NAME_DEFAULT = "Default";

    //ToDo NOTE columns

    public static final String  COLUMN_NOTE_TITLE = "NOTE_TITLE";

    public static final String COLUMN_NOTE_ID = "NOTE_ID";
    public static final String COLUMN_NOTE_CREATEAT = "CREATE_AT";
    public static final String COLUMN_NOTE_COLOR = "COLOR";


    //ToDo TEXT SEGMENT columns
    public static final String COLUMN_TEXT_ID = "TEXT_ID";
    public static final String COLUMN_TEXT = "TEXT";
    public static final String COLUMN_TEXT_CREATEAT = "CREATE_AT";


    //ToDo IMAGE columns
    public static final String COLUMN_IMAGE_ID = "IMAGE_ID";
    public static final String COLUMN_IMAGE_DATA = "IMAGE_DATA";
    public static final String COLUMN_IMAGE_CREATEAT = "CREATE_AT";

    //ToDo AUDIO columns
    public static final String COLUMN_AUDIO_ID = "AUDIO_ID";
    public static final String COLUMN_AUDIO_DATA = "AUDIO_DATA";
    public static final String COLUMN_AUDIO_CREATEAT = "CREATE_AT";

    //ToDo TAG columns
    public static final String COLUMN_TAG_ID = "TAG_ID";
    public static final String COLUMN_TAG_NAME = "TAG_NAME";

    //ToDo TODO columns
    public static final String COLUMN_TODO_CONTENT = "TODO_CONTENT";
    public static final String COLUMN_TODO_ID = "TODO_ID";
    public static final String COLUMN_TODO_CREATEAT = "CREATE_AT";
    public static final String COLUMN_TODO_DURATION = "DURATION";


    //ToDo COMPONENT columns
    public static final String COLUMN_COMPONENT_ID = "COMPONENT_ID";
    public static final String COLUMN_COMPONENT_TYPE = "TYPE";
    public static final String COLUMN_COMPONENT_CREATEAT = "CREATE_AT";



    //ToDo TABLE NAME
    public static final String TAG_TABLE = "TAG";
    public static final String NOTE_TABLE = "NOTE";
    public static final String TEXTSEGMENT_TABLE = "TEXTSEGMENT";
    public static final String IMAGE_TABLE = "IMAGE";
    public static final String AUDIO_TABLE = "AUDIO";
    public static final String TODO_TABLE= "TODO";
    public static final String  NOTE_TAG_TABLE = "NOTE_TAG";
    public static final String COMPONENT_TABLE = "COMPONENT";


    //todo------------------------------------------------------ NOTE-----------------------------------
    //todo thêm note vào database (Deprecated)
    @Deprecated
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


    //todo public long insertNote(Context context,@Nullable String title,@Nullable String color)

    // thêm note vào database
    public static long insertNote(Context context,@Nullable String title,@Nullable String color) {

        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_NOTE_TITLE, title);
        ct.put(COLUMN_NOTE_CREATEAT, System.currentTimeMillis());
        ct.put(COLUMN_NOTE_COLOR, color);

        return db.insert(NOTE_TABLE, null, ct);
    }


    //todo sửa note trong database (Deprecated)
    @Deprecated
    public int updateNote(Context context, int noteId,@Nullable String title,@Nullable String color,@Nullable Integer tagId) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_NOTE_TITLE, title);
        ct.put(COLUMN_NOTE_COLOR, color);
        if (tagId != null) ct.put(COLUMN_TAG_ID, tagId);

        return db.update(NOTE_TABLE, ct, COLUMN_NOTE_ID+" = ?", new String[]{Integer.toString(noteId)});
    }


    //ToDo public int updateNote(Context context, int noteId, String title, String color)

    // Update note trong database và các

    public int updateNote(Context context, int noteId, String title, String color){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_NOTE_TITLE, title);
        ct.put(COLUMN_NOTE_COLOR, color);

        return db.update(NOTE_TABLE, ct, COLUMN_NOTE_ID+" = ?", new String[]{Integer.toString(noteId)});
    }

    //ToDo public int updateNote(Context context, int noteId, String newProp, boolean isTitle)

    // hàm update note over loading
    public int updateNote(Context context, int noteId, String newProp, boolean isTitle){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        if (isTitle){
            ct.put(COLUMN_NOTE_TITLE, newProp);
        }
        else {
            ct.put(COLUMN_NOTE_COLOR, newProp);
        }

        return db.update(NOTE_TABLE, ct, COLUMN_NOTE_ID+" = ?", new String[]{Integer.toString(noteId)});
    }

    // Xóa 1 Note. Những gì liên quan đến note này (audio, text, image) cũng bị xóa theo
    public void deleteNote(Context context, int noteId) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        //xoa du lieu trong bang text
        db.delete(TEXTSEGMENT_TABLE, COLUMN_NOTE_ID + " = ?", new String[]{Integer.toString(noteId)});

        //xoa du lieu trong bang image
        db.delete(IMAGE_TABLE, COLUMN_NOTE_ID + " = ?", new String[]{Integer.toString(noteId)});

        //xoa du lieu trong bang audio
        db.delete(AUDIO_TABLE, COLUMN_NOTE_ID + " = ?", new String[]{Integer.toString(noteId)});

        //xoa du lieu trong bang note_tag
        db.delete(NOTE_TAG_TABLE, COLUMN_NOTE_ID + " = ?", new String[]{Integer.toString(noteId)});

        //xoa du lieu trong bang component
        db.delete(COMPONENT_TABLE, COLUMN_NOTE_ID + " = ?", new String[]{Integer.toString(noteId)});

        db.delete(NOTE_TABLE, COLUMN_NOTE_ID + " = ?", new String[]{Integer.toString(noteId)});
    }

	//Xoá tất cả các Note trong database (Huy test)
	public static void deleteAllNote(Context context) {
		SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
		SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + NOTE_TABLE);
	}

    // Trả về 1 đối tượng Note thông qua noteId
    public Note getNoteById(Context context, int noteId) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        String query = "SELECT * FROM "+ NOTE_TABLE+" WHERE " + COLUMN_NOTE_ID +" = ?";

        Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(noteId)});

        if (cursor.moveToFirst()){
            return new Note(cursor.getInt(0),  //noteId
                    cursor.getString(1),       //title
                    cursor.getLong(2),          //createAt
                    cursor.getString(3) );       //color
        }
        else return null;
    }


    //ToDo ArrayList<Note> getNoteByTag(Context context, String tagName)

    // trả về ArrayList<Note> thông qua tag
    public ArrayList<Note> getNoteByTag(Context context, String tagName) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        String query = "SELECT * FROM "+ NOTE_TABLE+" WHERE " + COLUMN_TAG_NAME  +" = ?";

        Cursor cursor = db.rawQuery(query, new String[]{tagName});

        if (cursor.moveToFirst()){
            ArrayList<Note> listNote = new ArrayList<Note>();
            do{
                listNote.add(new Note(cursor.getInt(0),  //noteId
                        cursor.getString(1),       //title
                        cursor.getLong(2),          //createAt
                        cursor.getString(3) ) );      //color
            } while (cursor.moveToNext());
            return listNote;
        }
        else return null;
    }


    //ToDo ArrayList<Note> searchNote(Context context, String searchText)

    // tìm note bằng search text
    public ArrayList<Note> searchNote(Context context, String searchText){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        String query = "SELECT * FROM "+ NOTE_TABLE +" WHERE " + COLUMN_TODO_CONTENT + " LIKE ? ";

        Cursor cursor = db.rawQuery(query, new String[]{"'%" + searchText + "%'"});

        if (cursor.moveToFirst()){
            ArrayList<Note> listNote = new ArrayList<Note>();
            do{
                listNote.add(new Note(cursor.getInt(0),  //noteId
                        cursor.getString(1),       //title
                        cursor.getLong(2),          //createAt
                        cursor.getString(3) ) );      //color
            } while (cursor.moveToNext());
            return listNote;
        }
        else return null;
    }


    //ToDo ArrayList<Note> getAllNote(Context context)

    // lấy tất cả các Note
    public ArrayList<Note> getAllNote(Context context) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        String query = "SELECT * FROM "+ NOTE_TABLE;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            ArrayList<Note> listNote = new ArrayList<Note>();
            do{
                listNote.add(new Note(cursor.getInt(0),  //noteId
                        cursor.getString(1),       //title
                        cursor.getLong(2),          //createAt
                        cursor.getString(3) ) );      //color
            } while (cursor.moveToNext());
            return listNote;
        }
        else return null;
    }


    //ToDo ArrayList<Note> getNoteByCreateAt(Context context, String order)
    public ArrayList<Note> getNoteByCreateAt(Context context, String order) {    //truyền giá trị biến order là  "desc" hoặc "asc"
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        String query = "SELECT * FROM " + NOTE_TABLE + " ORDER BY " + COLUMN_NOTE_CREATEAT + " " + order;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            ArrayList<Note> listNote = new ArrayList<Note>();
            do{
                listNote.add(new Note(cursor.getInt(0),  //noteId
                        cursor.getString(1),       //title
                        cursor.getLong(2),          //createAt
                        cursor.getString(3) ) );      //color
            } while (cursor.moveToNext());
            return listNote;
        }
        else return null;
    }


    //ToDo public ArrayList<Component> getAllComponent(Context context, int noteId)
    //trả về 1 list component . thằng duy dùng cái này để update
    @SuppressLint("Range")
    public ArrayList<Component> getAllComponent(Context context, int noteId){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();
        //query
        String query = "SELECT * FROM "+ COMPONENT_TABLE + " WHERE " + COLUMN_NOTE_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(noteId)});

        if (cursor.moveToFirst()){
            ArrayList<Component> list = new ArrayList<>();
            do{
                list.add(new Component(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_COMPONENT_ID)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_COMPONENT_ID)),
                        cursor.getLong(cursor.getColumnIndex(COLUMN_COMPONENT_CREATEAT)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_COMPONENT_TYPE))
                ));
            } while (cursor.moveToNext());
            return  list;
        }
        else {
            return null;
        }
    }

    //ToDo ---------------------------------------------TEXTSEGMENT-----------------------------------------
    //ToDo public long insertTextSegment(Context context, int noteId, String text)

    // Thêm  1 text Segment
    public static long insertTextSegment(Context context, int noteId, String text) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_NOTE_ID, noteId);
        ct.put(COLUMN_TEXT, text);
        ct.put(COLUMN_TEXT_CREATEAT, System.currentTimeMillis());

        // thêm text Segment vào bảng Component
        long componentId = db.insert(TEXTSEGMENT_TABLE, null, ct);
        insertComponent(context, noteId,(int) componentId, Item.TYPE_EDIT_TEXT);

        return componentId;
    }

    //ToDo public int updateTextSegment(Context context,int textId, String text)

    // Cập nhật 1 text segemnt bằng id của nó
    public int updateTextSegment(Context context,int textId, String text){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_TEXT, text);

        return db.update(TEXTSEGMENT_TABLE, ct, COLUMN_TEXT_ID + " = ?", new String[]{Integer.toString(textId)});
    }

    //ToDo  public int deleteTextSegment(Context context, int textId)

    // xóa 1 textsegment bằng id của nó
    public int deleteTextSegment(Context context, int textId) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        // lấy noteId
        int noteId = getNoteIdByTextId(context, textId);

        //xóa bản ghi trong bảng Component
        deleteComponent(context, noteId, textId, Item.TYPE_EDIT_TEXT);
        //xóa text segment trong bảng text segment
        return db.delete(TEXTSEGMENT_TABLE, COLUMN_TEXT_ID + " = ?", new String[]{Integer.toString(textId)});
    }

	//delete all text segment (Huy test)
	public static void deleteAllTextSegment(Context context) {
		SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
		SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TEXTSEGMENT_TABLE);
	}

    //ToDo public int getNoteIdByTextId(Context context, int textId)
    //lấy noteid bằng textId
    @SuppressLint("Range")
    public int getNoteIdByTextId(Context context, int textId){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        //query
        Cursor cursor = db.query(TEXTSEGMENT_TABLE, new String[]{COLUMN_NOTE_ID}, COLUMN_TEXT_ID + " = ?", new String[]{Integer.toString(textId)}, null, null, null);

        if (cursor.moveToFirst()){
            return cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID));
        }
        else {
            return -1;
        }
    }


    //ToDo public TextSegment getTextSegment (Context context, Component component )
    @SuppressLint("Range")
    public TextSegment getTextSegment (Context context, Component component ){
        if (component.getType() != Item.TYPE_EDIT_TEXT){
            return null;
        }
        else {
            SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
            SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

            String query = "SELECT * FROM " + TEXTSEGMENT_TABLE + " WHERE " + COLUMN_TEXT_ID + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(component.getComponentId())});

            if (cursor.moveToFirst()){
                return new TextSegment(
                        component.getComponentId(),
                        component.getNoteId(),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TEXT)),
                        component.getCreateAt()
                        );
            }
            else {
                return null;
            }
        }
    }

    //ToDo------------------------------------------------------ IMAGE------------------------------------------

    //ToDo insertImage(Context context, int noteId,byte[] imageData)

    // thêm 1 bản ghi image vào bảng Image
    public static long insertImage(Context context, int noteId, byte[] imageData) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_NOTE_ID, noteId);
        ct.put(COLUMN_IMAGE_DATA, imageData);
        ct.put(COLUMN_IMAGE_CREATEAT, System.currentTimeMillis());

        //thêm image vào bảng Component
        long componentId = db.insert(IMAGE_TABLE, null, ct);
        insertComponent(context, noteId, (int) componentId, Item.TYPE_IMAGE_VIEW);

        return componentId;
    }

    //ToDo updateImage(Context context,int imageId, byte[] imageData)
    public int updateImage(Context context,int imageId, byte[] imageData) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_IMAGE_ID, imageData);

        return db.update(IMAGE_TABLE, ct, COLUMN_IMAGE_ID +" = ?", new String[]{Integer.toString(imageId)});
    }


    //todo deleteImage(Context context, int imageId)
    public int deleteImage(Context context, int imageId) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();
        // lấy noteId
        int noteId = getNoteIdByImageId(context, imageId);
        // xóa image trong bảng component
        deleteComponent(context, noteId, imageId, Item.TYPE_IMAGE_VIEW);
        //xóa image trong bảng image
        return db.delete(IMAGE_TABLE, COLUMN_IMAGE_ID + " = ?", new String[]{Integer.toString(imageId)});
    }

    //ToDo public byte[] getImageById(Context context, int imageId)
    public byte[] getImageById(Context context, int imageId) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();
        String query = "SELECT * FROM "+ IMAGE_TABLE+" WHERE " + COLUMN_IMAGE_ID  +" = ?";
        Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(imageId)});
        if (cursor.moveToFirst()) {
            return cursor.getBlob(1);
        }
        return null;
    }


    //ToDo public int getNoteIdByImageId(Context context, int imageId)

    @SuppressLint("Range")
    public int getNoteIdByImageId(Context context, int imageId){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(IMAGE_TABLE, new String[]{COLUMN_NOTE_ID}, COLUMN_IMAGE_ID + " = ?", new String[]{Integer.toString(imageId)}, null, null, null);

        if (cursor.moveToFirst()){
            return cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID));
        }
        else {
            return -1;
        }
    }


    //ToDo public Image getImage (Context context, Component component)
    @SuppressLint("Range")
    public Image getImage (Context context, Component component){
        if (component.getType() != Item.TYPE_IMAGE_VIEW){
            return null;
        }
        else {
            SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
            SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

            String query = "SELECT * FROM " + IMAGE_TABLE + " WHERE " + COLUMN_IMAGE_ID + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(component.getComponentId())});

            if (cursor.moveToFirst()){
                return new Image(
                        component.getComponentId(),
                        component.getNoteId(),
                        cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE_DATA)),
                        component.getCreateAt()
                );
            }
            else {
                return null;
            }
        }
    }



    //ToDo----------------------------------------------------- AUDIO-------------------------------------------

    //ToDo public long insertAudio(Context context, int noteId,byte[] audioData)
    public static long insertAudio(Context context, int noteId, byte[] audioData) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_NOTE_ID, noteId);
        ct.put(COLUMN_AUDIO_DATA, audioData);
        ct.put(COLUMN_AUDIO_CREATEAT, System.currentTimeMillis());
        //lấy componentID qua hàm insert
        long componentId = db.insert(AUDIO_TABLE, null, ct);
        //thêm audio vào bảng component
        insertComponent(context, noteId,(int) componentId, Item.TYPE_VOICE_VIEW);
        //return
        return componentId;
    }

    //ToDo public int updateAudio(Context context,int audioId, byte[] audioData)
    public int updateAudio(Context context,int audioId, byte[] audioData) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_AUDIO_DATA , audioData);

        return db.update(AUDIO_TABLE , ct, COLUMN_AUDIO_ID  +" = ?", new String[]{Integer.toString(audioId)});
    }


    //ToDo public int deleteAudio(Context context, int audioId)
    public int deleteAudio(Context context, int audioId) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        //lấy noteId
        int noteId = getNoteIdByAudioId(context, audioId);
        //xóa audio trong bảng componet
        deleteComponent(context, noteId, audioId, Item.TYPE_VOICE_VIEW);

        return db.delete(AUDIO_TABLE, COLUMN_AUDIO_ID + " = ?", new String[]{Integer.toString(audioId)});
    }

    //ToDo public int getNoteIdByAudioId(Context context, int audioId)
    @SuppressLint("Range")
    public int getNoteIdByAudioId(Context context, int audioId){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(AUDIO_TABLE, new String[]{COLUMN_NOTE_ID}, COLUMN_AUDIO_ID + " = ?", new String[]{Integer.toString(audioId)}, null, null, null);

        if (cursor.moveToFirst()){
            return cursor.getInt(cursor.getColumnIndex(COLUMN_NOTE_ID));
        }
        else {
            return -1;
        }
    }

    //ToDo public Audio getAudio (Context context, Component component)
    @SuppressLint("Range")
    public Audio getAudio (Context context, Component component){
        if (component.getType() != Item.TYPE_VOICE_VIEW){
            return null;
        }
        else {
            SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
            SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

            String query = "SELECT * FROM " + AUDIO_TABLE + " WHERE " + COLUMN_AUDIO_ID + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(component.getComponentId())});

            if (cursor.moveToFirst()){
                return new Audio(
                        component.getComponentId(),
                        component.getNoteId(),
                        cursor.getBlob(cursor.getColumnIndex(COLUMN_AUDIO_DATA)),
                        component.getCreateAt()
                );
            }
            else {
                return null;
            }
        }
    }



    //ToDO --------------------------------------------------------TAG------------------------------------------


    //ToDo public long createNewTag(Context context, String tagName)
    public long createNewTag(Context context, String tagName) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_TAG_NAME, tagName.trim());

        return db.insert(TAG_TABLE, null, ct);
    }



    //ToDo public int deleteTag(Context context,int tagId)

    //Xóa 1 tag . Thông tin lưu trong bảng Note_Tag cũng bị xóa
    public void deleteTag(Context context,int tagId) {
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        db.delete(NOTE_TAG_TABLE, COLUMN_TAG_ID + " = ?", new String[]{Integer.toString(tagId)});
        db.delete(TAG_TABLE, COLUMN_TAG_ID + " = ?", new String[]{Integer.toString(tagId)});
    }



    //ToDo public int getTagByTagName(Context context, String tagName)
//    public int getTagByTagName(Context context, String tagName){
//        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
//        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();
//
//        Cursor cursor = db.query(TAG_TABLE,new String[]{COLUMN_TAG_ID, COLUMN_TAG_NAME}, COLUMN_TAG_NAME+" = ?", new String[]{tagName.trim()},
//                null, null, null);
//        if (cursor.moveToFirst()){
//            return cursor.getInt(0);
//        }
//        else return -1;
//    }



    // ToDo --------------------------------------------------------ToDo---------------------------------------
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
                        cursor.getLong(2),
                        cursor.getLong(3) ) );
            } while (cursor.moveToNext());
            return listToDo;
        }
        else return null;
    }


    //ToDo----------------------------------------------------- NOTE_TAG --------------------------------------


    //ToDo public void setTagForNote(Context context, int noteId, int tagId)

    // thêm 1 bản ghi gồm noteId và tagId vào bảng Note_Tag.
    public void setTagForNote(Context context, int noteId, int tagId){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_NOTE_ID, noteId);
        ct.put(COLUMN_TAG_ID, tagId);


        db.insert(NOTE_TAG_TABLE, null, ct);
    }

    //ToDo public void deleteTagForNote(Context context, int noteId, int tagId)

    // Xóa 1 bản ghi trong bảng Note_Tag.
    public void removeTagForNote(Context context, int noteId, int tagId){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        db.delete(NOTE_TAG_TABLE,
                NOTE_TAG_TABLE + " = ? AND " + COLUMN_TAG_ID + " = ?",
                new String[] {Integer.toString(noteId), Integer.toString(tagId)});
    }



    //ToDo----------------------------------------------------- COMPONENT------------------------------------

    //ToDo Thêm 1 bản ghi vào bảng component (Dùng phụ trợ cho hàm khác)
    public static long insertComponent(Context context, int noteId, int componentId, int type){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        ContentValues ct = new ContentValues();

        ct.put(COLUMN_NOTE_ID, noteId);
        ct.put(COLUMN_COMPONENT_ID, componentId);

        switch (type){
            case Item.TYPE_EDIT_TEXT:
                ct.put(COLUMN_COMPONENT_CREATEAT, getCreateAtText(context, componentId));
                ct.put(COLUMN_COMPONENT_TYPE, type);
                break;
            case Item.TYPE_IMAGE_VIEW:
                ct.put(COLUMN_COMPONENT_CREATEAT, getCreateAtImage(context, componentId));
                ct.put(COLUMN_COMPONENT_TYPE, type);
                break;
            case Item.TYPE_VOICE_VIEW:
                ct.put(COLUMN_COMPONENT_CREATEAT, getCreateAtAudio(context, componentId));
                ct.put(COLUMN_COMPONENT_TYPE, type);
                break;
        }

        return db.insert(COMPONENT_TABLE, null, ct);
    }


    //ToDo Xóa 1 bản ghi trong bảng component. (Dùng phụ trợ cho hàm khác)

    public int deleteComponent(Context context, int noteId, int componentId, int type){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

        String whereClause = COLUMN_NOTE_ID + " = ? AND " + COLUMN_COMPONENT_ID + " = ? AND " + COLUMN_COMPONENT_TYPE + " = ? ";
        String[] whereArgs = new String[]{Integer.toString(noteId), Integer.toString(componentId), Integer.toString(type)};

        return db.delete(COMPONENT_TABLE, whereClause, whereArgs);
    }



    //ToDo----------------------------------- CÁC HÀM LẤY CREATE AT CỦA COMPONENT.-----------------------

    //Todo Lấy createAt của text
    @SuppressLint("Range")
    public static long getCreateAtText(Context context, int textId){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        Cursor cursor = db.query(TEXTSEGMENT_TABLE, new String[]{COLUMN_TEXT_CREATEAT},
                COLUMN_TEXT_ID + " = ?", new String[]{Integer.toString(textId)},
                null, null, null);


        if (cursor.moveToFirst()){
            return cursor.getLong(cursor.getColumnIndex(COLUMN_TEXT_CREATEAT));
        }
        else return -1;
    }

    //Todo: Lấy createAt của image
    @SuppressLint("Range")
    public static long getCreateAtImage(Context context, int imageId){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        Cursor cursor = db.query(IMAGE_TABLE, new String[]{COLUMN_IMAGE_CREATEAT},
                COLUMN_IMAGE_ID + " = ?", new String[]{Integer.toString(imageId)},
                null, null, null);


        if (cursor.moveToFirst()){
            return cursor.getLong(cursor.getColumnIndex(COLUMN_IMAGE_CREATEAT));
        }
        else return -1;
    }

    //Todo Lấy createAt của audio
    @SuppressLint("Range")
    public static long getCreateAtAudio(Context context, int audioId){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        Cursor cursor = db.query(AUDIO_TABLE, new String[]{COLUMN_AUDIO_CREATEAT},
                COLUMN_AUDIO_ID + " = ?", new String[]{Integer.toString(audioId)},
                null, null, null);


        if (cursor.moveToFirst()){
            return cursor.getLong(cursor.getColumnIndex(COLUMN_AUDIO_CREATEAT));
        }
        else return -1;
    }

    //ToDo Lấy createAt của note title

    @SuppressLint("Range")
    public long getCreateAt(Context context, int noteId){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        Cursor cursor = db.query(NOTE_TABLE, new String[]{COLUMN_NOTE_CREATEAT},
                COLUMN_NOTE_ID + " = ?",new String[]{Integer.toString(noteId)},
                null,null, null);
        if (cursor.moveToFirst()){
            return cursor.getLong(cursor.getColumnIndex(COLUMN_NOTE_CREATEAT));
        }
        else return -1;
    }


    //ToDo Lấy createAt của component (text, image, audio)

    @SuppressLint("Range")
    public long getCreateAtComponent(Context context, int componentId, int type){
        SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();

        Cursor cursor;
        switch (type){
            case Item.TYPE_EDIT_TEXT:
                cursor = db.query(TEXTSEGMENT_TABLE, new String[]{COLUMN_TEXT_CREATEAT},
                        COLUMN_TEXT_ID + " = ?", new String[]{Integer.toString(componentId)},
                        null, null, null);

                if (cursor.moveToFirst()){
                    return cursor.getLong(cursor.getColumnIndex(COLUMN_TEXT_CREATEAT));
                }
                break;
            case Item.TYPE_IMAGE_VIEW:
                cursor = db.query(IMAGE_TABLE, new String[]{COLUMN_IMAGE_CREATEAT},
                        COLUMN_IMAGE_ID + " = ?", new String[]{Integer.toString(componentId)},
                        null, null, null);

                if (cursor.moveToFirst()){
                    return cursor.getLong(cursor.getColumnIndex(COLUMN_IMAGE_CREATEAT));
                }
                break;

            case Item.TYPE_VOICE_VIEW:
                cursor = db.query(AUDIO_TABLE, new String[]{COLUMN_AUDIO_CREATEAT},
                        COLUMN_AUDIO_ID + " = ?", new String[]{Integer.toString(componentId)},
                        null, null, null);

                if (cursor.moveToFirst()){
                    return cursor.getLong(cursor.getColumnIndex(COLUMN_AUDIO_CREATEAT));
                }
                break;
        }
        return -1;
    }

	public static void resetAllAutoincrement(Context context) {
		SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
		SQLiteDatabase db = noteTakingDatabaseHelper.getWritableDatabase();
		db.execSQL("DELETE FROM SQLITE_SEQUENCE");
	}
}
