package com.mobdeve.s17.catchow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.s17.catchow.adapters.Cart_RVAdapter;
import com.mobdeve.s17.catchow.models.Order;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Order> orderList = new ArrayList<>();
    private Cart_RVAdapter cartRVAdapter;

    Button placeOrderButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cartRVAdapter = new Cart_RVAdapter(orderList);
        recyclerView.setAdapter(cartRVAdapter);

        updateTotalPrice();



        // Handle "Place Order" button click
        placeOrderButton = findViewById(R.id.button);
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement the logic to place the order
            }
        });

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity
                Intent mainIntent = new Intent(CartActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish(); // Optional: Call finish() to close the current activity
            }
        });
        // Set up RadioGroup for payment method
        RadioGroup paymentRadioGroup = findViewById(R.id.paymentRadioGroup);
        paymentRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO: Implement logic based on the selected payment method
            }
        });

        // Retrieve the Order from the intent
        Intent intent = getIntent();
        if (intent != null) {
            Order order = (Order) intent.getSerializableExtra("order");
            if (order != null) {
                orderList.add(order);
                cartRVAdapter.notifyDataSetChanged();
                updateTotalPrice();
            }
        }
    }
    private void onMenuItemSelected(SelectedMenuItem selectedMenuItem) {
        // Add the selected item to the cart
        cartRVAdapter.addItem(selectedMenuItem);
    }
    // Add a method to update the total price based on cart items
    private void updateTotalPrice() {
        double totalPrice = 0.0;
        for (Order order : orderList) {
            totalPrice += order.getPrice() * order.getQuantity();
        }

        TextView totalPriceTextView = findViewById(R.id.price);
        totalPriceTextView.setText("₱" + String.format("%.2f", totalPrice));
    }
    private List<Order> getCartItems() {
        List<Order> items = new ArrayList<>();
        return items;
    }
    @Override
    public void onBackPressed() {
        // Navigate back to MainActivity
        super.onBackPressed();
        Intent mainIntent = new Intent(CartActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish(); // Optional: Call finish() to close the current activity
    }
}
