package com.example.hipenjava.Activities.Challenge;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hipenjava.R;

import java.util.List;

public class ChallengeAdapter extends RecyclerView.Adapter<ChallengeAdapter.ChallengeViewHolder>{
    private List<Challenge> challengeList;
    private Context context;

    public ChallengeAdapter(List<Challenge> challenges, Context context) {
        this.challengeList = challenges;
        this.context = context;
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_challenge, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        Challenge challenge = challengeList.get(position);
        holder.title.setText(challenge.getName());
        holder.status.setText(challenge.getStatus());
        Glide.with(holder.itemView.getContext())
                .load(challenge.getImageUrl())
                .into(holder.image);

        holder.btnViewDetails.setOnClickListener(v -> {
            if (context != null) {
                // Debugging step: Log the context
                Log.d("ChallengeAdapter", "Context is not null, proceeding with Intent.");

                Intent intent = new Intent(context, ChallengeDetailActivity.class);
                intent.putExtra("challengeId", challenge.getId());
                context.startActivity(intent);
            } else {
                // Handle case where context is null
                Log.e("ChallengeAdapter", "Context is null, cannot start activity.");
            }
        });
    }

    @Override
    public int getItemCount() {
        return challengeList.size();
    }

    static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        TextView title, status;
        ImageView image;
        Button btnViewDetails;

        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.challengeTitle);
            status = itemView.findViewById(R.id.challengeStatus);
            image = itemView.findViewById(R.id.challengeImage);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }
    }
}
