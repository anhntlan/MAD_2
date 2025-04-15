package com.example.hipenjava.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Activities.Auth.LoginActivity;
import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuActivity extends AppCompatActivity {

    private ImageButton btnBackMenu;
    private Button btnLogout;
    private FirebaseAuth mAuth;
    private TextView userName, userEmail,userCoursesCount;
    private ImageView userProfileImage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();
        btnBackMenu = findViewById(R.id.btnBackMenu);
        btnLogout = findViewById(R.id.btnLogout);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userProfileImage = findViewById(R.id.userProfileImage);
        userCoursesCount = findViewById(R.id.userCoursesCount);

        btnBackMenu.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loadUserInfo();

        loadUserCoursesCount();

    }

    private void loadUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userName.setText(user.getDisplayName() != null ? user.getDisplayName() : "No Name");
            userEmail.setText(user.getEmail() != null ? user.getEmail() : "No Email");

            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl())
                        .placeholder(R.drawable.ic_user_placeholder)
                        .into(userProfileImage);
            }
        }
    }

    private void loadUserCoursesCount() {
        String currentUserId = FirebaseAuth.getInstance().getUid();
        if (currentUserId == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("course_user");
        ref.orderByChild("userID").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        int count = (int) snapshot.getChildrenCount();
                        userCoursesCount.setText("Số khóa học đã học: " + count);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        userCoursesCount.setText("Lỗi tải số khóa học");
                    }
                });
    }
}