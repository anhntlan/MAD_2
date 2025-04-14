/*
package com.example.hipenjava.Activities.Post;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Activities.Gallery.ArtGalleryActivity;
import com.example.hipenjava.Models.Post;
import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivityPost extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private TextView createPostPrompt;
    //private ImageView galleryButton;
    private ImageView btnBack;
    private ImageView btnGallery, ivCurrentUserAvatar;

    private Uri selectedImageUri;

    private String userAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_post);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // User not logged in, redirect to login
            finish();
            return;
        }

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewPosts);
        createPostPrompt = findViewById(R.id.createPostPrompt);
        btnGallery = findViewById(R.id.btnGallery);
        btnBack = findViewById(R.id.btnBack);
        btnGallery = findViewById(R.id.btnGallery);

        //để hiển thị avatar
        ivCurrentUserAvatar = findViewById(R.id.ivCurrentUserAvatar);
        db.collection("Users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userAvatar = documentSnapshot.getString("avatar");

                        if (userAvatar != null && !userAvatar.isEmpty()) {
                            Glide.with(MainActivityPost.this)
                                    .load(userAvatar)
                                    .placeholder(R.drawable.default_avatar) // ảnh mặc định nếu URL rỗng
                                    .error(R.drawable.default_avatar)       // ảnh lỗi nếu load thất bại
                                    .into(ivCurrentUserAvatar);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivityPost.this, "Không thể tải avatar", Toast.LENGTH_SHORT).show()
                );



        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(postAdapter);

        // Load posts
        loadPosts();

        // Setup create post prompt
        createPostPrompt.setOnClickListener(v -> showCreatePostDialog());

        // Setup gallery button
        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Setup back button
        btnBack.setOnClickListener(v -> finish());

        // Setup gallery button
        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityPost.this, ArtGalleryActivity.class);
            startActivity(intent);
        });
    }

    private void loadPosts() {
        // Check if user is liked each post
        String userId = currentUser.getUid();

        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(MainActivityPost.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        postList.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            Post post = document.toObject(Post.class);
                            if (post != null) {
                                post.setId(document.getId());

                                // Check if user liked this post
                                db.collection("likes")
                                        .whereEqualTo("postId", post.getId())
                                        .whereEqualTo("userId", userId)
                                        .get()
                                        .addOnSuccessListener(likeSnapshots -> {
                                            post.setLikedByCurrentUser(!likeSnapshots.isEmpty());
                                            postAdapter.notifyDataSetChanged();
                                        });

                                postList.add(post);
                            }
                        }
                        postAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void showCreatePostDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_post);
        dialog.setCancelable(true);

        EditText postContent = dialog.findViewById(R.id.editTextPostContent);
        ImageView addImageButton = dialog.findViewById(R.id.addImageButton);
        TextView postButton = dialog.findViewById(R.id.buttonPost);

        addImageButton.setOnClickListener(v -> {
            // Open image picker
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
            dialog.dismiss();
        });

        postButton.setOnClickListener(v -> {
            String content = postContent.getText().toString().trim();
            if (!content.isEmpty()) {
                // Create post without image
                Intent intent = new Intent(MainActivityPost.this, CreatePostActivity.class);
                intent.putExtra("content", content);
                startActivity(intent);
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivityPost.this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Navigate to create post with selected image
            Intent intent = new Intent(MainActivityPost.this, CreatePostActivity.class);
            intent.putExtra("imageUri", selectedImageUri.toString());
            startActivity(intent);
        }
    }
}
*/

