package com.gamiro.covidjournal.fragments.dialog;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import com.gamiro.covidjournal.viewmodels.DateTimeViewModel;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProvider;

public class DatePickerFragment extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "DatePickerFragment";
    private String dateKey;

    // Min and max dates
    private String minDate;
    private String maxDate;

    @NonNull
    @Override
    public DatePickerDialog onCreateDialog(Bundle savedInstanceState) {
        // Set the current date as the default date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if (getArguments() != null) {
            //dateKey = Date.fromBundle(getArguments()).getDateKey();
            DatePickerFragmentArgs args = DatePickerFragmentArgs.fromBundle(getArguments());
            dateKey = args.getDateKey();
            minDate = args.getMinDate();
            maxDate = args.getMaxDate();

            Log.i(TAG, "minDate = " + minDate);
            Log.i(TAG, "maxDate = " + maxDate);
        }

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), DatePickerFragment.this, year, month, day);
        // Return a new instance of DatePickerDialog

        Calendar minCalendar = Calendar.getInstance();
        if (minDate != null) {
            if (!minDate.isEmpty()) {
                Log.i(TAG, "minDate = today ? " + minDate.equals("today"));
                if (!minDate.equals("today")) {
                    int[] mintIntDate = getIntDateFromString(minDate);
                    minCalendar.set(Calendar.DAY_OF_MONTH, mintIntDate[0]);
                    minCalendar.set(Calendar.MONTH, mintIntDate[1] - 1);
                    minCalendar.set(Calendar.YEAR, mintIntDate[2]);
                }
                dialog.getDatePicker().setMinDate(minCalendar.getTimeInMillis());
            }
        }

        Calendar maxCalendar = Calendar.getInstance();
        if (maxDate != null) {
            if (!maxDate.isEmpty()) {
                if (maxDate.equals("18")) {
                    maxCalendar.add(Calendar.YEAR, -18);
                } else if (!maxDate.equals("today")) {
                    int[] maxIntDate = getIntDateFromString(maxDate);
                    maxCalendar.set(Calendar.DAY_OF_MONTH, maxIntDate[0]);
                    maxCalendar.set(Calendar.MONTH, maxIntDate[1] - 1);
                    maxCalendar.set(Calendar.YEAR, maxIntDate[2]);
                }
                // This gets called when today is given
                dialog.getDatePicker().setMaxDate(maxCalendar.getTimeInMillis());
            }
        }

        return dialog;
    }

    // called when a date has been selected
    public void onDateSet(DatePicker view, int year, int month, int day) {
        DateTimeViewModel viewModel = new ViewModelProvider(requireActivity()).get(DateTimeViewModel.class);
        if (dateKey != null) {
            viewModel.setSelectedDate(dateKey, year, month + 1, day);
        }
    }

    private int[] getIntDateFromString(String date) {
        int[] result = new int[3];

        String[] dateSplit = date.split("/");
        for (int i = 0; i < result.length; i++) {
            result[i] = Integer.parseInt(dateSplit[i]);
        }

        return result;
    }
}
