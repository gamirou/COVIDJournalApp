package com.gamiro.covidjournal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";

    private TextInputLayout editEmail;
    private TextInputLayout editConfirm;

    private Button btnSendLink;
    private Button btnBack;

    private ProgressBar progressForget;

    // Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Layout elements
        editEmail = findViewById(R.id.edit_fb_email);
        editConfirm = findViewById(R.id.edit_fb_confirm);
        btnSendLink = findViewById(R.id.btn_fb);
        btnBack = findViewById(R.id.btn_back);
        progressForget = findViewById(R.id.pb_fb);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Back pressed
        btnBack.setOnClickListener(view -> ForgotPasswordActivity.super.onBackPressed());

        // Send verification link
        btnSendLink.setOnClickListener(view -> {
            progressForget.setVisibility(View.VISIBLE);
            btnSendLink.setEnabled(false);

            final String email = editEmail.getEditText().getText().toString();
            final String confirm = editConfirm.getEditText().getText().toString();

            if (!validateInput(email, confirm)) {
                progressForget.setVisibility(View.INVISIBLE);
            } else {
                sendResetPasswordLink(email);
            }
        });
    }

    private void sendResetPasswordLink(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            Toast.makeText(this, "Email sent! Check your emails", Toast.LENGTH_SHORT).show();
            ForgotPasswordActivity.super.onBackPressed();
        });
    }

    private boolean validateInput(String email, String confirm) {
        boolean isValidated = true;

        if (email.isEmpty()) {
            editEmail.setError("Field can't be empty");
            isValidated = false;
        } else {
            editEmail.setError(null);
        }

        if (confirm.isEmpty()) {
            editConfirm.setError("Field can't be empty");
            isValidated = false;
        } else {
            editConfirm.setError(null);
        }

        if (isValidated) {
            if (!email.equals(confirm)) {
                editEmail.setError("The emails don't match");
                editConfirm.setError("The emails don't match");
                isValidated = false;
            } else {
                editEmail.setError(null);
                editConfirm.setError(null);
            }
        }
        btnSendLink.setEnabled(!isValidated);

        return isValidated;
    }
}