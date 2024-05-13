package com.example.notestakingapp.database;
import com.example.notestakingapp.database.DatabaseHandler;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TempDatabaseHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "test";
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

	public boolean checkExistByCreateAt(Context context, String tableName, String CreateAt) {
		SQLiteOpenHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
		SQLiteDatabase db = noteTakingDatabaseHelper.getReadableDatabase();

		String query = "SELECT * FROM "+ tableName + " WHERE CREATE_AT = ?";
		Cursor cursor = db.rawQuery(query, new String[] {CreateAt});
		return cursor.getCount() >= 1;
	}
}
