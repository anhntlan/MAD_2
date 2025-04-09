package com.example.hipenjava.Activities.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hipenjava.Activities.HomeActivity;
import com.example.hipenjava.R;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ImageButton btnTogglePassword;
    
   // private com.example.hipenjava.DatabaseHelper databaseHelper;
    private boolean isPasswordVisible = false;
    private FirebaseAuth mAuth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize UI components
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);


        // Initialize SQLite Database Helper
        //databaseHelper = new com.example.hipenjava.DatabaseHelper(this);
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Authentication

        // Handle login button click
 //       btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = etEmail.getText().toString().trim();
//                String password = etPassword.getText().toString().trim();
//
//                String role = databaseHelper.getUserRole(email, password); // Get user role
//
//                if (role != null) {
//                    // Save login state
//                    SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putBoolean("isLoggedIn", true);
//                    editor.putString("userRole", role); // Save role in SharedPreferences
//                    editor.apply();
//
//                    // Log all users (for debugging, you may remove this)
//                    databaseHelper.logAllUsers();
//
//                    Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
//
//                    // Redirect based on user role
//                    if (role.equals("admin")) {
//                        startActivity(new Intent(LoginActivity.this, com.example.hipenjava.Activities.HomeActivity.class)); // Admin Dashboard
//                    } else {
//                        startActivity(new Intent(LoginActivity.this, com.example.hipenjava.Activities.HomeActivity.class)); // Regular User Home
//                    }
//                    finish();
//                } else {
//                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
//                }
//            }
 //       });
        btnLogin.setOnClickListener(v -> loginUser());

        // Navigate to Register Activity
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, com.example.hipenjava.Activities.Auth.RegisterActivity1.class);
                startActivity(intent);
            }
        });
        

    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ email và mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Sai email hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                    }
                });

    }


}