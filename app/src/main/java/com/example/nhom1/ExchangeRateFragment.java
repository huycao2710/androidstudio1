package com.example.nhom1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExchangeRateFragment extends Fragment {

    private EditText editTextInput;
    private TextView textViewOutput;
    private TextView textViewExchangeRate;
    private RadioGroup radioGroup;
    private RadioButton radioUSDToJPY, radioBTCToEUR, radioEURToVND, radioUSDToVND;
    private Button buttonConvert;

    private static final String API_KEY_ALPHA = "XN1FEXV1509NRC6H"; // Thay thế bằng API key của Alpha Vantage
    private static final String API_KEY_EXCHANGE_RATE = "624b132cb20ce5bcee82d1df"; // Thay thế bằng API key của Exchange Rate API cho EUR to VND
    private static final String BASE_URL_ALPHA = "https://www.alphavantage.co";
    private static final String BASE_URL_EXCHANGE_RATE = "https://v6.exchangerate-api.com";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);

        editTextInput = view.findViewById(R.id.editTextInput);
        textViewOutput = view.findViewById(R.id.textViewOutput);
        textViewExchangeRate = view.findViewById(R.id.textViewExchangeRate);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioUSDToJPY = view.findViewById(R.id.radioUSDToJPY);
        radioBTCToEUR = view.findViewById(R.id.radioBTCToEUR);
        radioEURToVND = view.findViewById(R.id.radioEURToVND);
        radioUSDToVND = view.findViewById(R.id.radioUSDToVND);
        buttonConvert = view.findViewById(R.id.buttonConvert);

        buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueToConvert = editTextInput.getText().toString().trim();
                if (valueToConvert.isEmpty()) {
                    Toast.makeText(getActivity(), "Vui lòng nhập giá trị để quy đổi", Toast.LENGTH_SHORT).show();
                    return;
                }

                double amount = Double.parseDouble(valueToConvert);
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();

                if (checkedRadioButtonId == R.id.radioUSDToJPY) {
                    convertCurrency(amount, "USD", "JPY");
                } else if (checkedRadioButtonId == R.id.radioBTCToEUR) {
                    convertCurrency(amount, "BTC", "EUR");
                } else if (checkedRadioButtonId == R.id.radioEURToVND) {
                    convertCurrencyEURToVND(amount);
                } else if (checkedRadioButtonId == R.id.radioUSDToVND) {
                    convertCurrencyUSDToVND();
                } else {
                    Toast.makeText(getActivity(), "Vui lòng chọn một tùy chọn quy đổi", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void convertCurrency(double amount, String fromCurrency, String toCurrency) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_ALPHA)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ExchangeRateApi exchangeRateApi = retrofit.create(ExchangeRateApi.class);

        Call<ExchangeRateResponse> call = exchangeRateApi.getExchangeRate("CURRENCY_EXCHANGE_RATE", fromCurrency, toCurrency, API_KEY_ALPHA);
        call.enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ExchangeRateResponse.ExchangeRate exchangeRate = response.body().getExchangeRate();
                    if (exchangeRate != null) {
                        double rate = Double.parseDouble(exchangeRate.getExchangeRate());
                        double convertedValue = amount * rate;
                        String displayText = String.format("%s: %.2f", fromCurrency + " to " + toCurrency, rate);
                        textViewOutput.setText(String.valueOf(convertedValue));
                        textViewExchangeRate.setText(displayText);
                    } else {
                        Toast.makeText(getActivity(), "Không thể lấy tỷ giá", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Phản hồi không thành công hoặc không có dữ liệu", Toast.LENGTH_SHORT).show();
                    Log.d("API Response", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {
                Log.e("API Call", "Gọi API thất bại: " + t.getMessage());
                if (call != null && call.request() != null && call.request().url() != null) {
                    Log.d("API Call", "URL: " + call.request().url());
                }
                Toast.makeText(getActivity(), "Gọi API thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void convertCurrencyEURToVND(double amount) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_EXCHANGE_RATE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ExchangeRateApi exchangeRateApi = retrofit.create(ExchangeRateApi.class);
        Call<ExchangeRateResponse> call = exchangeRateApi.getExchangeRates(API_KEY_EXCHANGE_RATE);
        call.enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ExchangeRateResponse exchangeRateApiResponse = response.body();
                    double eurToUsd = 1 / exchangeRateApiResponse.getConversionRate("EUR");
                    double usdToVnd = exchangeRateApiResponse.getConversionRate("VND");

                    if (eurToUsd != 0 && usdToVnd != 0) {
                        double eurToVndRate = eurToUsd * usdToVnd;
                        double convertedValue = amount * eurToVndRate;
                        String displayText = String.format("EUR to VND: %.2f", eurToVndRate);
                        textViewOutput.setText(String.valueOf(convertedValue) + " VNĐ");
                        textViewExchangeRate.setText(displayText);
                    } else {
                        Toast.makeText(getActivity(), "Không thể tính toán tỷ giá chuyển đổi",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Phản hồi không thành công hoặc không có dữ liệu", Toast.LENGTH_SHORT).show();
                    Log.d("API Response", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {
                Log.e("API Call", "Gọi API thất bại: " + t.getMessage());
                if (call != null && call.request() != null && call.request().url() != null) {
                    Log.d("API Call", "URL: " + call.request().url());
                }
                Toast.makeText(getActivity(), "Gọi API thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void convertCurrencyUSDToVND() {
        String valueToConvert = editTextInput.getText().toString().trim();
        if (valueToConvert.isEmpty()) {
            Toast.makeText(getActivity(), "Vui lòng nhập giá trị để quy đổi", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(valueToConvert);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_EXCHANGE_RATE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ExchangeRateApi exchangeRateApi = retrofit.create(ExchangeRateApi.class);
        Call<ExchangeRateResponse> call = exchangeRateApi.getExchangeRates(API_KEY_EXCHANGE_RATE);
        call.enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ExchangeRateResponse exchangeRateApiResponse = response.body();
                    double usdToVnd = exchangeRateApiResponse.getConversionRate("VND");
                    double convertedValue = amount * usdToVnd;
                    String displayText = String.format("USD to VND: %.2f", usdToVnd);
                    textViewOutput.setText(String.valueOf(convertedValue)+ " VNĐ");
                    textViewExchangeRate.setText(displayText);
                } else {
                    Toast.makeText(getActivity(), "Phản hồi không thành công hoặc không có dữ liệu", Toast.LENGTH_SHORT).show();
                    Log.d("API Response", response.toString());
                }
            }

            @Override
            public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {
                Log.e("API Call", "Gọi API thất bại: " + t.getMessage());
                if (call != null && call.request() != null && call.request().url() != null) {
                    Log.d("API Call", "URL: " + call.request().url());
                }
                Toast.makeText(getActivity(), "Gọi API thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
