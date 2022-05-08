package com.gamiro.covidjournal.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gamiro.covidjournal.HomeActivity;
import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.RegisterActivity;
import com.gamiro.covidjournal.adapters.UserTestsAdapter;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.interfaces.listeners.UserTestListener;
import com.gamiro.covidjournal.models.user.UserData;
import com.gamiro.covidjournal.models.user.UserTest;
import com.gamiro.covidjournal.room.CityModel;
import com.gamiro.covidjournal.viewmodels.CityViewModel;
import com.gamiro.covidjournal.viewmodels.DateTimeViewModel;
import com.gamiro.covidjournal.viewmodels.HomeActivityViewModel;
import com.gamiro.covidjournal.viewmodels.SymptomsViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class ProfileFragment extends Fragment implements UserTestListener {

    // Request codes - Change to request class
    private static final String TAG = "ProfileFragment";

    // Activity view model
    private HomeActivityViewModel viewModel;
    private SymptomsViewModel symptomsViewModel;
    private CityViewModel cityViewModel;

    // Layout elements
    private CountryCodePicker countryPicker;
    private AutoCompleteTextView textCities;
    private TextView textProfileName, textGender, textDob;
    private Button btnApplyChanges;
    private ImageView profilePicture;
    private RecyclerView recyclerTests;

    // If user verified
    private Group premiumGroup;
    private View verifyView;
    private Button btnSendLink;

    // Add test
    private ExtendedFloatingActionButton fabAddTest;
    // Add symptoms
    private TextView textSymptoms;

    // Flags
    private boolean isCountryChosen = false;
    private boolean isCountryReadFromDatabase = false;
    private boolean isCityAccepted = false;
    private String previousCity = "";

    // Adapters
    private UserTestsAdapter userTestsAdapter;

    // Data for adapter - tests
    private ArrayList<UserTest> userTests = new ArrayList<>();
    private ArrayList<String> userTestsIds = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Add current country
        countryPicker = getView().findViewById(R.id.profile_country);
        textCities = getView().findViewById(R.id.profile_city);
        textProfileName = getView().findViewById(R.id.profile_name);
        textGender = getView().findViewById(R.id.tv_profile_gender);
        textDob = getView().findViewById(R.id.profile_dob);
        profilePicture = getView().findViewById(R.id.profile_picture);

        // Tests
        recyclerTests = getView().findViewById(R.id.recycler_profile_tests);
        fabAddTest = getView().findViewById(R.id.efab_add_test);

        // Symptoms
        textSymptoms = getView().findViewById(R.id.tv_enter_symptoms);

        // More info if corona
        btnApplyChanges = getView().findViewById(R.id.btn_apply_changes);

        profilePicture.setOnClickListener(view1 -> {
            if (Build.VERSION.SDK_INT >= 22) {
                checkAndRequestPermission();
            } else {
                pickAnImage();
            }
        });

        premiumGroup = getView().findViewById(R.id.profile_group);
        verifyView = getView().findViewById(R.id.verify_email_layout);
        btnSendLink = verifyView.findViewById(R.id.btn_verify_email);

        btnSendLink.setOnClickListener(view12 -> {
            Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_emailVerificationFragment);
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((HomeActivity) requireActivity()).setProgressState(true);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);
        symptomsViewModel = new ViewModelProvider(requireActivity()).get(SymptomsViewModel.class);
        cityViewModel = new ViewModelProvider(requireActivity()).get(CityViewModel.class);

        recyclerTests.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Adapter for recycler view
        userTestsAdapter = new UserTestsAdapter(getContext(), userTests, this);
        recyclerTests.setAdapter(userTestsAdapter);

        // Delete local symptoms
        symptomsViewModel.deleteLocalUserSymptoms();

        viewModel.isEmailVerified().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                premiumGroup.setVisibility(View.VISIBLE);
                fabAddTest.setVisibility(View.VISIBLE);
                verifyView.setVisibility(View.GONE);
            } else {
                premiumGroup.setVisibility(View.GONE);
                fabAddTest.setVisibility(View.GONE);
                verifyView.setVisibility(View.VISIBLE);
            }
        });

        // Observe user data
        viewModel.getUserData().observe(getViewLifecycleOwner(), userData -> {
            if (userData != null) {
                ((HomeActivity) getActivity()).setProgressState(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    textDob.setText(userData.getDob());
                    String age = AppUtil.getAge(userData.getDob());
                    textProfileName.setText(userData.getName() + ", " + age);
                } else {
                    textProfileName.setText(userData.getName());
                }

                // Gender
                textGender.setText(userData.getGender());

                // Country
                String code = AppUtil.getCodeFromPicker(userData.getCountry());
                countryPicker.setCountryForNameCode(code);
                viewModel.setUserCountry(userData.getCountry());
                cityViewModel.setUserCountry(userData.getCountry());

                isCountryReadFromDatabase = true;

                // City
                textCities.setText(userData.getCity());
                previousCity = userData.getCity();

                // Image stored in firebase storage
                if (userData.getImage() != null) {
                    if (!userData.getImage().isEmpty()) {
                        Glide.with(getContext()).load(userData.getImage()).into(profilePicture);
                    } else {
                        profilePicture.setImageResource(R.drawable.userphoto);
                    }
                } else {
                    profilePicture.setImageResource(R.drawable.userphoto);
                }

                ((HomeActivity) getActivity()).setProgressState(false);
            }
        });

        viewModel.getProfilePicture().observe(getViewLifecycleOwner(), uri -> {
            if (uri != null) {
                Glide.with(getContext()).load(uri).into(profilePicture);
            }
        });

        cityViewModel.getCities().observe(getViewLifecycleOwner(), cityModels -> {
            for (CityModel cityModel: cityModels) {
                Log.i(TAG, cityModel.getCountry() + " = " + cityModel.getCitiesList());
            }
        });

        // Check if city is in database
        textCities.setOnItemClickListener((adapterView, view, i, l) -> isCityAccepted = true);
        textCities.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isCityAccepted = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Replace the data
        btnApplyChanges.setOnClickListener(view -> {
            // Details changed
            String newCity = textCities.getText().toString();

            if (isCityAccepted || isCountryChosen)  {
                viewModel.setUserData(newCity);
                Toast.makeText(getContext(), "Your details are saved", Toast.LENGTH_SHORT).show();
            } else {
                if (!isCityAccepted) {
                    Toast.makeText(getContext(), "Choose a city from the list", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "No changes to your city and country", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Cities and countries
        countryPicker.setOnCountryChangeListener(() -> {
            String countryChosen = countryPicker.getSelectedCountryName();
            if (!isCountryChosen && isCountryReadFromDatabase) isCountryChosen = true;

            if (countryChosen.contains(""+",")) {
                if (countryChosen.toLowerCase().contains("the democratic republic of")) {
                    countryChosen = "Democratic Republic of Congo";
                } else {
                    countryChosen = countryChosen.substring(0, countryChosen.indexOf(","));
                }
            }

            viewModel.setUserCountry(countryChosen);
            cityViewModel.setUserCountry(countryChosen);
            textCities.setText("");
        });

        // Read cities from ROOM database
        cityViewModel.getCitiesByCountry().observe(getViewLifecycleOwner(), cityModel -> {
            if (cityModel != null) {
                ArrayList<String> cities = AppUtil.stringToList(cityModel.getCitiesList(), ",");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(),
                        android.R.layout.simple_spinner_dropdown_item, cities);
                textCities.setAdapter(adapter);
            }
        });

        // Tests recycler view
        viewModel.getUserTests().observe(getViewLifecycleOwner(), stringUserTestHashMap -> {
            userTests.clear();
            userTestsIds.clear();

            // Add tests data
            userTests.addAll(stringUserTestHashMap.values());
            userTestsIds.addAll(stringUserTestHashMap.keySet());

            // Update adapter
            userTestsAdapter.notifyDataSetChanged();

            ((HomeActivity) getActivity()).setProgressState(false);
        });

        // Button to add date
        fabAddTest.setOnClickListener(view -> {
            if (userTests.size() < AppUtil.MAXIMUM_TESTS) {
                if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.profileFragment) {
                    ProfileFragmentDirections.ActionProfileFragmentToUserTestFragment action =
                            ProfileFragmentDirections.actionProfileFragmentToUserTestFragment(new UserTest(), "", "", "");

                    Navigation.findNavController(requireView()).navigate(action);
                }
            } else {
                Toast.makeText(getContext(), "You have the maximum amount of tests. Delete the earliest one!", Toast.LENGTH_SHORT).show();
            }
        });

        // Enter symptoms
        textSymptoms.setOnClickListener(view -> {
            if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.profileFragment) {
                Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_symptomsFragment);
            }
        });
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private int calculateAge(Date birthdate) {
//        Calendar birth = Calendar.getInstance();
//        birth.setTime(birthdate);
//        Calendar today = Calendar.getInstance();
//
//        int yearDifference = today.get(Calendar.YEAR)
//                - birth.get(Calendar.YEAR);
//
//        if (today.get(Calendar.MONTH) < birth.get(Calendar.MONTH)) {
//            yearDifference--;
//        } else {
//            if (today.get(Calendar.MONTH) == birth.get(Calendar.MONTH)
//                    && today.get(Calendar.DAY_OF_MONTH) < birth
//                    .get(Calendar.DAY_OF_MONTH)) {
//                yearDifference--;
//            }
//
//        }
//
//        return yearDifference;
//    }

    // THIS STUFF IS FOR PROFILE IMAGE PICKER
    private void checkAndRequestPermission() {
        // Check if permission already granted
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Check if permission shown
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getContext(), "Please accept the following permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        AppUtil.REQUEST_PROFILE_IMAGE);
            }
        } else {
            pickAnImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AppUtil.REQUEST_PROFILE_IMAGE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickAnImage();
            } else {
                //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                Toast.makeText(getContext(), "Please accept the following permission", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Pick Image
    private void pickAnImage() {
        CropImage.startPickImageActivity(requireActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {}

    // For recycler view - tests
    @Override
    public void editTest(UserTest userTest, String id) {}

    @Override
    public void deleteTest(UserTest userTest, String id) {}

    @Override
    public void onUserTestClick(int position) {
        UserTest test = userTests.get(position);
        String id = userTestsIds.get(position);

        // TODO: Add navigator to new fragment
        Log.i(TAG, "Test id: @" + id);

        if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.profileFragment) {
            ProfileFragmentDirections.ActionProfileFragmentToUserTestFragment action =
                    ProfileFragmentDirections.actionProfileFragmentToUserTestFragment(test, id, "", "");

            Navigation.findNavController(requireView()).navigate(action);
        }
    }
}