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

import com.mobdeve.s17.catchow.adapters.CartAdapter;
import com.mobdeve.s17.catchow.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<CartItem> cartItemList = new ArrayList<>();
    private CartAdapter cartAdapter;

    Button placeOrderButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize RecyclerView and its adapter
        cartItemList = getCartItems();
        recyclerView = findViewById(R.id.cart_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cartAdapter = new CartAdapter(cartItemList);
        recyclerView.setAdapter(cartAdapter);


        for (CartItem item : cartItemList) {
            Log.d("CartItemDebug", "Product: " + item.getName() + ", Quantity: " + item.getQuantity());
        }

        updateTotalPrice();

        // Handle "Place Order" button click
        placeOrderButton = findViewById(R.id.placeOrder_button);
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to navigate to OrderPlacedActivity
                Intent orderPlacedIntent = new Intent(CartActivity.this, OrderPlacedActivity.class);

                // Start the OrderPlacedActivity
                startActivity(orderPlacedIntent);
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

        // Retrieve the CartItem from the intent
        Intent intent = getIntent();
        if (intent != null) {
            CartItem cartItem = (CartItem) intent.getSerializableExtra("cartItem");
            if (cartItem != null) {
                cartItemList.add(cartItem);
                cartAdapter.notifyDataSetChanged(); // Notify adapter about data change
                updateTotalPrice(); // Update the total price after adding the item
            }
        }
    }
    private void onMenuItemSelected(SelectedMenuItem selectedMenuItem) {
        // Add the selected item to the cart
        cartAdapter.addItem(selectedMenuItem);
    }
    // Add a method to update the total price based on cart items
    private void updateTotalPrice() {
        double totalPrice = 0.0;
        for (CartItem cartItem : cartItemList) {
            totalPrice += cartItem.getPrice() * cartItem.getQuantity();
        }

        TextView totalPriceTextView = findViewById(R.id.price);
        totalPriceTextView.setText("₱" + String.format("%.2f", totalPrice));
    }
    private List<CartItem> getCartItems() {
        List<CartItem> items = new ArrayList<>();
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