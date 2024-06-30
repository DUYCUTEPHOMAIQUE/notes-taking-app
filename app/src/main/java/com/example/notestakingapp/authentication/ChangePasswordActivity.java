package com.example.notestakingapp.authentication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notestakingapp.R;
import com.example.notestakingapp.firebase.FirebaseAuthHandler;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText oldPasswordEditText, newPasswordEditText, reEnteredPasswordEditText;
    TextView backButton, changePasswordButton;
    private FirebaseAuthHandler authHandler;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.change_password), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUI(); // initialize UI components
        setupUI(findViewById(R.id.change_password)); // hide software keyboard
        authHandler = new FirebaseAuthHandler(this); // initialize FirebaseAuthHandler

        // methods for buttons
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();
                changePassword(oldPassword, newPassword);
            }
        });
    }

    private void changePassword(String oldPassword, String newPassword) {
        authHandler.changePassword(oldPassword, newPassword, this);
    }

    // hide software keyboard
    // https://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(),
                    0
            );
        }
    }
    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(ChangePasswordActivity.this);
                    return false;
                }
            });
        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void initUI() {
        backButton = findViewById(R.id.back_button);
        oldPasswordEditText = findViewById(R.id.input_current_password);
        newPasswordEditText = findViewById(R.id.input_new_password);
        reEnteredPasswordEditText = findViewById(R.id.input_reentered_new_password);
        changePasswordButton = findViewById(R.id.change_password_button);
    }
}
