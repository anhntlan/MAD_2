package com.example.hipenjava.Activities.Post;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    
    private EditText etPostContent;
    private ImageView ivSelectedImage;
    private Button btnAddImage;
    private Button btnPost;
    private ImageView btnRemoveImage;
    private ProgressBar progressBar;
    private ImageView btnBack;
    
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private String userName, userAvatar;
    
    private Uri selectedImageUri;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        
        if (currentUser == null) {
            // User not logged in, redirect to login
            finish();
            return;
        }

        // Initialize views
        etPostContent = findViewById(R.id.etPostContent);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnPost = findViewById(R.id.btnPost);
        btnRemoveImage = findViewById(R.id.btnRemoveImage);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
        
        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            content = intent.getStringExtra("content");
            String imageUriString = intent.getStringExtra("imageUri");
            
            if (content != null) {
                etPostContent.setText(content);
            }
            
            if (imageUriString != null) {
                selectedImageUri = Uri.parse(imageUriString);
                displaySelectedImage();
            }
        }

        // Setup click listeners
        btnAddImage.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
        });
        
        btnRemoveImage.setOnClickListener(v -> {
            selectedImageUri = null;
            ivSelectedImage.setVisibility(View.GONE);
            btnRemoveImage.setVisibility(View.GONE);
        });
        
        btnPost.setOnClickListener(v -> {
            createPost();
        });
        
        btnBack.setOnClickListener(v -> finish());
    }

    private void displaySelectedImage() {
        if (selectedImageUri != null) {
            ivSelectedImage.setVisibility(View.VISIBLE);
            btnRemoveImage.setVisibility(View.VISIBLE);
            
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(ivSelectedImage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            displaySelectedImage();
        }
    }

    private void createPost() {
        String content = etPostContent.getText().toString().trim();
        if (content.isEmpty() && selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng nhập nội dung hoặc chọn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        btnPost.setEnabled(false);


        /*// If there's an image, upload it first
        if (selectedImageUri != null) {
            uploadImageAndCreatePost(content);
        } else {
            // Create post without image
            savePostToFirestore(content, null);
        }*/
        // Lấy thông tin user từ Firestore trước
        db.collection("Users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userName = documentSnapshot.getString("userName");
                        userAvatar = documentSnapshot.getString("avatar");

                        if (selectedImageUri != null) {
                            uploadImageAndCreatePost(content);
                        } else {
                            savePostToFirestore(content, null);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnPost.setEnabled(true);
                        Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnPost.setEnabled(true);
                    Toast.makeText(this, "Lỗi lấy thông tin người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void uploadImageAndCreatePost(String content) {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference storageRef = storage.getReference()
                .child("post_images")
                .child(currentUser.getUid())
                .child(fileName);

        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get download URL
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                savePostToFirestore(content, imageUrl);
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                btnPost.setEnabled(true);
                                Toast.makeText(CreatePostActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnPost.setEnabled(true);
                    Toast.makeText(CreatePostActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void savePostToFirestore(String content, String imageUrl) {
        Map<String, Object> postData = new HashMap<>();
        postData.put("userId", currentUser.getUid());
        //postData.put("userName", currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Người dùng");
        //postData.put("userAvatar", currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : "");
        postData.put("userName", userName != null ? userName : "Người dùng");
        postData.put("userAvatar", userAvatar != null ? userAvatar : "");
        postData.put("content", content);
        postData.put("imageUrl", imageUrl);
        postData.put("timestamp", new Date());
        postData.put("likes", 0);
        postData.put("comments", 0);

        db.collection("posts")
                .add(postData)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CreatePostActivity.this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    btnPost.setEnabled(true);
                    Toast.makeText(CreatePostActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
