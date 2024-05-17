package com.example.notestakingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.ui.DrawingView;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class DrawingActivity extends AppCompatActivity {
	private int imageId;
	public DrawingView dv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EdgeToEdge.enable(this);
		setContentView(R.layout.activity_drawing);
		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
			Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
			return insets;
		});
		Intent intent = getIntent();
		imageId = intent.getIntExtra("imageId", 0);
		imageId = 3;
		dv = (DrawingView) findViewById(R.id.drawing_view);
		byte[] imageByteArray = DatabaseHandler.getImageById(this, imageId);
		Log.d("BYTE ARRAY", Arrays.toString(imageByteArray));
		Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);

		dv.drawBitmap(bitmap);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void onClickSave(View v) {
		Bitmap bitmap = dv.getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
//		Log.d("Inserted", "OK");
		DatabaseHandler.insertImage(this, 0, byteArray);
	}

	public void saveImage() {

	}
}