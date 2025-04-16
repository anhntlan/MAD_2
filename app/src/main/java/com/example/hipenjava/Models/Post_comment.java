package com.example.hipenjava.Models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.List;

public class Post_comment {
    @DocumentId
    private String id;
    private String postId;
    private String userId;
    private String userName;
    private String userAvatar;
    private String content;

    @ServerTimestamp
    private Timestamp timestamp;

    private String parentId; // null nếu là bình luận cha
    private boolean isReply = false;
    private int replyCount = 0;

    private int likeCount = 0;

    // Tuỳ chọn nếu bạn dùng cách like lưu theo user
    private List<String> likedUserIds;

    // Các field tạm, không lưu Firestore
    private transient List<Post_comment> replies = new ArrayList<>();
    private transient int depth = 0; // cấp độ hiển thị
    private transient boolean likedByCurrentUser = false;

    public Post_comment() {}

    public Post_comment(String postId, String userId, String userName, String userAvatar, String content) {
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.content = content;
    }

    public Post_comment(String postId, String userId, String userName, String userAvatar, String content, String parentId) {
        this(postId, userId, userName, userAvatar, content);
        this.parentId = parentId;
        this.isReply = true;
    }

    // --- Getters / Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public boolean isReply() { return isReply; }
    public void setReply(boolean reply) { isReply = reply; }

    public int getReplyCount() { return replyCount; }
    public void setReplyCount(int replyCount) { this.replyCount = replyCount; }

    public List<Post_comment> getReplies() { return replies; }
    public void setReplies(List<Post_comment> replies) { this.replies = replies; }
    public void addReply(Post_comment reply) {
        if (this.replies == null) this.replies = new ArrayList<>();
        this.replies.add(reply);
    }

    public boolean hasReplies() { return replyCount > 0; }

    public int getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public boolean isLikedByCurrentUser() { return likedByCurrentUser; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.likedByCurrentUser = likedByCurrentUser; }

    public List<String> getLikedUserIds() { return likedUserIds; }
    public void setLikedUserIds(List<String> likedUserIds) { this.likedUserIds = likedUserIds; }
}
