package com.example.brainhub;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText txtUserName, txtPassword;
    Button btnSubmit;
    TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtUserName = findViewById(R.id.txtUserName);
        txtPassword = findViewById(R.id.txtPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvRegister = findViewById(R.id.tvRegister);

        // Handle login functionality
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredUsername = txtUserName.getText().toString().trim();
                String enteredPassword = txtPassword.getText().toString().trim();

                // Retrieve saved user credentials from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String savedUsername = sharedPreferences.getString("USERNAME", "");
                String savedPassword = sharedPreferences.getString("PASSWORD", "");

                // Validate login credentials
                if (enteredUsername.equals(savedUsername) && enteredPassword.equals(savedPassword)) {
                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    intent.putExtra("USERNAME", enteredUsername);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle registration redirection with alert
        tvRegister.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Redirecting to Registration")
                    .setMessage("You will be redirected to the registration page.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Proceed to Registration Activity after confirmation
                            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", null)  // Cancel action, closes the dialog
                    .show();
        });
    }
}
