//package com.example.notestakingapp.firebase;
//
//import android.content.Context;
//import android.net.Uri;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.OnPausedListener;
//import com.google.firebase.storage.OnProgressListener;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//
//import java.io.File;
//
//public class FirebaseHandler {
//    //userId-> app.db -> upload
//    //ghi chu-> thoat app -> app.db ->upload
//    //khi thoat nick thi ghi chu van con, khong dang nhap tiep tuc tao ghi chu-> lan tiep theo dang nhap lai-> id cua note o local
//
//    //upload app.db
//    public void syncFromFirebase() {
//		String userId = FirebaseAuthHandler.getUserId();
//
//    }
//	public static void syncToFirebase(Context context) {
//		String userId = FirebaseAuthHandler.getUserId();
//		//todo: sửa tên database theo Dương
//		File dbFile = context.getDatabasePath("test");
//		String dbPath = dbFile.getPath();
//		Log.d("DB PATH", dbPath);
//
//		FirebaseStorage storage = FirebaseStorage.getInstance("gs://androidtest-c883b.appspot.com");
//		StorageReference storageRef = storage.getReference();
//
//		Uri upload = Uri.fromFile(dbFile);
//		UploadTask uploadTask = storageRef.child(userId).child("test.db").putFile(upload);
//		uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//			@Override
//			public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//				double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//				Log.d("Upload Status", "Upload is " + progress + "% done");
//			}
//		}).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
//			@Override
//			public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
//			}
//		}).addOnFailureListener(new OnFailureListener() {
//			@Override
//			public void onFailure(@NonNull Exception exception) {
//				// Handle unsuccessful uploads
//			}
//		}).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//			@Override
//			public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//			}
//		});
//	}
//}
