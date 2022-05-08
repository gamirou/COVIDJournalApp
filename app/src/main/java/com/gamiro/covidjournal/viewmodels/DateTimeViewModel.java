package com.gamiro.covidjournal.viewmodels;

import android.util.Log;

import java.util.HashMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DateTimeViewModel extends ViewModel {

    private static final String TAG = "DateTimeViewModel";

    private MutableLiveData<String> selectedTime = new MutableLiveData<>();
    private MutableLiveData<HashMap<String, String>> selectedDates = new MutableLiveData<>();
    private MutableLiveData<HashMap<String, Double>> selectedNumbers = new MutableLiveData<>();

    /**
     * param key: key of the date inside hashmap;
     * other params - date: int year, int month, int day
     */
    public LiveData<HashMap<String, String>> getSelectedDate() {
        return selectedDates;
    }
    public void setSelectedDate(String key, int year, int month, int day) {
        HashMap<String, String> dates = selectedDates.getValue();

        String y = Integer.toString(year);
        String m = month < 10 ? "0" + month : Integer.toString(month);
        String d = day < 10 ? "0" + day : Integer.toString(day);

        if (dates == null) {
            dates = new HashMap<>();
        }

        dates.put(key, d + "/" + m + "/" + y);
        selectedDates.setValue(dates);
    }
    public void setSelectedDate(String key) {
        HashMap<String, String> dates = selectedDates.getValue();

        if (dates == null) {
            dates = new HashMap<>();
        }

        dates.put(key, "");
        selectedDates.setValue(dates);
    }
    public void setSelectedDate(String key, String date) {
        HashMap<String, String> dates = selectedDates.getValue();

        if (dates == null) {
            dates = new HashMap<>();
        }

        Log.d(TAG, "setSelectedDates: " + date);
        Log.d(TAG, "setSelectedDates: " + key);
        dates.put(key, date);
        selectedDates.setValue(dates);
    }
    private void setSelectedDate() {selectedDates.setValue(new HashMap<>());}

    // Time!!
    public LiveData<String> getSelectedTime() {
        return selectedTime;
    }
    public void setSelectedTime(int hour, int minute) {
        String h = hour < 10 ? "0" + hour : Integer.toString(hour);
        String m = minute < 10 ? "0" + minute : Integer.toString(minute);
        this.selectedTime.postValue(h + ":" + m);
    }
    public void setSelectedTime() {selectedTime.setValue("");}

    // Numbers
    public LiveData<HashMap<String, Double>> getSelectedNumbers() {
        return selectedNumbers;
    }
    public void setSelectedNumber(String key, double number) {
        HashMap<String, Double> numbers = selectedNumbers.getValue();

        if (numbers == null) {
            numbers = new HashMap<>();
        }

        numbers.put(key, number);
        selectedNumbers.setValue(numbers);
    }
    public void setSelectedNumber(String key) {
        HashMap<String, Double> numbers = selectedNumbers.getValue();

        if (numbers == null) {
            numbers = new HashMap<>();
        }

        numbers.put(key, -1.0);
        selectedNumbers.setValue(numbers);
    }
    private void setSelectedNumber() {selectedNumbers.setValue(new HashMap<>());}

    // Remove date and time
    public void reset() {
        setSelectedTime();
        setSelectedDate();
        setSelectedNumber();
    }
}
