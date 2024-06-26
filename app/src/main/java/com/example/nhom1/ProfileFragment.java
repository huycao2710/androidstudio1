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

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    public TextView textViewUsernameValue, textViewEmailValue, textViewPhoneNumberValue;
    private Button logoutButton;
    private Button changePasswordButton;


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

                // Validate input and update password
                //...
            }
        });

        dialog.show();
    }
}
