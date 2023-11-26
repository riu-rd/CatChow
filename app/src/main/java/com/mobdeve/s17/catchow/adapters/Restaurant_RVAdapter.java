package com.mobdeve.s17.catchow.adapters;

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
import com.mobdeve.s17.catchow.R;
import com.mobdeve.s17.catchow.ResMenuActivity;
import com.mobdeve.s17.catchow.models.Restaurant;

import java.util.ArrayList;

public class Restaurant_RVAdapter extends RecyclerView.Adapter<Restaurant_RVAdapter.MyViewHolder> {
    Context context;
    ArrayList<Restaurant> restaurantList;

    public Restaurant_RVAdapter(Context context, ArrayList<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }
    @NonNull
    @Override
    public Restaurant_RVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.restaurant_rv_item, parent, false);
        return new Restaurant_RVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Restaurant_RVAdapter.MyViewHolder holder, int position) {
        final Restaurant currRestaurant = restaurantList.get(position);

        Glide.with(context).load(currRestaurant.getImageurl()).into(holder.restaurant_iv);
        holder.name_tv.setText(currRestaurant.getName());
        holder.type_tv.setText(currRestaurant.getType());
        holder.level_tv.setText(currRestaurant.getLevel());
        holder.duration_tv.setText(currRestaurant.getDuration());
        holder.distance_tv.setText(currRestaurant.getDistance());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ResMenuActivity.class);
                intent.putExtra("imageurl",currRestaurant.getImageurl());
                intent.putExtra("name", currRestaurant.getName());
                intent.putExtra("distance", currRestaurant.getDistance());
                intent.putExtra("fee", currRestaurant.getFee());
                intent.putExtra("minimum", currRestaurant.getMinimum());
                intent.putExtra("duration", currRestaurant.getDuration());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView restaurant_iv;
        TextView name_tv, type_tv, level_tv, duration_tv, distance_tv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            restaurant_iv = itemView.findViewById(R.id.item_img);
            name_tv = itemView.findViewById(R.id.name_txt);
            type_tv = itemView.findViewById(R.id.type_txt);
            level_tv = itemView.findViewById(R.id.level_txt);
            duration_tv = itemView.findViewById(R.id.duration_txt);
            distance_tv = itemView.findViewById(R.id.distance_txt);
        }
    }

        public void setFilteredList(ArrayList<Restaurant> searchedList) {
            this.restaurantList = searchedList;
            notifyDataSetChanged();
        }
}
