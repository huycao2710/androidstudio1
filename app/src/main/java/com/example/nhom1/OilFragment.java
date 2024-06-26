package com.example.nhom1;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OilFragment extends Fragment {

    private LineChart lineChart;
    private RadioGroup radioGroup;

    private static final String BASE_URL = "https://www.alphavantage.co";
    private static final String API_KEY = "demo"; // Replace with your actual API key

    public OilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_oil, container, false);

        lineChart = view.findViewById(R.id.lineChart);
        radioGroup = view.findViewById(R.id.radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioWTI) {
                    fetchOilData("WTI");
                } else if (checkedId == R.id.radioBrent) {
                    fetchOilData("Brent");
                }
            }
        });

        // Set default selection
        radioGroup.check(R.id.radioWTI); // Default selection: WTI

        // Set up chart
        setupChart();
        fetchOilData("WTI");

        return view;
    }

    private void setupChart() {
        // Configure line chart properties as needed
        // For example, enable touch gestures
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
    }

    private void fetchOilData(final String oilType) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OilApi oilApi = retrofit.create(OilApi.class);

        Call<JsonObject> call = null; // Sử dụng JsonObject để parse dữ liệu

        switch (oilType) {
            case "WTI":
                call = oilApi.getWTIData("WTI", "monthly", API_KEY);
                break;
            case "Brent":
                call = oilApi.getBrentData("BRENT", "monthly", API_KEY);
                break;
        }

        if (call != null) {
            call.enqueue(new Callback<JsonObject>() { // Sử dụng JsonObject thay vì OilResponse
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject jsonResponse = response.body();
                        if (jsonResponse != null && jsonResponse.has("data")) {
                            JsonArray data = jsonResponse.getAsJsonArray("data");
                            List<Entry> entries = new ArrayList<>();
                            List<String> labels = new ArrayList<>();

                            for (int i = 0; i < data.size(); i++) {
                                JsonObject dataPoint = data.get(i).getAsJsonObject();
                                String date = dataPoint.get("date").getAsString();
                                float value = dataPoint.get("value").getAsFloat();
                                Entry entry = new Entry(i, value);
                                entries.add(entry);
                                labels.add(date);
                            }

                            // Create a dataset with entries
                            LineDataSet dataSet = new LineDataSet(entries, oilType);
                            dataSet.setColor(Color.GREEN);
                            dataSet.setCircleColor(Color.GREEN);
                            dataSet.setCircleRadius(4f);
                            dataSet.setLineWidth(2f);

                            List<ILineDataSet> dataSets = new ArrayList<>();
                            dataSets.add(dataSet);

                            // Create LineData and set it to chart
                            LineData lineData = new LineData(dataSets);
                            lineChart.setData(lineData);
                            lineChart.invalidate(); // refresh chart

                            // Set up listener for chart value selected event
                            lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                @Override
                                public void onValueSelected(Entry e, Highlight h) {
                                    // Lấy ngày và giá trị tương ứng với điểm đã chọn
                                    int index = (int) e.getX();
                                    String selectedDate = labels.get(index); // labels là danh sách các ngày đã lấy từ API
                                    float selectedValue = e.getY(); // Lấy giá trị từ Entry

                                    // Hiển thị hộp dialog với ngày và giá trị đã chọn
                                    showDialog(selectedDate, selectedValue);
                                }

                                @Override
                                public void onNothingSelected() {
                                    // Không làm gì khi không có điểm nào được chọn
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "No data available", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showDialog(String selectedDate, float selectedValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Selected Data");
        builder.setMessage("Date: " + selectedDate + "\nValue: " + selectedValue);
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}
