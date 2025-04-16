package com.example.hipenjava.Activities.Challenge;

public class SubmittedArtwork {

    private String id;

    private String authorName;

    private String imageUrl;

    private String challengeId;

    private String userId;

    private int voteCount;

    public SubmittedArtwork(String id, String authorName, String imageUrl, String challengeId, String userId, int voteCount) {
        this.id = id;
        this.authorName = authorName;
        this.imageUrl = imageUrl;
        this.challengeId = challengeId;
        this.userId = userId;
        this.voteCount = voteCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getAuthorName() {
        return authorName;
    }
}
