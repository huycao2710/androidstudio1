package com.example.nhom1;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StockFragment extends Fragment {

    private LineChart lineChartDaily, lineChartWeekly, lineChartMonthly;
    private Map<String, StockResponse.StockData> stockDataMap;
    private Map<String, LineChart> chartMap = new HashMap<>();

    private static final int RADIO_DAILY = 1;
    private static final int RADIO_WEEKLY = 2;
    private static final int RADIO_MONTHLY = 3;

    public StockFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stock, container, false);

        // Initialize LineCharts
        lineChartDaily = view.findViewById(R.id.lineChartDaily);
        lineChartWeekly = view.findViewById(R.id.lineChartWeekly);
        lineChartMonthly = view.findViewById(R.id.lineChartMonthly);

        // Initialize chartMap
        chartMap.put("Daily", lineChartDaily);
        chartMap.put("Weekly", lineChartWeekly);
        chartMap.put("Monthly", lineChartMonthly);

        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);

        // Set IDs for RadioButtons to use them in switch-case
        view.findViewById(R.id.radioDaily).setId(RADIO_DAILY);
        view.findViewById(R.id.radioWeekly).setId(RADIO_WEEKLY);
        view.findViewById(R.id.radioMonthly).setId(RADIO_MONTHLY);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String chartType = null;
                switch (checkedId) {
                    case RADIO_DAILY:
                        chartType = "Daily";
                        break;
                    case RADIO_WEEKLY:
                        chartType = "Weekly";
                        break;
                    case RADIO_MONTHLY:
                        chartType = "Monthly";
                        break;
                }
                if (chartType != null) {
                    updateChartVisibility(chartType);
                    fetchStockData(chartType);
                }
            }
        });

        // Set default chart to Daily
        ((RadioButton) view.findViewById(RADIO_DAILY)).setChecked(true);

        return view;
    }

    private void updateChartVisibility(String chartType) {
        for (Map.Entry<String, LineChart> entry : chartMap.entrySet()) {
            if (entry.getKey().equals(chartType)) {
                entry.getValue().setVisibility(View.VISIBLE);
            } else {
                entry.getValue().setVisibility(View.GONE);
            }
        }
    }

    private void fetchStockData(final String chartType) {
        final LineChart chartToUpdate = chartMap.get(chartType);
        if (chartToUpdate != null) {
            chartToUpdate.clear();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://www.alphavantage.co")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            StockApi stockApi = retrofit.create(StockApi.class);

            Call<StockResponse> call = null;
            switch (chartType) {
                case "Daily":
                    call = stockApi.getStockData("TIME_SERIES_DAILY", "IBM", null, "demo");
                    break;
                case "Weekly":
                    call = stockApi.getStockData("TIME_SERIES_WEEKLY", "IBM", null, "demo");
                    break;
                case "Monthly":
                    call = stockApi.getStockData("TIME_SERIES_MONTHLY", "IBM", null, "demo");
                    break;
            }

            if (call != null) {
                call.enqueue(new Callback<StockResponse>() {
                    @Override
                    public void onResponse(Call<StockResponse> call, Response<StockResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            stockDataMap = response.body().getTimeSeries();
                            Log.d("API Response", "Data: " + stockDataMap);  // Log dữ liệu trả về từ API
                            if (stockDataMap != null && !stockDataMap.isEmpty()) {
                                List<Entry> entriesClose = new ArrayList<>();
                                int index = 0;
                                for (Map.Entry<String, StockResponse.StockData> entry : stockDataMap.entrySet()) {
                                    StockResponse.StockData stockData = entry.getValue();
                                    stockData.setTimestamp(entry.getKey());
                                    float closeValue = Float.parseFloat(stockData.getClose());
                                    Entry entryClose = new Entry(index, closeValue);
                                    entryClose.setData(stockData);
                                    entriesClose.add(entryClose);
                                    index++;
                                }
                                if (!entriesClose.isEmpty()) {
                                    LineDataSet dataSetClose = new LineDataSet(entriesClose, "Close");
                                    dataSetClose.setColor(Color.RED);
                                    dataSetClose.setCircleColor(Color.RED);
                                    dataSetClose.setCircleRadius(5f);
                                    dataSetClose.setDrawCircles(true);

                                    LineData lineData = new LineData(dataSetClose);
                                    chartToUpdate.setData(lineData);
                                    chartToUpdate.invalidate();

                                    // Đăng ký sự kiện OnChartValueSelectedListener
                                    chartToUpdate.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                        @Override
                                        public void onValueSelected(Entry e, Highlight h) {
                                            displayInfoForEntry(e);
                                        }

                                        @Override
                                        public void onNothingSelected() {
                                            // Không cần xử lý gì khi không có điểm nào được chọn
                                        }
                                    });
                                } else {
                                    Log.e("StockData", "No data to display in chart");
                                }
                            } else {
                                Log.e("StockData", "Time Series data is null or empty");
                            }
                        } else {
                            Log.e("StockData", "Response unsuccessful or body is null");
                        }
                    }


                    @Override
                    public void onFailure(Call<StockResponse> call, Throwable t) {
                        Log.e("StockData", "API call failed", t);
                        Log.e(TAG, "Network request failed", t);
                    }
                });
            }
        }
    }

    private void displayInfoForEntry(Entry e) {
        StockResponse.StockData stockData = (StockResponse.StockData) e.getData();

        float closePrice = e.getY(); // Giá đóng cửa
        String time = stockData.getFormattedTimestamp();
        float volume = Float.parseFloat(stockData.getVolume());

        // Hiển thị thông tin
        displayInfo(closePrice, volume, time);
    }

    private void displayInfo(float closePrice, float volume, String time) {
        // Tạo AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_info, null);
        builder.setView(dialogView);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        // Hiển thị thông tin
        TextView textClose = dialogView.findViewById(R.id.text_close);
        TextView textVolume = dialogView.findViewById(R.id.text_volume);
        TextView textTime = dialogView.findViewById(R.id.text_time);

        textClose.setText("Close Price: " + closePrice);
        textVolume.setText("Volume: " + volume);
        textTime.setText("Time: " + time);

        builder.show();
    }
}
