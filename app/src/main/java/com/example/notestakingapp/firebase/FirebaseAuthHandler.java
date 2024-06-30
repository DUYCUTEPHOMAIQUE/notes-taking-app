package com.example.notestakingapp.firebase;

import static android.provider.Settings.System.getString;
import static com.example.notestakingapp.ui.NotesFragment.sharedViewModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.notestakingapp.R;
import com.example.notestakingapp.database.DatabaseHandler;
import com.example.notestakingapp.ui.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Objects;

public class FirebaseAuthHandler {
	public static final String TAG = "EmailPassword";
	private final FirebaseAuth mAuth;
	public static String userId;
    private GoogleSignInClient mGoogleSignInClient;
    public static final int RC_SIGN_IN = 9001;

    public FirebaseAuthHandler(Context context) {
        mAuth = FirebaseAuth.getInstance();
    }

    public void signUp(String email, String password, final Context context) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        SharedPreferences sharedUserPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorUserPreferences = sharedUserPreferences.edit();
                        editorUserPreferences.putBoolean("isSignedIn", true);
                        editorUserPreferences.putString("userEmail", email);
                        editorUserPreferences.apply();
                        updateUI(user, context);
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(context, "Sign Up failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                        updateUI(null, context);
                    }
                }
            });
    }
    public void signIn(String email, String password, final Context context) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        SharedPreferences sharedUserPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorUserPreferences = sharedUserPreferences.edit();
                        editorUserPreferences.putBoolean("isSignedIn", true);
                        editorUserPreferences.putString("userEmail", email);
                        editorUserPreferences.apply();
                        updateUI(user, context);
                        FirebaseHandler.syncFromFirebase(context);

                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(context, "Sign In failed: " + Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_LONG).show();
                        updateUI(null, context);
                    }
                }
            });
    }

    public void signOut(Context context) {
        String userId = FirebaseAuthHandler.getUserId();
        if (userId != null) {
            FirebaseHandler.syncToFirebase(context);
            File dbFile = context.getDatabasePath("note.db");
            if (dbFile.exists()) {

                Log.d(TAG, "Local database file deleted");
            }
            SharedPreferences sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();

            mAuth.signOut();

        } else {
            Toast.makeText(context, "No user signed in", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "signOut:failure - no user");
        }
    }

    public void changePassword(String oldPassword, final String newPassword, final Context context) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mAuth.signInWithEmailAndPassword(Objects.requireNonNull(user.getEmail()), oldPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                currentUser.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Failed to update password: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            }
                        } else {
                            Toast.makeText(context, "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        } else {
            Toast.makeText(context, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }
    public static final String LOCAL_USER = "LOCAL_USER";
	public static String getUserId() {
		FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		if (user != null) {
			userId = user.getUid();
		} else {
			userId = LOCAL_USER;
		}
		return userId;
	}

	public void updateUI(FirebaseUser user, Context context) {
		if (user != null) {
			SharedPreferences preferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("user_id", user.getUid());
			editor.putString("user_email", user.getEmail());
			editor.apply();
			Toast.makeText(context, "Welcome, " + user.getEmail(), Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			context.startActivity(intent);
		} else {
			Toast.makeText(context, "User not signed in", Toast.LENGTH_SHORT).show();
		}
	}

	public void reload(Context context) {
		FirebaseUser user = mAuth.getCurrentUser();
		if (user != null) {
			user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
				@Override
				public void onComplete(@NonNull Task<Void> task) {
					if (task.isSuccessful()) {
						Log.d(TAG, "reload:success");
						updateUI(user, context);
					} else {
						Log.e(TAG, "reload:failure", task.getException());
						Toast.makeText(context, "Failed to reload user.", Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}

	public String getUserEmail() {
		FirebaseUser firebaseUser = mAuth.getCurrentUser();
		if (firebaseUser != null) {
			return firebaseUser.getEmail();
		}
		return null;
	}
}










