package com.mobdeve.s17.catchow;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class SelectedMenuItem extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth auth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    BottomNavigationView navbar;


    String currentEmail;

    private int quantity = 1;
    private double originalPrice;
    private String name;
    private double price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_menu_item);

        navbar = findViewById(R.id.navbar);
        setupBottomNavigationView();


        Intent intent = getIntent();

        if (intent != null) {
            // Setup Firestore Database and Auth
            db = FirebaseFirestore.getInstance();
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            gsc = GoogleSignIn.getClient(this,gso);
            auth = FirebaseAuth.getInstance();

            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                currentEmail = acct.getEmail();
            }

            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                currentEmail = currentUser.getEmail();
            }
            // Retrieve data from the Intent
            String imageUrl = intent.getStringExtra("imageurl");
            String name = intent.getStringExtra("name");
            String storename = intent.getStringExtra("storename");
            Log.d("asdasd",storename);
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
//            addToCartButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (name.isEmpty() || originalPrice == 0 || quantity == 0) {
//                        Log.e(TAG, "Fill up all fields");
//                        return; // Exit the method if the required fields are not filled
//                    }
//
//                    // Assuming you have a SharedPreferences instance named "cartPreferences"
//                    SharedPreferences sharedPreferences = getSharedPreferences("cartPreferences", Context.MODE_PRIVATE);
//
//                    // Check if the order with the same name already exists
//                    String existingOrderKey = isOrderExists(sharedPreferences, name);
//                    if (existingOrderKey != null) {
//                        // If the order already exists, display a toast indicating that the item is already added
//                        Toast.makeText(getApplicationContext(), "Item is already added to the cart", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // If the order does not exist, proceed to add it
//                        int orderCount = sharedPreferences.getInt("orderCount", 0);
//
//                        // Create a unique key for the new order
//                        String orderKey = "order_" + orderCount;
//
//                        // Increment the order count for the next order
//                        orderCount++;
//
//                        // Save the updated order count
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putInt("orderCount", orderCount);
//                        editor.apply();
//
//                        // Save the order details to SharedPreferences
//                        editor.putString(orderKey + "_name", name);
//                        editor.putFloat(orderKey + "_price", (float) originalPrice);
//                        editor.putInt(orderKey + "_quantity", quantity);
//                        editor.apply();
//
//                        Toast.makeText(getApplicationContext(), "New order added to the cart", Toast.LENGTH_SHORT).show();
//                    }
//
//                    setResult(RESULT_OK, new Intent());
//                    finish();
//                }
//
//                // Helper method to check if an order with the given name already exists
//                private String isOrderExists(SharedPreferences sharedPreferences, String itemName) {
//                    Map<String, ?> allEntries = sharedPreferences.getAll();
//
//                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//                        // Check if the entry is a name key and has the same name as the one being added
//                        if (entry.getKey().endsWith("_name") && entry.getValue().equals(itemName)) {
//                            // Extract the order key from the name key
//                            return entry.getKey().replace("_name", ""); // Return the order key if the item already exists
//                        }
//                    }
//                    return null; // Order with the given name does not exist
//                }
//            });
            addToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (name.isEmpty() || quantity == 0) {
                        Log.e(TAG, "Fill up all fields");
                        return; // Exit the method if the required fields are not filled
                    }

                    // Assuming you have a SharedPreferences instance named "cartPreferences"
                    SharedPreferences sharedPreferences = getSharedPreferences("cartPreferences", Context.MODE_PRIVATE);

                    // Check if the order with the same name already exists
                    String existingOrderKey = isOrderExists(sharedPreferences, name);
                    if (existingOrderKey != null) {
                        // If the order already exists, display a toast indicating that the item is already added
                        Toast.makeText(getApplicationContext(), "Item is already added to the cart", Toast.LENGTH_SHORT).show();
                    } else {
                        // If the order does not exist, proceed to add it
                        int orderCount = sharedPreferences.getInt("orderCount", 0);

                        // Create a unique key for the new order
                        String orderKey = "order_" + orderCount;

                        // Increment the order count for the next order
                        orderCount++;

                        // Save the updated order count
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("orderCount", orderCount);
                        editor.apply();

                        // Save the order details to SharedPreferences using the updated total price
                        editor.putString(orderKey + "_storename", storename);
                        editor.putString(orderKey + "_name", name);
                        editor.putFloat(orderKey + "_price", (float) (originalPrice * quantity));
                        editor.putInt(orderKey + "_quantity", quantity);
                        editor.apply();

                        Toast.makeText(getApplicationContext(), "New order added to the cart", Toast.LENGTH_SHORT).show();
                    }

                    setResult(RESULT_OK, new Intent());
                    finish();
                }

                // Helper method to check if an order with the given name already exists
                private String isOrderExists(SharedPreferences sharedPreferences, String itemName) {
                    Map<String, ?> allEntries = sharedPreferences.getAll();

                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        // Check if the entry is a name key and has the same name as the one being added
                        if (entry.getKey().endsWith("_name") && entry.getValue().equals(itemName)) {
                            // Extract the order key from the name key
                            return entry.getKey().replace("_name", ""); // Return the order key if the item already exists
                        }
                    }
                    return null; // Order with the given name does not exist
                }
            });


            ImageView backButton = findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
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

    public void profile(View v) {
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }

    private void setupBottomNavigationView() {
        navbar.setSelectedItemId(R.id.menu_home);
        navbar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) {
                return true;
            }
            else if (id == R.id.menu_cart) {
                startActivity(new Intent(getApplicationContext(),CartActivity.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                return true;
            }
            else if (id == R.id.menu_address) {
                startActivity(new Intent(getApplicationContext(), AddressActivity.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                return true;
            }
            else if (id == R.id.menu_profile) {
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                return true;
            }
            return false;
        });
    }
}
