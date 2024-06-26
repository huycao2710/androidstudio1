package com.example.nhom1;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ExchangeRateApi {
    @GET("/query")
    Call<ExchangeRateResponse> getExchangeRate(
            @Query("function") String function,
            @Query("from_currency") String fromCurrency,
            @Query("to_currency") String toCurrency,
            @Query("apikey") String apiKey
    );

    @GET("/v6/{apiKey}/latest/USD")
    Call<ExchangeRateResponse> getExchangeRates(
            @Path("apiKey") String apiKey
    );
}
