package com.example.hipenjava.Activities.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hipenjava.R;

public class RegisterActivity2 extends AppCompatActivity {
    private EditText etName;
    private Button btnContinue;
    private ImageButton btnBack;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        // Initialize views
        etName = findViewById(R.id.etEmail);
        btnContinue = findViewById(R.id.btnContinue);
        btnBack = findViewById(R.id.btnBack);

        // Retrieve email from previous activity
        userEmail = getIntent().getStringExtra("EMAIL");

        // Handle Continue button click
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();

                if (!name.isEmpty()) {
                    // Proceed to next activity with email & name data
                    Intent intent = new Intent(RegisterActivity2.this, RegisterActivity3.class);
                    intent.putExtra("EMAIL", userEmail);// Passing Email
                    intent.putExtra("NAME", name);   // Passing Name
                    startActivity(intent); // 🔹 Moves to RegisterActivity3
                    Toast.makeText(RegisterActivity2.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Handle Back button click
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Closes this activity
            }
        });
    }
}
