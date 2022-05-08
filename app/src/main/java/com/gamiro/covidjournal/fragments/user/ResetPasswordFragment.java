package com.gamiro.covidjournal.fragments.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gamiro.covidjournal.HomeActivity;
import com.gamiro.covidjournal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ResetPasswordFragment extends Fragment {

    private static final String TAG = "ResetPasswordFragment";
    private TextInputLayout editEmail, editPassword;
    private Button btnSendEmail;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editEmail = getView().findViewById(R.id.edit_rb_email);
        editPassword = getView().findViewById(R.id.edit_rb_password);
        btnSendEmail = getView().findViewById(R.id.btn_fb);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnSendEmail.setOnClickListener(view -> {
            final String email = editEmail.getEditText().getText().toString().trim();
            final String password = editPassword.getEditText().getText().toString().trim();

            if (validateInput(email, password)) {
                sendEmailVerification(email, password);
            } else {
                Toast.makeText(getContext(), "Oops! Try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendEmailVerification(String email, String password) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        Log.d(TAG, "sendEmailVerification: email = " + email);
        ((HomeActivity) getActivity()).setProgressState(true);

        // Prompt the user to re-provide their sign-in credentials
        mUser.reauthenticate(credential).addOnCompleteListener(task -> {
            Log.d(TAG, "sendEmailVerification: " + task.isSuccessful());

            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Credentials verified!", Toast.LENGTH_SHORT).show();
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(getContext(), "Email verification sent!", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    }
                });
            } else {
                Log.d(TAG, "sendEmailVerification: " + task.getException().toString());
                Toast.makeText(getContext(), "Your email or password is wrong", Toast.LENGTH_SHORT).show();
            }

            ((HomeActivity) getActivity()).setProgressState(false);
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
}