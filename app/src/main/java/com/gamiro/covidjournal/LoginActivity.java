package com.gamiro.covidjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamiro.covidjournal.helpers.AppUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    //private EditText editEmail;
    //private EditText editPassword;
    private TextInputLayout editEmail;
    private TextInputLayout editPassword;

    private Button btnLogin;
    private Button btnBack;

    private TextView textRegisterPage, textForgotPassword;
    private ProgressBar progressLogin;

    private CheckBox checkEmail;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialise all components
        editEmail = findViewById(R.id.edit_login_email);
        editPassword = findViewById(R.id.edit_login_password);
        editPassword.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_NONE);
        checkEmail = findViewById(R.id.check_remember_user);

        btnLogin = findViewById(R.id.btn_login);
        btnBack = findViewById(R.id.btn_back);

        textRegisterPage = findViewById(R.id.tv_register_page);
        textForgotPassword = findViewById(R.id.tv_forgot_password);
        progressLogin = findViewById(R.id.pb_login);

        String rememberedEmail = AppUtil.restoreStringPreferenceData(getApplicationContext(), AppUtil.REMEMBER_EMAIL);

        // Restore email
        if (rememberedEmail != null) {
            if (!rememberedEmail.isEmpty()) {
                editEmail.getEditText().setText(rememberedEmail);
                checkEmail.setChecked(true);
            }
        }

        // UI Progress
        progressLogin.setVisibility(View.INVISIBLE);

        // Firebase auth
        mAuth = FirebaseAuth.getInstance();

        Log.i("LOGIN", String.valueOf(mAuth.getCurrentUser() == null));
        if (mAuth.getCurrentUser() != null) {
            Log.i("LOGIN", mAuth.getCurrentUser().getDisplayName());
        }

        // Open register page
        textRegisterPage.setOnClickListener(view -> openRegister());
        textForgotPassword.setOnClickListener(view -> openForgotPassword());
        btnBack.setOnClickListener(view -> LoginActivity.super.onBackPressed());

        // Log in button
        btnLogin.setOnClickListener(view -> {
            progressLogin.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);

            final String email = editEmail.getEditText().getText().toString();
            final String password = editPassword.getEditText().getText().toString();

            if (!validateInput(email, password)) {
                progressLogin.setVisibility(View.INVISIBLE);
            } else {
                loginUser(email, password);
            }
        });
    }

    private boolean validateInput(String email, String password) {
        boolean isValidated = true;

        if (email.isEmpty()) {
            editEmail.setError("Field can't be empty");
            isValidated = false;
        } else {
            editEmail.setError(null);
        }

        if (password.isEmpty()) {
            editPassword.setError("Field can't be empty");
            isValidated = false;
        } else {
            editPassword.setError(null);
        }

        return isValidated;
    }

    private void loginUser(String email, String password) {
        // This is where the user is logged in
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AppUtil.saveStringPreferenceData(getApplicationContext(), AppUtil.REMEMBER_EMAIL, checkEmail.isChecked() ? email : "");
                updateUI();
            } else {
                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                switch (errorCode) {

                    case "ERROR_INVALID_CUSTOM_TOKEN":
                        Toast.makeText(LoginActivity.this, "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_CUSTOM_TOKEN_MISMATCH":
                        Toast.makeText(LoginActivity.this, "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_INVALID_CREDENTIAL":
                        Toast.makeText(LoginActivity.this, "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_INVALID_EMAIL":
                        Toast.makeText(LoginActivity.this, "The email address is badly formatted.", Toast.LENGTH_LONG).show();
                        editEmail.setError("The email address is badly formatted.");
                        editEmail.requestFocus();
                        break;

                    case "ERROR_WRONG_PASSWORD":
                        Toast.makeText(LoginActivity.this, "The password is invalid or the user does not have a password.", Toast.LENGTH_LONG).show();
                        editPassword.setError("password is incorrect ");
                        editPassword.requestFocus();
                        editPassword.getEditText().setText("");
                        break;

                    case "ERROR_USER_MISMATCH":
                        Toast.makeText(LoginActivity.this, "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_REQUIRES_RECENT_LOGIN":
                        Toast.makeText(LoginActivity.this, "This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                        Toast.makeText(LoginActivity.this, "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_EMAIL_ALREADY_IN_USE":
                        Toast.makeText(LoginActivity.this, "The email address is already in use by another account.   ", Toast.LENGTH_LONG).show();
                        editEmail.setError("The email address is already in use by another account.");
                        editEmail.requestFocus();
                        break;

                    case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                        Toast.makeText(LoginActivity.this, "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_USER_DISABLED":
                        Toast.makeText(LoginActivity.this, "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_USER_TOKEN_EXPIRED":
                        Toast.makeText(LoginActivity.this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_USER_NOT_FOUND":
                        Toast.makeText(LoginActivity.this, "There is no user record corresponding to this identifier. The user may have been deleted.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_INVALID_USER_TOKEN":
                        Toast.makeText(LoginActivity.this, "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_OPERATION_NOT_ALLOWED":
                        Toast.makeText(LoginActivity.this, "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_WEAK_PASSWORD":
                        Toast.makeText(LoginActivity.this, "The given password is invalid.", Toast.LENGTH_LONG).show();
                        editPassword.setError("The password is invalid it must 6 characters at least");
                        editPassword.requestFocus();
                        break;
                }

                btnLogin.setEnabled(true);
            }
        }).addOnFailureListener(e -> {
            progressLogin.setVisibility(View.INVISIBLE);
            btnLogin.setEnabled(true);
        });
    }

    private void updateUI() {
        progressLogin.setVisibility(View.INVISIBLE);
        btnLogin.setEnabled(true);

        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

    private void openForgotPassword() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void openRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
