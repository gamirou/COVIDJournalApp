package com.gamiro.covidjournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blongho.country_data.World;
import com.bumptech.glide.Glide;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.helpers.ConnectionLiveData;
import com.gamiro.covidjournal.viewmodels.DateTimeViewModel;
import com.gamiro.covidjournal.viewmodels.HomeActivityViewModel;
import com.gamiro.covidjournal.viewmodels.TokenViewModel;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vorlonsoft.android.rate.AppRate;
import com.vorlonsoft.android.rate.StoreType;
import com.vorlonsoft.android.rate.Time;

import java.util.ArrayList;
import java.util.UUID;

// API for news curl --location --request GET "https://api.smartable.ai/coronavirus/news/GB" --header "Subscription-Key: 3009d4cc29e4808af1ccc25c69b4d5d"

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Google
//    private static final String some = "AIzaSyDOyaDd44QqV2acMKYEdLzjLNO9cagN8Wk";
    private static final String TAG = "HomeActivityPrint";

    // Drawer & Navigation
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RelativeLayout contentView;
    private Toolbar toolbar;
    private NavController navController;
    private ProgressBar progressBarMain;
    private View navHostFragment;
    private TextView noInternetBar;

    // Other layout
    private ImageView imageProfilePicture;
    private ImageView imageNotifications;

    // Name for dynamic link
    private String userName = "Your friend";

    // Firebase
    private FirebaseAuth mAuth;
    private boolean isUserVerified;

    // View model
    private HomeActivityViewModel homeActivityViewModel;
    private DateTimeViewModel locationViewModel;
    private TokenViewModel tokenViewModel;

    // Livedata connectivity
    private ConnectionLiveData connectionLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        connectionLiveData = new ConnectionLiveData(this);

        toolbar = findViewById(R.id.toolbar);
//        toolbar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        mAuth = FirebaseAuth.getInstance();

        // No internet
        noInternetBar = findViewById(R.id.no_internet_bar);

        // Flags for feed, country hub and countries fragments
        World.init(getApplicationContext());

        // For google maps and stuff
