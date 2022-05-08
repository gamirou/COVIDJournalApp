package com.gamiro.covidjournal.fragments.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.viewmodels.DateTimeViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProvider;

public class NumberPickerFragment extends AppCompatDialogFragment implements NumberPicker.OnValueChangeListener {

    private static final String TAG = "NumberPickerFragment";
    private String numberKey;

    // Min and max numbers
    private int minNumber;
    private int maxNumber;

    // Floating point for temperature
    private int minFloatingPoint;
    private int maxFloatingPoint;

    // number picker
    private NumberPicker numberPicker;
    private NumberPicker numberPickerDecimal;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String dialogTitle = "Choose a value: ";
        String currentValue = "";

        if (getArguments() != null) {
            NumberPickerFragmentArgs args = NumberPickerFragmentArgs.fromBundle(getArguments());
            numberKey = args.getNumberKey();
            minNumber = args.getMinNumber();
            maxNumber = args.getMaxNumber();
            dialogTitle = args.getDialogTitle();
            minFloatingPoint = args.getMinFloatingPoint();
            maxFloatingPoint = args.getMaxFloatingPoint();
            currentValue = args.getCurrentValue();
        }

        Log.i(TAG, "onCreateDialog: minNumber = " + minNumber);
        Log.i(TAG, "onCreateDialog: numberKey = " + numberKey);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(dialogTitle);
        builder.setMessage("Choose a number :");

        // Initialise view
        // floating point -> 2 number pickers
        View dialogView = null;
        if (minFloatingPoint == -1) {
            numberPicker = new NumberPicker(getActivity());
            if (currentValue != null) {
                numberPicker.setValue(Integer.parseInt(currentValue));
            }
            builder.setPositiveButton("OK", (dialog, which) -> setNumber(numberPicker.getValue(), 0));
        } else {
            dialogView = getLayoutInflater().inflate(R.layout.number_picker, null);
            numberPicker = dialogView.findViewById(R.id.picker_whole);
            numberPickerDecimal = dialogView.findViewById(R.id.picker_decimal);

            if (currentValue != null) {
                double temp = Double.parseDouble(currentValue);
                int whole = (int) temp;
                numberPicker.setValue(whole);
                numberPickerDecimal.setValue((int) ((temp - whole) * 10));
            }

            // Set 0 -> 9
            numberPickerDecimal.setMinValue(minFloatingPoint);
            numberPickerDecimal.setMaxValue(9);

            numberPicker.setOnValueChangedListener(this);
            builder.setPositiveButton("OK", (dialog, which) -> setNumber(numberPicker.getValue(), numberPickerDecimal.getValue()));
        }

        numberPicker.setMinValue(minNumber);
        numberPicker.setMaxValue(maxNumber);

        builder.setNegativeButton("CANCEL", (dialog, which) -> getActivity().onBackPressed());

        builder.setView(dialogView == null ? numberPicker : dialogView);
        return builder.create();
    }

    private void setNumber(int number, int decimal) {
        DateTimeViewModel viewModel = new ViewModelProvider(requireActivity()).get(DateTimeViewModel.class);
        if (numberKey != null) {
            double result = number + ((double) decimal) / 10;
            viewModel.setSelectedNumber(numberKey, result);
        }
    }

    @Override
    public void onValueChange(NumberPicker np, int i, int i1) {
        Log.i(TAG, "onValueChange: i = " + i + "; i1 = " + i1);
        if (i1 <= minNumber) {
            numberPickerDecimal.setMinValue(minFloatingPoint);
            numberPickerDecimal.setMaxValue(9);
        } else if (i1 >= maxNumber) {
            numberPickerDecimal.setMinValue(0);
            numberPickerDecimal.setMaxValue(maxFloatingPoint);
        } else {
            numberPickerDecimal.setMinValue(0);
            numberPickerDecimal.setMaxValue(9);
        }
    }
}
