package com.example.notestakingapp.authentication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

import com.example.notestakingapp.R;
import com.example.notestakingapp.firebase.*;
import com.example.notestakingapp.ui.MainActivity;

public class SignUpActivity extends AppCompatActivity {
    EditText emailEditText, passwordEditText, reenteredPassword;
    TextView backButton, signUpButton, signInText, googleButton;
    private FirebaseAuthHandler authHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sign_up), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUI(); // initialize UI components
        setupUI(findViewById(R.id.sign_up)); // hide software keyboard
        authHandler = new FirebaseAuthHandler(this); // initialize FirebaseAuthHandler

        // methods for buttons
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signUpAttempt();
            }

            private void signUpAttempt() {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String rePassword = reenteredPassword.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    if (validateInput(email, password, rePassword)) {
                        signUp(email, password);
                        Toast.makeText(SignUpActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private boolean validateInput(String email, String password, String rePassword) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SignUpActivity.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(rePassword)) {
            Toast.makeText(SignUpActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void signUp(String email, String password) {
        authHandler.signUp(email, password, this);
    }

    // hide software keyboard
    // https://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0
            );
        }
    }

    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(SignUpActivity.this);
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
        emailEditText = findViewById(R.id.input_email);
        passwordEditText = findViewById(R.id.input_password);
        reenteredPassword = findViewById(R.id.input_reentered_password);
        signUpButton = findViewById(R.id.sign_up_button);
        signInText = findViewById(R.id.sign_in_text);
        googleButton = findViewById(R.id.google_button);
    }
}
