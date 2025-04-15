/*
package com.example.hipenjava.Activities;
import com.bumptech.glide.Glide;
import com.example.hipenjava.Activities.Favorite.FavoritePostsActivity;
import com.example.hipenjava.Activities.Post.MainActivityPost;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hipenjava.Activities.Courses.CourseListActivity;
import com.example.hipenjava.Activities.Notification.NotificationActivity;
import com.example.hipenjava.Activities.BaseActivity;
import com.example.hipenjava.Activities.Post.PostAdapter;
import com.example.hipenjava.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends BaseActivity {
    private ImageButton btnNotification,btnMenu,btnCom;
    BottomNavigationView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // both header, bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.contentFrame));

        btnNotification = findViewById(R.id.btnNotification);

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
        btnCom = findViewById(R.id.btnCom);

        btnCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivityPost.class);
                startActivity(intent);
            }
        });

        LinearLayout favoriteContainer = findViewById(R.id.favoriteImagesContainer);
        RelativeLayout favoriteSection = findViewById(R.id.favoriteSection);

        // Khi nhấn mũi tên → chuyển trang
        favoriteSection.setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritePostsActivity.class));
        });

        // Load dữ liệu yêu thích từ Firestore
        loadFavoriteImages(favoriteContainer);


        LinearLayout navigationCourses = findViewById(R.id.navigation_courses_home);

        navigationCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_courses) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_draw) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_challenge) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_community) {
                Intent intent = new Intent(HomeActivity.this, MainActivityPost.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void loadFavoriteImages(LinearLayout container) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                                container.removeAllViews(); // clear cũ
                                int count = 0;
                                for (DocumentSnapshot postDoc : postsSnapshot.getDocuments()) {
                                    if (count >= 3) break;
                                    String imageUrl = postDoc.getString("imageUrl");
                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        ImageView imageView = new ImageView(this);
                                        */
/*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 120);
                                        params.weight = 1;*//*

                                        int sizeInPx = (int) (120 * getResources().getDisplayMetrics().density);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
                                        params.setMargins(4, 0, 4, 0);
                                        imageView.setLayoutParams(params);
                                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        imageView.setBackgroundColor(Color.WHITE);
                                        Glide.with(this).load(imageUrl).into(imageView);
                                        String postId = postDoc.getId(); // Lấy postId để truyền qua Intent

                                        imageView.setOnClickListener(v -> {
                                            Intent intent = new Intent(this, MainActivityPost.class);
                                            intent.putExtra("postId", postId); // truyền postId để hiển thị bài viết
                                            startActivity(intent);
                                        });

                                        container.addView(imageView);
                                        count++;
                                    }
                                }
                            });
                });
    }

}

*/

package com.example.hipenjava.Activities;
import com.bumptech.glide.Glide;
import com.example.hipenjava.Activities.Favorite.FavoritePostsActivity;
import com.example.hipenjava.Activities.Post.MainActivityPost;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hipenjava.Activities.Courses.CourseListActivity;
import com.example.hipenjava.Activities.Notification.NotificationActivity;
import com.example.hipenjava.Activities.BaseActivity;
import com.example.hipenjava.Activities.Post.PostAdapter;
import com.example.hipenjava.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends BaseActivity {
    private static final String TAG = "HomeActivity";
    private ImageButton btnNotification,btnMenu,btnCom;
    BottomNavigationView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // both header, bottom navigation
        bottomNavigation = findViewById(R.id.bottomNavigation);
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.contentFrame));

        btnNotification = findViewById(R.id.btnNotification);

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
        btnCom = findViewById(R.id.btnCom);

        btnCom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivityPost.class);
                startActivity(intent);
            }
        });

        LinearLayout favoriteContainer = findViewById(R.id.favoriteImagesContainer);
        RelativeLayout favoriteSection = findViewById(R.id.favoriteSection);

        // Khi nhấn mũi tên → chuyển trang
        favoriteSection.setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritePostsActivity.class));
        });

        // Load dữ liệu yêu thích từ Firestore
        loadFavoriteImages(favoriteContainer);


        LinearLayout navigationCourses = findViewById(R.id.navigation_courses_home);

        navigationCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_courses) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_draw) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_challenge) {
                Intent intent = new Intent(HomeActivity.this, CourseListActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_community) {
                Intent intent = new Intent(HomeActivity.this, MainActivityPost.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu yêu thích khi quay lại màn hình
        LinearLayout favoriteContainer = findViewById(R.id.favoriteImagesContainer);
        loadFavoriteImages(favoriteContainer);
    }

    private void loadFavoriteImages(LinearLayout container) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId == null) {
            Log.e(TAG, "User not logged in");
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
                        Log.d(TAG, "No liked posts found");
                        container.removeAllViews();
                        return;
                    }

                    db.collection("posts")
                            .whereIn(FieldPath.documentId(), likedPostIds)
                            .get()
                            .addOnSuccessListener(postsSnapshot -> {
                                container.removeAllViews(); // clear cũ
                                int count = 0;

                                if (postsSnapshot.isEmpty()) {
                                    Log.d(TAG, "No posts found with the liked IDs");
                                    return;
                                }

                                for (DocumentSnapshot postDoc : postsSnapshot.getDocuments()) {
                                    if (count >= 3) break;

                                    String imageUrl = postDoc.getString("imageUrl");
                                    String postId = postDoc.getId();

                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        Log.d(TAG, "Loading image: " + imageUrl + " for post: " + postId);

                                        ImageView imageView = new ImageView(this);
                                        int sizeInPx = (int) (120 * getResources().getDisplayMetrics().density);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
                                        params.setMargins(4, 0, 4, 0);
                                        imageView.setLayoutParams(params);
                                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        imageView.setBackgroundColor(Color.WHITE);

                                        // Tải ảnh với Glide và thêm placeholder/error
                                        Glide.with(this)
                                                .load(imageUrl)
                                                .placeholder(R.drawable.default_avatar)
                                                .error(R.drawable.default_avatar)
                                                .into(imageView);

                                        // Thêm sự kiện click
                                        final String finalPostId = postId;
                                        imageView.setOnClickListener(v -> {
                                            Intent intent = new Intent(this, MainActivityPost.class);
                                            intent.putExtra("postId", finalPostId);
                                            startActivity(intent);
                                        });

                                        container.addView(imageView);
                                        count++;
                                    }
                                }

                                if (count == 0) {
                                    Log.d(TAG, "No images found in liked posts");
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error loading posts: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading likes: " + e.getMessage());
                });
    }
}

