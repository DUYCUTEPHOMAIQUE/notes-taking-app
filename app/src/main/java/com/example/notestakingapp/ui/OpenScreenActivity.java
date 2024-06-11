package com.example.notestakingapp.ui;

import static com.example.notestakingapp.authentication.SettingsActivity.isNightModeOn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notestakingapp.R;
import com.example.notestakingapp.utils.LanguageUtils;

import java.util.Locale;

public class OpenScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedThemePreferences = getSharedPreferences("Theme", MODE_PRIVATE);
        boolean isNightModeOn = sharedThemePreferences.getBoolean("night", false);
        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_open_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String language = LanguageUtils.getSavedLanguage(this);
        if (language == null) {
            language = Locale.getDefault().getLanguage();
        }
        Log.d("duylang", "type="+language);
        LanguageUtils.setLocale(this, language);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(OpenScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1200);
    }
}