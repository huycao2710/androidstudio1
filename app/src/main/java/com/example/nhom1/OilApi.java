package com.example.nhom1;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OilApi {

    @GET("/query")
    Call<JsonObject> getWTIData(
            @Query("function") String function,
            @Query("interval") String interval,
            @Query("apikey") String apiKey
    );

    @GET("/query")
    Call<JsonObject> getBrentData(
            @Query("function") String function,
            @Query("interval") String interval,
            @Query("apikey") String apiKey
    );
}
