package com.example.notestakingapp.ui;

import static com.example.notestakingapp.adapter.NotesAdapter.listNoteIdChecked;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.notestakingapp.R;
import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.database.NoteComponent.ToDo;
import com.example.notestakingapp.database.NoteTakingDatabaseHelper;
import com.example.notestakingapp.notification.AlarmScheduler;
import com.example.notestakingapp.shared.SharedViewModel;
import com.example.notestakingapp.utils.HideKeyBoard;
import com.example.notestakingapp.utils.TextUtils;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BottomDialog {

    public static int noteId;
    private static SQLiteDatabase db;
    static DatabaseHandler databaseHandler;

    public static String selectedNoteColor = "#FFFFFF";

    public static boolean IS_TODO = false;
    private static Context mContext;

    public static void setColor(int colorId) {
        Log.d("duyTestColor", String.valueOf(colorId));
        if (mContext instanceof NoteEditActivity) {
            ((NoteEditActivity) mContext).setColorBackgroundNoteEdit(colorId);
        }
    }

    public static void showToolDialog(Context context, int _noteId) {
        noteId = _noteId;
        mContext = context;
        Log.d("duyColor", String.valueOf(mContext instanceof NoteEditActivity));
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_tool_dialog);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DiaLogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        LinearLayout linearLayoutXButton = dialog.findViewById(R.id.layout_x_button);
        RelativeLayout relativeLayoutFinishedNote = dialog.findViewById(R.id.layout_mark_as_finished);
        RelativeLayout relativeLayoutDeleteNote = dialog.findViewById(R.id.layout_delete_note);

        final ImageView colorNoteDefault = dialog.findViewById(R.id.color_note_default);
        final ImageView colorNoteVariant1 = dialog.findViewById(R.id.color_note_1);
        final ImageView colorNoteVariant2 = dialog.findViewById(R.id.color_note_2);
        final ImageView colorNoteVariant3 = dialog.findViewById(R.id.color_note_3);
        final ImageView colorNoteVariant4 = dialog.findViewById(R.id.color_note_4);
        final ImageView colorNoteVariant5 = dialog.findViewById(R.id.color_note_5);
        RelativeLayout relativeLayoutSetRemind = dialog.findViewById(R.id.tool_set_remind);
        RelativeLayout tagLayout = dialog.findViewById(R.id.tag_layout);
        TextView tagName = dialog.findViewById(R.id.tag_name_tool);
        tagLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showTagDiaLog(context, _noteId);
            }
        });

        relativeLayoutSetRemind.setOnClickListener(new View.OnClickListener() {
            final int[] yearPicker = {-1};
            final int[] monthPicker = {-1};
            final int[] dayPicker = {-1};
            final int[] hourPicker = {-1};
            final int[] minutePicker = {-1};

            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);
            int currentMonth = calendar.get(Calendar.MONTH);
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
            int currentMinutes = calendar.get(Calendar.MINUTE);

            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTheme(R.style.ThemeOverlay_App_MaterialCalendar).setTitleText("Select date bae").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();

                datePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Date");
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
                        builder.setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK).setTimeFormat(TimeFormat.CLOCK_24H).setHour(currentHour).setMinute(currentMinutes).setTitleText("Select Time Bae");
                        MaterialTimePicker timePicker = builder.build();

