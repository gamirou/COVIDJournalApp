package com.gamiro.covidjournal.fragments.dialog;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import com.gamiro.covidjournal.viewmodels.DateTimeViewModel;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProvider;

public class TimePickerFragment extends AppCompatDialogFragment implements TimePickerDialog.OnTimeSetListener {

    @NonNull
    @Override
    public TimePickerDialog onCreateDialog(Bundle savedInstanceState) {
        // Set the current date as the default date
        final Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);

        // TODO: Add on settings 24 hour view, date settings etc
        return new TimePickerDialog(getActivity(), TimePickerFragment.this, hours, minutes, true);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        DateTimeViewModel viewModel = new ViewModelProvider(requireActivity()).get(DateTimeViewModel.class);
        viewModel.setSelectedTime(hour, minute);
    }
}
