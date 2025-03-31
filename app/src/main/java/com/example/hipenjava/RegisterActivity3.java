package com.example.hipenjava;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class RegisterActivity3 extends AppCompatActivity {
    private EditText etPassword;
    private Button btnContinue;
    private ImageButton btnBack;
    private String userEmail, userName; // Stores email and name from previous activity
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Initialize UI components
        etPassword = findViewById(R.id.etEmail); // This should be renamed to etPassword in XML
        btnContinue = findViewById(R.id.btnContinue);
        btnBack = findViewById(R.id.btnBack);

        // Get email and name from previous activity
        Intent intent = getIntent();
        if (intent != null) {
            userEmail = intent.getStringExtra("EMAIL");
            userName = intent.getStringExtra("NAME");
        }
        // Continue button click event
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(password) || password.length() < 6) {
                    etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự!");
                    return;
                }
                // Insert user data into SQLite
                boolean inserted = databaseHelper.insertUser(userEmail, userName, password,"user");

                if (inserted) {
                    Toast.makeText(RegisterActivity3.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity3.this, LoginActivity.class)); // Redirect to Login
                    finish();
                } else {
                    Toast.makeText(RegisterActivity3.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Back button click event
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the current activity
            }
        });
    }
}