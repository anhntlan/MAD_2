package com.example.hipenjava.Activities.Challenge;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VoteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SubmittedArtworkAdapter artworkAdapter;
    private ArrayList<SubmittedArtwork> artworkList;
    private FirebaseFirestore db;
    private CollectionReference artworksRef;
    private String userId;

    private String challengeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        recyclerView = findViewById(R.id.artworkRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        challengeId = getIntent().getStringExtra("challengeId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user != null ? user.getUid() : null;

        db = FirebaseFirestore.getInstance();
        artworksRef = db.collection("submitted_artwork");

        ImageView btnBack = findViewById(R.id.backButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Or finish(); or custom logic
            }
        });

        artworkList = new ArrayList<>();
        artworkAdapter = new SubmittedArtworkAdapter(artworkList, this);
        recyclerView.setAdapter(artworkAdapter);

        fetchArtworkData();
    }

    private void fetchArtworkData() {
        artworksRef
                .whereEqualTo("challengeID", challengeId)
                .get().addOnSuccessListener(querySnapshots -> {
            if (querySnapshots == null || querySnapshots.isEmpty()) {
                Log.w("VoteActivity", "No artworks found.");
                return;
            }

            artworkList.clear();

            for (DocumentSnapshot snapshot : querySnapshots) {
                String id = snapshot.getString("id");
                String imageId = snapshot.getString("imageID");
                String challengeId = snapshot.getString("challengeID");
                String userId = snapshot.getString("userID");
                Long voteLong = snapshot.getLong("votecount");

                if (id == null || imageId == null || challengeId == null || userId == null || voteLong == null) {
                    Log.e("VoteActivity", "Incomplete data in submitted_artwork document");
                    continue;
                }

                int voteCount = voteLong.intValue();
                fetchImageAndUser(id, imageId, challengeId, userId, voteCount);
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error fetching artwork data", Toast.LENGTH_SHORT).show()
        );
    }

    private void fetchImageAndUser(String id, String imageId, String challengeId, String userId, int voteCount) {
        db.collection("images").document(imageId).get()
                .addOnSuccessListener(imageDoc -> {
                    String imageUrl = imageDoc.getString("imageUrl");
                    if (imageUrl == null) {
                        Log.e("VoteActivity", "Image URL is null for imageId: " + imageId);
                        return;
                    }

                    db.collection("Users").document(userId).get()
                            .addOnSuccessListener(userDoc -> {
                                String authorName = userDoc.getString("fullName");
                                if (authorName == null) {
                                    Log.e("VoteActivity", "User full name is null for userID: " + userId);
                                    return;
                                }

                                SubmittedArtwork artwork = new SubmittedArtwork(id, authorName, imageUrl, challengeId, userId, voteCount);
                                artworkList.add(artwork);
                                artworkAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e ->
                                    Log.e("VoteActivity", "Failed to fetch user info", e)
                            );
                })
                .addOnFailureListener(e ->
                        Log.e("VoteActivity", "Failed to fetch image info", e)
                );
    }

    public void onVoteClicked(SubmittedArtwork artwork, boolean isVoted) {
        DocumentReference artworkRef = db.collection("submitted_artwork").document(artwork.getId());
        if (isVoted) {
            artwork.setVoteCount(artwork.getVoteCount() + 1);
            artworkRef.update("votecount", artwork.getVoteCount());
            saveVote(artwork);
        } else {
            artwork.setVoteCount(artwork.getVoteCount() - 1);
            artworkRef.update("votecount", artwork.getVoteCount());
            removeVote(artwork);
        }
    }

    private void saveVote(SubmittedArtwork artwork) {
        Map<String, Object> vote = new HashMap<>();
        vote.put("userId", userId);
        vote.put("artworkId", artwork.getId());
        db.collection("artworkVote").add(vote)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(VoteActivity.this, "Đã lưu Vote!", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Toast.makeText(VoteActivity.this, "Lỗi khi lưu Vote", Toast.LENGTH_SHORT).show());
    }

    private void removeVote(SubmittedArtwork artwork) {
        db.collection("artworkVote")
                .whereEqualTo("userId", userId)
                .whereEqualTo("artworkId", artwork.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        snapshot.getReference().delete()
                                .addOnSuccessListener(aVoid -> Toast.makeText(VoteActivity.this, "Đã bỏ Vote!", Toast.LENGTH_SHORT).show());
                    }
                });
    }
}
