package com.mobdeve.s17.catchow.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s17.catchow.AddAddressActivity;
import com.mobdeve.s17.catchow.History;
import com.mobdeve.s17.catchow.R;
import com.mobdeve.s17.catchow.ResMenuActivity;
import com.mobdeve.s17.catchow.ViewAddressActivity;
import com.mobdeve.s17.catchow.models.Address;

import java.util.ArrayList;

public class History_RVAdapter extends RecyclerView.Adapter<History_RVAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<History> historyList;

    public History_RVAdapter(Context context, ArrayList<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == historyList.size()) ? R.layout.history_rv_button : R.layout.history_rv_item;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == R.layout.history_rv_item) {
            view = inflater.inflate(R.layout.history_rv_item, parent, false);
        } else {
            view = inflater.inflate(R.layout.history_rv_button, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Check if the item is the button
        if (position == historyList.size()) {
            // Handle button view
            holder.add_address_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the button click (e.g., start AddAddressActivity)
                    ((Activity) context).startActivityForResult(new Intent(context, AddAddressActivity.class), 69);
                }
            });
        } else {
            // Handle regular item view
            History currentHistory = historyList.get(position);

            // Bind data to views in the regular item layout
//            holder.label_txt.setText(currentHistory.getLabel());
//            holder.address_txt.setText(currentHistory.getAddress());

            // Set an onClickListener for regular items if needed
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle item click (e.g., start detailed view activity)
                    Intent intent = new Intent(context, ResMenuActivity.class);
                    // Pass any necessary data to the detailed view activity
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size() + 1;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView label_txt;
        TextView address_txt;
        CardView add_address_btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            label_txt = itemView.findViewById(R.id.labelAs_txt);
            address_txt = itemView.findViewById(R.id.full_address_txt);
            add_address_btn = itemView.findViewById(R.id.address_rv_button_card);
        }
    }
}