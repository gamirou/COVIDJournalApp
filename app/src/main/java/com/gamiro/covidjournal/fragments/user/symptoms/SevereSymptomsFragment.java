package com.gamiro.covidjournal.fragments.user.symptoms;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.viewmodels.HomeActivityViewModel;
import com.gamiro.covidjournal.viewmodels.SymptomsViewModel;

import java.util.HashMap;

public class SevereSymptomsFragment extends Fragment {

    // Layout elements
    private HashMap<String, Spinner> spinnerSevereSymptons = new HashMap<>();
    private Button btnNext;

    // View model
    private SymptomsViewModel viewModel;
    private boolean isLocalAvailable = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_severe_symptoms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnNext = getView().findViewById(R.id.btn_symptoms_next);

        // Spinners
        spinnerSevereSymptons.put("breathing", getView().findViewById(R.id.spinner_breathing));
        spinnerSevereSymptons.put("chestPain", getView().findViewById(R.id.spinner_chest_pain));
        spinnerSevereSymptons.put("awake", getView().findViewById(R.id.spinner_awake));
        spinnerSevereSymptons.put("lips", getView().findViewById(R.id.spinner_lips));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Options for spinners
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.input_options,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the adapter
        for (String key: spinnerSevereSymptons.keySet()) {
            Spinner spinner = spinnerSevereSymptons.get(key);
            spinner.setAdapter(spinnerAdapter);
        }

        viewModel = new ViewModelProvider(requireActivity()).get(SymptomsViewModel.class);

        // Fetch data
        viewModel.getUserSymptoms().observe(getViewLifecycleOwner(), userSymptoms -> {
            if (userSymptoms != null && !isLocalAvailable) {
                spinnerSevereSymptons.get("breathing").setSelection(getSelectionFromBoolean(userSymptoms.isBreathing()));
                spinnerSevereSymptons.get("chestPain").setSelection(getSelectionFromBoolean(userSymptoms.isChestPain()));
                spinnerSevereSymptons.get("awake").setSelection(getSelectionFromBoolean(userSymptoms.isAwake()));
                spinnerSevereSymptons.get("lips").setSelection(getSelectionFromBoolean(userSymptoms.isLips()));
            }
        });
        viewModel.getLocalUserSymptoms().observe(getViewLifecycleOwner(), userSymptoms -> {
            isLocalAvailable = userSymptoms != null;
            if (isLocalAvailable) {
                spinnerSevereSymptons.get("breathing").setSelection(getSelectionFromBoolean(userSymptoms.isBreathing()));
                spinnerSevereSymptons.get("chestPain").setSelection(getSelectionFromBoolean(userSymptoms.isChestPain()));
                spinnerSevereSymptons.get("awake").setSelection(getSelectionFromBoolean(userSymptoms.isAwake()));
                spinnerSevereSymptons.get("lips").setSelection(getSelectionFromBoolean(userSymptoms.isLips()));
            }
        });

        btnNext.setOnClickListener(view -> {
            int breathing = spinnerSevereSymptons.get("breathing").getSelectedItemPosition();
            int chestPain = spinnerSevereSymptons.get("breathing").getSelectedItemPosition();
            int awake = spinnerSevereSymptons.get("breathing").getSelectedItemPosition();
            int lips = spinnerSevereSymptons.get("breathing").getSelectedItemPosition();

            if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.severeSymptomsFragment) {
                viewModel.updateLocalUserSymptoms(
                        getBooleanFromSelection(breathing), getBooleanFromSelection(chestPain),
                        getBooleanFromSelection(awake), getBooleanFromSelection(lips)
                );
                Navigation.findNavController(requireView()).navigate(R.id.action_severeSymptomsFragment_to_whereAreYouFragment);
            }
        });
    }

    // Utility
    private int getSelectionFromBoolean(boolean b) {
        return !b ? 0 : 1;
    }
    private boolean getBooleanFromSelection(int position) {
        return position == 1;
    }
}