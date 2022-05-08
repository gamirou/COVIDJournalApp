package com.gamiro.covidjournal.repositories;

import android.app.Application;
import android.util.Log;

import com.gamiro.covidjournal.room.CityDao;
import com.gamiro.covidjournal.room.CityDatabase;
import com.gamiro.covidjournal.room.CityModel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class CityRepository {
    private static final String TAG = "CityRepository";
    private CityDao cityDao;
    private LiveData<List<CityModel>> allCities;
    private LiveData<CityModel> currentCountry;

    public CityRepository(Application application) {
        CityDatabase database = CityDatabase.getInstance(application);
        Log.i(TAG, "CityRepository: database exists? " + (database != null));
        cityDao = database.cityDao();
        allCities = cityDao.getAll();
    }

    public LiveData<CityModel> getCitiesByCountry(String country) {
        if (currentCountry == null) {
            currentCountry = new MutableLiveData<>();
        }

        currentCountry = cityDao.getCitiesByCountry(country);
        return currentCountry;
    }

    public LiveData<List<CityModel>> getAllCities() {
        return allCities;
    }
}
