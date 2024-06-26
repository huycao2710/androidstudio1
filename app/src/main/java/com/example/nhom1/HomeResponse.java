package com.example.nhom1;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class HomeResponse {

    @SerializedName("Realtime Currency Exchange Rate")
    private HomeExchangeRate exchangeRate;

    @SerializedName("conversion_rates")
    private Map<String, Double> conversionRates;

    public double getConversionRate(String currencyCode) {
        if (conversionRates != null) {
            return conversionRates.getOrDefault(currencyCode, 0.0);
        } else {
            return 0.0;
        }
    }

    public HomeExchangeRate getExchangeRate() {
        return exchangeRate;
    }

    public static class HomeExchangeRate {
        @SerializedName("5. Exchange Rate")
        private String exchangeRate;

        public String getExchangeRate() {
            return exchangeRate;
        }
    }

    // Bổ sung các phương thức getter/setter nếu cần thiết
}
