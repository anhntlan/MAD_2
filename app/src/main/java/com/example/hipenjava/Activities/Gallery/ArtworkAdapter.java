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
import com.example.hipenjava.Models.Artwork;
import com.example.hipenjava.R;

import java.util.List;

public class ArtworkAdapter extends RecyclerView.Adapter<ArtworkAdapter.ArtworkViewHolder> {

    private Context context;
    private List<Artwork> artworkList;

    public ArtworkAdapter(Context context, List<Artwork> artworkList) {
        this.context = context;
        this.artworkList = artworkList;
    }

    @NonNull
    @Override
    public ArtworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artwork, parent, false);
        return new ArtworkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtworkViewHolder holder, int position) {
        Artwork artwork = artworkList.get(position);
        
        // Set artwork title
        holder.tvTitle.setText(artwork.getTitle());
        
        // Set artwork artist and year
        String artistYear = artwork.getArtist();
        if (artwork.getYear() != null && !artwork.getYear().isEmpty()) {
            artistYear += " (" + artwork.getYear() + ")";
        }
        holder.tvArtistYear.setText(artistYear);
        
        // Set artwork image
        if (artwork.getImageUrl() != null && !artwork.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(artwork.getImageUrl())
                    .into(holder.ivArtworkImage);
        } else {
            holder.ivArtworkImage.setImageResource(R.drawable.placeholder_image);
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ArtworkDetailActivity.class);
            intent.putExtra("artworkId", artwork.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return artworkList.size();
    }

    public static class ArtworkViewHolder extends RecyclerView.ViewHolder {
        ImageView ivArtworkImage;
        TextView tvTitle, tvArtistYear;

        public ArtworkViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivArtworkImage = itemView.findViewById(R.id.ivArtworkImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvArtistYear = itemView.findViewById(R.id.tvArtistYear);
        }
    }
}
