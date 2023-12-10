package com.mobdeve.s17.catchow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s17.catchow.R;
import com.mobdeve.s17.catchow.models.Rating;

import java.util.List;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.ViewHolder> {

    private List<Rating> ratingList;
    private Context context; // Add a Context field

    public RatingAdapter(Context context, List<Rating> ratingList) {
        this.context = context; // Initialize the Context field
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rating_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rating rating = ratingList.get(position);
        holder.nameTextView.setText(rating.getName());
        String ratingText = String.valueOf(rating.getRating());

        StringBuilder ratingWithStars = new StringBuilder(ratingText + " ");
        int ratingValue = (int) rating.getRating();
        for (int i = 0; i < ratingValue; i++) {
            ratingWithStars.append("⭐");
        }

        holder.ratingTextView.setText(ratingWithStars.toString());
        holder.ratingBar.setRating((float) rating.getRating());

//        holder.ratingTextView.setText(String.valueOf(rating.getRating()));
        // Concatenate the rating value with " Stars" and set it in ratingTextView
        holder.reviewTextView.setText(rating.getReview());
    }

    @Override
    public int getItemCount() {
        return ratingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView ratingTextView;
        RatingBar ratingBar;

        TextView reviewTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);


            reviewTextView = itemView.findViewById(R.id.reviewTextView);
        }
    }
}