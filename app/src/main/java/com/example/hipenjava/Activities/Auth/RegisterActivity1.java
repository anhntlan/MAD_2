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

public class RegisterActivity1 extends AppCompatActivity {

    private EditText etEmail;
    private Button btnContinue;
    private ImageButton btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        btnContinue = findViewById(R.id.btnContinue);
        btnBack = findViewById(R.id.btnBack);

        // Handle Continue button click
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();

                if (!email.isEmpty()) {
                    // Proceed to next activity with email data
                    Intent intent = new Intent(RegisterActivity1.this, RegisterActivity2.class);
                    intent.putExtra("EMAIL", email);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity1.this, "Please enter your email", Toast.LENGTH_SHORT).show();
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