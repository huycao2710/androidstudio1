package com.example.nhom1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText loginEmail, loginPassword;
    Button loginButton;
    TextView signupRedirectText;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail() && validatePassword()) {
                    checkUser();
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public Boolean validateEmail(){
        String val = loginEmail.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()){
            loginEmail.setError("Vui lòng nhập email");
            return false;
        }
        else if (!val.matches(emailPattern)){
            loginEmail.setError("Vui lòng nhập đúng định dạng email");
            return false;
        } else {
            loginEmail.setError(null);
            return true;
        }
    }

    public Boolean validatePassword(){
        String val = loginPassword.getText().toString();
        if(val.isEmpty()){
            loginPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser(){
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(email);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loginEmail.setError(null);
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);
                        if (passwordFromDB != null && passwordFromDB.equals(password)) {
                            loginPassword.setError(null);

                            String fullNameFromDB = userSnapshot.child("fullName").getValue(String.class);
                            String emailFromDB = userSnapshot.child("email").getValue(String.class);
                            String phoneNumberFromDB = userSnapshot.child("phoneNumber").getValue(String.class);

                            saveSession(fullNameFromDB, emailFromDB, phoneNumberFromDB);

                            showToast("Đăng nhập thành công");

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("fullName", fullNameFromDB);
                            intent.putExtra("email", emailFromDB);
                            intent.putExtra("phoneNumber", phoneNumberFromDB);

                            startActivity(intent);
                            finish();
                            return;
                        } else {
                            loginPassword.setError("Mật khẩu chưa chính xác");
                            loginPassword.requestFocus();
                        }
                    }
                } else {
                    loginEmail.setError("Tài khoản hoặc mật khẩu không chính xác");
                    loginEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void saveSession(String fullName, String email, String phoneNumber) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fullName", fullName);
        editor.putString("email", email);
        editor.putString("phoneNumber", phoneNumber);
        editor.apply();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}