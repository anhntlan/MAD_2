/*
package com.example.hipenjava.Activities.Post;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Models.Post_comment;
import com.example.hipenjava.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_COMMENT = 0;
    private static final int TYPE_REPLY = 1;
    private static final String TAG = "CommentAdapter"; // For logging

    private Context context;
    private List<Post_comment> commentList;
    private OnCommentActionListener listener;

    private Map<String, String> viewRepliesTextMap = new HashMap<>();
    private Map<String, Post_comment> commentMap = new HashMap<>();

    public CommentAdapter(Context context, List<Post_comment> commentList, OnCommentActionListener listener) {
        this.context = context;
        this.commentList = commentList != null ? commentList : new ArrayList<>();
        this.listener = listener;
    }

    public interface OnCommentActionListener {
        void onReplyClick(Post_comment comment);
        void onViewRepliesClick(Post_comment comment);
    }

    public void setCommentList(List<Post_comment> list) {
        commentList = list != null ? list : new ArrayList<>();
        commentMap.clear();
        for (Post_comment c : commentList) {
            if (c.getId() != null) {
                commentMap.put(c.getId(), c);
            }
        }
        notifyDataSetChanged();
    }

    public void setCommentMap(Map<String, Post_comment> map) {
        this.commentMap = map != null ? map : new HashMap<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            if (viewType == TYPE_REPLY) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_comment_reply, parent, false);
                return new ReplyViewHolder(view);
            } else {
                View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
                return new CommentViewHolder(view);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateViewHolder: " + e.getMessage());
            // Fallback to comment view if there's an error
            View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (position >= commentList.size()) {
                Log.e(TAG, "Position out of bounds: " + position + ", size: " + commentList.size());
                return;
            }

            Post_comment comment = commentList.get(position);
            if (comment == null) {
                Log.e(TAG, "Comment at position " + position + " is null");
                return;
            }

            TextView tvReply = null;
            TextView tvViewReplies = null;
            ImageView ivLike = null;
            TextView tvLikeCount = null;

            if (holder instanceof CommentViewHolder) {
                CommentViewHolder commentHolder = (CommentViewHolder) holder;
                bindCommentData(commentHolder, comment);
                tvReply = commentHolder.tvReply;
                tvViewReplies = commentHolder.tvViewReplies;
                ivLike = commentHolder.ivLike;
                tvLikeCount = commentHolder.tvLikeCount;

            } else if (holder instanceof ReplyViewHolder) {
                ReplyViewHolder replyHolder = (ReplyViewHolder) holder;
                bindReplyData(replyHolder, comment);
                tvReply = replyHolder.tvReply;
                tvViewReplies = replyHolder.tvViewReplies;
                ivLike = replyHolder.ivLike;
                tvLikeCount = replyHolder.tvLikeCount;

                try {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
                    int indent = Math.max(0, comment.getDepth() * 32); // Ensure indent is not negative
                    layoutParams.setMarginStart(indent);
                    holder.itemView.setLayoutParams(layoutParams);
                } catch (Exception e) {
                    Log.e(TAG, "Error setting margin: " + e.getMessage());
                }
            }

            // Handle like functionality only if ivLike and tvLikeCount are not null
            if (ivLike != null && tvLikeCount != null) {
                try {
                    boolean isLiked = comment.isLikedByCurrentUser();
                    ivLike.setImageResource(isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

                    try {
                        ivLike.setColorFilter(context.getColor(isLiked ? R.color.red : R.color.gray));
                    } catch (Exception e) {
                        Log.e(TAG, "Error setting color filter: " + e.getMessage());
                        // Fallback if color resources are not found
                    }

                    tvLikeCount.setText(String.valueOf(comment.getLikeCount()));

                    final int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        ivLike.setOnClickListener(v -> toggleLike(comment, adapterPosition));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error setting like UI: " + e.getMessage());
                }
            }

            if (tvReply != null) {
                tvReply.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onReplyClick(comment);
                    }
                });
            }

            if (tvViewReplies != null) {
                if (comment.hasReplies()) {
                    tvViewReplies.setVisibility(View.VISIBLE);
                    String buttonText = viewRepliesTextMap.containsKey(comment.getId())
                            ? viewRepliesTextMap.get(comment.getId())
                            : comment.getReplyCount() + " phản hồi";
                    tvViewReplies.setText(buttonText);

                    tvViewReplies.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onViewRepliesClick(comment);
                        }
                    });
                } else {
                    tvViewReplies.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onBindViewHolder: " + e.getMessage());
        }
    }

    private void toggleLike(Post_comment comment, int position) {
        try {
            boolean currentlyLiked = comment.isLikedByCurrentUser();
            int newLikeCount = comment.getLikeCount() + (currentlyLiked ? -1 : 1);

            comment.setLikedByCurrentUser(!currentlyLiked);
            comment.setLikeCount(newLikeCount);

            if (position >= 0 && position < commentList.size()) {
                notifyItemChanged(position);
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("post_comments").document(comment.getId())
                    .update("likeCount", newLikeCount)
                    .addOnFailureListener(e -> Log.e(TAG, "Lỗi cập nhật likeCount: " + e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, "Error in toggleLike: " + e.getMessage());
        }
    }

    public void updateViewRepliesText(String commentId, String newText) {
        if (commentId == null) return;

        viewRepliesTextMap.put(commentId, newText);
        for (int i = 0; i < commentList.size(); i++) {
            Post_comment comment = commentList.get(i);
            if (comment != null && comment.getId() != null && comment.getId().equals(commentId)) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    private void bindCommentData(CommentViewHolder holder, Post_comment comment) {
        try {
            // Set user name with null check
            holder.tvUserName.setText(comment.getUserName() != null ? comment.getUserName() : "");

            // Load avatar with null check
            if (comment.getUserAvatar() != null && !comment.getUserAvatar().isEmpty()) {
                Glide.with(context)
                        .load(comment.getUserAvatar())
                        .placeholder(R.drawable.default_avatar)
                        .circleCrop()
                        .into(holder.ivUserAvatar);
            } else {
                holder.ivUserAvatar.setImageResource(R.drawable.default_avatar);
            }

            // Set content with null check
            holder.tvContent.setText(comment.getContent() != null ? comment.getContent() : "");

            // Set timestamp with null check
            if (comment.getTimestamp() != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    String formattedDate = sdf.format(comment.getTimestamp().toDate());
                    holder.tvTimestamp.setText(formattedDate);
                } catch (Exception e) {
                    Log.e(TAG, "Error formatting date: " + e.getMessage());
                    holder.tvTimestamp.setText("");
                }
            } else {
                holder.tvTimestamp.setText("");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in bindCommentData: " + e.getMessage());
        }
    }

    private void bindReplyData(ReplyViewHolder holder, Post_comment reply) {
        try {
            // Set user name with null check
            holder.tvUserName.setText(reply.getUserName() != null ? reply.getUserName() : "");

            // Load avatar with null check
            if (reply.getUserAvatar() != null && !reply.getUserAvatar().isEmpty()) {
                Glide.with(context)
                        .load(reply.getUserAvatar())
                        .placeholder(R.drawable.default_avatar)
                        .circleCrop()
                        .into(holder.ivUserAvatar);
            } else {
                holder.ivUserAvatar.setImageResource(R.drawable.default_avatar);
            }

            // Set content with null check
            holder.tvContent.setText(reply.getContent() != null ? reply.getContent() : "");

            // Set timestamp with null check
            if (reply.getTimestamp() != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    String formattedDate = sdf.format(reply.getTimestamp().toDate());
                    holder.tvTimestamp.setText(formattedDate);
                } catch (Exception e) {
                    Log.e(TAG, "Error formatting date: " + e.getMessage());
                    holder.tvTimestamp.setText("");
                }
            } else {
                holder.tvTimestamp.setText("");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in bindReplyData: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (commentList == null || position >= commentList.size()) {
            return TYPE_COMMENT; // Default to comment type
        }

        Post_comment comment = commentList.get(position);
        return comment != null && comment.getDepth() > 0 ? TYPE_REPLY : TYPE_COMMENT;
    }

    public void addComment(Post_comment comment) {
        if (comment == null) return;

        if (commentList == null) {
            commentList = new ArrayList<>();
        }

        commentList.add(comment);
        notifyItemInserted(commentList.size() - 1);
    }

    public void addReplies(String parentId, List<Post_comment> replies) {
        if (parentId == null || replies == null || replies.isEmpty() || commentList == null) return;

        int parentPosition = -1;
        for (int i = 0; i < commentList.size(); i++) {
            Post_comment comment = commentList.get(i);
            if (comment != null && comment.getId() != null && comment.getId().equals(parentId)) {
                parentPosition = i;
                break;
            }
        }

        if (parentPosition != -1) {
            commentList.addAll(parentPosition + 1, replies);
            notifyItemRangeInserted(parentPosition + 1, replies.size());
        }
    }

    public void removeReplies(String parentId) {
        if (parentId == null || commentList == null) return;

        List<Integer> positionsToRemove = new ArrayList<>();
        for (int i = 0; i < commentList.size(); i++) {
            Post_comment comment = commentList.get(i);
            if (comment != null && isDescendantOf(comment, parentId)) {
                positionsToRemove.add(i);
            }
        }

        for (int i = positionsToRemove.size() - 1; i >= 0; i--) {
            int position = positionsToRemove.get(i);
            if (position >= 0 && position < commentList.size()) {
                commentList.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    private boolean isDescendantOf(Post_comment comment, String ancestorId) {
        if (comment == null || ancestorId == null) return false;

        String currentParentId = comment.getParentId();
        while (currentParentId != null) {
            if (currentParentId.equals(ancestorId)) return true;

            Post_comment parentComment = commentMap.get(currentParentId);
            if (parentComment != null) {
                currentParentId = parentComment.getParentId();
            } else {
                break;
            }
        }
        return false;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar;
        TextView tvUserName, tvTimestamp, tvContent, tvReply, tvViewReplies;
        ImageView ivLike;
        TextView tvLikeCount;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvReply = itemView.findViewById(R.id.tvReply);
            tvViewReplies = itemView.findViewById(R.id.tvViewReplies);
            ivLike = itemView.findViewById(R.id.ivLike);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
        }
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar;
        TextView tvUserName, tvTimestamp, tvContent, tvReply, tvViewReplies;
        ImageView ivLike;
        TextView tvLikeCount;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvReply = itemView.findViewById(R.id.tvReply);
            tvViewReplies = itemView.findViewById(R.id.tvViewReplies);
            ivLike = itemView.findViewById(R.id.ivLike);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
        }
    }
}
*/
package com.example.hipenjava.Activities.Post;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Models.Post_comment;
import com.example.hipenjava.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.*;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_COMMENT = 0;
    private static final int TYPE_REPLY = 1;
    private static final String TAG = "CommentAdapter"; // For logging

    private Context context;
    private List<Post_comment> commentList;
    private OnCommentActionListener listener;

    private Map<String, String> viewRepliesTextMap = new HashMap<>();
    private Map<String, Post_comment> commentMap = new HashMap<>();

    public CommentAdapter(Context context, List<Post_comment> commentList, OnCommentActionListener listener) {
        this.context = context;
        this.commentList = commentList != null ? commentList : new ArrayList<>();
        this.listener = listener;
    }

    public interface OnCommentActionListener {
        void onReplyClick(Post_comment comment);
        void onViewRepliesClick(Post_comment comment);
    }

    public void setCommentList(List<Post_comment> list) {
        commentList = list != null ? list : new ArrayList<>();
        commentMap.clear();
        for (Post_comment c : commentList) {
            if (c.getId() != null) {
                commentMap.put(c.getId(), c);
            }
        }
        notifyDataSetChanged();
    }

    public void setCommentMap(Map<String, Post_comment> map) {
        this.commentMap = map != null ? map : new HashMap<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            if (viewType == TYPE_REPLY) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_comment_reply, parent, false);
                return new ReplyViewHolder(view);
            } else {
                View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
                return new CommentViewHolder(view);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateViewHolder: " + e.getMessage());
            // Fallback to comment view if there's an error
            View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (position >= commentList.size()) {
                Log.e(TAG, "Position out of bounds: " + position + ", size: " + commentList.size());
                return;
            }

            Post_comment comment = commentList.get(position);
            if (comment == null) {
                Log.e(TAG, "Comment at position " + position + " is null");
                return;
            }

            TextView tvReply = null;
            TextView tvViewReplies = null;
            ImageView ivLike = null;
            TextView tvLikeCount = null;

            if (holder instanceof CommentViewHolder) {
                CommentViewHolder commentHolder = (CommentViewHolder) holder;
                bindCommentData(commentHolder, comment);
                tvReply = commentHolder.tvReply;
                tvViewReplies = commentHolder.tvViewReplies;
                ivLike = commentHolder.ivLike;
                tvLikeCount = commentHolder.tvLikeCount;

            } else if (holder instanceof ReplyViewHolder) {
                ReplyViewHolder replyHolder = (ReplyViewHolder) holder;
                bindReplyData(replyHolder, comment);
                tvReply = replyHolder.tvReply;
                tvViewReplies = replyHolder.tvViewReplies;
                ivLike = replyHolder.ivLike;
                tvLikeCount = replyHolder.tvLikeCount;

                try {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
                    int indent = Math.max(0, comment.getDepth() * 32); // Ensure indent is not negative
                    layoutParams.setMarginStart(indent);
                    holder.itemView.setLayoutParams(layoutParams);
                } catch (Exception e) {
                    Log.e(TAG, "Error setting margin: " + e.getMessage());
                }
            }

            // Handle like functionality only if ivLike and tvLikeCount are not null
            if (ivLike != null && tvLikeCount != null) {
                try {
                    boolean isLiked = comment.isLikedByCurrentUser();
                    ivLike.setImageResource(isLiked ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

                    try {
                        ivLike.setColorFilter(context.getColor(isLiked ? R.color.red : R.color.gray));
                    } catch (Exception e) {
                        Log.e(TAG, "Error setting color filter: " + e.getMessage());
                        // Fallback if color resources are not found
                    }

                    tvLikeCount.setText(String.valueOf(comment.getLikeCount()));

                    final int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        ivLike.setOnClickListener(v -> toggleLike(comment, adapterPosition));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error setting like UI: " + e.getMessage());
                }
            }

            if (tvReply != null) {
                tvReply.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onReplyClick(comment);
                    }
                });
            }

            if (tvViewReplies != null) {
                if (comment.hasReplies()) {
                    tvViewReplies.setVisibility(View.VISIBLE);
                    String buttonText = viewRepliesTextMap.containsKey(comment.getId())
                            ? viewRepliesTextMap.get(comment.getId())
                            : comment.getReplyCount() + " phản hồi";
                    tvViewReplies.setText(buttonText);

                    tvViewReplies.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onViewRepliesClick(comment);
                        }
                    });
                } else {
                    tvViewReplies.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onBindViewHolder: " + e.getMessage());
        }
    }

    private void toggleLike(Post_comment comment, int position) {
        try {
            boolean currentlyLiked = comment.isLikedByCurrentUser();
            int newLikeCount = comment.getLikeCount() + (currentlyLiked ? -1 : 1);

            // Get current user ID
            String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Update the liked state
            comment.setLikedByCurrentUser(!currentlyLiked);
            comment.setLikeCount(newLikeCount);

            // Update the likedUserIds list
            List<String> likedUserIds = comment.getLikedUserIds();
            if (likedUserIds == null) {
                likedUserIds = new ArrayList<>();
                comment.setLikedUserIds(likedUserIds);
            }

            if (currentlyLiked) {
                // Remove user ID from liked list
                likedUserIds.remove(currentUserId);
            } else {
                // Add user ID to liked list if not already present
                if (!likedUserIds.contains(currentUserId)) {
                    likedUserIds.add(currentUserId);
                }
            }

            if (position >= 0 && position < commentList.size()) {
                notifyItemChanged(position);
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("post_comments").document(comment.getId())
                    .update(
                            "likeCount", newLikeCount,
                            "likedUserIds", likedUserIds
                    )
                    .addOnFailureListener(e -> Log.e(TAG, "Lỗi cập nhật like: " + e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, "Error in toggleLike: " + e.getMessage());
        }
    }

    public void updateViewRepliesText(String commentId, String newText) {
        if (commentId == null) return;

        viewRepliesTextMap.put(commentId, newText);
        for (int i = 0; i < commentList.size(); i++) {
            Post_comment comment = commentList.get(i);
            if (comment != null && comment.getId() != null && comment.getId().equals(commentId)) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    private void bindCommentData(CommentViewHolder holder, Post_comment comment) {
        try {
            // Set user name with null check
            holder.tvUserName.setText(comment.getUserName() != null ? comment.getUserName() : "");

            // Load avatar with null check
            if (comment.getUserAvatar() != null && !comment.getUserAvatar().isEmpty()) {
                Glide.with(context)
                        .load(comment.getUserAvatar())
                        .placeholder(R.drawable.default_avatar)
                        .circleCrop()
                        .into(holder.ivUserAvatar);
            } else {
                holder.ivUserAvatar.setImageResource(R.drawable.default_avatar);
            }

            // Set content with null check
            holder.tvContent.setText(comment.getContent() != null ? comment.getContent() : "");

            // Set timestamp with null check
            if (comment.getTimestamp() != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    String formattedDate = sdf.format(comment.getTimestamp().toDate());
                    holder.tvTimestamp.setText(formattedDate);
                } catch (Exception e) {
                    Log.e(TAG, "Error formatting date: " + e.getMessage());
                    holder.tvTimestamp.setText("");
                }
            } else {
                holder.tvTimestamp.setText("");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in bindCommentData: " + e.getMessage());
        }
    }

    private void bindReplyData(ReplyViewHolder holder, Post_comment reply) {
        try {
            // Set user name with null check
            holder.tvUserName.setText(reply.getUserName() != null ? reply.getUserName() : "");

            // Load avatar with null check
            if (reply.getUserAvatar() != null && !reply.getUserAvatar().isEmpty()) {
                Glide.with(context)
                        .load(reply.getUserAvatar())
                        .placeholder(R.drawable.default_avatar)
                        .circleCrop()
                        .into(holder.ivUserAvatar);
            } else {
                holder.ivUserAvatar.setImageResource(R.drawable.default_avatar);
            }

            // Set content with null check
            holder.tvContent.setText(reply.getContent() != null ? reply.getContent() : "");

            // Set timestamp with null check
            if (reply.getTimestamp() != null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    String formattedDate = sdf.format(reply.getTimestamp().toDate());
                    holder.tvTimestamp.setText(formattedDate);
                } catch (Exception e) {
                    Log.e(TAG, "Error formatting date: " + e.getMessage());
                    holder.tvTimestamp.setText("");
                }
            } else {
                holder.tvTimestamp.setText("");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in bindReplyData: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (commentList == null || position >= commentList.size()) {
            return TYPE_COMMENT; // Default to comment type
        }

        Post_comment comment = commentList.get(position);
        return comment != null && comment.getDepth() > 0 ? TYPE_REPLY : TYPE_COMMENT;
    }

    public void addComment(Post_comment comment) {
        if (comment == null) return;

        if (commentList == null) {
            commentList = new ArrayList<>();
        }

        commentList.add(comment);
        notifyItemInserted(commentList.size() - 1);
    }

    public void addReplies(String parentId, List<Post_comment> replies) {
        if (parentId == null || replies == null || replies.isEmpty() || commentList == null) return;

        int parentPosition = -1;
        for (int i = 0; i < commentList.size(); i++) {
            Post_comment comment = commentList.get(i);
            if (comment != null && comment.getId() != null && comment.getId().equals(parentId)) {
                parentPosition = i;
                break;
            }
        }

        if (parentPosition != -1) {
            commentList.addAll(parentPosition + 1, replies);
            notifyItemRangeInserted(parentPosition + 1, replies.size());
        }
    }

    public void removeReplies(String parentId) {
        if (parentId == null || commentList == null) return;

        List<Integer> positionsToRemove = new ArrayList<>();
        for (int i = 0; i < commentList.size(); i++) {
            Post_comment comment = commentList.get(i);
            if (comment != null && isDescendantOf(comment, parentId)) {
                positionsToRemove.add(i);
            }
        }

        for (int i = positionsToRemove.size() - 1; i >= 0; i--) {
            int position = positionsToRemove.get(i);
            if (position >= 0 && position < commentList.size()) {
                commentList.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    private boolean isDescendantOf(Post_comment comment, String ancestorId) {
        if (comment == null || ancestorId == null) return false;

        String currentParentId = comment.getParentId();
        while (currentParentId != null) {
            if (currentParentId.equals(ancestorId)) return true;

            Post_comment parentComment = commentMap.get(currentParentId);
            if (parentComment != null) {
                currentParentId = parentComment.getParentId();
            } else {
                break;
            }
        }
        return false;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar;
        TextView tvUserName, tvTimestamp, tvContent, tvReply, tvViewReplies;
        ImageView ivLike;
        TextView tvLikeCount;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvReply = itemView.findViewById(R.id.tvReply);
            tvViewReplies = itemView.findViewById(R.id.tvViewReplies);
            ivLike = itemView.findViewById(R.id.ivLike);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
        }
    }

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar;
        TextView tvUserName, tvTimestamp, tvContent, tvReply, tvViewReplies;
        ImageView ivLike;
        TextView tvLikeCount;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvReply = itemView.findViewById(R.id.tvReply);
            tvViewReplies = itemView.findViewById(R.id.tvViewReplies);
            ivLike = itemView.findViewById(R.id.ivLike);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
        }
    }
}
