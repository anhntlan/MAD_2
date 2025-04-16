package com.example.hipenjava.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Activities.Auth.LoginActivity;
import com.example.hipenjava.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class MenuActivity extends AppCompatActivity {

    private ImageButton btnBackMenu;
    private Button btnLogout;
    private FirebaseAuth mAuth;
    private TextView userName, userEmail,userCoursesCount;
    private ImageView userProfileImage;
    private AppCompatButton btnEditProfile;


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

        btnEditProfile = findViewById(R.id.btnEditProfile); // Kết nối với nút

        // Thiết lập sự kiện click cho nút
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang EditProfileActivity
                Intent intent = new Intent(MenuActivity.this, EditProfileActivity.class);
                startActivity(intent); // Mở EditProfileActivity
            }
        });

        // Initialize GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnBackMenu.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            // Sign out from Firebase
            mAuth.signOut();

            // Sign out from Google
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });

            Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        loadUserInfo();

        loadUserCoursesCount();

    }

    /*private void loadUserInfo() {
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
    }*/
    private void loadUserInfo() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Lấy userName từ Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("Users").document(user.getUid());

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String userNameFromFirestore = documentSnapshot.getString("userName");
                    userName.setText(userNameFromFirestore != null ? userNameFromFirestore : "No Name");
                } else {
                    userName.setText("No Name");  // Nếu không tìm thấy userName trong Firestore
                }
            }).addOnFailureListener(e -> {
                // Xử lý lỗi nếu không thể lấy dữ liệu từ Firestore
                userName.setText("Error loading name");
            });

            // Lấy email và ảnh đại diện từ FirebaseUser
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