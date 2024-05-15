package com.example.notestakingapp.database;
import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.database.NoteComponent.Note;
import com.example.notestakingapp.database.NoteComponent.TextSegment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TempDatabaseHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "test";
	public static final int DB_VERSION = 2;

	public TempDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	//Merge Note Table
	public static void mergeNoteTable(Context context, SQLiteDatabase mainDatabase) {
		SQLiteOpenHelper tempDatabaseHelper = new TempDatabaseHelper(context);
		SQLiteDatabase tempDb = tempDatabaseHelper.getReadableDatabase();

		//get all records from test's db note table
		String query = "SELECT * FROM " + DatabaseHandler.NOTE_TABLE;
		Cursor cursor = tempDb.rawQuery(query, null);
		while (cursor.moveToNext()) {
			Note note = new Note(cursor.getInt(0),  //noteId
					cursor.getString(1),       //title
					cursor.getLong(2),          //createAt
					cursor.getString(3) );       //color
			long firebaseNoteId = cursor.getInt(0);
			//Note với created at này chưa tồn tại
			if (!checkExistByCreateAt(context, DatabaseHandler.NOTE_TABLE, Long.toString(note.getCreateAt()))) {
				long newInsertedNoteId = DatabaseHandler.insertNote(context, note.getTitle(), note.getColor());

			}
		}
	}

	public static void mergeTextSegmentTable(Context context, long firebaseNoteId, long newInsertedNoteId) {
		SQLiteOpenHelper tempDatabaseHelper = new TempDatabaseHelper(context);
		SQLiteDatabase tempDb = tempDatabaseHelper.getReadableDatabase();

		String query = "SELECT * FROM " + DatabaseHandler.TEXTSEGMENT_TABLE + " WHERE " + DatabaseHandler.COLUMN_NOTE_ID + " = ?";
		Cursor cursor = tempDb.rawQuery(query, new String[] {Long.toString(firebaseNoteId)});
		while (cursor.moveToNext()) {
			TextSegment textSegment = new TextSegment(
				cursor.getInt(0), //textID
				cursor.getInt(1), //noteID
				cursor.getString(2), //text
				Long.parseLong(cursor.getString(3)) //created at
			);
			textSegment.setNoteId((int) newInsertedNoteId);
			DatabaseHandler.insertTextSegment(context, (int) newInsertedNoteId, textSegment.getText());
		}
	}
	public static void mergeAudioTable(Context context, long firebaseNoteId, long newInsertedNoteId) {

	}
	public static void mergeImageTable(Context context, long firebaseNoteId, long newInsertedNoteId) {

	}


	public static boolean checkExistByCreateAt(Context context, String tableName, String CreateAt) {
		SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
		SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

		String query = "SELECT * FROM "+ tableName + " WHERE CREATE_AT = ?";
		Cursor cursor = db.rawQuery(query, new String[] {CreateAt});
		return cursor.getCount() >= 1;
	}
}
