package com.mobdeve.s17.catchow;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s17.catchow.adapters.CartAdapter;
import com.mobdeve.s17.catchow.adapters.OrderItem;
import com.mobdeve.s17.catchow.adapters.Order_RVAdapter;
import com.mobdeve.s17.catchow.models.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderPlacedActivity extends AppCompatActivity {

    BottomNavigationView navbar;

    ArrayList<Order> orderList = new ArrayList<>();
    RecyclerView placedorder_rv;
    Order_RVAdapter order_adapter;
    LinearLayoutManager verticalLayoutManager;
    TextView total_price, deliveryPriceTextView, totalPriceTextView, totalPriceFinalTextView;

    FirebaseFirestore db;
    FirebaseAuth auth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);

        //        Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        placedorder_rv = findViewById(R.id.placedorder_rv);
        total_price = findViewById(R.id.total_price);

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

        // Setup Restaurant Recycler View
        setupOrdersRecyclerView();

        navbar = findViewById(R.id.navbar);

        //Setup Bottom Navigation View
        setupBottomNavigationView();

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        // Find references to the TextViews
        deliveryPriceTextView = findViewById(R.id.delivery_price);
        totalPriceTextView = findViewById(R.id.total_price);
        totalPriceFinalTextView = findViewById(R.id.total_price_final);

        // Find the top_profile_btn ImageView
        ImageView topProfileBtn = findViewById(R.id.top_profile_btn);
        topProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event for top_profile_btn
                goToProfileActivity();
            }
        });

        calculateTotalPrice();

    }


    private void setupOrdersRecyclerView() {
//        Log.d(TAG, "THIS IS WORKING");
//        db.collection("users")
//                .whereEqualTo("email", currentEmail)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
//                        for (DocumentSnapshot snapshot: snapshotList) {
//                            DocumentReference userRef = snapshot.getReference();
//                            userRef.collection("orders")
//                                    .get()
//                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                        @Override
//                                        public void onSuccess(QuerySnapshot orderQuerySnapshot) {
//                                            Log.d(TAG, "Success: Get USER ORDERS Request Successful!");
//                                            List<DocumentSnapshot> orderSnapshotList = orderQuerySnapshot.getDocuments();
//
//                                            orderList.clear();
//                                            for (DocumentSnapshot orderSnapshot : orderSnapshotList) {
//                                                Log.d(TAG, "Order: " + orderSnapshot.getData());
//                                                String name = orderSnapshot.getString("name");
//                                                Double price = orderSnapshot.getDouble("price");
//                                                Double quantity = orderSnapshot.getDouble("quantity");
//
//                                                Order order = new Order(name, price, quantity);
//                                                orderList.add(order);
//                                            }
//
//                                            // Initialize Recycler View
//                                            order_adapter = new Order_RVAdapter(OrderPlacedActivity.this, orderList);
//                                            placedorder_rv.setAdapter(order_adapter);
//                                            verticalLayoutManager = new LinearLayoutManager(OrderPlacedActivity.this, LinearLayoutManager.VERTICAL, false);
//                                            placedorder_rv.setLayoutManager(verticalLayoutManager);
//
//                                            calculateTotalPrice(orderList);
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Log.e(TAG, "Failure: ", e);
//                                        }
//                                    });
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e(TAG, "Failure: ", e);
//                    }
//                });
        // Assuming you have a RecyclerView named "cartRecyclerView" in your layout
        RecyclerView cartRecyclerView = findViewById(R.id.placedorder_rv);

        // Assuming you have a SharedPreferences instance named "cartPreferences"
        SharedPreferences sharedPreferences = getSharedPreferences("cartPreferences", Context.MODE_PRIVATE);

        // Get the total number of orders stored in SharedPreferences
        int orderCount = sharedPreferences.getInt("orderCount", 0);

        // Create a list to store the order details
        List<OrderItem> orderList = new ArrayList<>();

        // Retrieve each order from SharedPreferences
        for (int i = 0; i < orderCount; i++) {
            String orderKey = "order_" + i;
            String name = sharedPreferences.getString(orderKey + "_name", "");
            float price = sharedPreferences.getFloat(orderKey + "_price", 0.0f);
            int quantity = sharedPreferences.getInt(orderKey + "_quantity", 0);

            // Create an OrderItem object and add it to the list
            OrderItem orderItem = new OrderItem(name, price, quantity);
            orderList.add(orderItem);
        }

        // Create an adapter for the RecyclerView
        CartAdapter cartAdapter = new CartAdapter(orderList);

        // Set the adapter to the RecyclerView
        cartRecyclerView.setAdapter(cartAdapter);

        // Optionally, you can set a layout manager for the RecyclerView (e.g., LinearLayoutManager)
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void calculateTotalPrice(ArrayList<Order> orderList) {
        Double tempTotal = 0.0;
        for (Order order : orderList) {
            tempTotal += (order.getPrice() * order.getQuantity());
        }

        // Get the delivery fee value from the delivery_price TextView
        String deliveryFeeString = deliveryPriceTextView.getText().toString().replace("₱", "");
        Double deliveryFee = Double.parseDouble(deliveryFeeString);

        // Calculate the total with delivery fee
        Double totalWithDeliveryFee = tempTotal + deliveryFee;
        total_price.setText("₱ "+ String.format("%.2f", tempTotal));

        // Update the total_price_final TextView
        totalPriceFinalTextView.setText("₱ " + String.format("%.2f", totalWithDeliveryFee));
    }

    private void calculateTotalPrice() {
        // Assuming you have a SharedPreferences instance named "cartPreferences"
        SharedPreferences sharedPreferences = getSharedPreferences("cartPreferences", Context.MODE_PRIVATE);

        // Get the total number of orders stored in SharedPreferences
        int orderCount = sharedPreferences.getInt("orderCount", 0);

        // Initialize variables to store subtotal and total price
        float subtotal = 0.0f;
        float deliveryPrice = 40.0f; // Assuming a fixed delivery price of ₱40.0
        float totalPrice = 0.0f;

        // Iterate through each order and calculate the subtotal
        for (int i = 0; i < orderCount; i++) {
            String orderKey = "order_" + i;
            float price = sharedPreferences.getFloat(orderKey + "_price", 0.0f);

            // Add the price directly without multiplication by quantity
            subtotal += price;
        }

        // Calculate the total price by adding the subtotal and delivery price
        totalPrice = subtotal + deliveryPrice;

        // Assuming you have TextViews named "subtotal" and "total_price_final" in your layout
        TextView subtotalTextView = findViewById(R.id.total_price);
        subtotalTextView.setText(String.format(Locale.getDefault(), "₱ %.2f", subtotal));

        TextView totalPriceFinalTextView = findViewById(R.id.total_price_final);
        totalPriceFinalTextView.setText(String.format(Locale.getDefault(), "₱ %.2f", totalPrice));
    }


    private void setupBottomNavigationView() {
        navbar.setSelectedItemId(R.id.menu_cart);
        navbar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_cart) {
                clearSharedPreferencesData();
                return true;
            }
            else if (id == R.id.menu_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                clearSharedPreferencesData();
                finish();
                return true;
            }
            else if (id == R.id.menu_address) {
                startActivity(new Intent(getApplicationContext(), AddressActivity.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                clearSharedPreferencesData();
                finish();
                return true;
            }
            else if (id == R.id.menu_profile) {
                clearSharedPreferencesData();
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {
        // Call your method to clear SharedPreferences data
        clearSharedPreferencesData();

        // Call super.onBackPressed() to handle the default back button behavior
        super.onBackPressed();
    }

    private void goToProfileActivity() {
        Intent intent = new Intent(OrderPlacedActivity.this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish(); // Optional: Finish the current activity if you don't want to come back to it when pressing back from ProfileActivity
    }

    private void clearSharedPreferencesData() {
        SharedPreferences sharedPreferences = getSharedPreferences("cartPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Activity started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Activity resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Activity stopped");
        clearSharedPreferencesData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity destroyed");
        clearSharedPreferencesData();
    }

    public void goBack() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }
}