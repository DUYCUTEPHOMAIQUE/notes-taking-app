package com.example.notestakingapp.ui;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.notestakingapp.MainActivity;
import com.example.notestakingapp.NoteEditActivity;
import com.example.notestakingapp.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class BottomDialog {
    public static String selectedNoteColor = "#FFFFFF";
    private static Context mContext;

    public static void setColor(String color) {

        if (mContext instanceof NoteEditActivity) {
            ((NoteEditActivity) mContext).setColorBackgroundNoteEdit(color);
        }
    }

    public static void showToolDialog(Context context) {
        mContext = context;
        Log.d("duyColor", String.valueOf(mContext instanceof NoteEditActivity));
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

        final ImageView colorNoteDefault = dialog.findViewById(R.id.color_note_default);
        final ImageView colorNoteVariant1 = dialog.findViewById(R.id.color_note_1);
        final ImageView colorNoteVariant2 = dialog.findViewById(R.id.color_note_2);
        final ImageView colorNoteVariant3 = dialog.findViewById(R.id.color_note_3);
        final ImageView colorNoteVariant4 = dialog.findViewById(R.id.color_note_4);
        final ImageView colorNoteVariant5 = dialog.findViewById(R.id.color_note_5);

        dialog.findViewById(R.id.view_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FFFFFF";
                colorNoteDefault.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(selectedNoteColor);
            }
        });
        dialog.findViewById(R.id.view_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#F7F6D4";
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(selectedNoteColor);
            }
        });
        dialog.findViewById(R.id.view_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FDEBAB";
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(selectedNoteColor);

            }
        });
        dialog.findViewById(R.id.view_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#DAF6E4";
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(selectedNoteColor);
            }
        });
        dialog.findViewById(R.id.view_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#EFE9F7";
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(selectedNoteColor);
            }
        });
        dialog.findViewById(R.id.view_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#F7DEE3";
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(R.drawable.round_done_24);
                setColor(selectedNoteColor);

            }
        });

        linearLayoutFinishedNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: them note vao db

                dialog.dismiss();
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
                dialog.dismiss();
                showToolDialog(context);
            }
        });
        linearLayoutConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: handle delete note
                dialog.dismiss();
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

    public static void showVoiceDialog(Context context) {
        Log.d("duy123456", "clicked");
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_voice_press);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DiaLogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        LinearLayout linearLayoutXButton = dialog.findViewById(R.id.layout_x_button);
        linearLayoutXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public static void showToDoDiaLog(Context context) {
        final int[] yearPicker = {-1};
        final int[] monthPicker = {-1};
        final int[] dayPicker = {-1};
        final int[] hourPicker = {-1};
        final int[] minutePicker = {-1};

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentHour = calendar.get(Calendar.HOUR);
        int currentMinutes = calendar.get(Calendar.MINUTE);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_to_do_edit);
        EditText editText = dialog.findViewById(R.id.edittext_todo);
        TextView textViewDone = dialog.findViewById(R.id.textview_done);
        editText.requestFocus();
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 400);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editText.getText().toString().isEmpty()) {
                    int color = ContextCompat.getColor(context, R.color.colorTextHint);
                    Log.d("duyToDo", String.valueOf(color));
                    textViewDone.setTextColor(color);
                }
                else {
                    int colorAccent = ContextCompat.getColor(context, R.color.colorAccent);
                    textViewDone.setTextColor(colorAccent);
                    editText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //todo: add toDo
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DiaLogAnimation;
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.y = dpToPx(context, 20); // Chuyển đổi dp thành px
        dialog.getWindow().setAttributes(layoutParams);

        LinearLayout linearLayoutSetRemind = dialog.findViewById(R.id.layout_set_remind);
        linearLayoutSetRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder
                        .datePicker()
                        .setTheme(R.style.ThemeOverlay_App_MaterialCalendar)
                        .setTitleText("Select date bae")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

                datePicker.show(((AppCompatActivity)context).getSupportFragmentManager(), "Date");

                datePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        datePicker.dismiss();
                    }
                });
                datePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePicker.dismiss();
                    }
                });
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long aLong) {
                        calendar.setTimeInMillis(aLong);
                        MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder();
                        builder.setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                                .setTimeFormat(TimeFormat.CLOCK_24H)
                                .setHour(currentHour)
                                .setMinute(currentMinutes)
                                .setTitleText("Select Time Bae");
                        MaterialTimePicker timePicker = builder.build();

//                        https://github.com/material-components/material-components-android/blob/master/catalog/java/io/material/catalog/timepicker/TimePickerMainDemoFragment.java
                        timePicker.addOnPositiveButtonClickListener(dialog -> {

                            //todo: add setTime date todo task
                            int selectedHour = timePicker.getHour();
                            int selectedMinute = timePicker.getMinute();
                            Toast.makeText(context, "Date: " +
                                    calendar.get(Calendar.YEAR) + "-"
                                    + (calendar.get(Calendar.MONTH) + 1) + "-" +
                                    calendar.get(Calendar.DAY_OF_MONTH) +
                                    " Time: " + selectedHour + ":" +
                                    selectedMinute, Toast.LENGTH_SHORT).show();
                        });

                        timePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePicker.dismiss();
                                datePicker.show(((AppCompatActivity)context).getSupportFragmentManager(), "Time2");
                            }
                        });
                        timePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                timePicker.dismiss();
                                datePicker.show(((AppCompatActivity)context).getSupportFragmentManager(), "Time2");
                            }
                        });
                        timePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Time");
                    }
                });
            }
        });

    }

    private static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
