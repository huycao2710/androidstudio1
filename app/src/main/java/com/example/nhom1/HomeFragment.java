package com.example.nhom1;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    public TextView textViewUsernameValue;

    private TextView textViewUSDToVNDValue;
    private TextView textViewBTCToEURValue;
    private TextView textViewEURToVNDValue;
    private TextView textViewUSDToJPYValue;

    private static final String API_KEY_ALPHA = "demo";
    private static final String API_KEY_EXCHANGE_RATE = "624b132cb20ce5bcee82d1df";
    private static final String BASE_URL_ALPHA = "https://www.alphavantage.co";
    private static final String BASE_URL_EXCHANGE_RATE = "https://v6.exchangerate-api.com";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        textViewUsernameValue = view.findViewById(R.id.textViewUsername);
        textViewUSDToVNDValue = view.findViewById(R.id.textViewUSDToVNDValue);
        textViewBTCToEURValue = view.findViewById(R.id.textViewBTCToEURValue);
        textViewEURToVNDValue = view.findViewById(R.id.textViewEURToVNDValue);
        textViewUSDToJPYValue = view.findViewById(R.id.textViewUSDToJPYValue);

        // Gọi hàm để lấy dữ liệu tỷ giá và hiển thị
        loadExchangeRates();

        Bundle bundle = getArguments();
        if (bundle != null) {
            String fullName = bundle.getString("fullName");
            textViewUsernameValue.setText(fullName);
        }

        return view;
    }

    private void loadExchangeRates() {
        // Gọi API cho USD to JPY và BTC to EUR
        Retrofit retrofitAlpha = new Retrofit.Builder()
                .baseUrl(BASE_URL_ALPHA)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HomeApi homeApi = retrofitAlpha.create(HomeApi.class);

        Call<HomeResponse> callUSDToJPY = homeApi.getExchangeRate("CURRENCY_EXCHANGE_RATE", "USD", "JPY", API_KEY_ALPHA);
        callUSDToJPY.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HomeResponse.HomeExchangeRate exchangeRate = response.body().getExchangeRate();
                    if (exchangeRate != null) {
                        String rate = exchangeRate.getExchangeRate();
                        textViewUSDToJPYValue.setText(String.format("%.2f JPY", Double.parseDouble(rate)));
                    } else {
                        textViewUSDToJPYValue.setText("Error");
                    }
                } else {
                    textViewUSDToJPYValue.setText("Error");
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Log.e("API Call", "Gọi API thất bại: " + t.getMessage());
                textViewUSDToJPYValue.setText("Failed");
            }
        });

        Call<HomeResponse> callBTCToEUR = homeApi.getExchangeRate("CURRENCY_EXCHANGE_RATE", "BTC", "EUR", API_KEY_ALPHA);
        callBTCToEUR.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HomeResponse.HomeExchangeRate exchangeRate = response.body().getExchangeRate();
                    if (exchangeRate != null) {
                        String rate = exchangeRate.getExchangeRate();
                        textViewBTCToEURValue.setText(String.format("%.2f EUR", Double.parseDouble(rate)));
                    } else {
                        textViewBTCToEURValue.setText("Error");
                    }
                } else {
                    textViewBTCToEURValue.setText("Error");
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Log.e("API Call", "Gọi API thất bại: " + t.getMessage());
                textViewBTCToEURValue.setText("Failed");
            }
        });

        // Gọi API cho USD to VND và EUR to VND
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_EXCHANGE_RATE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HomeApi homeApiExchange = retrofit.create(HomeApi.class);

        Call<HomeResponse> callUSDToVND = homeApiExchange.getExchangeRates(API_KEY_EXCHANGE_RATE, "USD");
        callUSDToVND.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HomeResponse homeResponse = response.body();
                    double usdToVnd = homeResponse.getConversionRate("VND");
                    textViewUSDToVNDValue.setText(String.format("%.2f VNĐ", usdToVnd));
                } else {
                    textViewUSDToVNDValue.setText("Error");
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                textViewUSDToVNDValue.setText("Failed");
            }
        });

        Call<HomeResponse> callEURToVND = homeApiExchange.getExchangeRates(API_KEY_EXCHANGE_RATE, "EUR");
        callEURToVND.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HomeResponse homeResponse = response.body();
                    double eurToVnd = homeResponse.getConversionRate("VND");
                    textViewEURToVNDValue.setText(String.format("%.2f VNĐ", eurToVnd));
                } else {
                    textViewEURToVNDValue.setText("Error");
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                textViewEURToVNDValue.setText("Failed");
            }
        });
    }
}
