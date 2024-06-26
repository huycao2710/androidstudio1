package com.example.nhom1;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OilResponse {

    @SerializedName("name")
    private String name;

    @SerializedName("interval")
    private String interval;

    @SerializedName("unit")
    private String unit;

    @SerializedName("data")
    private List<OilData> data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<OilData> getData() {
        return data;
    }

    public void setData(List<OilData> data) {
        this.data = data;
    }

    public static class OilData {

        @SerializedName("date")
        private String date;

        @SerializedName("value")
        private String value;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
