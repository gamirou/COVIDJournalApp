package com.gamiro.covidjournal.viewmodels;

import android.app.Application;

import com.gamiro.covidjournal.repositories.CityRepository;
import com.gamiro.covidjournal.room.CityModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

public class CityViewModel extends AndroidViewModel {
    private CityRepository repository;
    private LiveData<List<CityModel>> allCities;
    private LiveData<CityModel> currentCities;

    private MutableLiveData<String> userCountry = new MutableLiveData<>();

    public CityViewModel(@NonNull Application application) {
        super(application);
        repository = new CityRepository(application);
        allCities = repository.getAllCities();
    }

    public void setUserCountry(String country) {
        userCountry.setValue(country);
    }

    public LiveData<CityModel> getCitiesByCountry() {
        currentCities = Transformations.switchMap(userCountry, country -> repository.getCitiesByCountry(country));
        return currentCities;
    }

    public LiveData<List<CityModel>> getCities() {
        return allCities;
    }
}
