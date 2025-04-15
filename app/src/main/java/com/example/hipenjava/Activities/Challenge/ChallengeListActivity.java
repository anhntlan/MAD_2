package com.example.hipenjava.Activities.Challenge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hipenjava.Activities.BaseActivity;
import com.example.hipenjava.Activities.Courses.CourseListActivity;
import com.example.hipenjava.Activities.HomeActivity;
import com.example.hipenjava.Activities.Image.ImageActivity;
import com.example.hipenjava.R;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChallengeListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChallengeAdapter adapter;
    private List<Challenge> challengeList;

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_list);

        setupListenerForBottomNav();
        setupChallengeList();
    }

    private void setupChallengeList() {
        recyclerView = findViewById(R.id.recyclerViewChallenges);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample data
        challengeList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("challenge")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getString("id");
                        String name = document.getString("name");
                        String description = document.getString("description");
                        String timeStart = document.getString("timeStart");
                        String timeEnd = document.getString("timeEnd");
                        String imageUrl = document.getString("thumbNail");

                        if (id == null || name == null || description == null || timeStart == null || timeEnd == null || imageUrl == null) {
                            Log.w("Firestore", "Missing one or more fields for document: " + document.getId());
                            continue;  // Skip this document
                        }

                        Log.d("Challenge list", "Got image: " + imageUrl);

                        challengeList.add(new Challenge(id, name, description, timeStart, timeEnd, imageUrl));
                    }
                    adapter = new ChallengeAdapter(challengeList, this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error getting documents", e);
                });
    }

    private void setupListenerForBottomNav() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                Intent intent = new Intent(ChallengeListActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_draw) {
                Intent intent = new Intent(ChallengeListActivity.this, ImageActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_courses) {
                Intent intent = new Intent(ChallengeListActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_community) {
                Intent intent = new Intent(ChallengeListActivity.this, HomeActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
}
