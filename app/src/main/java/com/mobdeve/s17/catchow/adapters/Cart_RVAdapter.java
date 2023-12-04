package com.mobdeve.s17.catchow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s17.catchow.R;
import com.mobdeve.s17.catchow.SelectedMenuItem;
import com.mobdeve.s17.catchow.models.Order;

import java.util.List;
import java.util.Locale;

public class Cart_RVAdapter extends RecyclerView.Adapter<Cart_RVAdapter.CartViewHolder> {

    private List<Order> orderList;

    public Cart_RVAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }
    // Add this method to add items to the cart
    public void addItem(SelectedMenuItem selectedMenuItem) {
        // Create a Order from the SelectedMenuItem
        Order order = new Order(selectedMenuItem.getName(), selectedMenuItem.getPrice(), selectedMenuItem.getQuantity());

        // Add the Order to the list
        orderList.add(order);

        // Notify the adapter about the data set change
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cart_rv_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Set the data to views
        holder.cartItemName.setText(order.getName());
        holder.cartItemPrice.setText(String.format(Locale.getDefault(), "₱%.2f", order.getPrice()));

        // You may load the image using a library like Picasso or Glide
        // Example using Glide:
        // Glide.with(holder.itemView.getContext()).load(order.getImageUrl()).into(holder.cartItemImage);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView cartItemImage;
        TextView cartItemName, cartItemPrice;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cartItemImage = itemView.findViewById(R.id.cart_item_image);
            cartItemName = itemView.findViewById(R.id.cart_item_name);
            cartItemPrice = itemView.findViewById(R.id.cart_item_price);
        }
    }
}
