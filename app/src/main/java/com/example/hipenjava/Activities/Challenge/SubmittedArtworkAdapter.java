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

public class SubmittedArtworkAdapter extends RecyclerView.Adapter<SubmittedArtworkAdapter.SubmittedArtworkViewHolder> {
    private ArrayList<SubmittedArtwork> artworkList;
    private Context context;

    public SubmittedArtworkAdapter(ArrayList<SubmittedArtwork> artworkList, Context context) {
        this.artworkList = artworkList;
        this.context = context;
    }

    @Override
    public SubmittedArtworkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_submitted_artwork, parent, false);
        return new SubmittedArtworkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubmittedArtworkViewHolder holder, int position) {
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
            ((VoteActivity) context).onVoteClicked(artwork, isVoted);
            holder.voteCount.setText(String.valueOf(artwork.getVoteCount()));
        });
    }

    @Override
    public int getItemCount() {
        return artworkList.size();
    }

    public static class SubmittedArtworkViewHolder extends RecyclerView.ViewHolder {

        TextView author, voteCount;
        ImageView artworkImage, voteIcon;

        public SubmittedArtworkViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.authorName);
            voteCount = itemView.findViewById(R.id.voteCount);
            artworkImage = itemView.findViewById(R.id.artImage);
            voteIcon = itemView.findViewById(R.id.voteIcon);
        }
    }
}
