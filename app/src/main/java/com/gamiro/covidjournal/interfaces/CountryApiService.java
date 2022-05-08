package com.gamiro.covidjournal.interfaces;

import com.gamiro.covidjournal.models.countries.CountryStatistics;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CountryApiService {

    @GET("countries/")
    Call<List<CountryStatistics>> getCountriesData();

}
