package com.gamiro.covidjournal.fragments.auth;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gamiro.covidjournal.HomeActivity;
import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.RegisterActivity;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.viewmodels.CityViewModel;
import com.gamiro.covidjournal.viewmodels.DateTimeViewModel;
import com.gamiro.covidjournal.viewmodels.RegisterViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;


public class SecondRegisterFragment extends Fragment {

    private static final String TAG = "SecondRegisterFragment";

    // To check if country and city has changed
    private boolean isCityInDatabase = false;

    // View Models
    private RegisterViewModel viewModel;
    private DateTimeViewModel datePickerViewModel;
    private CityViewModel cityViewModel;

    // Layout
    private CountryCodePicker countryPicker;
    private AutoCompleteTextView editCity;
    private ProgressBar progressRegister;
    private EditText editDob;
    private Button btnRegister;
    private CheckBox checkTermsPrivacy, checkAge;
    private TextView textTermsPrivacy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        countryPicker = getView().findViewById(R.id.countryCodePicker);
        editCity = getView().findViewById(R.id.edit_reg_city);
        editDob = getView().findViewById(R.id.edit_reg_dob);
        btnRegister = getView().findViewById(R.id.btn_register);
        progressRegister = getView().findViewById(R.id.progress_register);
        checkTermsPrivacy = getView().findViewById(R.id.check_terms_privacy);
        checkAge = getView().findViewById(R.id.check_age);
        textTermsPrivacy = getView().findViewById(R.id.tv_terms);

        SpannableString spannable = new SpannableString(getString(R.string.terms_privacy));
        ClickableSpan clickableSpanTerms = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppUtil.LINK_TERMS));
                getContext().startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(false);
            }
        };
        ClickableSpan clickableSpanPrivacy = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppUtil.LINK_PRIVACY));
                getContext().startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);
                ds.setUnderlineText(false);
            }
        };

        int termsIndex = 15;
        int privacyIndex = 44;
        spannable.setSpan(clickableSpanTerms, termsIndex, (termsIndex + "Terms and Conditions".length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(clickableSpanPrivacy, privacyIndex, (privacyIndex + "Privacy Policy".length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textTermsPrivacy.setText(spannable);
        textTermsPrivacy.setMovementMethod(LinkMovementMethod.getInstance());

        btnRegister.setEnabled(false);

        editDob.setInputType(InputType.TYPE_NULL);
        progressRegister.setVisibility(View.INVISIBLE);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);
        datePickerViewModel = new ViewModelProvider(requireActivity()).get(DateTimeViewModel.class);
        cityViewModel = new ViewModelProvider(requireActivity()).get(CityViewModel.class);

        cityViewModel.setUserCountry(countryPicker.getSelectedCountryName());
        viewModel.setUserCountry(countryPicker.getSelectedCountryName());

        ((RegisterActivity) getActivity()).setProgressState(false);

        countryPicker.setOnCountryChangeListener(() -> {
            String countryChosen = countryPicker.getSelectedCountryName();

            if (countryChosen.contains(""+",")) {
                countryChosen = countryChosen.substring(0, countryChosen.indexOf(","));
            }

            viewModel.setUserCountry(countryChosen);
            cityViewModel.setUserCountry(countryChosen);
        });

        // Cities
        cityViewModel.getCitiesByCountry().observe(getViewLifecycleOwner(), cityModel -> {
            if (cityModel != null) {
                ArrayList<String> cities = AppUtil.stringToList(cityModel.getCitiesList(), ",");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item, cities);
                editCity.setAdapter(adapter);
            }

            ((RegisterActivity) getActivity()).setProgressState(false);
        });

        // Check if city is in database
        editCity.setOnItemClickListener((adapterView, view, i, l) -> isCityInDatabase = true);
        editCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isCityInDatabase = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        checkTermsPrivacy.setOnClickListener(view -> btnRegister.setEnabled(checkTermsPrivacy.isChecked() && checkAge.isChecked()));
        checkAge.setOnClickListener(view -> btnRegister.setEnabled(checkTermsPrivacy.isChecked() && checkAge.isChecked()));

        // Display date picker on click
        editDob.setOnClickListener(view -> {
            if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.secondRegisterFragment) {
                SecondRegisterFragmentDirections.ActionSecondRegisterFragmentToDatePickerFragment action =
                        SecondRegisterFragmentDirections.actionSecondRegisterFragmentToDatePickerFragment("", "18",
                                AppUtil.REGISTER_DOB);

                Navigation.findNavController(requireView()).navigate(action);
            }
        });

        // Get date
        datePickerViewModel.getSelectedDate().observe(getViewLifecycleOwner(), dates -> {
            if (dates.containsKey(AppUtil.REGISTER_DOB)) {
                editDob.setText(dates.get(AppUtil.REGISTER_DOB));
            }
        });

        btnRegister.setOnClickListener(view -> {
            String country = countryPicker.getSelectedCountryName();
            String city = editCity.getText().toString();
            String dob = editDob.getText().toString();

            Log.i(TAG, "onActivityCreated: ");

            if (isCityInDatabase && !dob.isEmpty()) {
                // Everything clear
                viewModel.setUserData(dob, country, city);
                HashMap<String, String> privateDetails = viewModel.getUserPrivateDetails().getValue();
                progressRegister.setVisibility(View.VISIBLE);

                // Register user
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(privateDetails.get("email"), privateDetails.get("password"))
                        .addOnCompleteListener(getActivity(), task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                ((RegisterActivity) getActivity()).setProgressState(true);
                                viewModel.registerUser(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                viewModel.setIsSignInSuccessful(false);

                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                switch (errorCode) {

                                    case "ERROR_INVALID_CUSTOM_TOKEN":
                                        Toast.makeText(getActivity(), "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_CUSTOM_TOKEN_MISMATCH":
                                        Toast.makeText(getActivity(), "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_INVALID_CREDENTIAL":
                                        Toast.makeText(getActivity(), "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_INVALID_EMAIL":
                                        Toast.makeText(getActivity(), "The email address is badly formatted.", Toast.LENGTH_LONG).show();
                                        getActivity().onBackPressed();
                                        break;

                                    case "ERROR_USER_MISMATCH":
                                        Toast.makeText(getActivity(), "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_REQUIRES_RECENT_LOGIN":
                                        Toast.makeText(getActivity(), "This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                        Toast.makeText(getActivity(), "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        Toast.makeText(getActivity(), "The email address is already in use by another account.", Toast.LENGTH_LONG).show();
                                        getActivity().onBackPressed();
                                        break;

                                    case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                        Toast.makeText(getActivity(), "This credential is already associated with a different user account.", Toast.LENGTH_LONG).show();
                                        getActivity().onBackPressed();
                                        break;

                                    case "ERROR_USER_TOKEN_EXPIRED":
                                        Toast.makeText(getActivity(), "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_OPERATION_NOT_ALLOWED":
                                        Toast.makeText(getActivity(), "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                                        break;

                                    case "ERROR_WEAK_PASSWORD":
                                        Toast.makeText(getActivity(), "The given password is invalid.", Toast.LENGTH_LONG).show();
                                        getActivity().onBackPressed();
                                        break;
                                }
                            }
                        });
            }
        });

        viewModel.getIsSignInSuccessful().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                progressRegister.setVisibility(View.INVISIBLE);
                Intent homeIntent = new Intent(getActivity(), HomeActivity.class);
                startActivity(homeIntent);
                getActivity().finish();
            } else {
                progressRegister.setVisibility(View.INVISIBLE);
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }
}