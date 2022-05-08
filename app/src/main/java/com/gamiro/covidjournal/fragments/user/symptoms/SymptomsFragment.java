package com.gamiro.covidjournal.fragments.user.symptoms;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.models.user.UserSymptoms;
import com.gamiro.covidjournal.viewmodels.DateTimeViewModel;
import com.gamiro.covidjournal.viewmodels.SymptomsViewModel;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

public class SymptomsFragment extends Fragment {

    private static final String TAG = "SymptomsFragment";

    // Layout elements
    private EditText editTemperature;
    private Button btnNext;
    private HashMap<String, Spinner> spinnerSymptoms = new HashMap<>();

    // View model and other data
    private SymptomsViewModel viewModel;
    private DateTimeViewModel numberViewModel;
    private boolean isLocalAvailable = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_symptoms, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Input
        editTemperature = getView().findViewById(R.id.edit_temperature);
        spinnerSymptoms.put("ache", getView().findViewById(R.id.spinner_ache));
        spinnerSymptoms.put("chills", getView().findViewById(R.id.spinner_chills));
        spinnerSymptoms.put("cough", getView().findViewById(R.id.spinner_cough));
        spinnerSymptoms.put("fatigue", getView().findViewById(R.id.spinner_fatigue));
        spinnerSymptoms.put("fever", getView().findViewById(R.id.spinner_fever));
        spinnerSymptoms.put("headache", getView().findViewById(R.id.spinner_headache));
        spinnerSymptoms.put("lackOfTaste", getView().findViewById(R.id.spinner_lack_of_taste));
        spinnerSymptoms.put("temperatureMode", getView().findViewById(R.id.spinner_temperature));
        spinnerSymptoms.put("soreThroat", getView().findViewById(R.id.spinner_sore_throat));
        spinnerSymptoms.put("vomit", getView().findViewById(R.id.spinner_vomit));

        // Next button
        btnNext = getView().findViewById(R.id.btn_symptoms_next);

