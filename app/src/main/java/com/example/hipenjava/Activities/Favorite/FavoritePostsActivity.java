/*
package com.example.hipenjava.Activities.Favorite;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Activities.Favorite.FavoriteImageAdapter;
import com.example.hipenjava.Models.Post;
import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavoritePostsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavoriteImageAdapter adapter;
    private List<String> imageUrls;
    private FirebaseFirestore db;

    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_posts);

        recyclerView = findViewById(R.id.recyclerViewAllFavorites);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        imageUrls = new ArrayList<>();
        adapter = new FavoriteImageAdapter(this, imageUrls);
        recyclerView.setAdapter(adapter);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        loadAllLikedImages();
    }

    private void loadAllLikedImages() {
        String userId = FirebaseAuth.getInstance().getUid();

        db.collection("likes")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(likesSnapshot -> {
                    List<String> likedPostIds = new ArrayList<>();
                    for (DocumentSnapshot doc : likesSnapshot.getDocuments()) {
                        likedPostIds.add(doc.getString("postId"));
                    }

                    if (likedPostIds.isEmpty()) return;

                    db.collection("posts")
                            .whereIn(FieldPath.documentId(), likedPostIds)
                            .get()
                            .addOnSuccessListener(postsSnapshot -> {
                                imageUrls.clear();
                                for (DocumentSnapshot postDoc : postsSnapshot.getDocuments()) {
                                    String imageUrl = postDoc.getString("imageUrl");
                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        imageUrls.add(imageUrl);
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải ảnh yêu thích", Toast.LENGTH_SHORT).show();
                });
    }
}

*/

package com.example.hipenjava.Activities.Favorite;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Activities.Favorite.FavoriteImageAdapter;
import com.example.hipenjava.Models.Post;
import com.example.hipenjava.Models.User;
import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavoritePostsActivity extends AppCompatActivity {

    private static final String TAG = "FavoritePostsActivity";
    private RecyclerView recyclerView;
    private FavoriteImageAdapter adapter;
    private List<String> imageUrls;
    private List<String> postIds; // Thêm danh sách postIds
    private FirebaseFirestore db;

    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_posts);

        recyclerView = findViewById(R.id.recyclerViewAllFavorites);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        imageUrls = new ArrayList<>();
        postIds = new ArrayList<>(); // Khởi tạo danh sách postIds

        adapter = new FavoriteImageAdapter(this, imageUrls, postIds);
        recyclerView.setAdapter(adapter);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();
        loadAllLikedImages();
    }

    private void loadAllLikedImages() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("likes")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(likesSnapshot -> {
                    List<String> likedPostIds = new ArrayList<>();
                    for (DocumentSnapshot doc : likesSnapshot.getDocuments()) {
                        String postId = doc.getString("postId");
                        if (postId != null) {
                            likedPostIds.add(postId);
                        }
                    }

                    if (likedPostIds.isEmpty()) {
                        Toast.makeText(this, "Bạn chưa thích bài viết nào", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    db.collection("posts")
                            .whereIn(FieldPath.documentId(), likedPostIds)
                            .get()
                            .addOnSuccessListener(postsSnapshot -> {
                                imageUrls.clear();
                                postIds.clear();

                                for (DocumentSnapshot postDoc : postsSnapshot.getDocuments()) {
                                    String imageUrl = postDoc.getString("imageUrl");
                                    String postId = postDoc.getId();

                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        imageUrls.add(imageUrl);
                                        postIds.add(postId);
                                        Log.d(TAG, "Added image: " + imageUrl + " for post: " + postId);
                                    }
                                }

                                adapter.notifyDataSetChanged();

                                if (imageUrls.isEmpty()) {
                                    Toast.makeText(this, "Không có ảnh nào trong bài viết yêu thích", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error loading posts: " + e.getMessage());
                                Toast.makeText(this, "Lỗi tải bài viết yêu thích", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading likes: " + e.getMessage());
                    Toast.makeText(this, "Lỗi tải thông tin yêu thích", Toast.LENGTH_SHORT).show();
                });
    }
}

