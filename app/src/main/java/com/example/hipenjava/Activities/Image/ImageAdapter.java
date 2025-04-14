package com.example.hipenjava.Activities.Image;
import static com.example.hipenjava.Activities.Image.DateUtils.getRelativeTime;

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
import com.example.hipenjava.R;

import java.util.List;
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{
    private Context context;
    private List<ImageModel> imageList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ImageModel imageModel);
        void onItemLongClick(ImageModel imageModel, int position);
    }

    public ImageAdapter(Context context, List<ImageModel> imageList, OnItemClickListener listener) {
        this.context = context;
        this.imageList = imageList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageModel imageModel = imageList.get(position);

        // Load ảnh từ URL bằng Glide
        Glide.with(context)
                .load(imageModel.getImageUrl()) // Đường dẫn URL từ Cloudinary
                .centerCrop()
                .placeholder(R.drawable.placeholder) // ảnh mặc định nếu đang loading
                .into(holder.imageView);

        // Gán tên ảnh và thời gian
        holder.tvImageName.setText(imageModel.getName());
        holder.tvImageDate.setText(getRelativeTime(imageModel.getDate()));

        holder.imageView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(imageModel);
            }

            // Mở EditImageActivity và truyền dữ liệu ảnh cũ để chỉnh sửa
            Intent intent = new Intent(context, EditImageActivity.class);
            intent.putExtra("is_new", false);
            intent.putExtra("image_path", imageModel.getImageUrl());
            intent.putExtra("image_id", imageModel.getId());  // id Firebase
            intent.putExtra("image_name", imageModel.getName());
            context.startActivity(intent);
        });


        holder.imageView.setOnLongClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemLongClick(imageModel, position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvImageName, tvImageDate;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tvImageName = itemView.findViewById(R.id.tvImageName);
            tvImageDate = itemView.findViewById(R.id.tvImageDate);
        }
    }
}