        editTemperature.setInputType(InputType.TYPE_NULL);
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
        ArrayAdapter<CharSequence> temperatureAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.temperature_options,
                android.R.layout.simple_spinner_item
        );
        temperatureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        editTemperature.setOnClickListener(view -> {
            Log.i(TAG, "temperature clicked");
            if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.symptomsFragment) {
                SymptomsFragmentDirections.ActionSymptomsFragmentToNumberPickerFragment action =
                        SymptomsFragmentDirections.actionSymptomsFragmentToNumberPickerFragment();
                action.setNumberKey(AppUtil.TEMPERATURE_INDEX);
                // celsius: 35.5 - 39.5
                // > 38 - symptom true
                // fahrenheit: 95.8 - 103.2
                // > 100.4 - symptoms true
                int minNumber = 35;
                int maxNumber = 39;
                int minFP = 5;
                int maxFP = 5;

                if (spinnerSymptoms.get("temperatureMode").getSelectedItemPosition() == 1) {
                    minNumber = 95;
                    maxNumber = 103;
                    minFP = 8;
                    maxFP = 2;
                }

                action.setMinNumber(minNumber);
                action.setMaxNumber(maxNumber);
                action.setMinFloatingPoint(minFP);
                action.setMaxFloatingPoint(maxFP);
                action.setDialogTitle(getString(R.string.symptoms_temperature));

                String temp = editTemperature.getText().toString();
                if (!temp.isEmpty()) {
                    action.setCurrentValue(temp);
                }

                Navigation.findNavController(requireView()).navigate(action);
            }
        });

        // Set the adapter
        for (String key: spinnerSymptoms.keySet()) {
            Spinner spinner = spinnerSymptoms.get(key);

            if (key.equals("temperatureMode")) {
                spinner.setAdapter(temperatureAdapter);
            } else {
                spinner.setAdapter(spinnerAdapter);
            }
        }

        viewModel = new ViewModelProvider(requireActivity()).get(SymptomsViewModel.class);
        numberViewModel = new ViewModelProvider(requireActivity()).get(DateTimeViewModel.class);

        // Temperature reading
        numberViewModel.getSelectedNumbers().observe(getViewLifecycleOwner(), numbers -> {
            if (numbers.containsKey(AppUtil.TEMPERATURE_INDEX)) {
                editTemperature.setText(String.valueOf(numbers.get(AppUtil.TEMPERATURE_INDEX)));
            }
        });

        // Fetch data
        viewModel.getUserSymptoms().observe(getViewLifecycleOwner(), userSymptoms -> {
            if (userSymptoms != null && !isLocalAvailable) {
                spinnerSymptoms.get("ache").setSelection(getSelectionFromBoolean(userSymptoms.isAche()));
                spinnerSymptoms.get("chills").setSelection(getSelectionFromBoolean(userSymptoms.isChills()));
                spinnerSymptoms.get("cough").setSelection(getSelectionFromBoolean(userSymptoms.isCough()));
                spinnerSymptoms.get("fatigue").setSelection(getSelectionFromBoolean(userSymptoms.isFatigue()));
                spinnerSymptoms.get("fever").setSelection(getSelectionFromBoolean(userSymptoms.isFever()));
                spinnerSymptoms.get("headache").setSelection(getSelectionFromBoolean(userSymptoms.isHeadache()));
                spinnerSymptoms.get("lackOfTaste").setSelection(getSelectionFromBoolean(userSymptoms.isLackOfTaste()));
                spinnerSymptoms.get("soreThroat").setSelection(getSelectionFromBoolean(userSymptoms.isSoreThroat()));
                spinnerSymptoms.get("vomit").setSelection(getSelectionFromBoolean(userSymptoms.isVomit()));

                // Temperature mode
                int posTempMode = temperatureAdapter.getPosition(userSymptoms.getTemperatureMode());
                spinnerSymptoms.get("temperatureMode").setSelection(posTempMode);
            }
        });
        viewModel.getLocalUserSymptoms().observe(getViewLifecycleOwner(), userSymptoms -> {
            isLocalAvailable = userSymptoms != null;
            if (isLocalAvailable) {
                spinnerSymptoms.get("ache").setSelection(getSelectionFromBoolean(userSymptoms.isAche()));
                spinnerSymptoms.get("chills").setSelection(getSelectionFromBoolean(userSymptoms.isChills()));
                spinnerSymptoms.get("cough").setSelection(getSelectionFromBoolean(userSymptoms.isCough()));
                spinnerSymptoms.get("fatigue").setSelection(getSelectionFromBoolean(userSymptoms.isFatigue()));
                spinnerSymptoms.get("fever").setSelection(getSelectionFromBoolean(userSymptoms.isFever()));
                spinnerSymptoms.get("headache").setSelection(getSelectionFromBoolean(userSymptoms.isHeadache()));
                spinnerSymptoms.get("lackOfTaste").setSelection(getSelectionFromBoolean(userSymptoms.isLackOfTaste()));
                spinnerSymptoms.get("soreThroat").setSelection(getSelectionFromBoolean(userSymptoms.isSoreThroat()));
                spinnerSymptoms.get("vomit").setSelection(getSelectionFromBoolean(userSymptoms.isVomit()));

                // Temperature mode
                int posTempMode = temperatureAdapter.getPosition(userSymptoms.getTemperatureMode());
                spinnerSymptoms.get("temperatureMode").setSelection(posTempMode);
            }
        });

        spinnerSymptoms.get("temperatureMode").setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String temperature = editTemperature.getText().toString();
                if (!temperature.isEmpty()) {
                    double temperatureDouble = Double.parseDouble(temperature);
                    if (i == 0) {
                        if (temperatureDouble > 40) {
                            editTemperature.setText(String.valueOf(AppUtil.fahrenheitToCelsius(temperatureDouble)));
                        }
                    } else {
                        if (temperatureDouble < 90) {
                            editTemperature.setText(String.valueOf(AppUtil.celsiusToFahrenheit(temperatureDouble)));
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Go next
        btnNext.setOnClickListener(view -> {
            int ache = spinnerSymptoms.get("ache").getSelectedItemPosition();
            int chills = spinnerSymptoms.get("chills").getSelectedItemPosition();
            int cough = spinnerSymptoms.get("cough").getSelectedItemPosition();
            int fatigue = spinnerSymptoms.get("fatigue").getSelectedItemPosition();
            int fever = spinnerSymptoms.get("fever").getSelectedItemPosition();
            int headache = spinnerSymptoms.get("headache").getSelectedItemPosition();
            int lackOfTaste = spinnerSymptoms.get("lackOfTaste").getSelectedItemPosition();
            int soreThroat = spinnerSymptoms.get("soreThroat").getSelectedItemPosition();
            int vomit = spinnerSymptoms.get("vomit").getSelectedItemPosition();

            String temperature = editTemperature.getText().toString().trim();
            String temperatureMode = spinnerSymptoms.get("temperatureMode").getSelectedItem().toString();

            UserSymptoms symptoms = new UserSymptoms(
                    getBooleanFromSelection(ache), getBooleanFromSelection(chills),
                    getBooleanFromSelection(cough), getBooleanFromSelection(fatigue),
                    getBooleanFromSelection(fever), getBooleanFromSelection(headache),
                    getBooleanFromSelection(lackOfTaste), temperature, temperatureMode,
                    getBooleanFromSelection(soreThroat), getBooleanFromSelection(vomit)
            );
            viewModel.updateLocalUserSymptoms(symptoms);

            if (Navigation.findNavController(requireView()).getCurrentDestination().getId() == R.id.symptomsFragment) {
                Navigation.findNavController(requireView()).navigate(R.id.action_symptomsFragment_to_severeSymptomsFragment);
            }
        });
    }

    // TODO: Move this to view model
    private int getSelectionFromBoolean(boolean b) {
        return !b ? 0 : 1;
    }

    private boolean getBooleanFromSelection(int position) {
        return position == 1;
    }
}