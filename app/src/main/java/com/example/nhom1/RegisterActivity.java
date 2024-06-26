package com.example.nhom1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText fullNameVal, emailVal, passwordVal, phoneNumberVal;
    Button registerButton;
    TextView loginRedirectButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullNameVal = findViewById(R.id.register_fullName);
        emailVal = findViewById(R.id.register_email);
        passwordVal = findViewById(R.id.register_password);
        phoneNumberVal = findViewById(R.id.register_phoneNumber);
        registerButton = findViewById(R.id.register_button);
        loginRedirectButton = findViewById(R.id.loginRedirectText);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    registerUser();
                }
            }
        });

        loginRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateFields() {
        String fullName = fullNameVal.getText().toString();
        String email = emailVal.getText().toString();
        String password = passwordVal.getText().toString();
        String phoneNumber = phoneNumberVal.getText().toString();

        if (fullName.isEmpty()) {
            fullNameVal.setError("Vui lòng nhập họ tên");
            return false;
        } else if (email.isEmpty()) {
            emailVal.setError("Vui lòng nhập email");
            return false;
        } else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            emailVal.setError("Vui lòng nhập đúng định dạng email");
            return false;
        } else if (password.isEmpty()) {
            passwordVal.setError("Vui lòng nhập mật khẩu");
            return false;
        } else if (password.length() < 6) {
            passwordVal.setError("Vui lòng nhập mật khẩu dài hơn 6 ký tự");
            return false;
        } else if (phoneNumber.isEmpty()) {
            phoneNumberVal.setError("Vui lòng nhập số điện thoại");
            return false;
        } else if (!phoneNumber.matches("\\d+")) {
            phoneNumberVal.setError("Vui lòng nhập đúng định dạng số điện thoại");
            return false;
        } else {
            return true;
        }
    }

    private void registerUser() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        String fullName = fullNameVal.getText().toString();
        String email = emailVal.getText().toString();
        String password = passwordVal.getText().toString();
        String phoneNumber = phoneNumberVal.getText().toString();

        UserClass userClass = new UserClass(fullName, email, password, phoneNumber);
        reference.child(email.replace(".", ",")).setValue(userClass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Đăng ký không thành công. Hãy thử lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}