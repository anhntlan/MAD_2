package com.example.hipenjava.Activities.Gallery;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hipenjava.Models.Category;
import com.example.hipenjava.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ArtGalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArtCategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private FirebaseFirestore db;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_art_gallery);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewCategories);
        btnBack = findViewById(R.id.btnBack);


       /* String categoryDescription = getIntent().getStringExtra("categoryDescription");
        tvCategoryDescription.setText(categoryDescription);*/


        // Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        categoryList = new ArrayList<>();
        categoryAdapter = new ArtCategoryAdapter(this, categoryList);
        recyclerView.setAdapter(categoryAdapter);

        // Load categories
        loadCategories();

        // Setup back button
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadCategories() {
        //db.collection("artGallery").document("categories").collection("items")
        db.collection("categories")

                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoryList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Category category = document.toObject(Category.class);
                        if (category != null) {
                            category.setId(document.getId());
                            categoryList.add(category);
                        }
                    }
                    categoryAdapter.notifyDataSetChanged();
                });
    }
}
