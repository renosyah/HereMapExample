package com.syahputrareno975.heremapexample.service;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RetrofitService {
    // add more end point to access
    // @Headers("Authorization:Bearer ")
    // @POST("graphql")
    // public void do(@Body String json) : Observable<ModelResponse>


    public static final String baseURL = "https://warm-wave-76942.herokuapp.com/";

    public static RetrofitService create()  {
       Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseURL)
                .build();

        return retrofit.create(RetrofitService.class);
    }
}
