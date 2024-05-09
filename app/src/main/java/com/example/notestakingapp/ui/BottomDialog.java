package com.example.notestakingapp.ui;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.example.notestakingapp.MainActivity;
import com.example.notestakingapp.NoteEditActivity;
import com.example.notestakingapp.R;

public class BottomDialog {
    public static void showToolDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.tool_dialog_layout);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DiaLogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        LinearLayout linearLayoutXButton = dialog.findViewById(R.id.layout_x_button);
        LinearLayout linearLayoutFinishedNote = dialog.findViewById(R.id.layout_mask_as_finished);
        LinearLayout linearLayoutDeleteNote = dialog.findViewById(R.id.layout_delete_note);

        linearLayoutFinishedNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: them note vao db

                dialog.dismiss();
                try {
                    wait(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                context.startActivity(new Intent(context, MainActivity.class));

            }
        });
        linearLayoutDeleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showConfirmDeleteNote(context);
            }
        });
        linearLayoutXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private static void showConfirmDeleteNote(Context context) {
        Log.d("duy123456", "clicked");
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_confirm_delete_dialog);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DiaLogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        LinearLayout linearLayoutXButton = dialog.findViewById(R.id.layout_x_button);
        LinearLayout linearLayoutRestore = dialog.findViewById(R.id.layout_restore_note);
        LinearLayout linearLayoutConfirmDelete = dialog.findViewById(R.id.layout_confirm_delete_note);

        linearLayoutRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolDialog(context);
            }
        });
        linearLayoutConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: handle delete note
                dialog.dismiss();
                try {
                    wait(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                context.startActivity(new Intent(context, MainActivity.class));
            }
        });
        linearLayoutXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
