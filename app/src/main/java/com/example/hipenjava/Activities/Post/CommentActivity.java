package com.example.hipenjava.Activities.Post;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Models.Comment;
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
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private String postId;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText etComment;
    private TextView btnSendComment;
    private ImageView btnBack;
    
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    
    // Post details views
    private ImageView ivUserAvatar;
    private TextView tvUserName;
    private TextView tvContent;
    private ImageView ivPostImage;

    private String userName, userAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // Get post ID from intent
        postId = getIntent().getStringExtra("postId");
        if (postId == null) {
            finish();
            return;
        }

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
        recyclerView = findViewById(R.id.recyclerViewComments);
        etComment = findViewById(R.id.etComment);
        btnSendComment = findViewById(R.id.btnSendComment);
        btnBack = findViewById(R.id.btnBack);
        
        // Post details views
        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        tvUserName = findViewById(R.id.tvUserName);
        tvContent = findViewById(R.id.tvContent);
        ivPostImage = findViewById(R.id.ivPostImage);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerView.setAdapter(commentAdapter);

        // Load post details
        loadPostDetails();
        
        // Load comments
        loadComments();

        // Setup click listeners
        btnSendComment.setOnClickListener(v -> {
            String commentText = etComment.getText().toString().trim();
            if (!commentText.isEmpty()) {
                addComment(commentText);
                etComment.setText("");
            }
        });
        
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadPostDetails() {
        db.collection("posts").document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Post post = documentSnapshot.toObject(Post.class);
                    if (post != null) {
                        // Set user info
                        tvUserName.setText(post.getUserName());
                        if (post.getUserAvatar() != null && !post.getUserAvatar().isEmpty()) {
                            Glide.with(this)
                                    .load(post.getUserAvatar())
                                    .placeholder(R.drawable.default_avatar)
                                    .circleCrop()
                                    .into(ivUserAvatar);
                        } else {
                            ivUserAvatar.setImageResource(R.drawable.default_avatar);
                        }
                        
                        // Set post content
                        tvContent.setText(post.getContent());
                        
                        // Set post image if available
                        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                            ivPostImage.setVisibility(View.VISIBLE);
                            Glide.with(this)
                                    .load(post.getImageUrl())
                                    .into(ivPostImage);
                        } else {
                            ivPostImage.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadComments() {
        db.collection("comments")
                .whereEqualTo("postId", postId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(CommentActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        commentList.clear();
                        for (DocumentSnapshot document : value.getDocuments()) {
                            Comment comment = document.toObject(Comment.class);
                            if (comment != null) {
                                comment.setId(document.getId());
                                commentList.add(comment);
                            }
                        }
                        commentAdapter.notifyDataSetChanged();
                        
                        // Scroll to bottom
                        if (commentList.size() > 0) {
                            recyclerView.smoothScrollToPosition(commentList.size() - 1);
                        }
                    }
                });
    }

    /*private void addComment(String commentText) {

        String userId = currentUser.getUid();
        String userName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Người dùng";
        String userAvatar = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : "";

        Map<String, Object> commentData = new HashMap<>();
        commentData.put("postId", postId);
        commentData.put("userId", userId);
        commentData.put("userName", userName);
        commentData.put("userAvatar", userAvatar);
        commentData.put("content", commentText);
        commentData.put("timestamp", new Date());
        
        db.collection("comments")
                .add(commentData)
                .addOnSuccessListener(documentReference -> {
                    // Update post comment count
                    db.collection("posts").document(postId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                Post post = documentSnapshot.toObject(Post.class);
                                if (post != null) {
                                    int newCommentCount = post.getCommentCount() + 1;
                                    db.collection("posts").document(postId)
                                            .update("commentCount", newCommentCount);
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CommentActivity.this, "Lỗi khi thêm bình luận: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }*/
    private void addComment(String commentText) {
        String userId = currentUser.getUid();

        db.collection("Users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userName = documentSnapshot.getString("userName");
                        userAvatar = documentSnapshot.getString("avatar");

                        if (userName == null) userName = "Người dùng";
                        if (userAvatar == null) userAvatar = "";

                        Map<String, Object> commentData = new HashMap<>();
                        commentData.put("postId", postId);
                        commentData.put("userId", userId);
                        commentData.put("userName", userName);
                        commentData.put("userAvatar", userAvatar);
                        commentData.put("content", commentText);
                        commentData.put("timestamp", new Date());

                        db.collection("comments")
                                .add(commentData)
                                .addOnSuccessListener(documentReference -> {
                                    // Update comment count
                                    db.collection("posts").document(postId)
                                            .get()
                                            .addOnSuccessListener(document -> {
                                                Post post = document.toObject(Post.class);
                                                if (post != null) {
                                                    int newCommentCount = post.getCommentCount() + 1;
                                                    db.collection("posts").document(postId)
                                                            .update("commentCount", newCommentCount);
                                                }
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(CommentActivity.this, "Lỗi khi thêm bình luận: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                }); // <-- Phải có dấu này để kết thúc get()
    }

}

