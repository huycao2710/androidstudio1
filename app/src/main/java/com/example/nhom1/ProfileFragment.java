package com.example.nhom1;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    public TextView textViewUsernameValue, textViewEmailValue, textViewPhoneNumberValue;
    private Button logoutButton;
    private Button changePasswordButton;
    private String userEmail;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        textViewUsernameValue = view.findViewById(R.id.textViewUsername);
        textViewPhoneNumberValue = view.findViewById(R.id.phoneTextView);
        textViewEmailValue = view.findViewById(R.id.emailTextView);
        logoutButton = view.findViewById(R.id.logoutButton);
        changePasswordButton = view.findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            String fullName = bundle.getString("fullName");
            String phoneNumber = bundle.getString("phoneNumber");
            String email = bundle.getString("email");

            textViewUsernameValue.setText(fullName);
            textViewPhoneNumberValue.setText(phoneNumber);
            textViewEmailValue.setText(email);

            userEmail = email;
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return view;
    }

    private void logout() {
        Intent intent = new Intent(getActivity(), IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void showChangePasswordDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_changepass);

        // Set background drawable to rounded corner background
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_corner_background);

        EditText currentPasswordEditText = dialog.findViewById(R.id.current_password);
        EditText newPasswordEditText = dialog.findViewById(R.id.new_password);
        EditText confirmNewPasswordEditText = dialog.findViewById(R.id.confirm_new_password);
        Button confirmButton = dialog.findViewById(R.id.confirm_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle confirm button click
                String currentPassword = currentPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String confirmNewPassword = confirmNewPasswordEditText.getText().toString();

                if (validatePasswords(currentPassword, newPassword, confirmNewPassword)) {
                    updatePassword(currentPassword, newPassword, dialog);  // Pass dialog as parameter
                }
            }
        });

        dialog.show();
    }

    private boolean validatePasswords(String currentPassword, String newPassword, String confirmNewPassword) {
        if (currentPassword.isEmpty()) {
            showToast("Vui lòng nhập mật khẩu hiện tại");
            return false;
        }
        if (newPassword.isEmpty()) {
            showToast("Vui lòng nhập mật khẩu mới");
            return false;
        }
        if (newPassword.length() < 6) {
            showToast("Mật khẩu mới phải có ít nhất 6 ký tự");
            return false;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            showToast("Mật khẩu xác nhận không khớp");
            return false;
        }
        return true;
    }

    private void updatePassword(String currentPassword, String newPassword, Dialog dialog) { // Add dialog parameter
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);
                        if (passwordFromDB != null && passwordFromDB.equals(currentPassword)) {
                            userSnapshot.getRef().child("password").setValue(newPassword);
                            showToast("Đổi mật khẩu thành công");
                            dialog.dismiss();  // Close dialog on successful password change
                        } else {
                            showToast("Mật khẩu hiện tại không chính xác");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Đã xảy ra lỗi, vui lòng thử lại sau");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
