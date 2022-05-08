package com.gamiro.covidjournal.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.viewmodels.HomeActivityViewModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "SettingsFragment";

    private SwitchPreferenceCompat prefNotifications;
    private Preference prefEditProfile, prefResetPassword, prefTerms, prefPrivacy, prefVerifyEmail;
    private HomeActivityViewModel viewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);

        viewModel.isEmailVerified().observe(getViewLifecycleOwner(), aBoolean -> {
            prefVerifyEmail.setVisible(!aBoolean);
        });
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        // Fetch all settings
        prefNotifications = findPreference(getString(R.string.settings_key_notifications));
        prefEditProfile = findPreference(getString(R.string.settings_key_edit_profile));
        prefResetPassword = findPreference(getString(R.string.settings_key_reset_password));
        prefVerifyEmail = findPreference(getString(R.string.settings_key_verify_email));
        prefTerms = findPreference(getString(R.string.settings_key_terms));
        prefPrivacy = findPreference(getString(R.string.settings_key_privacy));

        // Notifications
        prefNotifications.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean enabled = (boolean) newValue;
            prefNotifications.setSummary(getString(
                    enabled ? R.string.state_on : R.string.state_off
            ));
            return true;
        });

        // Button clicks
        prefEditProfile.setOnPreferenceClickListener(preference -> {
            if (isValidDestination()) {
                Navigation.findNavController(requireView()).navigate(R.id.homeFragment);
                Navigation.findNavController(requireView()).navigate(R.id.profileFragment);

                return true;
            }

            return false;
        });

        prefResetPassword.setOnPreferenceClickListener(preference -> {
            if (isValidDestination()) {
                Navigation.findNavController(requireView()).navigate(R.id.action_settingsFragment_to_resetPasswordFragment);
                return true;
            }

            return false;
        });

        prefVerifyEmail.setOnPreferenceClickListener(preference -> {
            if (isValidDestination()) {
                Navigation.findNavController(requireView()).navigate(R.id.action_settingsFragment_to_emailVerificationFragment);
                return true;
            }

            return false;
        });

        // Links to website
        prefTerms.setOnPreferenceClickListener(preference -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppUtil.LINK_TERMS));
            getContext().startActivity(browserIntent);
            return false;
        });

        prefPrivacy.setOnPreferenceClickListener(preference -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppUtil.LINK_PRIVACY));
            getContext().startActivity(browserIntent);
            return true;
        });
    }

    private boolean isValidDestination() {
        return Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.settingsFragment;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.i(TAG, "onSharedPreferenceChanged: true");
    }
}