package com.example.hipenjava.Activities.Challenge;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;

public class ChallengeResultActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArtworkResultAdapter artworkAdapter;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Kết quả");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        challengeId = getIntent().getStringExtra("challengeId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user != null ? user.getUid() : null;

        db = FirebaseFirestore.getInstance();
        artworksRef = db.collection("submitted_artwork");

        artworkList = new ArrayList<>();
        artworkAdapter = new ArtworkResultAdapter(artworkList, this);
        recyclerView.setAdapter(artworkAdapter);

        fetchArtworkData();
    }

    private void fetchArtworkData() {
        artworksRef
                .whereEqualTo("challengeID", challengeId)
                .get().addOnSuccessListener(querySnapshots -> {
            if (querySnapshots == null || querySnapshots.isEmpty()) {
                Log.w("ChallengeResultActivity", "No artworks found.");
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
                    Log.e("ChallengeResultActivity", "Incomplete data in submitted_artwork document");
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
                        Log.e("ChallengeResultActivity", "Image URL is null for imageId: " + imageId);
                        return;
                    }

                    db.collection("Users").document(userId).get()
                            .addOnSuccessListener(userDoc -> {
                                String authorName = userDoc.getString("fullName");
                                if (authorName == null) {
                                    Log.e("ChallengeResultActivity", "User full name is null for userID: " + userId);
                                    return;
                                }

                                SubmittedArtwork artwork = new SubmittedArtwork(id, authorName, imageUrl, challengeId, userId, voteCount);
                                artworkList.add(artwork);
                                Collections.sort(artworkList, (a1, a2) -> Integer.compare(a2.getVoteCount(), a1.getVoteCount()));
                                artworkAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e ->
                                    Log.e("ChallengeResultActivity", "Failed to fetch user info", e)
                            );
                })
                .addOnFailureListener(e ->
                        Log.e("ChallengeResultActivity", "Failed to fetch image info", e)
                );
    }
}
