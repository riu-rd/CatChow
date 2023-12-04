package com.mobdeve.s17.catchow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.mobdeve.s17.catchow.models.CartItem;

public class SelectedMenuItem extends AppCompatActivity {

    private int quantity = 1;
    private double originalPrice;
    private String name;
    private double price;
    private String image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_menu_item);

        Intent intent = getIntent();

        if (intent != null) {
            // Retrieve data from the Intent
            String imageUrl = intent.getStringExtra("imageurl");
            String name = intent.getStringExtra("name");
            originalPrice = intent.getDoubleExtra("price", 0.0);

            // Load image into ImageView using Glide
            ImageView menuItemImageView = findViewById(R.id.menuItem);
            Glide.with(this).load(imageUrl).into(menuItemImageView);

            // Set text for name and price TextViews
            TextView menuNameTextView = findViewById(R.id.menuName);
            TextView menuPriceTextView = findViewById(R.id.menuPrice);
            menuNameTextView.setText(name);
            menuPriceTextView.setText("₱" + String.format("%.2f", originalPrice));


            // Set OnClickListener for decrease quantity button
            ImageButton decreaseQuantityButton = findViewById(R.id.decreaseQuantity);
            decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decreaseQuantity();
                    updateTotalPrice();
                }
            });

            // Set OnClickListener for increase quantity button
            ImageButton increaseQuantityButton = findViewById(R.id.increaseQuantity);
            increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    increaseQuantity();
                    updateTotalPrice();
                }
            });

            // Set OnClickListener for Add to Cart button
            Button addToCartButton = findViewById(R.id.add2Cart);
            addToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToCart(name, originalPrice, quantity);
                }
            });
            ImageView backButton = findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate back to MainActivity
                    Intent mainIntent = new Intent(SelectedMenuItem.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            });
        }
    }
    private void decreaseQuantity() {
        if (quantity > 1) {
            quantity--;
            updateQuantityTextView();
        }
    }

    private void increaseQuantity() {
        quantity++;
        updateQuantityTextView();
    }

    private void updateQuantityTextView() {
        TextView quantityTextView = findViewById(R.id.quantity);
        quantityTextView.setText(String.valueOf(quantity));
    }
    private void updateTotalPrice() {
        TextView menuPriceTextView = findViewById(R.id.menuPrice);
        double totalPrice = originalPrice * quantity;
        menuPriceTextView.setText("₱" + String.format("%.2f", totalPrice));
    }

    private void addToCart(String name, double originalPrice, int quantity) {
        // Create a CartItem object with the provided data
        CartItem cartItem = new CartItem(name, originalPrice, quantity);

        // Create an Intent to start the CartActivity
        Intent cartIntent = new Intent(SelectedMenuItem.this, CartActivity.class);

        // Pass the CartItem object as an extra in the Intent
        cartIntent.putExtra("cartItem", cartItem);

        // Start the CartActivity with the Intent
        startActivity(cartIntent);
    }

    @Override
    public void onBackPressed() {
        // Navigate back to MainActivity
        super.onBackPressed();
        Intent mainIntent = new Intent(SelectedMenuItem.this, MainActivity.class);
        startActivity(mainIntent);
        finish(); // Optional: Call finish() to close the current activity
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setImage(int quantity) {
        this.quantity = quantity;
    }
}