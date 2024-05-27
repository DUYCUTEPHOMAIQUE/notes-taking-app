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

import yuku.ambilwarna.AmbilWarnaDialog;

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
		dv = (DrawingView) findViewById(R.id.drawing_view);
		dv.mDefaultColor = 0;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void onClickSave(View v) {
		saveImage();
	}
	public void onClickChooseColor(View v) {
		openColorPickerDialogue();
	}
	private void openColorPickerDialogue() {

		// the AmbilWarnaDialog callback needs 3 parameters
		// one is the context, second is default color,
		final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(this, dv.mDefaultColor,
				new AmbilWarnaDialog.OnAmbilWarnaListener() {
					@Override
					public void onCancel(AmbilWarnaDialog dialog) {
						// leave this function body as
						// blank, as the dialog
						// automatically closes when
						// clicked on cancel button
					}

					@Override
					public void onOk(AmbilWarnaDialog dialog, int color) {
						// change the mDefaultColor to
						// change the GFG text color as
						// it is returned when the OK
						// button is clicked from the
						// color picker dialog
						dv.mDefaultColor = color;
						dv.changeColor(dv.mDefaultColor);
						// now change the picked color
						// preview box to mDefaultColor
//						mColorPreview.setBackgroundColor(mDefaultColor);
					}
				});
		colorPickerDialogue.show();
	}
	private void saveImage() {
		Bitmap bitmap = dv.getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		DatabaseHandler.insertImage(this, 0, byteArray);
	}
}