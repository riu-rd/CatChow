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
import com.mobdeve.s17.catchow.R;
import com.mobdeve.s17.catchow.ResMenuActivity;
import com.mobdeve.s17.catchow.ViewAddressActivity;
import com.mobdeve.s17.catchow.models.Address;

import java.util.ArrayList;

public class Address_RVAdapter extends RecyclerView.Adapter<Address_RVAdapter.MyViewHolder> {
    Context context;
    ArrayList<Address> addressList;

    public Address_RVAdapter(Context context, ArrayList<Address> addressList) {
        this.context = context;
        this.addressList = addressList;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == addressList.size()) ? R.layout.address_rv_button : R.layout.address_rv_item;
    }

    @NonNull
    @Override
    public Address_RVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        if(viewType == R.layout.address_rv_item){
            view = inflater.inflate(R.layout.address_rv_item, parent, false);
        }
        else {
            view = inflater.inflate(R.layout.address_rv_button, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Address_RVAdapter.MyViewHolder holder, int position) {
        if(position == addressList.size()) {
            holder.add_address_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) context).startActivityForResult(new Intent(context, AddAddressActivity.class),69);
                }
            });
        }
        else {
            final Address currAddress = addressList.get(position);
            String label = currAddress.getLabel();
            String region = currAddress.getRegion();
            String postal = currAddress.getPostalCode();
            String street = currAddress.getStreet();

            holder.label_txt.setText(label);
            holder.address_txt.setText(street + ", " + region + ", " + postal);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent resIntent = new Intent(context, ViewAddressActivity.class);
                    resIntent.putExtra("label", label);
                    resIntent.putExtra("region", region);
                    resIntent.putExtra("postal", postal);
                    resIntent.putExtra("street", street);
                    ((Activity) context).startActivityForResult(resIntent,69);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return addressList.size() + 1;
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

    public void updateAddressList (ArrayList<Address> newList) {
        this.addressList = newList;
        notifyDataSetChanged();
    }
}
