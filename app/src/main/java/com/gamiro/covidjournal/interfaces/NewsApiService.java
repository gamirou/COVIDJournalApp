package com.gamiro.covidjournal.interfaces;

import com.gamiro.covidjournal.models.news.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface NewsApiService {
    // base url
    // https://api.smartable.ai/coronavirus/news/{location}
    @Headers("Subscription-Key: 974ce251aa4a4400b2813dab35f2710c")
    @GET("coronavirus/news/{location}")
    Call<NewsResponse> getArticles(@Path("location") String location);

}