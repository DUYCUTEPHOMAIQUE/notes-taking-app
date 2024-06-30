package com.example.notestakingapp.firebase;

import static com.example.notestakingapp.ui.NotesFragment.sharedViewModel;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.database.TempDatabaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;

public class FirebaseHandler {


	public static void syncFromFirebase(Context context) {
		String userId = FirebaseAuthHandler.getUserId();
		FirebaseStorage storage = FirebaseStorage.getInstance("gs://androidtest-c883b.appspot.com");
		StorageReference storageRef = storage.getReference();
		StorageReference dbRef = storageRef.child(userId).child("note.db");
		File dbFile = context.getDatabasePath("test");
		String dbPath = dbFile.getPath();
		final long ONE_MEGABYTE = 1024 * 1024;
		dbRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
			@Override
			public void onSuccess(byte[] retrievedBytes) {
				Log.d("byte size", Integer.toString(retrievedBytes.length));
				try {
					FileOutputStream output = new FileOutputStream(dbPath);
					output.write(retrievedBytes);

					TempDatabaseHelper.mergeNoteTable(context);
					TempDatabaseHelper.mergeTodoTable(context);

					sharedViewModel.notifyDataChanged();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				// Handle any errors
			}
		});
	}

	public static void syncToFirebase(Context context) {
		String userId = FirebaseAuthHandler.getUserId();

		File dbFile = context.getDatabasePath("note.db");
		String dbPath = dbFile.getPath();
		Log.d("DB PATH", dbPath);

		FirebaseStorage storage = FirebaseStorage.getInstance("gs://androidtest-c883b.appspot.com");
		StorageReference storageRef = storage.getReference();

		Uri upload = Uri.fromFile(dbFile);
		UploadTask uploadTask = storageRef.child(userId).child("note.db").putFile(upload);
		uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
				double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
				Log.d("Upload Status", "Upload is " + progress + "% done");
			}
		}).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				// Handle unsuccessful uploads
				Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show();
			}
		}).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
				DatabaseHandler.deleteAllFirebaseData(context);
				sharedViewModel.notifyDataChanged();
				Toast.makeText(context, "Upload successfully", Toast.LENGTH_SHORT).show();
			}
		});


	}
}
