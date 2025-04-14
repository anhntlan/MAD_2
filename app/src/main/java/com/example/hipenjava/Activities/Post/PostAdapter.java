package com.example.hipenjava.Activities.Post;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Models.Post;
import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String userName, userAvatar;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        
        // Set user info
        holder.tvUserName.setText(post.getUserName());
        if (post.getUserAvatar() != null && !post.getUserAvatar().isEmpty()) {
            Glide.with(context)
                    .load(post.getUserAvatar())
                    .placeholder(R.drawable.default_avatar)
                    .circleCrop()
                    .into(holder.ivUserAvatar);
        } else {
            holder.ivUserAvatar.setImageResource(R.drawable.default_avatar);
        }
        
        // Set post content
        holder.tvContent.setText(post.getContent());
        
        // Set post image if available
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.ivPostImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(post.getImageUrl())
                    .into(holder.ivPostImage);
        } else {
            holder.ivPostImage.setVisibility(View.GONE);
        }
        
        // Set timestamp
        if (post.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String formattedDate = sdf.format(post.getTimestamp().toDate());
            holder.tvTimestamp.setText(formattedDate);
        } else {
            holder.tvTimestamp.setText("");
        }
        
        // Set like count
        holder.tvLikeCount.setText(String.valueOf(post.getLikeCount()));
        
        // Set comment count
        holder.tvCommentCount.setText(String.valueOf(post.getCommentCount()));
        
        // Set like button state
        if (post.isLikedByCurrentUser()) {
            holder.ivLike.setImageResource(R.drawable.ic_like_filled);
        } else {
            holder.ivLike.setImageResource(R.drawable.ic_like);
        }
        
        // Handle like button click
        holder.ivLike.setOnClickListener(v -> {
            toggleLike(post, holder);
        });
        
        // Handle comment button click
        holder.ivComment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", post.getId());
            context.startActivity(intent);
        });
        
        // Handle share button click
        holder.ivShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, post.getContent());
            context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
        });
        
        // Handle comment input
        holder.btnSendComment.setOnClickListener(v -> {
            String commentText = holder.etComment.getText().toString().trim();
            if (!commentText.isEmpty()) {
                addComment(post.getId(), commentText);
                holder.etComment.setText("");
            }
        });
    }

    private void toggleLike(Post post, PostViewHolder holder) {
        String userId = auth.getCurrentUser().getUid();
        String postId = post.getId();
        
        db.collection("likes")
                .whereEqualTo("postId", postId)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // User hasn't liked the post yet, add like
                        Map<String, Object> likeData = new HashMap<>();
                        likeData.put("postId", postId);
                        likeData.put("userId", userId);
                        likeData.put("timestamp", new Date());
                        
                        db.collection("likes")
                                .add(likeData)
                                .addOnSuccessListener(documentReference -> {
                                    // Update post like count
                                    int newLikeCount = post.getLikeCount() + 1;
                                    db.collection("posts").document(postId)
                                            .update("likeCount", newLikeCount)
                                            .addOnSuccessListener(aVoid -> {
                                                post.setLikeCount(newLikeCount);
                                                post.setLikedByCurrentUser(true);
                                                holder.tvLikeCount.setText(String.valueOf(newLikeCount));
                                                holder.ivLike.setImageResource(R.drawable.ic_like_filled);
                                            });
                                });
                    } else {
                        // User already liked the post, remove like
                        String likeId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        db.collection("likes").document(likeId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Update post like count
                                    int newLikeCount = Math.max(0, post.getLikeCount() - 1);
                                    db.collection("posts").document(postId)
                                            .update("likeCount", newLikeCount)
                                            .addOnSuccessListener(aVoid1 -> {
                                                post.setLikeCount(newLikeCount);
                                                post.setLikedByCurrentUser(false);
                                                holder.tvLikeCount.setText(String.valueOf(newLikeCount));
                                                holder.ivLike.setImageResource(R.drawable.ic_like);
                                            });
                                });
                    }
                });
    }

    private void addComment(String postId, String commentText) {
        String userId = auth.getCurrentUser().getUid();
        /*String userName = auth.getCurrentUser().getDisplayName();
        String userAvatar = auth.getCurrentUser().getPhotoUrl() != null ? 
                auth.getCurrentUser().getPhotoUrl().toString() : "";*/
        db.collection("Users").document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userName = documentSnapshot.getString("userName");
                        userAvatar = documentSnapshot.getString("avatar");

                        if (userName == null) userName = "Người dùng";
                        if (userAvatar == null) userAvatar = "";
                    }
                });
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
                    
                    // Redirect to comments page
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("postId", postId);
                    context.startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi khi thêm bình luận: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    /*private void addComment(String commentText) {
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
    }*/

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar, ivPostImage, ivLike, ivComment, ivShare;
        TextView tvUserName, tvTimestamp, tvContent, tvLikeCount, tvCommentCount;
        EditText etComment;
        TextView btnSendComment;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivComment = itemView.findViewById(R.id.ivComment);
            ivShare = itemView.findViewById(R.id.ivShare);
            
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            
            etComment = itemView.findViewById(R.id.etComment);
            btnSendComment = itemView.findViewById(R.id.btnSendComment);
        }
    }
}
