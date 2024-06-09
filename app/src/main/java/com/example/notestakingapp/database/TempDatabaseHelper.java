package com.example.notestakingapp.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.notestakingapp.database.NoteComponent.Note;
import com.example.notestakingapp.firebase.FirebaseAuthHandler;

public class TempDatabaseHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "test";
	public static final int DB_VERSION = 5;

	public TempDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	public static void mergeTodoTable(Context context) {
		try {
			SQLiteOpenHelper tempDatabaseHelper = new TempDatabaseHelper(context);
			SQLiteDatabase tempDb = tempDatabaseHelper.getReadableDatabase();
			String query = "SELECT * FROM " + DatabaseHandler.TODO_TABLE + " WHERE " + DatabaseHandler.COLUMN_USER_ID + " = ?";
			Cursor cursor = tempDb.rawQuery(query, new String[]{FirebaseAuthHandler.getUserId()});
			while (cursor.moveToNext()) {
				String todoContent = cursor.getString(1);
				Long todoCreateAt = cursor.getLong(2);
				Long todoDuration = cursor.getLong(3);
				boolean todoComplete = cursor.getInt(4) == 1;

				DatabaseHandler.insertTodo(context, todoContent, todoDuration, todoComplete);
			}
		} catch (SQLiteException e) {
			Toast.makeText(context, "MERGE TODO Cannot connect to Database", Toast.LENGTH_SHORT).show();
		}
	}
	public static void mergeNoteTable(Context context) {
		try {
			SQLiteOpenHelper tempDatabaseHelper = new TempDatabaseHelper(context);
			SQLiteDatabase tempDb = tempDatabaseHelper.getReadableDatabase();
			//get all records from test's db note table
			String query = "SELECT * FROM " + DatabaseHandler.NOTE_TABLE + " WHERE " + DatabaseHandler.COLUMN_USER_ID + " = ?";
			Cursor cursor = tempDb.rawQuery(query, new String[]{FirebaseAuthHandler.getUserId()});
			while (cursor.moveToNext()) {
				Note note = new Note(cursor.getInt(0),  //noteId
						cursor.getString(1),            //title
						cursor.getLong(2),              //createAt
						cursor.getString(3));          //color
				long firebaseNoteId = cursor.getInt(0);

				long newInsertedNoteId = DatabaseHandler.insertNote(context, note.getTitle(), note.getColor());
				mergeTextSegmentTable(context, firebaseNoteId, newInsertedNoteId);
				mergeAudioTable(context, firebaseNoteId, newInsertedNoteId);
				mergeImageTable(context, firebaseNoteId, newInsertedNoteId);

				long tagId = getTempNoteTagId(context, firebaseNoteId);
				Log.d("TAG ID", Integer.toString((int) tagId));

				if (tagId != 0) {
					String tagName = getTempTagById(context, tagId);

					int dbTagId = checkTagExistByName(context, tagName);
					if (dbTagId == -1) {
						//Tag name not exist in local database
						//Insert new tag with firebase's name and get the inserted id
						long newInsertedTadId = DatabaseHandler.createNewTag(context, tagName);
						DatabaseHandler.setTagForNote(context, (int) newInsertedNoteId, (int) newInsertedTadId);
					} else {
						DatabaseHandler.setTagForNote(context, (int) newInsertedNoteId, dbTagId);
					}
				}
			}

		} catch (SQLiteException e) {
			Toast.makeText(context, "MERGE NOTE Cannot connect to Database", Toast.LENGTH_SHORT).show();
		}
	}

	public static void mergeTextSegmentTable(Context context, long firebaseNoteId, long newInsertedNoteId) {
		try {
			SQLiteOpenHelper tempDatabaseHelper = new TempDatabaseHelper(context);
			SQLiteDatabase tempDb = tempDatabaseHelper.getReadableDatabase();
			String query = "SELECT * FROM " + DatabaseHandler.TEXTSEGMENT_TABLE + " WHERE " + DatabaseHandler.COLUMN_NOTE_ID + " = ?";
			Cursor cursor = tempDb.rawQuery(query, new String[]{Long.toString(firebaseNoteId)});
			while (cursor.moveToNext()) {
				DatabaseHandler.insertTextSegment(context, (int) newInsertedNoteId, cursor.getString(2));
			}
		} catch (SQLiteException e) {
			Toast.makeText(context, "TEXT SEGMENT Cannot connect to Database", Toast.LENGTH_SHORT).show();
		}

	}

	@SuppressLint("Range")
	public static void mergeAudioTable(Context context, long firebaseNoteId, long newInsertedNoteId) {
		try {
			SQLiteOpenHelper tempDatabaseHelper = new TempDatabaseHelper(context);
			SQLiteDatabase tempDb = tempDatabaseHelper.getReadableDatabase();

			String query = "SELECT * FROM " + DatabaseHandler.AUDIO_TABLE + " WHERE " + DatabaseHandler.COLUMN_NOTE_ID + " = ?";
			Cursor cursor = tempDb.rawQuery(query, new String[]{Long.toString(firebaseNoteId)});
			while (cursor.moveToNext()) {
				DatabaseHandler.insertAudio(context, (int) newInsertedNoteId, cursor.getBlob(cursor.getColumnIndex(DatabaseHandler.COLUMN_AUDIO_DATA)));
			}
		} catch (SQLiteException e) {
			Toast.makeText(context, "AUDIO Cannot connect to Database", Toast.LENGTH_SHORT).show();
		}
	}

	public static void mergeImageTable(Context context, long firebaseNoteId, long newInsertedNoteId) {
		try {
			SQLiteOpenHelper tempDatabaseHelper = new TempDatabaseHelper(context);
			SQLiteDatabase tempDb = tempDatabaseHelper.getReadableDatabase();

			String query = "SELECT * FROM " + DatabaseHandler.IMAGE_TABLE + " WHERE " + DatabaseHandler.COLUMN_NOTE_ID + " = ?";
			Cursor cursor = tempDb.rawQuery(query, new String[]{Long.toString(firebaseNoteId)});
			while (cursor.moveToNext()) {
				DatabaseHandler.insertImage(context, (int) newInsertedNoteId, cursor.getBlob(1));
			}
		} catch (SQLiteException e) {
			Toast.makeText(context, "TODO Cannot connect to Database", Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressLint("Range")
	public static String getTempTagById(Context context, long tagId) {
		try {
			SQLiteOpenHelper tempDatabaseHelper = new TempDatabaseHelper(context);
			SQLiteDatabase tempDb = tempDatabaseHelper.getReadableDatabase();

			String query = "SELECT TAG_NAME FROM " + DatabaseHandler.TAG_TABLE + " WHERE " + DatabaseHandler.COLUMN_TAG_ID + " = ?";
			Cursor cursor = tempDb.rawQuery(query, new String[]{Long.toString(tagId)});
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				return cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_TAG_NAME));
			}
		} catch (SQLiteException e) {
			Toast.makeText(context, "GET TEMP TAG ID Cannot connect to Database", Toast.LENGTH_SHORT).show();
		}
		return "TAG ID NOT FOUND";
	}

	/**
	 * Check if tag already exist in local database
	 *
	 * @return return tag_id if tag exists and -1 if doesn't exist
	 */
	public static int checkTagExistByName(Context context, String tagName) {
		try {
			SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
			SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

			String query = "SELECT * FROM " + DatabaseHandler.TAG_TABLE + " WHERE " + DatabaseHandler.COLUMN_TAG_NAME + " = ?";
			Cursor cursor = db.rawQuery(query, new String[]{tagName});
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				return cursor.getInt(0); //tag id
			}
		} catch (SQLiteException e) {
			Toast.makeText(context, "TAG CHECK Cannot connect to Database", Toast.LENGTH_SHORT).show();
		}
		return -1;
	}

	public static int getTempNoteTagId(Context context, long noteId) {
		try {
			SQLiteOpenHelper tempDatabaseHelper = new TempDatabaseHelper(context);
			SQLiteDatabase tempDb = tempDatabaseHelper.getReadableDatabase();

			String query = "SELECT TAG_ID FROM " + DatabaseHandler.NOTE_TAG_TABLE + " WHERE " + DatabaseHandler.COLUMN_NOTE_ID + " = ?";
			Cursor cursor = tempDb.rawQuery(query, new String[]{Long.toString(noteId)});
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				int tempNoteTagId = cursor.getInt(0);
				cursor.close();
				return tempNoteTagId;
			}
			cursor.close();
		} catch (SQLiteException e) {
			Toast.makeText(context, "Cannot connect to Database", Toast.LENGTH_SHORT).show();
		}
		return 0;
	}

}
