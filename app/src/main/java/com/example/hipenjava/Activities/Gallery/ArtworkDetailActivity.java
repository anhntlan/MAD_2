package com.example.hipenjava.Activities.Gallery;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Models.Artwork;
import com.example.hipenjava.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class ArtworkDetailActivity extends AppCompatActivity {

    private String artworkId;
    
    private ImageView ivArtworkImage;
    private TextView tvTitle;
    private TextView tvArtist;
    private TextView tvYear;
    private TextView tvDescription;
    private ImageView btnBack;
    
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artwork_detail);

        // Get artwork ID from intent
        artworkId = getIntent().getStringExtra("artworkId");
        
        if (artworkId == null) {
            finish();
            return;
        }

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        ivArtworkImage = findViewById(R.id.ivArtworkImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvArtist = findViewById(R.id.tvArtist);
        tvYear = findViewById(R.id.tvYear);
        tvDescription = findViewById(R.id.tvDescription);
        btnBack = findViewById(R.id.btnBack);

        // Load artwork details
        loadArtworkDetails();

        // Setup back button
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadArtworkDetails() {
        //db.collection("artGallery").document("artworks").collection("items").document(artworkId)
        db.collection("artworks").document(artworkId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Artwork artwork = documentSnapshot.toObject(Artwork.class);
                    if (artwork != null) {
                        // Set artwork image
                        if (artwork.getImageUrl() != null && !artwork.getImageUrl().isEmpty()) {
                            Glide.with(this)
                                    .load(artwork.getImageUrl())
                                    .into(ivArtworkImage);
                        } else {
                            ivArtworkImage.setImageResource(R.drawable.placeholder_image);
                        }
                        
                        // Set artwork details
                        tvTitle.setText(artwork.getTitle());
                        tvArtist.setText(artwork.getArtist());
                        tvYear.setText(artwork.getYear());
                        //tvDescription.setText(artwork.getDescription());
                        tvDescription.setText(artwork.getDescription().replace("\\n", "\n"));

                    }
                });
    }
}
