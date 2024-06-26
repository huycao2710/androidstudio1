package com.example.nhom1;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class ExchangeRateResponse {

    @SerializedName("Realtime Currency Exchange Rate")
    private ExchangeRate exchangeRate;

    @SerializedName("conversion_rates")
    private Map<String, Double> conversionRates;

    public double getConversionRate(String currencyCode) {
        if (conversionRates != null) {
            return conversionRates.getOrDefault(currencyCode, 0.0);
        } else {
            return 0.0;
        }
    }

    public ExchangeRate getExchangeRate() {
        return exchangeRate;
    }

    public static class ExchangeRate {
        @SerializedName("5. Exchange Rate")
        private String exchangeRate;

        public String getExchangeRate() {
            return exchangeRate;
        }
    }

    // Bổ sung các phương thức getter/setter nếu cần thiết
}
