package com.example.hipenjava.Activities.Challenge;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hipenjava.Activities.Image.ImageModel;
import com.example.hipenjava.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        recyclerView = findViewById(R.id.artworkRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bình chọn");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user != null ? user.getUid() : null;

        db = FirebaseFirestore.getInstance();
        artworksRef = db.collection("submitted_artwork");

        artworkList = new ArrayList<>();
        artworkAdapter = new SubmittedArtworkAdapter(artworkList, this);
        recyclerView.setAdapter(artworkAdapter);

        fetchArtworkData();
    }

    private void fetchArtworkData() {
        artworksRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null) {
                artworkList.clear();
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    //fetch từ bảng submitted_artwork
                    String id = snapshot.getString("id");
                    String imageId = snapshot.getString("imageID");
                    String challengeId = snapshot.getString("challengeID");
                    String userID = snapshot.getString("userID");
                    int voteCount = snapshot.getLong("votecount").intValue();

                    if (id == null || challengeId == null || userID == null || imageId == null){
                        Log.e("VoteActivity", "Some fields be null");
                        continue;
                    }
                    db.collection("images")
                            .document(imageId)
                            .get()
                            .addOnSuccessListener(doc -> {
                                // sau đó dùng imageId để fetch tiếp name và imageURL
                                String imageUrl = doc.getString("imageUrl");

                                if (imageUrl != null) {
                                    db.collection("Users")
                                            .document(userID)
                                            .get()
                                            .addOnSuccessListener(doc2 -> {
                                                String authorName = doc2.getString("fullName");

                                                if (authorName != null) {
                                                    artworkList.add(new SubmittedArtwork(id, authorName, imageUrl, challengeId, userID, voteCount));
                                                    artworkAdapter.notifyDataSetChanged();
                                                }

                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Failed to load images", Toast.LENGTH_SHORT).show();
                                            });
                                }

                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to load images", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(VoteActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show());
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
                    Toast.makeText(VoteActivity.this, "Vote saved!", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Toast.makeText(VoteActivity.this, "Error saving vote", Toast.LENGTH_SHORT).show());
    }

    private void removeVote(SubmittedArtwork artwork) {
        db.collection("artworkVote")
                .whereEqualTo("userId", userId)
                .whereEqualTo("artworkId", artwork.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        snapshot.getReference().delete()
                                .addOnSuccessListener(aVoid -> Toast.makeText(VoteActivity.this, "Vote removed!", Toast.LENGTH_SHORT).show());
                    }
                });
    }
}
