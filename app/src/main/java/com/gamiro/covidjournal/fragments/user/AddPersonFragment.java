package com.gamiro.covidjournal.fragments.user;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.adapters.friends.AutoCompleteFriendAdapter;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.models.user.FriendModel;
import com.gamiro.covidjournal.models.user.UserData;
import com.gamiro.covidjournal.models.user.UserPost;
import com.gamiro.covidjournal.viewmodels.DateTimeViewModel;
import com.gamiro.covidjournal.viewmodels.HomeActivityViewModel;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddPersonFragment extends Fragment {

    private static final String TAG = "AddPersonFragment";
    private boolean isUserChosen = false;
    private String currentName = "";

    // Layout elements
    private TextInputLayout editTitle, editDate, editTime, editLocation, editUser;
    private Button btnAddPost;
    private AutoCompleteTextView autoUser;

    // Ads
    private RewardedAd rewardedAd;
    private boolean isAdEnabled = false;

    // View model
    private HomeActivityViewModel viewModel;
    private DateTimeViewModel dateViewModel;

    // Data for adapter
    private ArrayList<FriendModel> friendModelList = new ArrayList<>();
    private AutoCompleteFriendAdapter userAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_person, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTitle = getView().findViewById(R.id.edit_add_person_title);
        editUser = getView().findViewById(R.id.edit_add_person_user);
        editDate = getView().findViewById(R.id.edit_add_person_date);
        editTime = getView().findViewById(R.id.edit_add_person_time);
        editLocation = getView().findViewById(R.id.edit_add_person_location);

        btnAddPost = getView().findViewById(R.id.btn_add_person_save);

        editDate.getEditText().setInputType(InputType.TYPE_NULL);
        editTime.getEditText().setInputType(InputType.TYPE_NULL);
        editLocation.getEditText().setInputType(InputType.TYPE_NULL);

        // TODO: Remove this when we fix location
        editLocation.getEditText().setText("TODO: Add location");

        autoUser = getView().findViewById(R.id.auto_edit_user);

        // Not this
        editLocation.setFocusable(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        userAdapter = new AutoCompleteFriendAdapter(getContext(), friendModelList);
        autoUser.setAdapter(userAdapter);

        autoUser.setDropDownBackgroundDrawable(
                new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorTransparent))
        );

        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);
        dateViewModel = new ViewModelProvider(requireActivity()).get(DateTimeViewModel.class);

        // Reset the time and date
        dateViewModel.reset();

        viewModel.getUserData().observe(getViewLifecycleOwner(), data -> {
            isAdEnabled = data.isAdEnabled();

            Log.i(TAG, "USER DATA \n" + data.print());

            if (data.isAdEnabled()) {
                // ca-app-pub-4730944331536678/8177600319
                // Test: ca-app-pub-3940256099942544/5224354917
                rewardedAd = new RewardedAd(requireContext(), "ca-app-pub-3940256099942544/5224354917");
                RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
                    @Override
                    public void onRewardedAdLoaded() {
                        // Ad successfully loaded.
                    }

                    @Override
                    public void onRewardedAdFailedToLoad(LoadAdError adError) {
                        // Ad failed to load.
                        Toast.makeText(requireContext(), "Oops, there has been an error!", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    }
                };

                rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
            }
        });

        viewModel.getAllFriends().observe(getViewLifecycleOwner(), stringUserDataHashMap -> {
            String currentUserId = viewModel.getCurrentUserID();
            friendModelList.clear();

            for (String id: stringUserDataHashMap.keySet()) {
                UserData friend = stringUserDataHashMap.get(id);
                if (friend.getFriends() != null) {
                    if (friend.getFriends().get(currentUserId).equals(AppUtil.FRIEND_ACCEPTED)) {
                        friendModelList.add(new FriendModel(id, friend));
                    }
                }
            }

            Log.d(TAG, "How many friends: " + (friendModelList.size()));

            userAdapter.update(friendModelList);
        });

        autoUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String textInside = charSequence.toString();

                if (!textInside.isEmpty()) {
                    isUserChosen = textInside.equals(currentName);
                }

                viewModel.setFriendSelectedIdAddPerson("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        viewModel.getFriendSelectedIdAddPerson().observe(getViewLifecycleOwner(),
//                s -> Log.d(TAG, "Selected ID: " + s));

        autoUser.setOnItemClickListener((adapterView, view, i, l) -> {
            String result = autoUser.getText().toString();
            int splitIndex = result.indexOf("+");

            currentName = result.substring(0, splitIndex);
            String id = result.substring(splitIndex + 1);

            isUserChosen = true;
            autoUser.setText(currentName);
            viewModel.setFriendSelectedIdAddPerson(id);
        });

        // Display date & time picker on click
        editDate.getEditText().setOnClickListener(view -> {
            if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.addPersonFragment) {
                AddPersonFragmentDirections.ActionAddPersonFragmentToDatePickerFragment2 action =
                        AddPersonFragmentDirections.actionAddPersonFragmentToDatePickerFragment2();
                action.setMaxDate("today");
                action.setDateKey(AppUtil.ADD_PERSON_DATE);

                Navigation.findNavController(requireView()).navigate(action);
            }
        });
        editTime.getEditText().setOnClickListener(view -> {
            if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.addPersonFragment) {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_addPersonFragment_to_timePickerFragment);
            }
        });

        // Get date & time
        dateViewModel.getSelectedDate().observe(getViewLifecycleOwner(), dates -> {
            if (dates.containsKey(AppUtil.ADD_PERSON_DATE)) {
                editDate.getEditText().setText(dates.get(AppUtil.ADD_PERSON_DATE));
            }
        });
        dateViewModel.getSelectedTime().observe(getViewLifecycleOwner(), s -> editTime.getEditText().setText(s));

        // TODO: Set up Places search
        editLocation.setOnClickListener(view -> {
            List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(getContext());
            getActivity().startActivityForResult(intent, AppUtil.REQUEST_PLACES);
        });

        // Add post
        btnAddPost.setOnClickListener(view -> {
            String title = editTitle.getEditText().getText().toString().trim();
            String userName = autoUser.getText().toString().trim();
            String date = editDate.getEditText().getText().toString().trim();
            String time = editTime.getEditText().getText().toString().trim();
//            String location = editLocation.getEditText().getText().toString().trim();
            String location = "";

            // TODO: Validate these fields especially name and location
            if (title.isEmpty() || userName.isEmpty() || date.isEmpty() || time.isEmpty() /* || location.isEmpty() */) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
            } else {
                if (rewardedAd != null) {
                    Log.d(TAG, "Rewarded = " + rewardedAd.isLoaded());
                    if (rewardedAd.isLoaded()) {
                        RewardedAdCallback adCallback = new RewardedAdCallback() {
                            @Override
                            public void onRewardedAdOpened() {
                                // Ad opened.
                            }

                            @Override
                            public void onRewardedAdClosed() {
                                // Ad closed.
                                Toast.makeText(getContext(), "You have to watch an ad in order to add the post", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem reward) {
                                // User earned reward.
                                addPost(title, date, time, location, isUserChosen, userName);
                            }

                            @Override
                            public void onRewardedAdFailedToShow(AdError adError) {
                                // Ad failed to display.
                                Toast.makeText(getContext(), "Oops, try again! The ad failed to show!", Toast.LENGTH_SHORT).show();
                            }
                        };
                        rewardedAd.show(getActivity(), adCallback);
                        getActivity().onBackPressed();
                    } else {
                        Log.d(TAG, "The rewarded ad wasn't loaded yet.");
                    }
                } else {
                    if (!isAdEnabled) {
                        addPost(title, date, time, location, isUserChosen, userName);
                    }
                }
            }

        });
    }

    private void addPost(String title, String date, String time, String location, boolean isUserChosen, String userName) {
        UserPost post = new UserPost("person", "",
                title, date, time, location,
                !isUserChosen, userName, "", viewModel.getCurrentUserID(), "");
        viewModel.addPostPerson(post);

        // Go back
        getActivity().onBackPressed();
    }
}