/*
package com.example.hipenjava.Activities.Post;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Models.Post;
import com.example.hipenjava.Models.Post_comment;
import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.*;

public class CommentActivity extends AppCompatActivity implements CommentAdapter.OnCommentActionListener {

    private static final String TAG = "CommentActivity";

    private String postId;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Post_comment> commentList;
    private EditText etComment;
    private TextView btnSendComment;
    private ImageView btnBack;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private ImageView ivUserAvatar;
    private TextView tvUserName;
    private TextView tvContent;
    private ImageView ivPostImage;

    private String userName, userAvatar;

    private Post_comment replyToComment;
    private TextView tvReplyingTo;
    private ImageView btnCancelReply;
    private View replyIndicator;

    private Set<String> expandedCommentIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        try {
            postId = getIntent().getStringExtra("postId");
            if (postId == null) {
                Log.e(TAG, "No postId provided");
                Toast.makeText(this, "Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            db = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();
            currentUser = auth.getCurrentUser();
            if (currentUser == null) {
                Log.e(TAG, "User not logged in");
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            initViews();
            setupRecyclerView();
            setupListeners();
            loadPostDetails();
            loadComments();
            hideReplyUI();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Đã xảy ra lỗi khi tải bình luận", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewComments);
        etComment = findViewById(R.id.etComment);
        btnSendComment = findViewById(R.id.btnSendComment);
        btnBack = findViewById(R.id.btnBack);

        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        tvUserName = findViewById(R.id.tvUserName);
        tvContent = findViewById(R.id.tvContent);
        ivPostImage = findViewById(R.id.ivPostImage);

        tvReplyingTo = findViewById(R.id.tvReplyingTo);
        btnCancelReply = findViewById(R.id.btnCancelReply);
        replyIndicator = findViewById(R.id.replyIndicator);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, this);
        recyclerView.setAdapter(commentAdapter);
    }

    private void setupListeners() {
        btnSendComment.setOnClickListener(v -> {
            try {
                String commentText = etComment.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    if (replyToComment != null) {
                        addReply(replyToComment, commentText);
                    } else {
                        addComment(commentText);
                    }
                    etComment.setText("");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error sending comment: " + e.getMessage());
                Toast.makeText(this, "Không thể gửi bình luận", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
        btnCancelReply.setOnClickListener(v -> cancelReply());
    }

    private void loadPostDetails() {
        db.collection("posts").document(postId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        Post post = documentSnapshot.toObject(Post.class);
                        if (post != null) {
                            tvUserName.setText(post.getUserName() != null ? post.getUserName() : "");

                            if (post.getUserAvatar() != null && !post.getUserAvatar().isEmpty()) {
                                Glide.with(this).load(post.getUserAvatar())
                                        .placeholder(R.drawable.default_avatar).circleCrop()
                                        .into(ivUserAvatar);
                            } else {
                                ivUserAvatar.setImageResource(R.drawable.default_avatar);
                            }

                            tvContent.setText(post.getContent() != null ? post.getContent() : "");

                            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                                ivPostImage.setVisibility(View.VISIBLE);
                                Glide.with(this).load(post.getImageUrl()).into(ivPostImage);
                            } else {
                                ivPostImage.setVisibility(View.GONE);
                            }
                        } else {
                            Log.e(TAG, "Post is null");
                            Toast.makeText(this, "Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading post details: " + e.getMessage());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting post: " + e.getMessage());
                    Toast.makeText(this, "Không thể tải bài viết", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadComments() {
        db.collection("post_comments")
                .whereEqualTo("postId", postId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    try {
                        if (error != null) {
                            Log.e(TAG, "Error listening for comments: " + error.getMessage());
                            Toast.makeText(CommentActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null) {
                            Map<String, List<Post_comment>> replyMap = new HashMap<>();
                            Map<String, Post_comment> commentById = new HashMap<>();
                            List<Post_comment> allComments = new ArrayList<>();

                            for (DocumentSnapshot doc : value.getDocuments()) {
                                try {
                                    Post_comment comment = doc.toObject(Post_comment.class);
                                    if (comment != null) {
                                        comment.setId(doc.getId());

                                        String parentId = comment.getParentId();
                                        if (parentId == null || parentId.equals("null") || parentId.isEmpty()) {
                                            parentId = null;
                                            comment.setParentId(null);
                                        }

                                        comment.setReply(parentId != null);
                                        commentById.put(comment.getId(), comment);

                                        // Initialize the list if it doesn't exist
                                        if (!replyMap.containsKey(parentId)) {
                                            replyMap.put(parentId, new ArrayList<>());
                                        }
                                        replyMap.get(parentId).add(comment);

                                        allComments.add(comment);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error processing comment: " + e.getMessage());
                                }
                            }

                            for (Post_comment comment : allComments) {
                                try {
                                    int totalReplies = countAllReplies(comment.getId(), replyMap);
                                    comment.setReplyCount(totalReplies);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error counting replies: " + e.getMessage());
                                }
                            }

                            List<Post_comment> topLevelComments = new ArrayList<>();
                            List<Post_comment> roots = replyMap.get(null);
                            if (roots != null) {
                                for (Post_comment top : roots) {
                                    top.setDepth(0);
                                    topLevelComments.add(top);
                                }
                            }

                            commentAdapter.setCommentList(topLevelComments);
                            commentAdapter.setCommentMap(commentById);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in loadComments: " + e.getMessage());
                        Toast.makeText(CommentActivity.this, "Lỗi khi tải bình luận", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int countAllReplies(String parentId, Map<String, List<Post_comment>> replyMap) {
        if (parentId == null || !replyMap.containsKey(parentId)) {
            return 0;
        }

        return replyMap.get(parentId).size();
    }

    private void loadReplies(Post_comment parentComment) {
        try {
            String parentId = parentComment.getId();
            if (parentId == null || parentId.equals("null")) return;

            commentAdapter.removeReplies(parentId);

            db.collection("post_comments")
                    .whereEqualTo("postId", postId)
                    .whereEqualTo("parentId", parentId)
                    .whereEqualTo("isReply", true)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        try {
                            List<Post_comment> replies = new ArrayList<>();
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                Post_comment reply = doc.toObject(Post_comment.class);
                                if (reply != null) {
                                    reply.setId(doc.getId());
                                    reply.setReply(true);
                                    reply.setDepth(1);
                                    reply.setParentId(parentId);
                                    replies.add(reply);
                                }
                            }

                            if (!replies.isEmpty()) {
                                commentAdapter.updateViewRepliesText(parentId, "Ẩn " + replies.size() + " phản hồi");
                                commentAdapter.addReplies(parentId, replies);
                                expandedCommentIds.add(parentId);
                            } else {
                                Toast.makeText(this, "Không có phản hồi nào", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing replies: " + e.getMessage());
                            Toast.makeText(this, "Lỗi khi tải phản hồi", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading replies: " + e.getMessage());
                        Toast.makeText(this, "Không thể tải phản hồi", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in loadReplies: " + e.getMessage());
        }
    }

    private void addComment(String commentText) {
        try {
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
                            commentData.put("isReply", false);
                            commentData.put("parentId", null);
                            commentData.put("replyCount", 0);
                            commentData.put("likeCount", 0);
                            commentData.put("likedUserIds", new ArrayList<>());

                            db.collection("post_comments")
                                    .add(commentData)
                                    .addOnSuccessListener(documentReference -> {
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
                                        Log.e(TAG, "Error adding comment: " + e.getMessage());
                                        Toast.makeText(CommentActivity.this, "Lỗi khi thêm bình luận: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting user data: " + e.getMessage());
                        Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in addComment: " + e.getMessage());
        }
    }

    private void addReply(Post_comment parentComment, String replyText) {
        try {
            String userId = currentUser.getUid();

            db.collection("Users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            userName = documentSnapshot.getString("userName");
                            userAvatar = documentSnapshot.getString("avatar");

                            if (userName == null) userName = "Người dùng";
                            if (userAvatar == null) userAvatar = "";

                            Map<String, Object> replyData = new HashMap<>();
                            replyData.put("postId", postId);
                            replyData.put("userId", userId);
                            replyData.put("userName", userName);
                            replyData.put("userAvatar", userAvatar);
                            replyData.put("content", replyText);
                            replyData.put("timestamp", new Date());
                            replyData.put("isReply", true);
                            replyData.put("parentId", parentComment.getId());
                            replyData.put("replyCount", 0);
                            replyData.put("likeCount", 0);
                            replyData.put("likedUserIds", new ArrayList<>());

                            db.collection("post_comments")
                                    .add(replyData)
                                    .addOnSuccessListener(documentReference -> {
                                        int newReplyCount = parentComment.getReplyCount() + 1;
                                        db.collection("post_comments").document(parentComment.getId())
                                                .update("replyCount", newReplyCount)
                                                .addOnSuccessListener(aVoid -> {
                                                    parentComment.setReplyCount(newReplyCount);
                                                    commentAdapter.notifyDataSetChanged();
                                                    cancelReply();
                                                    expandedCommentIds.add(parentComment.getId());
                                                    loadReplies(parentComment);
                                                });

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
                                        Log.e(TAG, "Error adding reply: " + e.getMessage());
                                        Toast.makeText(CommentActivity.this, "Lỗi khi thêm phản hồi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting user data: " + e.getMessage());
                        Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in addReply: " + e.getMessage());
        }
    }

    private void showReplyUI(Post_comment comment) {
        replyToComment = comment;
        replyIndicator.setVisibility(View.VISIBLE);
        tvReplyingTo.setVisibility(View.VISIBLE);
        btnCancelReply.setVisibility(View.VISIBLE);
        tvReplyingTo.setText("Đang trả lời " + comment.getUserName());
        etComment.setHint("Viết phản hồi...");
        etComment.requestFocus();
    }

    private void hideReplyUI() {
        replyToComment = null;
        replyIndicator.setVisibility(View.GONE);
        tvReplyingTo.setVisibility(View.GONE);
        btnCancelReply.setVisibility(View.GONE);
        etComment.setHint("Viết bình luận...");
    }

    private void cancelReply() {
        hideReplyUI();
    }

    @Override
    public void onReplyClick(Post_comment comment) {
        showReplyUI(comment);
    }

    @Override
    public void onViewRepliesClick(Post_comment comment) {
        try {
            String commentId = comment.getId();

            if (expandedCommentIds.contains(commentId)) {
                expandedCommentIds.remove(commentId);
                commentAdapter.removeReplies(commentId);
                commentAdapter.updateViewRepliesText(commentId, comment.getReplyCount() + " phản hồi");
            } else {
                loadReplies(comment);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewRepliesClick: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        expandedCommentIds.clear();
    }
}
*/

