package com.example.hipenjava.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    private ImageButton btnBackMenu;
    private Button btnLogout, btnChooseAvatar, btnSaveChanges;
    private FirebaseAuth mAuth;
    //private TextView userName, userEmail;
    private TextView userCoursesCount;
    private EditText etUserName, etUserEmail, etUserAddress;
    private ImageView userProfileImage;
    private Uri avatarUri;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();
        btnBackMenu = findViewById(R.id.btnBackMenu);
        btnLogout = findViewById(R.id.btnLogout);
        /*userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);*/
        userProfileImage = findViewById(R.id.userProfileImage);
        userCoursesCount = findViewById(R.id.userCoursesCount);

        etUserName = findViewById(R.id.etUserName);
        etUserEmail = findViewById(R.id.etUserEmail);
        etUserAddress = findViewById(R.id.etUserAddress);
        btnChooseAvatar = findViewById(R.id.btnChooseAvatar);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnBackMenu.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });

        userProfileImage.setOnClickListener(v -> {
            etUserName.setVisibility(View.VISIBLE);
            etUserEmail.setVisibility(View.VISIBLE);
            etUserAddress.setVisibility(View.VISIBLE);
            btnChooseAvatar.setVisibility(View.VISIBLE);
            btnSaveChanges.setVisibility(View.VISIBLE);
        });

        btnChooseAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 101);
        });

        btnSaveChanges.setOnClickListener(v -> saveUserProfileChanges());

        loadUserInfo();
        loadUserCoursesCount();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            avatarUri = data.getData();
            userProfileImage.setImageURI(avatarUri);
        }
    }

    private void loadUserInfo() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("Users").document(uid);

        ref.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("userName");
                String email = documentSnapshot.getString("userEmail");
                String address = documentSnapshot.getString("address");
                String avatarUrl = documentSnapshot.getString("avatar");

                etUserName.setText(name != null ? name : "");
                etUserEmail.setText(email != null ? email : "");
                etUserAddress.setText(address != null ? address : "");

                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(this)
                            .load(avatarUrl)
                            .placeholder(R.drawable.ic_user_placeholder)
                            .into(userProfileImage);
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
        });
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

    private void saveUserProfileChanges() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("Users").document(uid);

        Map<String, Object> updates = new HashMap<>();
        updates.put("userName", etUserName.getText().toString());
        updates.put("email", etUserEmail.getText().toString());
        updates.put("address", etUserAddress.getText().toString());

        if (avatarUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("avatars/" + uid + ".jpg");
            storageRef.putFile(avatarUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updates.put("avatar", uri.toString());
                        ref.update(updates)
                                .addOnSuccessListener(unused -> Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show());
                    }));
        } else {
            ref.update(updates)
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show());
        }
    }

}