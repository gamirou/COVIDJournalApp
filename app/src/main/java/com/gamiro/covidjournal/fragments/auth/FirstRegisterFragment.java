package com.gamiro.covidjournal.fragments.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gamiro.covidjournal.LoginActivity;
import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.RegisterActivity;
import com.gamiro.covidjournal.models.user.UserData;
import com.gamiro.covidjournal.viewmodels.HomeActivityViewModel;
import com.gamiro.covidjournal.viewmodels.RegisterViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Objects;

public class FirstRegisterFragment extends Fragment {

    private static final String TAG = "FirstRegisterFragment";
    private TextInputLayout editName;
    private TextInputLayout editEmail;
    private TextInputLayout editPassword;
    private TextInputLayout editConfirmPassword;

    private Button btnRegister;
    private RadioGroup genderRadioGroup;

    // View model
    private RegisterViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Initialize everything
        editName = getView().findViewById(R.id.edit_reg_name);
        editEmail = getView().findViewById(R.id.edit_reg_email);
        editPassword = getView().findViewById(R.id.edit_reg_password);
        editConfirmPassword = getView().findViewById(R.id.edit_reg_confirm_password);
        btnRegister = getView().findViewById(R.id.btn_register);
        genderRadioGroup = getView().findViewById(R.id.gender_radio_group);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);

        viewModel.getUserData().observe(getViewLifecycleOwner(), data -> {
            if (!data.getName().isEmpty()) {
                editName.getEditText().setText(data.getName());
            }
        });

        viewModel.getUserPrivateDetails().observe(getViewLifecycleOwner(), new Observer<HashMap<String, String>>() {
            @Override
            public void onChanged(HashMap<String, String> stringStringHashMap) {
                if (stringStringHashMap != null) {
                    editEmail.getEditText().setText(stringStringHashMap.get("email"));
                    stringStringHashMap.remove("password");
                }
            }
        });

        // Register button
        btnRegister.setOnClickListener(view -> {
            String name = editName.getEditText().getText().toString();
            String email = editEmail.getEditText().getText().toString();
            String password = editPassword.getEditText().getText().toString();
            String cPassword = editConfirmPassword.getEditText().getText().toString();
            int genderRadioId = genderRadioGroup.getCheckedRadioButtonId();

            if (!validateInputs(name, email, password, cPassword)) {
                // Fields are not right
                Toast.makeText(getActivity(), "Please verify all fields", Toast.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "btnNext: genderRadioId: " + genderRadioId);
                if (genderRadioId != -1) {
                    RadioButton radioButtonGender = getView().findViewById(genderRadioId);
                    String gender = radioButtonGender.getText().toString();

                    viewModel.setUserData(name, gender, email, password);

                    ((RegisterActivity) getActivity()).setProgressState(true);

                    Navigation.findNavController(requireView()).navigate(R.id.action_firstRegisterFragment_to_secondRegisterFragment);
                } else {
                    Toast.makeText(getActivity(), "Please enter your gender", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // UI for validating input
    private boolean validateInputs(String name, String email, String password, String confirm) {
        // Validate user's name
        boolean isValidated = true;

        if (name.isEmpty()) {
            editName.setError("Field can't be empty");
            isValidated = false;
        } else if (name.matches(".*\\d.*")) {
            editName.setError("Your name cannot have numbers");
            isValidated = false;
        } else {
            editName.setError(null);
        }

        // Validate user's email
        if (email.isEmpty()) {
            editEmail.setError("Field can't be empty");
            isValidated = false;
        } else {
            editEmail.setError(null);
        }

        // Validate password
        if (password.isEmpty()) {
            editPassword.setError("Field can't empty");
            isValidated = false;
        } else if (!viewModel.validatePassword(password)) {
            editPassword.setError("Your password needs at least 8 characters, at least a digit, a capital and a special character");
            isValidated = false;
        } else {
            editPassword.setError(null);
        }

        if (confirm.isEmpty()) {
            editConfirmPassword.setError("Field can't be empty");
            isValidated = false;
        } else if (!confirm.equals(password)) {
            editConfirmPassword.setError("Passwords don't match");
            isValidated = false;
        } else {
            editConfirmPassword.setError(null);
        }

        return isValidated;
    }
}