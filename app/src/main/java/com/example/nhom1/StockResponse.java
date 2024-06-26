package com.example.nhom1;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class StockResponse {
    @SerializedName("Time Series (Daily)")
    private Map<String, StockData> timeSeriesDaily;

    @SerializedName("Weekly Time Series")
    private Map<String, StockData> timeSeriesWeekly;

    @SerializedName("Monthly Time Series")
    private Map<String, StockData> timeSeriesMonthly;

    public Map<String, StockData> getTimeSeries() {
        if (timeSeriesDaily != null) {
            return timeSeriesDaily;
        } else if (timeSeriesWeekly != null) {
            return timeSeriesWeekly;
        } else if (timeSeriesMonthly != null) {
            return timeSeriesMonthly;
        } else {
            return null;
        }
    }

    public static class StockData {
        @SerializedName("1. open")
        private String open;
        @SerializedName("2. high")
        private String high;
        @SerializedName("3. low")
        private String low;
        @SerializedName("4. close")
        private String close;
        @SerializedName("5. volume")
        private String volume;

        private String timestamp;

        public String getOpen() {
            return open;
        }

        public String getHigh() {
            return high;
        }

        public String getLow() {
            return low;
        }

        public String getClose() {
            return close;
        }

        public String getVolume() {
            return volume;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getFormattedTimestamp() {
            return timestamp != null ? timestamp : "";
        }
    }
}
