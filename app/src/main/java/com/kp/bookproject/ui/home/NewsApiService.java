package com.kp.bookproject.ui.home;


import com.kp.bookproject.Entity.NewsApiAnswer;



import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {
        @GET("v2/sources")
    Call<NewsApiAnswer> getNews(@Query("apiKey") String key,@Query("category") String category);
}
