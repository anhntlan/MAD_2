package com.example.hipenjava.Activities.Gallery;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hipenjava.Models.Artwork;
import com.example.hipenjava.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ArtworksActivity extends AppCompatActivity {

    private String categoryId;
    private String categoryName;
    private String categoryDescription;
    private RecyclerView recyclerView;
    private ArtworkAdapter artworkAdapter;
    private List<Artwork> artworkList;
    private FirebaseFirestore db;
    private ImageView btnBack;
    private TextView tvCategoryName, tvCategoryDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artworks);

        // Get category ID from intent
        categoryId = getIntent().getStringExtra("categoryId");
        categoryName = getIntent().getStringExtra("categoryName");
        categoryDescription = getIntent().getStringExtra("categoryDescription");

        if (categoryId == null) {
            finish();
            return;
        }

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewArtworks);
        btnBack = findViewById(R.id.btnBack);
        tvCategoryName = findViewById(R.id.tvCategoryName);
        tvCategoryDescription = findViewById(R.id.tvCategoryDescription);

        // Set category name
        tvCategoryName.setText(categoryName);

        tvCategoryDescription.setText(categoryDescription);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        artworkList = new ArrayList<>();
        artworkAdapter = new ArtworkAdapter(this, artworkList);
        recyclerView.setAdapter(artworkAdapter);

        // Load artworks
        loadArtworks();

        // Setup back button
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadArtworks() {
        //db.collection("artGallery").document("artworks").collection("items")
        db.collection("artworks")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    artworkList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Artwork artwork = document.toObject(Artwork.class);
                        if (artwork != null) {
                            artwork.setId(document.getId());
                            artworkList.add(artwork);
                        }
                    }
                    //tvCategoryDescription.setText(category.getDescription());

                    artworkAdapter.notifyDataSetChanged();
                });
    }
}
