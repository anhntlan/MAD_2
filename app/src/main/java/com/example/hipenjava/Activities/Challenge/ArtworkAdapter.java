package com.example.hipenjava.Activities.Challenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.Activities.Image.ImageModel;
import com.example.hipenjava.R;

import java.util.List;

public class ArtworkAdapter extends RecyclerView.Adapter<ArtworkAdapter.ArtViewHolder> {
    private List<ImageModel> artworkList;
    private int selectedPosition = -1;
    private final Context context;
    private final OnImageSelected listener;

    public interface OnImageSelected {
        void onImageSelected(ImageModel selected);
    }

    public ArtworkAdapter(List<ImageModel> artworkList, Context context, OnImageSelected listener) {
        this.artworkList = artworkList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artwork_chall, parent, false);
        return new ArtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtViewHolder holder, int position) {
        ImageModel art = artworkList.get(position); // Assuming artworkList is your list

        Glide.with(context)
                .load(art.getImageUrl())
                .into(holder.imageView);

        holder.nameText.setText(art.getName());

        // Show overlay only if this item is selected
        holder.overlay.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            if (selectedPosition != pos) {
                int previous = selectedPosition;
                selectedPosition = pos;

                notifyItemChanged(previous); // Unhighlight old
                notifyItemChanged(pos);      // Highlight new
                listener.onImageSelected(artworkList.get(pos));
            }
        });
    }

    @Override
    public int getItemCount() {
        return artworkList.size();
    }

    public static class ArtViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, overlay;
        TextView nameText;

        public ArtViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.artImage);
            overlay = itemView.findViewById(R.id.tickOverlay);
            nameText = itemView.findViewById(R.id.artName);
        }
    }
}