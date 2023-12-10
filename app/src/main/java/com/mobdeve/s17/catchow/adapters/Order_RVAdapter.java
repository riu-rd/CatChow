package com.mobdeve.s17.catchow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s17.catchow.R;
import com.mobdeve.s17.catchow.models.Order;

import java.util.ArrayList;

public class Order_RVAdapter extends RecyclerView.Adapter<Order_RVAdapter.MyViewHolder> {

    Context context;
    ArrayList<Order> orderList = new ArrayList<>();

    public Order_RVAdapter(Context context, ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public Order_RVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cart_rv_item, parent, false);
        return new Order_RVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Order_RVAdapter.MyViewHolder holder, int position) {
        final Order order = orderList.get(position);

        holder.quantity_txt.setText(String.format("%.0f", order.getQuantity()));
        holder.name_txt.setText(order.getName());
        holder.price_txt.setText("₱ "+ String.format("%.2f", order.getPrice()));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView quantity_txt;
        TextView name_txt;
        TextView price_txt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            quantity_txt = itemView.findViewById(R.id.orderquantity_txt);
            name_txt = itemView.findViewById(R.id.ordername_txt);
            price_txt = itemView.findViewById(R.id.orderprice_txt);
        }
    }
}
