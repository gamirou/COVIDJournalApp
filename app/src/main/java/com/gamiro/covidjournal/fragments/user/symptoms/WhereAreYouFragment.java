package com.gamiro.covidjournal.fragments.user.symptoms;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.models.user.UserSymptoms;
import com.gamiro.covidjournal.viewmodels.HomeActivityViewModel;
import com.gamiro.covidjournal.viewmodels.SymptomsViewModel;
import com.google.android.gms.common.util.ArrayUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WhereAreYouFragment extends Fragment {

    private static final int[] TEXT_OPTIONS_ID = {
            R.id.tv_where_option1, R.id.tv_where_option2, R.id.tv_where_option3, R.id.tv_where_option4, R.id.tv_where_option5
    };
    private static final String TAG = "WhereAreYouFragment";

    // Layout
    private HashMap<Integer, TextView> textOptions = new HashMap<>();
    private Button btnSave;

    // Data
    private SymptomsViewModel symptomsViewModel;
    private HomeActivityViewModel homeViewModel;
    private boolean isLocalAvailable = false;
    private int selectedId = TEXT_OPTIONS_ID[0];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_where_are_you, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        for (int id : TEXT_OPTIONS_ID) {
            textOptions.put(id, getView().findViewById(id));
            textOptions.get(id).setOnClickListener(v -> {
                v.setBackgroundColor(getResources().getColor(R.color.where_are_you_item_selected));
                textOptions.get(selectedId).setBackgroundColor(getResources().getColor(
                        R.color.where_are_you_item_not_selected
                ));
                selectedId = v.getId();
                symptomsViewModel.updateLocalUserSymptoms(((TextView)v).getText().toString());
            });
        }

        btnSave = getView().findViewById(R.id.btn_symptoms_save);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);
        symptomsViewModel = new ViewModelProvider(requireActivity()).get(SymptomsViewModel.class);

        // User symptoms are from database or local live data
        symptomsViewModel.getUserSymptoms().observe(getViewLifecycleOwner(), symptoms -> {
            if (symptoms != null && !isLocalAvailable) {
                if (symptoms.getWhereAreYou() != null) {
                    for (int id: textOptions.keySet()) {
                        TextView text = textOptions.get(id);
                        if (text.getText().toString().equals(symptoms.getWhereAreYou())) {
                            textOptions.get(selectedId).setBackgroundColor(
                                    getResources().getColor(R.color.where_are_you_item_not_selected)
                            );
                            text.setBackgroundColor(getResources().getColor(R.color.where_are_you_item_selected));
                            selectedId = id;
                            break;
                        }
                    }
                }
            }
        });
        symptomsViewModel.getLocalUserSymptoms().observe(getViewLifecycleOwner(), symptoms -> {
            isLocalAvailable = symptoms.getWhereAreYou() != null;
            if (isLocalAvailable) {
                for (int id: textOptions.keySet()) {
                    TextView text = textOptions.get(id);
                    if (text.getText().toString().equals(symptoms.getWhereAreYou())) {
                        textOptions.get(selectedId).setBackgroundColor(
                                getResources().getColor(R.color.where_are_you_item_not_selected)
                        );
                        text.setBackgroundColor(getResources().getColor(R.color.where_are_you_item_selected));
                        selectedId = id;
                        break;
                    }
                }

            }
        });

        // Save symptoms
        btnSave.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle("Save your symptoms?");
            builder.setMessage("Are you sure your details are right?");
            builder.setPositiveButton("Yes", (dialog, id) -> {
                symptomsViewModel.saveUserSymptoms();

                Toast.makeText(requireContext(), "Your symptoms are saved", Toast.LENGTH_SHORT).show();

                // 0 and 3 are has corona

                int index = AppUtil.indexOf(TEXT_OPTIONS_ID, selectedId);
                Log.i(TAG, "btnSave.onClick: index = " + index);

                // TODO: Index = -1
                Boolean has = null;
                if (index == 0 || index == 3) {
                    has = true;
                }

                homeViewModel.setUserCoronaState(true, has, null);

                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true)
                        .build();
                Navigation.findNavController(requireView()).navigate(R.id.homeFragment);
            });

            builder.setNegativeButton("No", (dialog, id) -> {});
            builder.show();
        });
    }
}