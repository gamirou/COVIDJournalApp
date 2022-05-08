package com.gamiro.covidjournal.repositories;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.gamiro.covidjournal.HomeActivity;
import com.gamiro.covidjournal.fragments.HomeFragment;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.interfaces.CountryApiService;
import com.gamiro.covidjournal.models.countries.CountryStatistics;
import com.gamiro.covidjournal.models.news.NewsResponse;
import com.gamiro.covidjournal.models.user.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoronaRepository {

    private static final String TAG = "CoronaRepository";
    private static final String BASE_URL = "https://coronavirus-19-api.herokuapp.com/";

    private MutableLiveData<HashMap<String, CountryStatistics>> coronaData;
    private MutableLiveData<CountryStatistics> countryData;

    public CoronaRepository() {}

    /**
     * CORONA DATA
     */
    public MutableLiveData<HashMap<String, CountryStatistics>> getCoronaData() {
        if (coronaData == null) {
            coronaData = new MutableLiveData<>();
        }

        loadCoronaData(null);

        return coronaData;
    }

    private void loadCoronaData(String country) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CountryApiService countryApiService = retrofit.create(CountryApiService.class);

        Call<List<CountryStatistics>> call = countryApiService.getCountriesData();
        call.enqueue(new Callback<List<CountryStatistics>>() {
            @Override
            public void onResponse(Call<List<CountryStatistics>> call, Response<List<CountryStatistics>> response) {
                if (!response.isSuccessful()) {
                    System.out.println("Code: " + response.code());
                    return;
                }

                if (response.body() != null) {
                    List<CountryStatistics> countryResponse = response.body();
                    if (country == null) {
                        HashMap<String, CountryStatistics> result = new HashMap<>();
                        for (CountryStatistics country : countryResponse) {
                            result.put(country.getCountry(), country);
                        }
                        coronaData.postValue(result);
                    } else {
                        for (CountryStatistics statistics : countryResponse) {
                            if (statistics.getCountry().equals(country)) {
                                countryData.postValue(statistics);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<CountryStatistics>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }
    public LiveData<CountryStatistics> getCoronaDataOwnCountry(String country) {
        if (countryData == null) {
            countryData = new MutableLiveData<>();
        }


        loadCoronaData(AppUtil.getApiCountryFromPicker(country));
        return countryData;
    }
}
