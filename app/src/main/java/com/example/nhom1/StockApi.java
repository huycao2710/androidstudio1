package com.example.nhom1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StockApi {

    @GET("/query")
    Call<StockResponse> getStockData(
            @Query("function") String function,
            @Query("symbol") String symbol,
            @Query("datatype") String datatype,
            @Query("apikey") String apikey
    );
}
