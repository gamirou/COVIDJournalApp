package com.gamiro.covidjournal.fragments.user;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.helpers.AppUtil;
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
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

public class AddOtherFragment extends Fragment {

    private static final String TAG = "AddOtherFragment";

    // Layout
    private TextInputLayout editTitle, editDescription, editDate, editTime, editCount, editLocation;
    private Button btnAddPost;

    // Watch ad in order to add post
    private RewardedAd rewardedAd;
    private boolean isAdEnabled = false;

    // View models
    private HomeActivityViewModel viewModel;
    private DateTimeViewModel dateViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_other, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTitle = getView().findViewById(R.id.edit_add_other_title);
        editDescription = getView().findViewById(R.id.edit_add_other_description);
        editDate = getView().findViewById(R.id.edit_add_other_date);
        editTime = getView().findViewById(R.id.edit_add_other_time);
        editCount = getView().findViewById(R.id.edit_add_other_count);
        editLocation = getView().findViewById(R.id.edit_add_other_location);

        btnAddPost = getView().findViewById(R.id.btn_add_other_save);

        editDate.getEditText().setInputType(InputType.TYPE_NULL);
        editTime.getEditText().setInputType(InputType.TYPE_NULL);
        editCount.getEditText().setInputType(InputType.TYPE_NULL);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);
        dateViewModel = new ViewModelProvider(requireActivity()).get(DateTimeViewModel.class);

        dateViewModel.reset();

//        rewardedAd = new RewardedAd(requireContext(),
//                "ca-app-pub-3940256099942544/5224354917");

        viewModel.getUserData().observe(getViewLifecycleOwner(), data -> {
            isAdEnabled = data.isAdEnabled();

            Log.i(TAG, "USER DATA \n" + data.print());

            if (data.isAdEnabled()) {
                // ca-app-pub-4730944331536678/8177600319
                // Test: ca-app-pub-3940256099942544/5224354917
                rewardedAd = new RewardedAd(requireContext(), "ca-app-pub-4730944331536678/8177600319");
                RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
                    @Override
                    public void onRewardedAdLoaded() {
                        // Ad successfully loaded.
                    }

                    @Override
                    public void onRewardedAdFailedToLoad(LoadAdError adError) {
                        // Ad failed to load.
                        Log.i(TAG, "onRewardedAdFailedToLoad: " + adError.getMessage());

                        Toast.makeText(requireContext(), "Oops, there has been an error!", Toast.LENGTH_SHORT).show();
                        requireActivity().onBackPressed();
                    }
                };

                rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
            }
        });

        // Display date & time picker on click
        editDate.getEditText().setOnClickListener(view -> {
            if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.addOtherFragment) {
                AddOtherFragmentDirections.ActionAddOtherFragmentToDatePickerFragment2 action =
                        AddOtherFragmentDirections.actionAddOtherFragmentToDatePickerFragment2();
                action.setMaxDate("today");
                action.setDateKey(AppUtil.ADD_ACTIVITY_DATE);

                Navigation.findNavController(requireView()).navigate(action);
            }
        });
        editTime.getEditText().setOnClickListener(view -> {
            if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.addOtherFragment) {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_addOtherFragment_to_timePickerFragment);
            }
        });
        editCount.getEditText().setOnClickListener(view -> {
            if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.addOtherFragment) {
                AddOtherFragmentDirections.ActionAddOtherFragmentToNumberPickerFragment action =
                        AddOtherFragmentDirections.actionAddOtherFragmentToNumberPickerFragment();
                action.setNumberKey(AppUtil.ADD_OTHER_COUNT);
                action.setMinNumber(0);
                action.setMaxNumber(1000);
                action.setDialogTitle(getString(R.string.people_count));

                String count = editCount.getEditText().getText().toString();
                if (!count.isEmpty()) {
                    action.setCurrentValue(count);
                }

                Navigation.findNavController(requireView()).navigate(action);
            }
        });

        // Get date & time
        dateViewModel.getSelectedDate().observe(getViewLifecycleOwner(), dates -> {
            if (dates.containsKey(AppUtil.ADD_ACTIVITY_DATE)) {
                editDate.getEditText().setText(dates.get(AppUtil.ADD_ACTIVITY_DATE));
            }
        });
        dateViewModel.getSelectedTime().observe(getViewLifecycleOwner(), s -> editTime.getEditText().setText(s));
        dateViewModel.getSelectedNumbers().observe(getViewLifecycleOwner(), numbers -> {
            if (numbers.containsKey(AppUtil.ADD_OTHER_COUNT)) {
                int n = numbers.get(AppUtil.ADD_OTHER_COUNT).intValue();
                editCount.getEditText().setText(String.valueOf(n));
            }
        });

        // Add post
        btnAddPost.setOnClickListener(view -> {
            String title = editTitle.getEditText().getText().toString().trim();
            String description = editDescription.getEditText().getText().toString().trim();
            String date = editDate.getEditText().getText().toString().trim();
            String time = editTime.getEditText().getText().toString().trim();
            String count = editCount.getEditText().getText().toString().trim();
//            String location = editLocation.getEditText().getText().toString().trim();
            String location = "";

            if (title.isEmpty() || description.isEmpty() || date.isEmpty() ||
                    time.isEmpty() || /* location.isEmpty() || */ count.isEmpty()) {
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
                                addPost(title, date, time, location, description, count);
                            }

                            @Override
                            public void onRewardedAdFailedToShow(AdError adError) {
                                // Ad failed to display.
                            }
                        };
                        rewardedAd.show(getActivity(), adCallback);
                        getActivity().onBackPressed();
                    } else {
                        Log.d(TAG, "The rewarded ad wasn't loaded yet.");
                    }
                } else {
                    if (!isAdEnabled) {
                        addPost(title, date, time, location, description, count);
                    }
                }
            }
        });
    }

    private void addPost(String title, String date, String time, String location, String description, String count) {
        int people = Integer.parseInt(count);
        UserPost post = new UserPost("activity", "",
                title, date, time, location,
                description, people);
        viewModel.addPostOther(post);
        getActivity().onBackPressed();
    }
}