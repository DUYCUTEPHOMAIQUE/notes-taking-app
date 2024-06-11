package com.example.notestakingapp.authentication;

import static com.example.notestakingapp.adapter.NotesAdapter.listNoteIdChecked;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.notestakingapp.firebase.FirebaseAuthHandler;
import com.example.notestakingapp.shared.SharedViewModel;
import com.example.notestakingapp.ui.MainActivity;
import com.example.notestakingapp.utils.LanguageUtils;
import com.example.notestakingapp.utils.NotificationUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import com.example.notestakingapp.R;

public class SettingsActivity extends AppCompatActivity {
    private static final String TYPE_VI = "vi";
    private static final String TYPE_EN = "en";
    private static final String ENABLE_NOTI = "enable";
    private static final String DISABLE_NOTI = "disable";
    TextView backButton, pickLanguage;
    RelativeLayout profile, editProfileButton, signInButton, signUpButton, changePasswordButton, signOutButton, languageButton;
    SwitchCompat darkModeSwitch, notificationsSwitch;
    public static SharedViewModel sharedViewModelSettings;
    private FirebaseAuthHandler authHandler;
    public static boolean isNightModeOn;
    SharedPreferences sharedThemePreferences;
    SharedPreferences.Editor themeEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        sharedViewModelSettings = new ViewModelProvider(this).get(SharedViewModel.class);
        SharedPreferences sharedUserPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedUserPreferences.getString("userEmail", "No Email");
        TextView textProfileName = findViewById(R.id.text_profile_name);
        textProfileName.setText(userEmail);

        initUI(); // initialize UI components
        initPickLanguage();

        authHandler = new FirebaseAuthHandler(this); // initialize FirebaseAuthHandler

        // methods for buttons
        backButton.setOnClickListener(v -> finish());
        profile.setOnClickListener(v -> {

        });
        editProfileButton.setOnClickListener(v -> {

        });
        sharedThemePreferences = getSharedPreferences("Theme", Context.MODE_PRIVATE);
        isNightModeOn = sharedThemePreferences.getBoolean("night", false);
        darkModeSwitch.setChecked(isNightModeOn);

        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        darkModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNightModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    themeEditor = sharedThemePreferences.edit();
                    themeEditor.putBoolean("night", false);
                    isNightModeOn = false;
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    themeEditor = sharedThemePreferences.edit();
                    themeEditor.putBoolean("night", true);
                    isNightModeOn = true;
                }
                themeEditor.apply();
                darkModeSwitch.setChecked(isNightModeOn);
                recreate();
            }
        });

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableNotification(this);
            } else {
                disableNotification(this);
            }
        });

        languageButton.setOnClickListener(v -> {
            languageHandle(v);
        });
        sharedViewModelSettings.getLanguage().observe(this, language -> pickLanguage.setText(language));
        sharedViewModelSettings.getStateNotification().observe(this, state -> notificationsSwitch.setChecked(state != null && state.equals(ENABLE_NOTI)));
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmSignOut(SettingsActivity.this);
            }
        });
    }

    private void disableNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationUtils.saveNotificationState(context, DISABLE_NOTI);
                notificationManager.deleteNotificationChannel(getString(R.string.app_name));
            }
        }
    }

    private void enableNotification(Context context) {
        NotificationUtils.saveNotificationState(context, ENABLE_NOTI);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    getString(R.string.app_name),
                    "Task Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for task notifications");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void initPickLanguage() {
        String defaultLang = LanguageUtils.getSavedLanguage(this);
        if (defaultLang == null) {
            pickLanguage.setText("");
        } else {
            if (defaultLang.equals(TYPE_VI)) {
                pickLanguage.setText(getString(R.string.vietnamese));
            } else if (defaultLang.equals(TYPE_EN)) {
                pickLanguage.setText(getString(R.string.english));
            }
        }
        String stateNotification = NotificationUtils.getNotificationState(this);
        if (stateNotification == null) {
            notificationsSwitch.setChecked(true);
        } else {
            if (stateNotification.equals(ENABLE_NOTI)) {
                notificationsSwitch.setChecked(true);
            } else if (stateNotification.equals(DISABLE_NOTI)) {
                notificationsSwitch.setChecked(false);
            }
        }
    }

    private void languageHandle(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_language, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> onLanguageClick(item));
    }

    private boolean onLanguageClick(MenuItem item) {
        Toast.makeText(this, getString(R.string.restart_app_to_apply), Toast.LENGTH_SHORT).show();
        if (item.getItemId() == R.id.vi) {
            Log.d("language", "type= " + TYPE_VI);
            LanguageUtils.saveLanguage(SettingsActivity.this, TYPE_VI);
            LanguageUtils.setLocale(SettingsActivity.this, TYPE_VI);
            sharedViewModelSettings.setLanguage(getString(R.string.vietnamese));
//            restartApp(SettingsActivity.this);
        } else if (item.getItemId() == R.id.en) {
            Log.d("language", "type= " + TYPE_EN);
            LanguageUtils.saveLanguage(SettingsActivity.this, TYPE_EN);
            LanguageUtils.setLocale(SettingsActivity.this, TYPE_EN);
            sharedViewModelSettings.setLanguage(getString(R.string.english));
//            restartApp(SettingsActivity.this);
        }
        return true;
    }

    private void restartApp(Context context) {
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    public void showConfirmSignOut(Context context) {
        final Dialog dialog = new Dialog(context);
        SharedViewModel sharedViewModel = new SharedViewModel();
        if (context instanceof MainActivity) {
            ViewModelProvider viewModelProvider = new ViewModelProvider((MainActivity) context);
            sharedViewModel = viewModelProvider.get(SharedViewModel.class);
        }

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_confirm_sign_out);
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DiaLogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        RelativeLayout backButton = dialog.findViewById(R.id.sign_out_back_button);
        RelativeLayout confirmSignOutButton = dialog.findViewById(R.id.confirm_sign_out_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirmSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authHandler.signOut(SettingsActivity.this);
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    // Show a toast message
                    Toast.makeText(SettingsActivity.this, "Sign out successful!", Toast.LENGTH_SHORT).show();

                    // redirect to login screen or main screen
                    Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    // finish the current activity
                    finish();
                } else {
                    // Show a toast message indicating sign out failed
                    Toast.makeText(SettingsActivity.this, "Sign out failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
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

    public void initUI() {
        backButton = findViewById(R.id.back_button);
        profile = findViewById(R.id.profile);
        editProfileButton = findViewById(R.id.edit_profile_button);
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        notificationsSwitch = findViewById(R.id.notifications_switch);
        signInButton = findViewById(R.id.sign_in_button);
        signUpButton = findViewById(R.id.sign_up_button);
        changePasswordButton = findViewById(R.id.change_password_button);
        signOutButton = findViewById(R.id.sign_out_button);
        languageButton = findViewById(R.id.language_button);
        pickLanguage = findViewById(R.id.pick_language);
    }
}