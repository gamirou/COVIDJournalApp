package com.gamiro.covidjournal.repositories;

import android.util.Log;

import com.gamiro.covidjournal.interfaces.NewsApiService;
import com.gamiro.covidjournal.models.news.News;
import com.gamiro.covidjournal.models.news.NewsResponse;

import java.util.HashMap;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsRepository {

    private static final String TAG = "NewsRepository";
    private HashMap<String, String> countriesCodes;
    private static final String BASE_URL = "https://api.smartable.ai/";
    //private String countryCode = "global";
    private MutableLiveData<List<News>> newsForCountry;

    public NewsRepository() {
        countriesCodes = new HashMap<>();
        countriesCodes.put("UK", "GB");
        countriesCodes.put("USA", "US");
        countriesCodes.put("Australia", "AU");
        countriesCodes.put("Switzerland", "CH");
        countriesCodes.put("China", "CN");
        countriesCodes.put("Germany", "DE");
        countriesCodes.put("Spain", "ES");
        countriesCodes.put("South Korea", "KR");
        countriesCodes.put("Japan", "JP");
        countriesCodes.put("Sweden", "SE");
        countriesCodes.put("Netherlands", "NL");
        countriesCodes.put("Italy", "IT");
        countriesCodes.put("France", "FR");
        countriesCodes.put("Singapore", "SG");
        // For others use global
    }

    public MutableLiveData<List<News>> getNewsForCountry(String country) {
        if (newsForCountry == null) {
            newsForCountry = new MutableLiveData<>();
        }

        loadNewsForCountry(country);
        return newsForCountry;
    }

    public void loadNewsForCountry(String country) {
        String countryCode = "";
        if (countriesCodes.containsKey(country)) {
            countryCode = countriesCodes.get(country);
        } else {
            if (country.equals("United Kingdom")) {
                countryCode = "GB";
            } else {
                countryCode = "global";
            }
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewsApiService newsApiService = retrofit.create(NewsApiService.class);

        Call<NewsResponse> call = newsApiService.getArticles(countryCode);
        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                Log.d(TAG, "Response is successful? " + response.isSuccessful());
                if (!response.isSuccessful()) {
                    System.out.println("Code: " + response.code());
                    return;
                }

                Log.i(TAG, "Response raw: " + response.body().getNews().get(0).getTags());

                NewsResponse newsResponse = response.body();
                newsForCountry.postValue(newsResponse.getNews());
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: failure", t);
            }
        });
    }
}
