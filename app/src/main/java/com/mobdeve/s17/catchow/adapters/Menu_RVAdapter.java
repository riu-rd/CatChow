package com.mobdeve.s17.catchow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobdeve.s17.catchow.R;
import com.mobdeve.s17.catchow.models.Food;

import java.util.ArrayList;

public class Menu_RVAdapter extends RecyclerView.Adapter<Menu_RVAdapter.MyViewHolder> {
    Context context;
    ArrayList<Food> menu;

    public Menu_RVAdapter(Context context, ArrayList<Food> menu) {
        this.context = context;
        this.menu = menu;
    }

    @NonNull
    @Override
    public Menu_RVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.menu_rv_item, parent, false);
        return new Menu_RVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Menu_RVAdapter.MyViewHolder holder, int position) {
        final Food currFood = menu.get(position);

        Glide.with(context).load(currFood.getImageurl()).into(holder.food_iv);
        holder.price_txt.setText("₱ "+ String.format("%.2f", currFood.getPrice()));
        holder.name_txt.setText(currFood.getName());
    }

    @Override
    public int getItemCount() {
        return menu.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView food_iv;
        TextView price_txt;
        TextView name_txt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            food_iv = itemView.findViewById(R.id.currfood_img);
            price_txt = itemView.findViewById(R.id.currfood_price);
            name_txt = itemView.findViewById(R.id.currfood_name);
        }
    }

    public void setMenu(ArrayList<Food> searchedList) {
        this.menu = searchedList;
        notifyDataSetChanged();
    }
}
