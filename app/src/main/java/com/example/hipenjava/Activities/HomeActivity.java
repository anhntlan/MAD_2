package com.example.hipenjava.Activities;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hipenjava.Activities.Challenge.ChallengeListActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.hipenjava.Activities.Courses.CourseListActivity;
import com.example.hipenjava.Activities.Favorite.FavoritePostsActivity;
import com.example.hipenjava.Activities.Image.EditImageActivity;
import com.example.hipenjava.Activities.Image.ImageActivity;
import com.example.hipenjava.Activities.Image.ImageAdapter;
import com.example.hipenjava.Activities.Image.ImageModel;
import com.example.hipenjava.Activities.Courses.CourseHomeActivity;
import com.example.hipenjava.Activities.Notification.NotificationActivity;
import com.example.hipenjava.Activities.Post.MainActivityPost;
import com.example.hipenjava.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.hipenjava.Activities.Image.ImageAdapter;
import com.example.hipenjava.Activities.Post.PostAdapter;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HomeActivity extends BaseActivity {
    private ImageButton btnNotification,btnMenu,btnCom;
    BottomNavigationView bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // both header, bottom navigation
        loadLatestImages();
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

        LinearLayout favoriteImagesContainer = findViewById(R.id.favoriteImagesContainer);
        RelativeLayout favoriteSection = findViewById(R.id.favoriteSection);
        TextView tvNoFavoritesShort = findViewById(R.id.tvNoFavoritesShort);


        // Khi nhấn mũi tên → chuyển trang
        favoriteSection.setOnClickListener(v -> {
            startActivity(new Intent(this, FavoritePostsActivity.class));
        });

        // Load dữ liệu yêu thích từ Firestore

        loadFavoriteImages(favoriteImagesContainer, tvNoFavoritesShort);


        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, MenuActivity.class);
            startActivity(intent);
        });
        LinearLayout navigationCourses = findViewById(R.id.navigation_courses_home);

        navigationCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CourseHomeActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout navigationImage = findViewById(R.id.navigation_image);

        navigationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ImageActivity.class);
                startActivity(intent);
            }
        });

        ImageView vv = findViewById(R.id.ViewImage);
        vv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ImageActivity.class);
                startActivity(intent);
            }
        });
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_courses) {
                Intent intent = new Intent(HomeActivity.this, CourseHomeActivity.class);
                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_draw) {
                Intent intent = new Intent(HomeActivity.this, ImageActivity.class);

                startActivity(intent);
                return true;
            } else if (id == R.id.navigation_challenge) {
                Intent intent = new Intent(HomeActivity.this, ChallengeListActivity.class);
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
    private final ImageAdapter.OnItemClickListener listener = new ImageAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(ImageModel imageModel) {
            loadImageAndNavigateToEdit(imageModel);
        }

        @Override
        public void onItemLongClick(ImageModel imageModel, int position) {

        }
    };
    private void loadImageAndNavigateToEdit(ImageModel imageModel) {
        String imageUrl = imageModel.getImageUrl();
        Log.d("Image URL", imageUrl);  // Log URL to check its validity

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)  // Placeholder image while loading
//                .error(R.drawable.error_image)  // Error image in case of failure
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            File file = saveImageToCache(resource, imageModel.getName());
                            navigateToEditImage(file.getAbsolutePath(), imageModel);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, "Lỗi tải ảnh", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle image clearance if needed
                    }
                });
    }
    private File saveImageToCache(Bitmap bitmap, String imageName) throws IOException {
        File file = new File(getCacheDir(), imageName + "_edit.png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }
        return file;
    }

    private void navigateToEditImage(String imagePath, ImageModel imageModel) {
        Intent intent = new Intent(HomeActivity.this, EditImageActivity.class);
        intent.putExtra("image_path", imagePath);
        intent.putExtra("image_name", imageModel.getName());
        intent.putExtra("image_id", imageModel.getId());
        intent.putExtra("is_new", false);
        startActivityForResult(intent, 1);

    }
    private void loadLatestImages() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView textNoImages = findViewById(R.id.textNoImages);
        ImageView image1 = findViewById(R.id.image1);
        ImageView image2 = findViewById(R.id.image2);
        ImageView image3 = findViewById(R.id.image3);

        // Ẩn cả 3 image khi chưa có dữ liệu
        image1.setVisibility(View.GONE);
        image2.setVisibility(View.GONE);
        image3.setVisibility(View.GONE);

        String currentUserId = user.getUid();
        CollectionReference imageRef = FirebaseFirestore.getInstance().collection("images");

        imageRef
                .whereEqualTo("userId", currentUserId)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(3) // Lấy tối đa 3 ảnh
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        textNoImages.setVisibility(View.VISIBLE);
                    } else {
                        textNoImages.setVisibility(View.GONE);

                        List<ImageView> imageViews = Arrays.asList(image1, image2, image3);
                        int index = 0;

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            if (index >= 3) break;

                            ImageModel image = doc.toObject(ImageModel.class);
                            String imageUrl = image.getImageUrl(); // Hoặc getImagePath() tùy bạn lưu

                            ImageView currentImageView = imageViews.get(index);
                            currentImageView.setVisibility(View.VISIBLE);

                            Glide.with(this)
                                    .load(imageUrl)
                                    .into(currentImageView);
                            currentImageView.setOnClickListener(v -> loadImageAndNavigateToEdit(image));
                            index++;
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    textNoImages.setVisibility(View.VISIBLE);
                });
    }
    /*protected void onResume() {
        super.onResume();
        loadLatestImages(); // Reload after adding/editing
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu yêu thích khi quay lại màn hình
        LinearLayout favoriteImagesContainer = findViewById(R.id.favoriteImagesContainer);
        TextView tvNoFavoritesShort = findViewById(R.id.tvNoFavoritesShort);

        loadFavoriteImages(favoriteImagesContainer, tvNoFavoritesShort);

        loadLatestImages();
    }

    /*private void loadFavoriteImages(LinearLayout container) {
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
    }*/
    private void loadFavoriteImages(LinearLayout container, TextView noFavoritesTextView) {
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
                        noFavoritesTextView.setVisibility(View.VISIBLE); // Hiện dòng thông báo
                        return;
                    }

                    db.collection("posts")
                            .whereIn(FieldPath.documentId(), likedPostIds)
                            .get()
                            .addOnSuccessListener(postsSnapshot -> {
                                container.removeAllViews();
                                int count = 0;

                                if (postsSnapshot.isEmpty()) {
                                    Log.d(TAG, "No posts found with the liked IDs");
                                    noFavoritesTextView.setVisibility(View.VISIBLE);
                                    return;
                                }

                                for (DocumentSnapshot postDoc : postsSnapshot.getDocuments()) {
                                    if (count >= 3) break;

                                    String imageUrl = postDoc.getString("imageUrl");
                                    String postId = postDoc.getId();

                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        ImageView imageView = new ImageView(this);
                                        int sizeInPx = (int) (120 * getResources().getDisplayMetrics().density);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
                                        params.setMargins(4, 0, 4, 0);
                                        imageView.setLayoutParams(params);
                                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        imageView.setBackgroundColor(Color.WHITE);

                                        Glide.with(this)
                                                .load(imageUrl)
                                                .placeholder(R.drawable.default_avatar)
                                                .error(R.drawable.default_avatar)
                                                .into(imageView);

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

                                // Ẩn hoặc hiện thông báo tùy theo có ảnh hay không
                                if (count == 0) {
                                    Log.d(TAG, "No images found in liked posts");
                                    noFavoritesTextView.setVisibility(View.VISIBLE);
                                } else {
                                    noFavoritesTextView.setVisibility(View.GONE);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error loading posts: " + e.getMessage());
                                noFavoritesTextView.setVisibility(View.VISIBLE);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading likes: " + e.getMessage());
                    noFavoritesTextView.setVisibility(View.VISIBLE);
                });
    }

}