//                        https://github.com/material-components/material-components-android/blob/master/catalog/java/io/material/catalog/timepicker/TimePickerMainDemoFragment.java
                        timePicker.addOnPositiveButtonClickListener(dialog -> {

                            int selectedHour = timePicker.getHour();
                            int selectedMinute = timePicker.getMinute();


                            // Set time in calendar
                            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                            calendar.set(Calendar.MINUTE, selectedMinute);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            long miLiSecond;
                            miLiSecond = calendar.getTimeInMillis();
                            AlarmScheduler.scheduleTaskAlarm(context, noteId, miLiSecond, AlarmScheduler.IS_NOTE);
                            //todo: add setTime date todo task
                            Toast.makeText(context, "Date: " + calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " Time: " + selectedHour + ":" + selectedMinute, Toast.LENGTH_SHORT).show();

                        });

                        timePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePicker.dismiss();
                                datePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Time2");
                            }
                        });
                        timePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                timePicker.dismiss();
                                datePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Time2");
                            }
                        });
                        timePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Time");
                    }
                });
            }
        });
        dialog.findViewById(R.id.view_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int colorId = R.color.colorNoteDefault;
                int colorInt = context.getResources().getColor(R.color.colorNoteDefault);
                selectedNoteColor = String.format("#%06X", (0xFFFFFF & colorInt));
                colorNoteDefault.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(colorId);
            }
        });
        dialog.findViewById(R.id.view_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int colorId = R.color.colorNoteVariant1;
                int colorInt = context.getResources().getColor(R.color.colorNoteVariant1);
                Log.d("duyColor", String.valueOf(colorInt));
                selectedNoteColor = String.format("#%06X", (0xFFFFFF & colorInt));
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(colorId);
            }
        });
        dialog.findViewById(R.id.view_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int colorId = R.color.colorNoteVariant2;
                int colorInt = context.getResources().getColor(R.color.colorNoteVariant2);
                selectedNoteColor = String.format("#%06X", (0xFFFFFF & colorInt));
                Log.d("duyColor", String.valueOf(colorInt));

                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(colorId);

            }
        });
        dialog.findViewById(R.id.view_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int colorId = R.color.colorNoteVariant3;
                int colorInt = context.getResources().getColor(R.color.colorNoteVariant3);
                selectedNoteColor = String.format("#%06X", (0xFFFFFF & colorInt));
                Log.d("duyColor", String.valueOf(colorInt));

                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(colorId);
            }
        });
        dialog.findViewById(R.id.view_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int colorId = R.color.colorNoteVariant4;

                int colorInt = context.getResources().getColor(R.color.colorNoteVariant4);
                selectedNoteColor = String.format("#%06X", (0xFFFFFF & colorInt));
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(R.drawable.round_done_24);
                colorNoteVariant5.setBackgroundResource(0);
                setColor(colorId);
            }
        });
        dialog.findViewById(R.id.view_5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int colorId = R.color.colorNoteVariant5;
                int colorInt = context.getResources().getColor(R.color.colorNoteVariant5);
                selectedNoteColor = String.format("#%06X", (0xFFFFFF & colorInt));
                colorNoteDefault.setBackgroundResource(0);
                colorNoteVariant1.setBackgroundResource(0);
                colorNoteVariant2.setBackgroundResource(0);
                colorNoteVariant3.setBackgroundResource(0);
                colorNoteVariant4.setBackgroundResource(0);
                colorNoteVariant5.setBackgroundResource(R.drawable.round_done_24);
                setColor(colorId);

            }
        });

        relativeLayoutFinishedNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: them note vao db

                dialog.dismiss();
                context.startActivity(new Intent(context, MainActivity.class));

            }
        });
        relativeLayoutDeleteNote.setOnClickListener(new View.OnClickListener() {
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

    private static void showTagDiaLog(Context context, int noteId) {
        int color = ContextCompat.getColor(context, R.color.colorTextHint);
        int colorAccent = ContextCompat.getColor(context, R.color.colorAccent);
        final String[] content = {""};
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_tag_insert);
        EditText editText = dialog.findViewById(R.id.edittext_tag);
        TextView textViewDone = dialog.findViewById(R.id.textview_done_tag);
        if (DatabaseHandler.getTagNameByTagId(context, DatabaseHandler.getTagIdByNoteId(context, noteId)) != null && !DatabaseHandler.getTagNameByTagId(context, DatabaseHandler.getTagIdByNoteId(context, noteId)).isEmpty()) {
            editText.setText(DatabaseHandler.getTagNameByTagId(context, DatabaseHandler.getTagIdByNoteId(context, noteId)));
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getText().toString().isEmpty()) {
                    textViewDone.setTextColor(color);
                } else {
                    content[0] = s.toString();
                    textViewDone.setTextColor(colorAccent);
                    textViewDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (DatabaseHandler.getTagIdByNoteId(context, noteId) != -1) {
                                //todo: update tag
                                DatabaseHandler.updateTag(context, noteId, content[0]);
                                Toast.makeText(context, "tag=" + content[0], Toast.LENGTH_SHORT).show();
                                NotesFragment.sharedViewModel.setTagChanged();
                                dialog.dismiss();
                            } else {
                                //todo: add tag
                                DatabaseHandler.insertTag(context, noteId, content[0]);
                                NotesFragment.sharedViewModel.setTagChanged();
                                Toast.makeText(context, "tag=" + content[0], Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
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
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public static void showConfirmDeleteNote(Context context) {
        final Dialog dialog = new Dialog(context);
        SharedViewModel sharedViewModel = new SharedViewModel();
        if (context instanceof MainActivity) {
            ViewModelProvider viewModelProvider = new ViewModelProvider((MainActivity) context);
            sharedViewModel = viewModelProvider.get(SharedViewModel.class);
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_confirm_delete_dialog);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DiaLogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        LinearLayout linearLayoutXButton = dialog.findViewById(R.id.layout_x_button);
        RelativeLayout relativeLayoutRestore = dialog.findViewById(R.id.layout_restore_note);
        RelativeLayout relativeLayoutConfirmDelete = dialog.findViewById(R.id.layout_confirm_delete_note);

        SharedViewModel finalSharedViewModel = sharedViewModel;

        relativeLayoutRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MainActivity) {
                    finalSharedViewModel.setItemLongPressed(false);
                    finalSharedViewModel.triggerClearUiEvent();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    showToolDialog(context, noteId);
                }
            }
        });
        relativeLayoutConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteTakingDatabaseHelper noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
                db = noteTakingDatabaseHelper.getReadableDatabase();
                databaseHandler = new DatabaseHandler();
                if (context instanceof MainActivity) {
                    if (listNoteIdChecked != null && !listNoteIdChecked.isEmpty()) {
                        for (int i : listNoteIdChecked) {
                            databaseHandler.deleteNote(context, i);
                            NotesFragment.sharedViewModel.notifyDataChanged();
                            NotesFragment.sharedViewModel.setTagChanged();
                            if (finalSharedViewModel != null) {
                                finalSharedViewModel.setItemLongPressed(false);
                                finalSharedViewModel.triggerClearUiEvent();
                                finalSharedViewModel.notifyDataChanged();
                            }
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(context, "No Item Selected!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    int noteId = NoteEditActivity.noteId;
                    //todo: handle delete note

                    databaseHandler.deleteNote(context, noteId);
                    dialog.dismiss();
                    context.startActivity(new Intent(context, MainActivity.class));
                }
            }
        });
        linearLayoutXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MainActivity) listNoteIdChecked.clear();
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (context instanceof MainActivity && listNoteIdChecked != null)
                    listNoteIdChecked.clear();
            }
        });
    }

    //test
    public static void showAwaitDiaLog(Context context, final DrawingActivity activity) {
        final Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_await_dialog);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DiaLogAnimationFromTop;
        dialog.getWindow().setGravity(Gravity.TOP);
        dialog.show();
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 1500);

        } catch (Exception e) {
            Log.e("loi", e.toString());
        }
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                activity.finish();
            }
        });
    }


    public static void showToDoDiaLog(Context context, @Nullable ToDo todo) {
        SharedViewModel sharedViewModel = new ViewModelProvider((FragmentActivity) context).get(SharedViewModel.class);
        SQLiteDatabase db;
        DatabaseHandler databaseHandler;
        NoteTakingDatabaseHelper noteTakingDatabaseHelper;
        noteTakingDatabaseHelper = new NoteTakingDatabaseHelper(context);
        db = noteTakingDatabaseHelper.getReadableDatabase();
        databaseHandler = new DatabaseHandler();
        final int[] yearPicker = {-1};
        final int[] monthPicker = {-1};
        final int[] dayPicker = {-1};
        final int[] hourPicker = {-1};
        final int[] minutePicker = {-1};
        final long[] miLiSecond = {-1};

        int color = ContextCompat.getColor(context, R.color.colorTextHint);
        int colorAccent = ContextCompat.getColor(context, R.color.colorAccent);
        final String[] content = {""};

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinutes = calendar.get(Calendar.MINUTE);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_to_do_edit);
        EditText editText = dialog.findViewById(R.id.edittext_todo);
        TextView textViewDone = dialog.findViewById(R.id.textview_done);
        TextView textViewDate = dialog.findViewById(R.id.text_view_date_edit);

        if (todo != null) {
            editText.setText(todo.getContent());
            textViewDone.setTextColor(colorAccent);
            if (todo.getDuration() != null) miLiSecond[0] = todo.getDuration();
            if (todo.getDuration() != null && miLiSecond[0] != -1) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                String dateOK = sdf.format(new Date(todo.getDuration()));
                textViewDate.setVisibility(View.VISIBLE);
                textViewDate.setText(String.valueOf(dateOK.substring(0, 16)));
            } else {
                textViewDate.setVisibility(View.GONE);
            }
        }
        editText.requestFocus();
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 400);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d("duyText", "no focus");
                    // EditText không còn focus, thực hiện xử lý ở đây
                    String text = editText.getText().toString();
                    List<String> urls = new ArrayList<>();
                    urls = TextUtils.linkDetectFromText(text);
                    SpannableString spannableString = new SpannableString(text);

                    for (String url : urls) {
                        int startIndex = text.indexOf(url);
                        while (startIndex != -1) {
                            int endIndex = startIndex + url.length();
                            spannableString.setSpan(new UnderlineSpan(), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new ForegroundColorSpan(colorAccent), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            startIndex = text.indexOf(url, endIndex);
                        }
                    }

                    editText.setText(spannableString);
                }
            }
        });


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getText().toString().isEmpty()) {
                    Log.d("duyToDo", String.valueOf(color));
                    textViewDone.setTextColor(color);
                } else {
                    content[0] = s.toString();
                    textViewDone.setTextColor(colorAccent);
                    textViewDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (todo != null) {
                                //todo: update todo
                                databaseHandler.updateTodo(context, todo.getId(), editText.getText().toString().trim(), miLiSecond[0], false);
                                AlarmScheduler.cancelTaskAlarm(context, todo.getId());
                                if (miLiSecond[0] != -1)
                                    AlarmScheduler.scheduleTaskAlarm(context, todo.getId(), miLiSecond[0], IS_TODO);
                                dialog.dismiss();

                            } else {
                                //todo: add toDo
                                if (miLiSecond[0] != -1) {
                                    long id = DatabaseHandler.insertTodo(context, content[0], miLiSecond[0]);
                                    AlarmScheduler.scheduleTaskAlarm(context, (int) id, miLiSecond[0], IS_TODO);
                                } else {
                                    DatabaseHandler.insertTodo(context, content[0], null);

                                }
                                dialog.dismiss();
                            }
                            TodoFragment.sharedViewModel.notifyDataChanged();

                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                String text = s.toString();
//                List<String> urls = new ArrayList<>();
//                urls = TextUtils.linkDetectFromText(text);
//                SpannableString spannableString = new SpannableString(text);
//
//                if(!urls.isEmpty()) {
//                    for (String url: urls) {
//                        int startIndex = text.indexOf(url);
//                        int endIndex = startIndex + url.length();
//                        spannableString.setSpan(new UnderlineSpan(), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    }
//                }
//                editText.setText(spannableString);
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

        TextView textViewSetRemind = dialog.findViewById(R.id.button_set_remind);
        textViewSetRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.clearFocus();
                HideKeyBoard.hideKeyboard((Activity) context);
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTheme(R.style.ThemeOverlay_App_MaterialCalendar).setTitleText("Select date bae").setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();

                datePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Date");

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
                        builder.setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK).setTimeFormat(TimeFormat.CLOCK_24H).setHour(currentHour).setMinute(currentMinutes).setTitleText("Select Time Bae");
                        MaterialTimePicker timePicker = builder.build();

                        // https://github.com/material-components/material-components-android/blob/master/catalog/java/io/material/catalog/timepicker/TimePickerMainDemoFragment.java
                        timePicker.addOnPositiveButtonClickListener(dialog -> {

                            //todo: add setTime date todo task
                            int selectedHour = timePicker.getHour();
                            int selectedMinute = timePicker.getMinute();

                            // Set time in calendar
                            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                            calendar.set(Calendar.MINUTE, selectedMinute);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            miLiSecond[0] = calendar.getTimeInMillis();
                            Log.d("timePickDuy", String.valueOf(miLiSecond[0]));
                            Log.d("timePickDuy", String.valueOf(System.currentTimeMillis()) + "system");

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                            String dateOK = sdf.format(new Date(miLiSecond[0]));
                            textViewDate.setText(String.valueOf(dateOK.substring(0, 16)));
                            //todo: add OK cho duong lam ham
                        });

                        timePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePicker.dismiss();
                                datePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Time2");
                            }
                        });
                        timePicker.addOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                timePicker.dismiss();
                                datePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "Time2");
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