//        Places.initialize(getApplicationContext(), API_KEY);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        contentView = findViewById(R.id.content);
        progressBarMain = findViewById(R.id.progress_main);
        navHostFragment = findViewById(R.id.nav_host_fragment);

        imageProfilePicture = toolbar.findViewById(R.id.toolbar_profile_picture);
        imageNotifications = toolbar.findViewById(R.id.toolbar_notification_picture);

        homeActivityViewModel = new ViewModelProvider(this).get(HomeActivityViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(DateTimeViewModel.class);
        tokenViewModel = new ViewModelProvider(this).get(TokenViewModel.class);

        homeActivityViewModel.getUserData().observe(this, data -> {
            if (data != null ) {
                userName = data.getName();
                if (!data.getImage().isEmpty()) {
                    Glide.with(this).load(data.getImage()).into(imageProfilePicture);
                }
            }
        });

        connectionLiveData.observe(this, aBoolean -> {
            Log.i(TAG, "Connection: " + aBoolean);
            homeActivityViewModel.setIsNetworkAvailable(aBoolean);
        });

        homeActivityViewModel.isNetworkAvailable().observe(this, aBoolean -> toggleNoInternetBar(!aBoolean));

        homeActivityViewModel.getNotificationModels().observe(this, notificationModels -> {
            imageNotifications.setImageDrawable(getDrawable(
                    notificationModels.isEmpty() ? R.drawable.ic_baseline_notifications_24 : R.drawable.ic_baseline_notifications_active_24
            ));
        });

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        init();

        // Ads
        MobileAds.initialize(this, initializationStatus -> {
//            AppRate.with(this)
//                    .setStoreType(StoreType.GOOGLEPLAY) /* default is GOOGLEPLAY (Google Play), other options are AMAZON (Amazon Appstore), BAZAAR (Cafe Bazaar),
//             *         CHINESESTORES (19 chinese app stores), MI (Mi Appstore (Xiaomi Market)), SAMSUNG (Samsung Galaxy Apps),
//             *         SLIDEME (SlideME Marketplace), TENCENT (Tencent App Store), YANDEX (Yandex.Store),
//             *         setStoreType(BLACKBERRY, long) (BlackBerry World, long - your application ID),
//             *         setStoreType(APPLE, long) (Apple App Store, long - your application ID),
//             *         setStoreType(String...) (Any other store/stores, String... - an URI or array of URIs to your app) and
//             *         setStoreType(Intent...) (Any custom intent/intents, Intent... - an intent or array of intents) */
//                    .setTimeToWait(Time.DAY, (short) 0) // default is 10 days, 0 means install millisecond, 10 means app is launched 10 or more time units later than installation
//                    .setLaunchTimes((byte) 3)           // default is 10, 3 means app is launched 3 or more times
//                    .setRemindTimeToWait(Time.DAY, (short) 2) // default is 1 day, 1 means app is launched 1 or more time units after neutral button clicked
//                    .setRemindLaunchesNumber((byte) 1)  // default is 0, 1 means app is launched 1 or more times after neutral button clicked
//                    .setSelectedAppLaunches((byte) 1)   // default is 1, 1 means each launch, 2 means every 2nd launch, 3 means every 3rd launch, etc
//                    .setShowLaterButton(true)           // default is true, true means to show the Neutral button ("Remind me later").
//                    .set365DayPeriodMaxNumberDialogLaunchTimes((short) 3) // default is unlimited, 3 means 3 or less occurrences of the display of the Rate Dialog within a 365-day period
//                    .setVersionCodeCheck(true)          // default is false, true means to re-enable the Rate Dialog if a new version of app with different version code is installed
//                    .setVersionNameCheck(true)          // default is false, true means to re-enable the Rate Dialog if a new version of app with different version name is installed
//                    .setDebug(false)                    // default is false, true is for development only, true ensures that the Rate Dialog will be shown each time the app is launched
//                    .setOnClickButtonListener(which -> Log.d(this.getLocalClassName(), Byte.toString(which))) // Java 8+, change for Java 7-
//                    .monitor();                         // Monitors the app launch times
//
//            if (AppRate.with(this).getStoreType() == StoreType.GOOGLEPLAY) { // Checks that current app store type from library options is StoreType.GOOGLEPLAY
//                if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SERVICE_MISSING) { // Checks that Google Play is available
//                    AppRate.showRateDialogIfMeetsConditions(this); // Shows the Rate Dialog when conditions are met
//                }
//            } else {
//                AppRate.showRateDialogIfMeetsConditions(this);     // Shows the Rate Dialog when conditions are met
//            }

        });


        // Toolbar buttons
        imageProfilePicture.setOnClickListener(view -> openProfile());

        imageNotifications.setOnClickListener(view -> {
            if (isValidDestination(R.id.notificationsFragment)) {
                Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment).navigate(R.id.notificationsFragment);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        setProgressState(false);
    }

    /**
     * Opens user test
     */
    private void openProfile() {
        if (isValidDestination(R.id.profileFragment)) {
            Navigation.findNavController(HomeActivity.this, R.id.nav_host_fragment).navigate(R.id.profileFragment);
            navigationView.setCheckedItem(R.id.nav_profile);
        }
    }
    private void openFriendsPage() {
        if (isValidDestination(R.id.friendsFragment)) {
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.friendsFragment);
            navigationView.setCheckedItem(R.id.nav_friends);
        }
    }

    private void init() {
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();
        navigationView.setCheckedItem(R.id.nav_home);
        animateNavigationDrawer();

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w(TAG, "getInstanceId failed", task.getException());
                return;
            }

            // Get new Instance ID token
            String token = task.getResult().getToken();
            Log.d(TAG, "init: token: " + token);
            Log.i(TAG, "User ID: " + homeActivityViewModel.getCurrentUserID());
            homeActivityViewModel.addTokenToServerDatabase(token);
            tokenViewModel.setToken(token);
        });

        homeActivityViewModel.isEmailVerified().observe(this, aBoolean -> isUserVerified = aBoolean);
    }

    @Override
    public void onBackPressed() {
        toggleToolBar(false);
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // "Stolen" from Taimoor Sikander Thank you Bro
    private void animateNavigationDrawer() {
        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        drawerLayout.setScrimColor(getResources().getColor(R.color.colorPrimaryLight));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - AppUtil.NAVIGATION_END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        Log.i(TAG, "onNavigationItemSelected: " + (id == R.id.nav_how_to_use));
        toggleToolBar(id == R.id.nav_how_to_use);
        toggleDrawer(id == R.id.nav_how_to_use);

        switch (id) {
            case R.id.nav_how_to_use:
                if (isValidDestination(R.id.webFragment)) {
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.webFragment);
                    navigationView.setCheckedItem(id);
                }
                break;

            case R.id.nav_home:
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.homeFragment);
                navigationView.setCheckedItem(id);
                break;

            case R.id.nav_countries:
                if (isValidDestination(R.id.countriesFragment)) {
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.countriesFragment);
                    navigationView.setCheckedItem(id);
                }
                break;

            case R.id.nav_feed:
                if (isValidDestination(R.id.feedFragment)) {
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.feedFragment);
                    navigationView.setCheckedItem(id);
                }
                break;

            case R.id.nav_add_person:
                if (isUserVerified) {
                    if (isValidDestination(R.id.addPersonFragment)) {
                        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.addPersonFragment);
                        navigationView.setCheckedItem(id);
                    }
                } else {
                    Toast.makeText(this, "You need to verify your email to unlock this feature", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_add_activity:
                if (isUserVerified) {
                    if (isValidDestination(R.id.addOtherFragment)) {
                        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.addOtherFragment);
                        navigationView.setCheckedItem(id);
                    }
                } else {
                    Toast.makeText(this, "You need to verify your email to unlock this feature", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_add_friend:
                if (isUserVerified) {
                    if (isValidDestination(R.id.addFriendFragment)) {
                        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.addFriendFragment);
                        navigationView.setCheckedItem(id);
                    }
                } else {
                    Toast.makeText(this, "You need to verify your email to unlock this feature", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_profile:
                openProfile();
                break;

            case R.id.nav_friends:
                if (isUserVerified) {
                    openFriendsPage();
                } else {
                    Toast.makeText(this, "You need to verify your email to unlock this feature", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.nav_settings:
                if (isValidDestination(R.id.settingsFragment)) {
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.settingsFragment);
                    navigationView.setCheckedItem(id);
                }
                break;

            case R.id.nav_share:
                //String dynamicLink = "https://covidjournal.page.link/XktS";
                String dynamicLink = "https://covidjournal.net/download";
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, userName + " has invited you to join COVIDJournal:\n\n" + dynamicLink);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Track the coronavirus and save lives!");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, null));
                break;

            case R.id.nav_logout:
                signOut();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setCheckedNavigation(int id) {
        navigationView.setCheckedItem(id);
    }

    public void signOut() {
        // TODO: Do it in user repository
        mAuth.signOut();

        tokenViewModel.deleteToken();

        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);

        finish();
    }

    // Set progress visible / gone
    public void setProgressState(boolean state) {
        int progressState = state ? View.VISIBLE : View.GONE;
        int contentState = state ? View.INVISIBLE : View.VISIBLE;

        if (navigationView != null) {
            progressBarMain.setVisibility(progressState);
            navHostFragment.setVisibility(contentState);
        }
    }

    public boolean isValidDestination(int destination) {
        return destination != Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination().getId();
    }

    public void toggleToolBar(boolean isHidden) {
        toolbar.setVisibility(isHidden ? View.GONE : View.VISIBLE);
    }

    public void toggleDrawer(boolean isHidden) {
        Log.i(TAG, "toggleDrawer: " + isHidden);
        Log.i(TAG, "toggleDrawer: " + drawerLayout.getDrawerLockMode(0));
        drawerLayout.setDrawerLockMode(
                isHidden ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED
        );
    }

    private void toggleNoInternetBar(boolean display) {
        if (display) {
            Animation enterAnim = AnimationUtils.loadAnimation(this, R.anim.from_bottom);
            noInternetBar.startAnimation(enterAnim);
        } else {
            Animation enterAnim = AnimationUtils.loadAnimation(this, R.anim.exit_bottom);
            noInternetBar.startAnimation(enterAnim);
        }
        noInternetBar.setVisibility(display ? View.VISIBLE : View.GONE);
    }

    private void cropRequest(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // result from selected image
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            cropRequest(imageUri);
        }

        // result from cropping activity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                // Return cropped image
                Uri croppedImageUri = result.getUri();
                homeActivityViewModel.setProfilePicture(croppedImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(TAG, "onActivityResult: ", error);
            }
        }

        // result from places api
        if (requestCode == AppUtil.REQUEST_PLACES && resultCode == RESULT_OK) {
            // TODO: Store place id
            // TODO: Look at billing
            Place place = Autocomplete.getPlaceFromIntent(data);
            Log.i(TAG, place.getAddress());
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawerLayout);
    }
}
