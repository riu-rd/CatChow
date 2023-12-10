package com.mobdeve.s17.catchow;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s17.catchow.adapters.CartAdapter;
import com.mobdeve.s17.catchow.adapters.OrderItem;
import com.mobdeve.s17.catchow.adapters.Order_RVAdapter;
import com.mobdeve.s17.catchow.models.Order;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CartActivity extends AppCompatActivity {

//    ArrayList<Order> orderList = new ArrayList<>();
    RecyclerView cart_rv;
    Order_RVAdapter order_adapter;
    LinearLayoutManager verticalLayoutManager;
    TextView total_price, pinLocation, exactLocation;

    Button place_order;
    RadioButton cash_option, gcash_option;

    RadioGroup paymentRadioGroup;

    FirebaseFirestore db;
    FirebaseAuth auth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

//        Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

//        place order
        place_order = findViewById(R.id.place_order);

//        radio button
        paymentRadioGroup = findViewById(R.id.paymentRadioGroup);
        cash_option = findViewById(R.id.cash_option);
        gcash_option = findViewById(R.id.gcash_option);

        cart_rv = findViewById(R.id.cart_rv);
        total_price = findViewById(R.id.total_price);

        // Setup Firestore Database and Auth
        db = FirebaseFirestore.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        auth = FirebaseAuth.getInstance();

//        textviews for address
        pinLocation = findViewById(R.id.pinLocation);
        exactLocation = findViewById(R.id.exactLocation);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            currentEmail = acct.getEmail();
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            currentEmail = currentUser.getEmail();
        }

        // Setup Restaurant Recycler View
        setupCartRecyclerView();

        place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if cart_rv is empty
                RecyclerView.Adapter adapter = cart_rv.getAdapter();
                if (adapter == null || adapter.getItemCount() == 0) {
                    // Cart is empty, show a toast message
                    Toast.makeText(CartActivity.this, "Your cart is empty. Add items before placing an order.", Toast.LENGTH_SHORT).show();
                } else if (pinLocation.getText().toString().equals("No Address Added")) {
                    Toast.makeText(CartActivity.this, "Setup your address first.", Toast.LENGTH_SHORT).show();
                } else if (paymentRadioGroup.getCheckedRadioButtonId() == -1) {
                    // No radio button is selected, show a toast message
                    Toast.makeText(CartActivity.this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                } else {
                    // Cart is not empty, proceed with the order placement
                    saveSharedPreferencesDataToFirestore();

                    // Start the OrderPlacedActivity
                    startActivity(new Intent(CartActivity.this, OrderPlacedActivity.class));
                    finish();
                }
            }
        });

        // Check if the user is signed in
        if (currentEmail != null) {
            // Log the current email
            Log.d("YourTag", "Current Email: " + currentEmail);

            // Query to find the user document based on the email
            db.collection("users")
                    .whereEqualTo("email", currentEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        // Assuming there is only one matching document
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot userSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            // Get the current user ID
                            String currentUserId = userSnapshot.getId();

                            // Log the current user ID
                            Log.d("YourTag", "Current User ID: " + currentUserId);

                            // Create a reference to the user's "Addresses" collection in Firestore
                            CollectionReference addressesCollection = db.collection("users").document(currentUserId).collection("addresses");

                            // Log that the Addresses collection reference is created
                            Log.d("YourTag", "Addresses Collection Reference created");

                            // Retrieve the first document in the "Addresses" collection
                            addressesCollection.get().addOnSuccessListener(addressesQuerySnapshot -> {
                                if (!addressesQuerySnapshot.isEmpty()) {
                                    // Assuming there is only one address document
                                    DocumentSnapshot addressDocument = addressesQuerySnapshot.getDocuments().get(0);

                                    // Get the values of "label" and "region" fields
                                    String label = addressDocument.getString("label");
                                    String region = addressDocument.getString("region");

                                    // Log the retrieved values
                                    Log.d("YourTag", "Label: " + label + ", Region: " + region);

                                    // Convert label text to all caps
                                    String labelCaps = label.toUpperCase();

                                    // Set the values to your pinLocation and exactLocation TextViews
                                    pinLocation.setText(labelCaps);
                                    exactLocation.setText(region);
                                } else {
                                    Log.e("YourTag", "No documents found in Addresses collection");
                                }
                            });
                        } else {
                            Log.e("YourTag", "No matching documents found for email: " + currentEmail);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("YourTag", "Error querying user document: ", e);
                    });
        }



        calculateTotalPrice();

    }


    private void calculateTotalPrice() {
        // Assuming you have a SharedPreferences instance named "cartPreferences"
        SharedPreferences sharedPreferences = getSharedPreferences("cartPreferences", Context.MODE_PRIVATE);

        // Get the total number of orders stored in SharedPreferences
        int orderCount = sharedPreferences.getInt("orderCount", 0);

        // Initialize a variable to store the total price
        float totalPrice = 0.0f;

        // Iterate through each order and calculate the total price
        for (int i = 0; i < orderCount; i++) {
            String orderKey = "order_" + i;
            float price = sharedPreferences.getFloat(orderKey + "_price", 0.0f);

            // Add the price directly without multiplication by quantity
            totalPrice += price;
        }

        // Assuming you have a TextView named "total_price" in your layout
        TextView totalPriceTextView = findViewById(R.id.total_price);
        totalPriceTextView.setText(String.format(Locale.getDefault(), "₱ %.2f", totalPrice));

    }


    private void saveSharedPreferencesDataToFirestore() {
        // Assuming you have a SharedPreferences instance named "cartPreferences"
        SharedPreferences sharedPreferences = getSharedPreferences("cartPreferences", Context.MODE_PRIVATE);

        // Get the current user from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if the user is signed in
        if (currentEmail != null) {
            Log.d(TAG, "Current Email: " + currentEmail);

            // Query to find the user document based on the email
            db.collection("users")
                    .whereEqualTo("email", currentEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        Log.d(TAG, "Success: User found!");

                        // Assuming there is only one matching document
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot userSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            // Get the current user ID
                            String currentUserId = userSnapshot.getId();
                            Log.d(TAG, "Current User ID: " + currentUserId);

                            // Create a reference to the user's document in Firestore
                            DocumentReference userDocumentRef = db.collection("users").document(currentUserId);
                            Log.d(TAG, "User Document Reference created: " + userDocumentRef.getPath());

                            // Get the selected payment method from the RadioGroup
                            RadioButton selectedPaymentMethod = findViewById(paymentRadioGroup.getCheckedRadioButtonId());
                            String paymentMethod = selectedPaymentMethod.getText().toString();
                            Log.d(TAG, "Selected Payment Method: " + paymentMethod);

                            // Create a new orders collection for the user
                            CollectionReference ordersCollection = userDocumentRef.collection("orders");

                            // Get the total number of orders stored in SharedPreferences
                            int orderCount = sharedPreferences.getInt("orderCount", 0);

                            List<Task<Void>> tasks = new ArrayList<>();

                            for (int i = 0; i < orderCount; i++) {
                                String orderKey = "order_" + i;
                                String storename = sharedPreferences.getString(orderKey + "_storename", "");
                                String name = sharedPreferences.getString(orderKey + "_name", "");
                                float price = sharedPreferences.getFloat(orderKey + "_price", 0.0f);
                                int quantity = sharedPreferences.getInt(orderKey + "_quantity", 0);

                                // Create a Map with order details
                                Map<String, Object> orderData = new HashMap<>();
                                orderData.put("storename", storename);
                                orderData.put("name", name);
                                orderData.put("price", price);
                                orderData.put("quantity", quantity);
                                orderData.put("paidBy", paymentMethod);

                                // Set the timestamp field for the order
                                orderData.put("timestamp", FieldValue.serverTimestamp());

                                // Set the order data to a new document in the "Orders" collection with a unique ID
                                String uniqueOrderId = UUID.randomUUID().toString();
                                DocumentReference orderDocument = ordersCollection.document(uniqueOrderId);
                                tasks.add(orderDocument.set(orderData));
                            }

                            // Use Tasks.whenAllComplete to handle the completion of all tasks
                            Task<List<Task<?>>> allTasks = Tasks.whenAllComplete(tasks);
                            allTasks.addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "All order details added successfully to Firestore");
                                // Optionally, you can clear SharedPreferences after successfully saving the data
                                // clearSharedPreferences();
                            }).addOnFailureListener(e -> Log.w(TAG, "Error adding order details to Firestore", e));
                        } else {
                            Log.e(TAG, "No matching user document found");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failure: ", e);
                    });
        }
    }



    private void setupCartRecyclerView() {
        // Assuming you have a RecyclerView named "cartRecyclerView" in your layout
        RecyclerView cartRecyclerView = findViewById(R.id.cart_rv);

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

    public void goBack (View v) {
//        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }

    public void profile(View v) {
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }

    public void addItems (View v) {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity destroyed");
    }
}
