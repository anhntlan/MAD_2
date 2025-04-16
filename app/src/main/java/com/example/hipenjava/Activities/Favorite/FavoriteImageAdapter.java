package com.example.hipenjava.Activities.Favorite;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Activities.Post.MainActivityPost;
import com.example.hipenjava.R;

import java.util.ArrayList;
import java.util.List;

public class FavoriteImageAdapter extends RecyclerView.Adapter<FavoriteImageAdapter.ImageViewHolder> {

    private Context context;
    private List<String> imageUrls;
    private List<String> postIds; // Thêm danh sách postIds

    public FavoriteImageAdapter(Context context, List<String> imageUrls, List<String> postIds) {
        this.context = context;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
        this.postIds = postIds != null ? postIds : new ArrayList<>();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_favorite, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Tải ảnh bằng Glide
        Glide.with(context)
                .load(imageUrls.get(position))
                .placeholder(R.drawable.default_avatar) // Thêm placeholder
                .error(R.drawable.default_avatar) // Thêm error image
                .into(holder.imageView);

        // Thêm sự kiện click cho ảnh
        final int pos = position;
        holder.imageView.setOnClickListener(v -> {
            if (pos < postIds.size()) {
                String postId = postIds.get(pos);
                Intent intent = new Intent(context, MainActivityPost.class);
                intent.putExtra("postId", postId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgFavorite);
        }
    }
}

