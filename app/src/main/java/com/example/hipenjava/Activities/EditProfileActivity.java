package com.example.hipenjava.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView userAvatar;
    private EditText etUserName, etUserEmail, etUserAddress;
    private Button btnChooseImage, btnSaveChanges;
    private Uri avatarUri;
    private ImageButton btnBackMenu;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference("avatars");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile); // giả định layout bạn đặt tên như vậy

        userAvatar = findViewById(R.id.userAvatar);
        etUserName = findViewById(R.id.etUserName);
        etUserEmail = findViewById(R.id.etUserEmail);
        etUserAddress = findViewById(R.id.etUserAddress);
        btnChooseImage = findViewById(R.id.btnChooseAvatar);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnBackMenu = findViewById(R.id.btnBackMenu);


        loadUserData();

        btnChooseImage.setOnClickListener(v -> openFileChooser());
        btnBackMenu.setOnClickListener(v -> finish());


        btnSaveChanges.setOnClickListener(v -> {
            if (avatarUri != null) {
                uploadImageAndSaveData();
            } else {
                saveProfileToFirestore(null);
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            avatarUri = data.getData();
            Glide.with(this).load(avatarUri).into(userAvatar);
        }
    }

    private void loadUserData() {
        String uid = mAuth.getCurrentUser().getUid();
        if (uid == null) return;

        db.collection("Users").document(uid).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                etUserName.setText(snapshot.getString("userName"));
                etUserEmail.setText(snapshot.getString("userEmail"));
                etUserAddress.setText(snapshot.getString("userAddress"));

                String imageUrl = snapshot.getString("userImage");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this).load(imageUrl).into(userAvatar);
                }
            }
        });
    }

    private void uploadImageAndSaveData() {
        String uid = mAuth.getCurrentUser().getUid();
        if (uid == null || avatarUri == null) return;

        StorageReference fileRef = storageRef.child(uid + ".jpg");

        fileRef.putFile(avatarUri).addOnSuccessListener(taskSnapshot ->
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveProfileToFirestore(imageUrl);
                })
        ).addOnFailureListener(e ->
                Toast.makeText(EditProfileActivity.this, "Upload thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

    private void saveProfileToFirestore(String imageUrl) {
        String uid = mAuth.getCurrentUser().getUid();
        if (uid == null) return;

        DocumentReference ref = db.collection("Users").document(uid);
        Map<String, Object> updates = new HashMap<>();
        updates.put("userName", etUserName.getText().toString());
        updates.put("userEmail", etUserEmail.getText().toString());
        updates.put("userAddress", etUserAddress.getText().toString());

        if (imageUrl != null) {
            updates.put("userImage", imageUrl);
        }

        ref.update(updates).addOnSuccessListener(unused ->
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
        ).addOnFailureListener(e ->
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
}
