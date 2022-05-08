package com.gamiro.covidjournal.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blongho.country_data.Country;
import com.blongho.country_data.World;
import com.gamiro.covidjournal.HomeActivity;
import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.adapters.PostsAdapter;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.models.countries.CountryStatistics;
import com.gamiro.covidjournal.models.user.UserData;
import com.gamiro.covidjournal.models.user.UserPost;
import com.gamiro.covidjournal.viewmodels.HomeActivityViewModel;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

// Coronavirus API
// https://coronavirus-19-api.herokuapp.com/countries

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    // View model
    private HomeActivityViewModel viewModel;

    // Layout elements
    private TextView textUserName;
    private ImageView imgUserFlag;
    private RecyclerView recyclerPosts;

    // country hub
    // Layout elements
    private View countryHubView;
    private TextView textCountryName;
    private TextView textConfirmedCases, textActiveCases, textRecovered, textDeaths, textTests;
    private TextView textTodayCases, textTodayDeaths, textCritical, textCasesMillion, textMortality;
    private ConstraintLayout expandContent;
    private Button expandButton;
    private CardView cardContainer;
    private boolean isExpanded = false;

    // Add Person/Activity
    private Button btnAddPerson, btnAddActivity;
    private PostsAdapter postsAdapter;
    private ArrayList<UserPost> userPosts = new ArrayList<>();
    private HashMap<String, String> userImages = new HashMap<>();

    // Break mvvm but I can't think of any other solution
    private String currentCountry;
    private boolean isUserVerified = false;
    private LinearLayout emptyContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Exit the app if on main fragment
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Close the app");
                builder.setMessage("Do you want to close the app?");
                builder.setPositiveButton("Yes", (dialog, id) -> {
                    Intent intentOBJ = new Intent(Intent.ACTION_MAIN);
                    intentOBJ.addCategory(Intent.CATEGORY_HOME);
                    intentOBJ.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentOBJ.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentOBJ);
                });

                builder.setNegativeButton("No", (dialog, id) -> {});
                builder.show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textUserName = getView().findViewById(R.id.home_user_name);
        imgUserFlag = getView().findViewById(R.id.home_flag_next_to_name);
        recyclerPosts = getView().findViewById(R.id.recycler_home_posts);

        // Country hub
        countryHubView = getView().findViewById(R.id.country_hub);

        textCountryName = countryHubView.findViewById(R.id.hub_country_name);

        // Left half
        textConfirmedCases = countryHubView.findViewById(R.id.hub_confirmed_cases);
        textActiveCases = countryHubView.findViewById(R.id.hub_active_cases);
        textRecovered = countryHubView.findViewById(R.id.hub_recovered);
        textDeaths = countryHubView.findViewById(R.id.hub_deceased);
        textTests = countryHubView.findViewById(R.id.hub_tests);

        // Right half
        textTodayCases = countryHubView.findViewById(R.id.hub_today_cases);
        textTodayDeaths = countryHubView.findViewById(R.id.hub_today_deaths);
        textCritical = countryHubView.findViewById(R.id.hub_critical);
        textCasesMillion = countryHubView.findViewById(R.id.hub_cases_million);
        textMortality = countryHubView.findViewById(R.id.hub_mortality_rate);

        expandContent = countryHubView.findViewById(R.id.expand_content);
        expandButton = countryHubView.findViewById(R.id.expand_button);
        cardContainer = countryHubView.findViewById(R.id.card_container);

        expandContent.setVisibility(View.GONE);
        imgUserFlag.setVisibility(View.GONE);

        // Buttons
        btnAddPerson = getView().findViewById(R.id.btn_add_person);
        btnAddActivity = getView().findViewById(R.id.btn_add_activity);

        recyclerPosts.setLayoutManager(new LinearLayoutManager(getActivity()));

        btnAddPerson.setOnClickListener(view1 -> {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            if (isUserVerified) {
                if (homeActivity.isValidDestination(R.id.addPersonFragment)) {
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.addPersonFragment);
                    ((HomeActivity) getActivity()).setCheckedNavigation(R.id.nav_add_person);
                }
            } else {
                Toast.makeText(homeActivity, "You need to verify your email to unlock this feature", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddActivity.setOnClickListener(view12 -> {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            if (isUserVerified) {
                if (homeActivity.isValidDestination(R.id.addOtherFragment)) {
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.addOtherFragment);
                    ((HomeActivity) getActivity()).setCheckedNavigation(R.id.nav_add_activity);
                }
            } else {
                Toast.makeText(homeActivity, "You need to verify your email to unlock this feature", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        ((HomeActivity) getActivity()).setCheckedNavigation(R.id.nav_home);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);

        ((HomeActivity) getActivity()).setCheckedNavigation(R.id.nav_home);
        ((HomeActivity) getActivity()).setProgressState(true);

        postsAdapter = new PostsAdapter(getContext(), userPosts, userImages);
        recyclerPosts.setAdapter(postsAdapter);

        // For some reason it scrolls down because of progress state inside activity
        NestedScrollView scrollContent = getView().findViewById(R.id.home_nested_scroll);
        scrollContent.getParent().requestChildFocus(scrollContent, scrollContent);

        viewModel.isEmailVerified().observe(getViewLifecycleOwner(), isVerified -> {
            isUserVerified = isVerified;

            String text = "You can add posts using the two buttons above.";
            if (!isVerified) {
                text = "You need to verify your email";
            }

            textUserName.setTextColor(getResources().getColor(
                    isVerified ? R.color.normalTextColor : R.color.colorCancel
            ));

            emptyContainer = AppUtil.addEmptyViewRecyclerView(
                    getView().findViewById(R.id.home_buttons),
                    R.id.tv_home_posts,
                    text
            );
        });

        viewModel.getUserCountry().observe(getViewLifecycleOwner(), string -> currentCountry = string);

        viewModel.getUserData().observe(getViewLifecycleOwner(), userData -> {
            ((HomeActivity) getActivity()).setProgressState(true);

            if (userData != null) {
                textUserName.setText(userData.getName());
                // Country flag
                String countryName = userData.getCountry();

                if (!countryName.equals(currentCountry)) {
                    currentCountry = countryName;
                    viewModel.setUserCountry(currentCountry);
                }

                imgUserFlag.setImageResource(AppUtil.getFlagFromPickerCountry(countryName));
                imgUserFlag.setVisibility(View.VISIBLE);


//                viewModel.setAdStatus(userData, true);

                // For country hub
                textCountryName.setText(countryName);
            }
            ((HomeActivity) getActivity()).setProgressState(false);
        });

        // Read country data
        viewModel.getCoronaCountry().observe(getViewLifecycleOwner(), countryStatistics -> {
            // For mortality rate
            ((HomeActivity) getActivity()).setProgressState(true);

            Log.d(TAG, "CountryStatistics = null ? " + (countryStatistics == null));
            Log.d(TAG, "Cases: " + countryStatistics.getCases());

            int deaths = countryStatistics.getDeaths();
            int cases = countryStatistics.getCases();

            textActiveCases.setText(countryStatistics.getActive() != null ? countryStatistics.getActive()+"" : "-");
            textConfirmedCases.setText(cases != -1 ? cases+"" : "-");
            textRecovered.setText(countryStatistics.getRecovered() != null ? countryStatistics.getRecovered()+"" : "-");
            textDeaths.setText(deaths != -1 ? deaths+"" : "-");
            textTests.setText(countryStatistics.getTotalTests() != null ? countryStatistics.getTotalTests()+"" : "-");
            textTodayCases.setText(countryStatistics.getTodayCases() != null ? countryStatistics.getTodayCases()+"" : "-");
            textTodayDeaths.setText(countryStatistics.getTodayDeaths() != null ? countryStatistics.getTodayDeaths()+"" : "-");
            textCasesMillion.setText(countryStatistics.getCasesPerOneMillion() != null ? countryStatistics.getCasesPerOneMillion()+"" : "-");
            textCritical.setText(countryStatistics.getCritical() != null ? countryStatistics.getCritical()+"" : "-");

            // Mortality rate
            if (deaths != -1 && cases != -1) {
                double mortalityRate = (double) deaths / cases * 100.0;
                double percMortality = (double) Math.round(mortalityRate * 100.0) / 100.0;
                textMortality.setText(String.valueOf(percMortality) + "%");
            } else {
                textMortality.setText("-");
            }

            expandContent.setVisibility(View.VISIBLE);
            isExpanded = true;

            // Corona data is fetched last
            ((HomeActivity) getActivity()).setProgressState(false);
            expandButton.setOnClickListener(view -> collapseExpandContent());
        });

        // Used to retrieve friends user pictures for posts -> come up with a better solution
        viewModel.getAllFriends().observe(getViewLifecycleOwner(), stringUserDataHashMap -> {
            userImages.clear();
            for (String id: stringUserDataHashMap.keySet()) {
                UserData data = stringUserDataHashMap.get(id);

                if (!data.getImage().isEmpty()) {
                    userImages.put(id, data.getImage());
                    Log.i(TAG, "Friend id: @" + id);
                }
            }

            postsAdapter.notifyDataSetChanged();
        });

        // Posts retrieved first
        viewModel.getUserPosts().observe(getViewLifecycleOwner(), posts -> {
            userPosts.clear();
            Log.i(TAG, "onActivityCreated: userPosts length = " + userPosts.size());

            if (isUserVerified) {
                userPosts.addAll(posts);
            }

            // Show empty view if no posts
            if (emptyContainer != null) {
                emptyContainer.setVisibility(userPosts.isEmpty() ? View.VISIBLE : View.GONE);
            }
            postsAdapter.notifyDataSetChanged();
        });
    }

    private void collapseExpandContent() {
        isExpanded = !isExpanded;

        if (isExpanded) {
            // it's collapsed - expand it
            TransitionManager.beginDelayedTransition(cardContainer, new AutoTransition());

            expandContent.setVisibility(View.VISIBLE);
            expandButton.setBackgroundResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
        } else {
            // it's expanded - collapse it
            TransitionManager.beginDelayedTransition(cardContainer, new AutoTransition());

            expandContent.setVisibility(View.GONE);
            expandButton.setBackgroundResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        }
    }
}