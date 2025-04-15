package com.example.hipenjava.Activities.Challenge;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.R;

import java.util.ArrayList;

public class ArtworkResultAdapter extends RecyclerView.Adapter<ArtworkResultAdapter.ArtworkResultViewHolder>{
    private ArrayList<SubmittedArtwork> artworkList;
    private Context context;

    public ArtworkResultAdapter(ArrayList<SubmittedArtwork> artworkList, Context context) {
        this.artworkList = artworkList;
        this.context = context;
    }

    @Override
    public ArtworkResultAdapter.ArtworkResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_result_challenge, parent, false);
        return new ArtworkResultAdapter.ArtworkResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtworkResultAdapter.ArtworkResultViewHolder holder, int position) {
        SubmittedArtwork artwork = artworkList.get(position);
        holder.author.setText(artwork.getAuthorName());
        holder.voteCount.setText(String.valueOf(artwork.getVoteCount()));
        Glide.with(context).load(artwork.getImageUrl()).into(holder.artworkImage);

        holder.voteIcon.setOnClickListener(v -> {
            boolean isVoted = !holder.voteIcon.isSelected();
            holder.voteIcon.setSelected(isVoted);
            if (isVoted) {
                holder.voteIcon.setImageResource(R.drawable.ic_redheart);
            } else {
                holder.voteIcon.setImageResource(R.drawable.ic_heart);
            }
            holder.voteCount.setText(String.valueOf(artwork.getVoteCount()));
        });

        if (holder.topTag == null){
            return;
        }
        if (position == 0) {
            holder.topTag.setVisibility(View.VISIBLE);
            holder.topTag.setText("TOP 1");
        } else if (position == 1) {
            holder.topTag.setVisibility(View.VISIBLE);
            holder.topTag.setText("TOP 2");
        } else if (position == 2) {
            holder.topTag.setVisibility(View.VISIBLE);
            holder.topTag.setText("TOP 3");
        } else {
            holder.topTag.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return artworkList.size();
    }

    public static class ArtworkResultViewHolder extends RecyclerView.ViewHolder {

        TextView author, voteCount;
        ImageView artworkImage, voteIcon;
        TextView topTag;

        public ArtworkResultViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.authorName);
            voteCount = itemView.findViewById(R.id.voteCount);
            artworkImage = itemView.findViewById(R.id.artImage);
            voteIcon = itemView.findViewById(R.id.voteIcon);
            topTag = itemView.findViewById(R.id.topTag);
        }
    }
}
