package com.example.notestakingapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TempDatabaseHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "temp";
	public static final int DB_VERSION = 1;

	public TempDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public static void merge(SQLiteDatabase mainDatabase) {

	}
	private void mergeTable(SQLiteDatabase mainDatabase, String tableName) {

	}
}
