package com.example.nhom1;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

// MainActivity.java
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (item.getItemId() == R.id.nav_exchange) {
                    selectedFragment = new ExchangeRateFragment();
                } else if (item.getItemId() == R.id.nav_charts) {
                    selectedFragment = new StockFragment();
                } else if (item.getItemId() == R.id.nav_oil_chart) {
                    selectedFragment = new OilFragment();
                }else if (item.getItemId() == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("fullName", getIntent().getStringExtra("fullName"));
                    bundle.putString("email", getIntent().getStringExtra("email"));
                    bundle.putString("phoneNumber", getIntent().getStringExtra("phoneNumber"));
                    selectedFragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                }

                return false;
            }
        });

        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
}