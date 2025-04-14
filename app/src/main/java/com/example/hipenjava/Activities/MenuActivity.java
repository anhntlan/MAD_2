package com.example.hipenjava.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hipenjava.Activities.Auth.LoginActivity;
import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {

    private ImageButton btnBackMenu;
    private Button btnLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();
        btnBackMenu = findViewById(R.id.btnBackMenu);
        btnLogout = findViewById(R.id.btnLogout);

        btnBackMenu.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}