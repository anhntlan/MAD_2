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
import com.example.hipenjava.Models.User;
import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;



public class RegisterActivity3 extends AppCompatActivity {
    private EditText etPassword;
    private Button btnContinue;
    private ImageButton btnBack;
    private String userEmail, userName; // Stores email and name from previous activity
  //  private DatabaseHelper databaseHelper;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        // Initialize DatabaseHelper
       // databaseHelper = new DatabaseHelper(this);
        mAuth = FirebaseAuth.getInstance();
        //initialize firestore
        db = FirebaseFirestore.getInstance();


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
// Default values or optional empty strings for the new fields
        String address = "";  // Default empty address
        String fullName = ""; // Default empty full name
        String avatar = "";   // Default empty avatar (could be an empty URL)
        String role = "user"; // Default role, you can choose another value like "admin" if needed
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
                        saveUserData(user, password, address, fullName, avatar, role);  // Pass the new fields


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
    private void saveUserData(FirebaseUser user, String password, String address, String fullName, String avatar, String role) {
        // Create a User object to store data
        User userData = new User(userName, userEmail, user.getUid(), password, address, fullName, avatar, role);

        // Save user data to Firestore
        db.collection("Users")
                .document(user.getUid())  // Use user UID as document ID
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    // Successfully saved user data to Firestore
                    Toast.makeText(RegisterActivity3.this, "Thông tin người dùng đã được lưu.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to save user data
                    Toast.makeText(RegisterActivity3.this, "Lỗi lưu dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                });
    }
    }
