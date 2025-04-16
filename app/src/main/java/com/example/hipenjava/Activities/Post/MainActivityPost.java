// MainActivityPost.java (Đã chỉnh sửa để dùng ảnh từ Firestore collection "images")

package com.example.hipenjava.Activities.Post;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivityPost extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private TextView createPostPrompt;
    private ImageView btnBack;
    private ImageView btnGallery;
    private ImageView ivCurrentUserAvatar;

    private Uri selectedImageUri;
    private String selectedImageUrlFromFirestore = null;
    private String userAvatar, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_post);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerViewPosts);
        createPostPrompt = findViewById(R.id.createPostPrompt);
        btnBack = findViewById(R.id.btnBack);
        btnGallery = findViewById(R.id.btnGallery);
        ivCurrentUserAvatar = findViewById(R.id.ivCurrentUserAvatar);

        db.collection("Users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userAvatar = documentSnapshot.getString("avatar");
                        userName = documentSnapshot.getString("userName");
                        Glide.with(this).load(userAvatar)
                                .placeholder(R.drawable.default_avatar)
                                .into(ivCurrentUserAvatar);
                    }
                });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(postAdapter);

        loadPosts();

        createPostPrompt.setOnClickListener(v -> showCreatePostDialog());
        btnBack.setOnClickListener(v -> finish());
        btnGallery.setOnClickListener(v -> {
            // Mở thư viện ảnh nghệ thuật nếu muốn
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
                        Toast.makeText(this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        postList.clear();
                        List<String> postIds = new ArrayList<>();

                        for (DocumentSnapshot document : value.getDocuments()) {
                            Post post = document.toObject(Post.class);
                            if (post != null) {
                                post.setId(document.getId());
                                postList.add(post);
                                postIds.add(post.getId());
                            }
                        }

                        if (postIds.isEmpty()) {
                            postAdapter.notifyDataSetChanged();
                            return;
                        }

                        // Lấy danh sách các post đã được like bởi user hiện tại
                        db.collection("likes")
                                .whereEqualTo("userId", userId)
                                .whereIn("postId", postIds)
                                .get()
                                .addOnSuccessListener(likesSnapshot -> {
                                    Set<String> likedPostIds = new HashSet<>();
                                    for (DocumentSnapshot likeDoc : likesSnapshot.getDocuments()) {
                                        likedPostIds.add(likeDoc.getString("postId"));
                                    }

                                    // Đánh dấu những bài đã like
                                    for (Post post : postList) {
                                        post.setLikedByCurrentUser(likedPostIds.contains(post.getId()));
                                    }

                                    postAdapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi khi kiểm tra like: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    postAdapter.notifyDataSetChanged(); // vẫn hiển thị bài viết
                                });
                    }
                });
    }


    private void showCreatePostDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_post);

        ImageView ivUserAvatar = dialog.findViewById(R.id.ivUserAvatar);
        TextView tvUserName = dialog.findViewById(R.id.tvUserName);
        TextView postContent = dialog.findViewById(R.id.editTextPostContent);
        ImageView addImageButton = dialog.findViewById(R.id.addImageButton);
        ImageView ivSelectedImage = dialog.findViewById(R.id.ivSelectedImage);
        ImageView btnRemoveImage = dialog.findViewById(R.id.btnRemoveImage);
        TextView postButton = dialog.findViewById(R.id.buttonPost);
        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);



        Glide.with(this).load(userAvatar).placeholder(R.drawable.default_avatar).into(ivUserAvatar);
        tvUserName.setText(userName != null ? userName : "Người dùng");

        addImageButton.setOnClickListener(v -> showImagePickerDialog(ivSelectedImage, btnRemoveImage));

        btnRemoveImage.setOnClickListener(v -> {
            selectedImageUrlFromFirestore = null;
            ivSelectedImage.setVisibility(View.GONE);
            btnRemoveImage.setVisibility(View.GONE);
        });

        // Close button
        btnClose.setOnClickListener(v -> dialog.dismiss());


        postButton.setOnClickListener(v -> {
            String content = postContent.getText().toString().trim();
            if (content.isEmpty() && selectedImageUrlFromFirestore == null) {
                Toast.makeText(this, "Vui lòng nhập nội dung hoặc chọn ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            postButton.setEnabled(false);
            savePostToFirestore(content, selectedImageUrlFromFirestore, progressBar, postButton, dialog);
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
        }
        dialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    private void showImagePickerDialog(ImageView ivSelectedImage, ImageView btnRemoveImage) {
        Dialog imageDialog = new Dialog(this);
        imageDialog.setContentView(R.layout.dialog_image_picker);

        RecyclerView recyclerView = imageDialog.findViewById(R.id.recyclerViewImages);
        TextView txtNoImages = imageDialog.findViewById(R.id.txtNoImages);
        ImageView btnCloseDialog = imageDialog.findViewById(R.id.btnCloseDialog);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        List<String> imageUrls = new ArrayList<>();
        ImageAdapter imageAdapter = new ImageAdapter(imageUrls, url -> {
            selectedImageUrlFromFirestore = url;
            Glide.with(this).load(url).into(ivSelectedImage);
            ivSelectedImage.setVisibility(View.VISIBLE);
            btnRemoveImage.setVisibility(View.VISIBLE);
            imageDialog.dismiss();
        });

        recyclerView.setAdapter(imageAdapter);

        btnCloseDialog.setOnClickListener(v -> imageDialog.dismiss());

        db.collection("images")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot) {
                        String url = doc.getString("imageUrl");
                        if (url != null) imageUrls.add(url);
                    }
                    imageAdapter.notifyDataSetChanged();

                    // Hiển thị hoặc ẩn tùy theo danh sách ảnh
                    if (imageUrls.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        txtNoImages.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        txtNoImages.setVisibility(View.GONE);
                    }
                });

        imageDialog.show();
    }


    private void savePostToFirestore(String content, String imageUrl, ProgressBar progressBar, TextView postButton, Dialog dialog) {
        Map<String, Object> postData = new HashMap<>();
        postData.put("userId", currentUser.getUid());
        postData.put("userName", userName);
        postData.put("userAvatar", userAvatar);
        postData.put("content", content);
        postData.put("imageUrl", imageUrl);
        postData.put("timestamp", new Date());
        postData.put("likes", 0);
        postData.put("comments", 0);

        db.collection("posts")
                .add(postData)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    postButton.setEnabled(true);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