package com.example.hipenjava.Activities.Post;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Activities.Gallery.ArtGalleryActivity;
import com.example.hipenjava.Models.Post;
import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivityPost extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;

    private TextView createPostPrompt;
    private ImageView btnBack;
    private ImageView btnGallery;
    private ImageView ivCurrentUserAvatar;

    private Uri selectedImageUri;
    private String userAvatar, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_post);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            finish();
            return;
        }

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewPosts);
        createPostPrompt = findViewById(R.id.createPostPrompt);
        btnBack = findViewById(R.id.btnBack);
        btnGallery = findViewById(R.id.btnGallery);
        ivCurrentUserAvatar = findViewById(R.id.ivCurrentUserAvatar);

        // Load user avatar
        db.collection("Users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userAvatar = documentSnapshot.getString("avatar");
                        userName = documentSnapshot.getString("userName");
                        if (userAvatar != null && !userAvatar.isEmpty()) {
                            Glide.with(MainActivityPost.this)
                                    .load(userAvatar)
                                    .placeholder(R.drawable.default_avatar)
                                    .error(R.drawable.default_avatar)
                                    .into(ivCurrentUserAvatar);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivityPost.this, "Không thể tải avatar", Toast.LENGTH_SHORT).show());

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(postAdapter);

        // Load posts
        loadPosts();

        // Setup create post prompt
        createPostPrompt.setOnClickListener(v -> showCreatePostDialog());

        // Setup back button
        btnBack.setOnClickListener(v -> finish());

        // Setup gallery button
        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityPost.this, ArtGalleryActivity.class);
            startActivity(intent);
        });
    }

    private void loadPosts() {
        String userId = currentUser.getUid();
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(MainActivityPost.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null) {
                        postList.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            Post post = document.toObject(Post.class);
                            if (post != null) {
                                post.setId(document.getId());
                                db.collection("likes")
                                        .whereEqualTo("postId", post.getId())
                                        .whereEqualTo("userId", userId)
                                        .get()
                                        .addOnSuccessListener(likeSnapshots -> {
                                            post.setLikedByCurrentUser(!likeSnapshots.isEmpty());
                                            postAdapter.notifyDataSetChanged();
                                        });
                                postList.add(post);
                            }
                        }
                        postAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void showCreatePostDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_post);
        dialog.setCancelable(true);

        /*TextView postContent = dialog.findViewById(R.id.editTextPostContent);
        ImageView addImageButton = dialog.findViewById(R.id.addImageButton);
        ImageView ivSelectedImage = dialog.findViewById(R.id.ivSelectedImage);
        ImageView btnRemoveImage = dialog.findViewById(R.id.btnRemoveImage);
        TextView postButton = dialog.findViewById(R.id.buttonPost);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
*/
        // Header views
        ImageView ivUserAvatar = dialog.findViewById(R.id.ivUserAvatar);
        TextView tvUserName = dialog.findViewById(R.id.tvUserName);
        ImageView btnClose = dialog.findViewById(R.id.btnClose);

        // Other views
        TextView postContent = dialog.findViewById(R.id.editTextPostContent);
        ImageView addImageButton = dialog.findViewById(R.id.addImageButton);
        ImageView ivSelectedImage = dialog.findViewById(R.id.ivSelectedImage);
        ImageView btnRemoveImage = dialog.findViewById(R.id.btnRemoveImage);
        TextView postButton = dialog.findViewById(R.id.buttonPost);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);

        // Set user info in header
        if (userAvatar != null && !userAvatar.isEmpty()) {
            Glide.with(this)
                    .load(userAvatar)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(ivUserAvatar);
        }
        tvUserName.setText(userName != null ? userName : "Người dùng");

        // Close button
        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Display selected image if exists
        if (selectedImageUri != null) {
            ivSelectedImage.setVisibility(View.VISIBLE);
            btnRemoveImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(ivSelectedImage);
        } else {
            ivSelectedImage.setVisibility(View.GONE);
            btnRemoveImage.setVisibility(View.GONE);
        }

        addImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnRemoveImage.setOnClickListener(v -> {
            selectedImageUri = null;
            ivSelectedImage.setVisibility(View.GONE);
            btnRemoveImage.setVisibility(View.GONE);
        });

        postButton.setOnClickListener(v -> {
            String content = postContent.getText().toString().trim();
            if (content.isEmpty() && selectedImageUri == null) {
                Toast.makeText(MainActivityPost.this, "Vui lòng nhập nội dung hoặc chọn ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            postButton.setEnabled(false);

            // Fetch user info if not already fetched
            if (userName == null || userAvatar == null) {
                db.collection("Users").document(currentUser.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                userName = documentSnapshot.getString("userName");
                                userAvatar = documentSnapshot.getString("avatar");
                                proceedWithPost(content, progressBar, postButton, dialog);
                            } else {
                                progressBar.setVisibility(View.GONE);
                                postButton.setEnabled(true);
                                Toast.makeText(MainActivityPost.this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            postButton.setEnabled(true);
                            Toast.makeText(MainActivityPost.this, "Lỗi lấy thông tin người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                proceedWithPost(content, progressBar, postButton, dialog);
            }
        });

        //chỉnh kích thuóc dialog
        // Adjust dialog size
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            // Get screen dimensions
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;

            // Set dialog width to 80% of screen width
            params.width = (int) (screenWidth * 0.8);
            // Set dialog height to max 70% of screen height
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);

            /*// Optional: Add dim background behind dialog
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.6f); // Dim amount (0.0f to 1.0f)*/
        }
        dialog.show();
    }

    private void proceedWithPost(String content, ProgressBar progressBar, TextView postButton, Dialog dialog) {
        if (selectedImageUri != null) {
            uploadImageAndCreatePost(content, progressBar, postButton, dialog);
        } else {
            savePostToFirestore(content, null, progressBar, postButton, dialog);
        }
    }

    private void uploadImageAndCreatePost(String content, ProgressBar progressBar, TextView postButton, Dialog dialog) {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference storageRef = storage.getReference()
                .child("post_images")
                .child(currentUser.getUid())
                .child(fileName);

        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            savePostToFirestore(content, imageUrl, progressBar, postButton, dialog);
                        })
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            postButton.setEnabled(true);
                            Toast.makeText(MainActivityPost.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    postButton.setEnabled(true);
                    Toast.makeText(MainActivityPost.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void savePostToFirestore(String content, String imageUrl, ProgressBar progressBar, TextView postButton, Dialog dialog) {
        Map<String, Object> postData = new HashMap<>();
        postData.put("userId", currentUser.getUid());
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
                    Toast.makeText(MainActivityPost.this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    selectedImageUri = null; // Reset selected image
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    postButton.setEnabled(true);
                    Toast.makeText(MainActivityPost.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            showCreatePostDialog(); // Reopen dialog to show selected image
        }
    }
}