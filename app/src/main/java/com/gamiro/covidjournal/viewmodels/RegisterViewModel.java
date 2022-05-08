package com.gamiro.covidjournal.viewmodels;

import android.net.Uri;
import android.util.Log;

import com.gamiro.covidjournal.models.user.UserData;
import com.gamiro.covidjournal.repositories.AuthRepository;
import com.gamiro.covidjournal.repositories.CoronaRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {

    private static final String TAG = "RegisterViewModel";
    // Live Data
    private MutableLiveData<UserData> userData;
    private MutableLiveData<Uri> userImage = new MutableLiveData<>();
    private MutableLiveData<HashMap<String, String>> userPrivateDetails;

    // Country and cities
    private MutableLiveData<String> userCountry = new MutableLiveData<>();
    private MutableLiveData<Boolean> isSignInSuccessful = new MutableLiveData<>();

    // Repo
    private AuthRepository authRepository;
    // Firebase and passwords
    private final String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,24})";
    private Pattern pattern;

    public RegisterViewModel() {
        // For password validation
        pattern = Pattern.compile(PASSWORD_PATTERN);
        authRepository = new AuthRepository();
    }

    public void setUserCountry(String country) {
        Log.i(TAG, "Set User Country: " + country);
        userCountry.setValue(country);
    }

    public void setUserData(String name, String gender, String email, String password) {
        if (userData == null) {
            userData = new MutableLiveData<>();
            UserData data = new UserData(name, "", "", "", gender, false,
                    false, "", true);
            userData.setValue(data);
        } else {
            UserData data = userData.getValue();
            if (data == null) {
                data = new UserData(name, "", "", "", gender, false,
                        false, "", true);}

            userData.setValue(data);
        }

        if (userPrivateDetails == null) {
            userPrivateDetails = new MutableLiveData<>();
        }

        HashMap<String, String> details = new HashMap<>();
        details.put("email", email);
        details.put("password", password);
        userPrivateDetails.setValue(details);
    }
    public void setUserData(String dob, String country, String city) {
        UserData data = userData.getValue();
        data.setDob(dob);
        data.setCountry(country);
        data.setCity(city);
        userData.setValue(data);
    }

    public LiveData<Boolean> getIsSignInSuccessful() {
        return authRepository.getIsSignInSuccessful();
    }

    public void setIsSignInSuccessful(Boolean bool) {
        authRepository.setIsSignInSuccessful(bool);
    }

    public LiveData<UserData> getUserData() {
        if (userData == null) {
            return new MutableLiveData<>();
        }
        return userData;
    }

    public LiveData<HashMap<String, String>> getUserPrivateDetails() {
        if (userPrivateDetails == null) {
            return new MutableLiveData<>();
        }
        return userPrivateDetails;
    }

    public void setUserImage(Uri image) {
        if (userImage == null) {
            userImage = new MutableLiveData<>();
        }

        userImage.setValue(image);
    }

    public void registerUser(FirebaseUser user) {
        UserData data = userData.getValue();
        Uri image = userImage.getValue();

        authRepository.createUserWithEmail(data, user, image);
    }

    // Validate password
    public boolean validatePassword(String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
