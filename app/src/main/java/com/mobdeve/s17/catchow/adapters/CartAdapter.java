package com.mobdeve.s17.catchow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s17.catchow.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<OrderItem> orderList;

    public CartAdapter(List<OrderItem> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your item layout here
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Bind data to your views here
        OrderItem orderItem = orderList.get(position);
        holder.nameTextView.setText(orderItem.getName());
        holder.priceTextView.setText(String.valueOf(orderItem.getPrice()));
        holder.quantityTextView.setText(String.valueOf(orderItem.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView, quantityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize your views here
            nameTextView = itemView.findViewById(R.id.ordername_txt);
            priceTextView = itemView.findViewById(R.id.orderprice_txt);
            quantityTextView = itemView.findViewById(R.id.orderquantity_txt);
        }
    }
}
