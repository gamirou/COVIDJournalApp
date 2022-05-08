package com.gamiro.covidjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.gamiro.covidjournal.adapters.IntroViewPagerAdapter;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.models.IntroScreenItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private static final String TAG = "IntroActivity";
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Button btnNext;
    private Button btnRegister;
    private TextView textLogIn;
    private TextView textCopyright;

    private IntroViewPagerAdapter introViewPagerAdapter;
    private List<IntroScreenItem> introListScreenItems;

    private String[] titles = {"Track the virus", "Check your local area", "Alert the others", "Stay informed", "Get started and save lives"};
    private String[] descriptions = {
            "Keep a list of all the people you came in contact with and let everyone know if you have been tested positive for the new coronavirus",
            "Look how your local area and country is tackling the coronavirus pandemic, and what to do to prevent the spread of the virus",
            "Have you come in contact with someone who has been tested positive? The app will let your dear ones know so they can order a test",
            "Read news and tips all over the world on how the world is responding to these unprecedented times. We promise there are positive news as well",
            "Knowledge is power is what they say. Keep track of the people you came in contact with. Protect yourself and the others. Click here to register. It's completely free"
    };
    private int[] imagesId = {
        R.drawable.track_the_virus, R.drawable.local_area, R.drawable.alert_others, R.drawable.stay_informed, R.drawable.twitter_header_photo_1
    };

    public int position = 0;
    private boolean isLastScreen = false;
    private Animation btnAnimation;
    private boolean isComingFromStartup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            isComingFromStartup = extras.getBoolean(AppUtil.COMES_FROM_STARTUP);

            FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Successful dynamic link " + task.getResult());
                        } else {
                            Log.i(TAG, "Error dynamic link " + task.getException());
                        }
                    })
                    .addOnSuccessListener(this, pendingDynamicLinkData -> {
                        Log.i(TAG, "We have a dynamic link");

                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if (deepLink != null) {
                            Log.i(TAG, "Deep link = " + deepLink);
                        }
                    });
        }

        if (restorePreferenceData() && !isComingFromStartup) {
            Intent intent = new Intent(getApplicationContext(), StartupActivity.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.activity_intro);

        viewPager = findViewById(R.id.intro_view_pager);
        tabLayout = findViewById(R.id.intro_tab_layout);
        btnNext = findViewById(R.id.btn_intro_next);
        btnRegister = findViewById(R.id.btn_to_register);
        textLogIn = findViewById(R.id.tv_log_in);
        textCopyright = findViewById(R.id.tv_copyright);
        btnAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);

        // Date for copyright
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        textCopyright.setText(textCopyright.getText() + year + " George Sima");


        introListScreenItems = new ArrayList<>();
        for (int i = 0 ; i < titles.length; i++) {
            introListScreenItems.add(new IntroScreenItem(titles[i], descriptions[i], imagesId[i]));
        }

        // Set adapter and pager
        introViewPagerAdapter = new IntroViewPagerAdapter(this, introListScreenItems);
        viewPager.setAdapter(introViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        btnNext.setOnClickListener(view -> {
            position = viewPager.getCurrentItem();
            System.out.println(position);
            System.out.print(introListScreenItems.size());

            if (position < introListScreenItems.size()) {
                position++;
                viewPager.setCurrentItem(position);
            }

            // When it reaches the end
            if (position == introListScreenItems.size() - 1) {
                loadLastScreen();
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == introListScreenItems.size() - 1) {
                    loadLastScreen();
                } else if (tabLayout.getVisibility() == View.INVISIBLE) {
                    loadBefore();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnRegister.setOnClickListener(view -> {
            if (isLastScreen) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);

                // Save in storage so we know the user already went through this
                savePreferencesData();
                finish();
            }
        });

        textLogIn.setOnClickListener(view -> {
            if (isLastScreen) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

                // Save in storage so we know the user already went through this
                savePreferencesData();
                finish();
            }
        });
    }

    private boolean restorePreferenceData() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(AppUtil.MY_PREFS, MODE_PRIVATE);
        return preferences.getBoolean(AppUtil.INTRO_OPENED, false);
    }

    private void savePreferencesData() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences(AppUtil.MY_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(AppUtil.INTRO_OPENED, true);
        editor.commit();
    }

    private void loadBefore() {
        btnNext.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.INVISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        textLogIn.setVisibility(View.INVISIBLE);
        textCopyright.setVisibility(View.INVISIBLE);

        isLastScreen = false;
    }

    private void loadLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnRegister.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.INVISIBLE);
        textLogIn.setVisibility(View.VISIBLE);
        textCopyright.setVisibility(View.VISIBLE);

        btnRegister.setAnimation(btnAnimation);
        textLogIn.setAnimation(btnAnimation);

        isLastScreen = true;
    }
}
