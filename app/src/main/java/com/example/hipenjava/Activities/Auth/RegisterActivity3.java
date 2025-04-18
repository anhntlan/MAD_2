package com.example.hipenjava.Activities.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hipenjava.DatabaseHelper;
import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity3 extends AppCompatActivity {
    private EditText etPassword;
    private Button btnContinue;
    private ImageButton btnBack;
    private String userEmail, userName; // Stores email and name from previous activity
  //  private DatabaseHelper databaseHelper;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        // Initialize DatabaseHelper
       // databaseHelper = new DatabaseHelper(this);
        mAuth = FirebaseAuth.getInstance();

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

        btnContinue.setOnClickListener(v -> registerUser());
        // Back button click event
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the current activity
            }
        });
    }

    private void registerUser() {
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự!");
            return;
        }
        // Basic validation
        if (userEmail.isEmpty() || password.isEmpty() || userName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }


        //check password length
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải chứa ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(userEmail, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(RegisterActivity3.this, "Đăng ký thành công.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity3.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(RegisterActivity3.this, "Đăng ký thất bại: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }
