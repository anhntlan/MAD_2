package com.example.hipenjava.Activities.Gallery;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Models.Category;
import com.example.hipenjava.R;

import java.util.List;
import com.example.hipenjava.Models.Category;
import com.example.hipenjava.Activities.Gallery.ArtCategoryAdapter;

public class ArtCategoryAdapter extends RecyclerView.Adapter<ArtCategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;

    public ArtCategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_art_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        // Set category name
        holder.tvCategoryName.setText(category.getName());
        //holder.tvCategoryDescription.setText(category.getDescription());


        // Set category thumbnail
        if (category.getThumbnail() != null && !category.getThumbnail().isEmpty()) {
            Glide.with(context)
                    .load(category.getThumbnail())
                    .into(holder.ivCategoryThumbnail);
        } else {
            holder.ivCategoryThumbnail.setImageResource(R.drawable.placeholder_image);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArtworksActivity.class);
            intent.putExtra("categoryId", category.getId());
            intent.putExtra("categoryName", category.getName());
            intent.putExtra("categoryDescription", category.getDescription()); // Đảm bảo có dòng này
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryThumbnail;
        TextView tvCategoryName;
        //TextView tvCategoryDescription;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCategoryThumbnail = itemView.findViewById(R.id.ivCategoryThumbnail);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            //tvCategoryDescription = itemView.findViewById(R.id.tvCategoryDescription);
        }
    }
}
