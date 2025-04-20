package com.example.hipenjava.Activities.Auth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnResetPassword;
    private FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtEmail = findViewById(R.id.edtEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        mAuth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            if (email.isEmpty()) {
                edtEmail.setError("Vui lòng nhập email");
                edtEmail.requestFocus();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Đã gửi email khôi phục. Vui lòng kiểm tra hộp thư!", Toast.LENGTH_LONG).show();
                            finish(); // Quay lại màn hình đăng nhập
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Không gửi được email. Kiểm tra lại địa chỉ!", Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}