package com.example.hipenjava.Activities.Post;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Models.Post;
import com.example.hipenjava.Models.Post_comment;
import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.*;

public class CommentActivity extends AppCompatActivity implements CommentAdapter.OnCommentActionListener {

    private static final String TAG = "CommentActivity";

    private String postId;
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Post_comment> commentList;
    private EditText etComment;
    private TextView btnSendComment;
    private ImageView btnBack;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private ImageView ivUserAvatar;
    private TextView tvUserName;
    private TextView tvContent;
    private ImageView ivPostImage;

    private String userName, userAvatar;

    private Post_comment replyToComment;
    private TextView tvReplyingTo;
    private ImageView btnCancelReply;
    private View replyIndicator;

    private Set<String> expandedCommentIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        try {
            postId = getIntent().getStringExtra("postId");
            if (postId == null) {
                Log.e(TAG, "No postId provided");
                Toast.makeText(this, "Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            db = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();
            currentUser = auth.getCurrentUser();
            if (currentUser == null) {
                Log.e(TAG, "User not logged in");
                Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            initViews();
            setupRecyclerView();
            setupListeners();
            loadPostDetails();
            loadComments();
            hideReplyUI();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            Toast.makeText(this, "Đã xảy ra lỗi khi tải bình luận", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewComments);
        etComment = findViewById(R.id.etComment);
        btnSendComment = findViewById(R.id.btnSendComment);
        btnBack = findViewById(R.id.btnBack);

        ivUserAvatar = findViewById(R.id.ivUserAvatar);
        tvUserName = findViewById(R.id.tvUserName);
        tvContent = findViewById(R.id.tvContent);
        ivPostImage = findViewById(R.id.ivPostImage);

        tvReplyingTo = findViewById(R.id.tvReplyingTo);
        btnCancelReply = findViewById(R.id.btnCancelReply);
        replyIndicator = findViewById(R.id.replyIndicator);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, this);
        recyclerView.setAdapter(commentAdapter);
    }

    private void setupListeners() {
        btnSendComment.setOnClickListener(v -> {
            try {
                String commentText = etComment.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    if (replyToComment != null) {
                        addReply(replyToComment, commentText);
                    } else {
                        addComment(commentText);
                    }
                    etComment.setText("");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error sending comment: " + e.getMessage());
                Toast.makeText(this, "Không thể gửi bình luận", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
        btnCancelReply.setOnClickListener(v -> cancelReply());
    }

    private void loadPostDetails() {
        db.collection("posts").document(postId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    try {
                        Post post = documentSnapshot.toObject(Post.class);
                        if (post != null) {
                            tvUserName.setText(post.getUserName() != null ? post.getUserName() : "");

                            if (post.getUserAvatar() != null && !post.getUserAvatar().isEmpty()) {
                                Glide.with(this).load(post.getUserAvatar())
                                        .placeholder(R.drawable.default_avatar).circleCrop()
                                        .into(ivUserAvatar);
                            } else {
                                ivUserAvatar.setImageResource(R.drawable.default_avatar);
                            }

                            tvContent.setText(post.getContent() != null ? post.getContent() : "");

                            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                                ivPostImage.setVisibility(View.VISIBLE);
                                Glide.with(this).load(post.getImageUrl()).into(ivPostImage);
                            } else {
                                ivPostImage.setVisibility(View.GONE);
                            }
                        } else {
                            Log.e(TAG, "Post is null");
                            Toast.makeText(this, "Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading post details: " + e.getMessage());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting post: " + e.getMessage());
                    Toast.makeText(this, "Không thể tải bài viết", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadComments() {
        db.collection("post_comments")
                .whereEqualTo("postId", postId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    try {
                        if (error != null) {
                            Log.e(TAG, "Error listening for comments: " + error.getMessage());
                            Toast.makeText(CommentActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null) {
                            Map<String, List<Post_comment>> replyMap = new HashMap<>();
                            Map<String, Post_comment> commentById = new HashMap<>();
                            List<Post_comment> allComments = new ArrayList<>();

                            for (DocumentSnapshot doc : value.getDocuments()) {
                                try {
                                    Post_comment comment = doc.toObject(Post_comment.class);
                                    if (comment != null) {
                                        comment.setId(doc.getId());

                                        // Check if current user has liked this comment
                                        List<String> likedUserIds = comment.getLikedUserIds();
                                        if (likedUserIds != null && currentUser != null) {
                                            comment.setLikedByCurrentUser(likedUserIds.contains(currentUser.getUid()));
                                        }

                                        String parentId = comment.getParentId();
                                        if (parentId == null || parentId.equals("null") || parentId.isEmpty()) {
                                            parentId = null;
                                            comment.setParentId(null);
                                        }

                                        comment.setReply(parentId != null);
                                        commentById.put(comment.getId(), comment);

                                        // Initialize the list if it doesn't exist
                                        if (!replyMap.containsKey(parentId)) {
                                            replyMap.put(parentId, new ArrayList<>());
                                        }
                                        replyMap.get(parentId).add(comment);

                                        allComments.add(comment);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error processing comment: " + e.getMessage());
                                }
                            }

                            for (Post_comment comment : allComments) {
                                try {
                                    int totalReplies = countAllReplies(comment.getId(), replyMap);
                                    comment.setReplyCount(totalReplies);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error counting replies: " + e.getMessage());
                                }
                            }

                            List<Post_comment> topLevelComments = new ArrayList<>();
                            List<Post_comment> roots = replyMap.get(null);
                            if (roots != null) {
                                for (Post_comment top : roots) {
                                    top.setDepth(0);
                                    topLevelComments.add(top);
                                }
                            }

                            commentAdapter.setCommentList(topLevelComments);
                            commentAdapter.setCommentMap(commentById);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in loadComments: " + e.getMessage());
                        Toast.makeText(CommentActivity.this, "Lỗi khi tải bình luận", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int countAllReplies(String parentId, Map<String, List<Post_comment>> replyMap) {
        if (parentId == null || !replyMap.containsKey(parentId)) {
            return 0;
        }

        return replyMap.get(parentId).size();
    }

    private void loadReplies(Post_comment parentComment) {
        try {
            String parentId = parentComment.getId();
            if (parentId == null || parentId.equals("null")) return;

            commentAdapter.removeReplies(parentId);

            db.collection("post_comments")
                    .whereEqualTo("postId", postId)
                    .whereEqualTo("parentId", parentId)
                    .whereEqualTo("isReply", true)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        try {
                            List<Post_comment> replies = new ArrayList<>();
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                Post_comment reply = doc.toObject(Post_comment.class);
                                if (reply != null) {
                                    reply.setId(doc.getId());
                                    reply.setReply(true);
                                    reply.setDepth(1);
                                    reply.setParentId(parentId);

                                    // Check if current user has liked this reply
                                    List<String> likedUserIds = reply.getLikedUserIds();
                                    if (likedUserIds != null && currentUser != null) {
                                        reply.setLikedByCurrentUser(likedUserIds.contains(currentUser.getUid()));
                                    }

                                    replies.add(reply);
                                }
                            }

                            if (!replies.isEmpty()) {
                                commentAdapter.updateViewRepliesText(parentId, "Ẩn " + replies.size() + " phản hồi");
                                commentAdapter.addReplies(parentId, replies);
                                expandedCommentIds.add(parentId);
                            } else {
                                Toast.makeText(this, "Không có phản hồi nào", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing replies: " + e.getMessage());
                            Toast.makeText(this, "Lỗi khi tải phản hồi", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading replies: " + e.getMessage());
                        Toast.makeText(this, "Không thể tải phản hồi", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in loadReplies: " + e.getMessage());
        }
    }

    private void addComment(String commentText) {
        try {
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
                            commentData.put("isReply", false);
                            commentData.put("parentId", null);
                            commentData.put("replyCount", 0);
                            commentData.put("likeCount", 0);
                            commentData.put("likedUserIds", new ArrayList<>());

                            db.collection("post_comments")
                                    .add(commentData)
                                    .addOnSuccessListener(documentReference -> {
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
                                        Log.e(TAG, "Error adding comment: " + e.getMessage());
                                        Toast.makeText(CommentActivity.this, "Lỗi khi thêm bình luận: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting user data: " + e.getMessage());
                        Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in addComment: " + e.getMessage());
        }
    }

    private void addReply(Post_comment parentComment, String replyText) {
        try {
            String userId = currentUser.getUid();

            db.collection("Users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            userName = documentSnapshot.getString("userName");
                            userAvatar = documentSnapshot.getString("avatar");

                            if (userName == null) userName = "Người dùng";
                            if (userAvatar == null) userAvatar = "";

                            Map<String, Object> replyData = new HashMap<>();
                            replyData.put("postId", postId);
                            replyData.put("userId", userId);
                            replyData.put("userName", userName);
                            replyData.put("userAvatar", userAvatar);
                            replyData.put("content", replyText);
                            replyData.put("timestamp", new Date());
                            replyData.put("isReply", true);
                            replyData.put("parentId", parentComment.getId());
                            replyData.put("replyCount", 0);
                            replyData.put("likeCount", 0);
                            replyData.put("likedUserIds", new ArrayList<>());

                            db.collection("post_comments")
                                    .add(replyData)
                                    .addOnSuccessListener(documentReference -> {
                                        int newReplyCount = parentComment.getReplyCount() + 1;
                                        db.collection("post_comments").document(parentComment.getId())
                                                .update("replyCount", newReplyCount)
                                                .addOnSuccessListener(aVoid -> {
                                                    parentComment.setReplyCount(newReplyCount);
                                                    commentAdapter.notifyDataSetChanged();
                                                    cancelReply();
                                                    expandedCommentIds.add(parentComment.getId());
                                                    loadReplies(parentComment);
                                                });

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
                                        Log.e(TAG, "Error adding reply: " + e.getMessage());
                                        Toast.makeText(CommentActivity.this, "Lỗi khi thêm phản hồi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting user data: " + e.getMessage());
                        Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in addReply: " + e.getMessage());
        }
    }

    private void showReplyUI(Post_comment comment) {
        replyToComment = comment;
        replyIndicator.setVisibility(View.VISIBLE);
        tvReplyingTo.setVisibility(View.VISIBLE);
        btnCancelReply.setVisibility(View.VISIBLE);
        tvReplyingTo.setText("Đang trả lời " + comment.getUserName());
        etComment.setHint("Viết phản hồi...");
        etComment.requestFocus();
    }

    private void hideReplyUI() {
        replyToComment = null;
        replyIndicator.setVisibility(View.GONE);
        tvReplyingTo.setVisibility(View.GONE);
        btnCancelReply.setVisibility(View.GONE);
        etComment.setHint("Viết bình luận...");
    }

    private void cancelReply() {
        hideReplyUI();
    }

    @Override
    public void onReplyClick(Post_comment comment) {
        showReplyUI(comment);
    }

    @Override
    public void onViewRepliesClick(Post_comment comment) {
        try {
            String commentId = comment.getId();

            if (expandedCommentIds.contains(commentId)) {
                expandedCommentIds.remove(commentId);
                commentAdapter.removeReplies(commentId);
                commentAdapter.updateViewRepliesText(commentId, comment.getReplyCount() + " phản hồi");
            } else {
                loadReplies(comment);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onViewRepliesClick: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        expandedCommentIds.clear();
    }
}
