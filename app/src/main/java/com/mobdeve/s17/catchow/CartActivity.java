package com.mobdeve.s17.catchow;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s17.catchow.adapters.Address_RVAdapter;
import com.mobdeve.s17.catchow.adapters.CartAdapter;
import com.mobdeve.s17.catchow.models.Address;
import com.mobdeve.s17.catchow.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<CartItem> cartItemList = new ArrayList<>();
    private CartAdapter cartAdapter;

    FirebaseFirestore db;
    FirebaseAuth auth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    String currentEmail;

    Button placeOrderButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Setup Firestore Database and Auth
        db = FirebaseFirestore.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        auth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            currentEmail = acct.getEmail();
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            currentEmail = currentUser.getEmail();
        }

        // Setup Restaurant Recycler View
        // setupCartRecyclerView();

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

    @Override
    public void onBackPressed() {
        // Navigate back to MainActivity
        super.onBackPressed();
        Intent mainIntent = new Intent(CartActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish(); // Optional: Call finish() to close the current activity
    }

    public void profile(View v) {
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }

    private void setupCartRecyclerView() {
        db.collection("users")
                .whereEqualTo("email", currentEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot: snapshotList) {
                            DocumentReference userRef = snapshot.getReference();
                            userRef.collection("cart")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot addressQuerySnapshot) {
                                            Log.d(TAG, "Success: Get USER CART Request Successful!");
                                            List<DocumentSnapshot> cartSnapshotList = addressQuerySnapshot.getDocuments();
                                            cartItemList.clear();
                                            for (DocumentSnapshot cartSnapshot : cartSnapshotList) {
                                                Log.d(TAG, "Cart: " + cartSnapshot.getData());
                                                CartItem cartItem = new CartItem(
                                                        cartSnapshot.getString("name"),
                                                        Double.parseDouble(cartSnapshot.getString("originalPrice")),
                                                        Integer.parseInt(cartSnapshot.getString("quantity"))
                                                );
                                                cartItemList.add(cartItem);
                                            }

                                            // Initialize RecyclerView and its adapter
                                            cartAdapter = new CartAdapter(CartActivity.this, cartItemList);
                                            recyclerView.setAdapter(cartAdapter);
                                            LinearLayoutManager layoutManager = new LinearLayoutManager(CartActivity.this);
                                            recyclerView.setLayoutManager(layoutManager);

                                            updateTotalPrice();

                                            for (CartItem item : cartItemList) {
                                                Log.d("CartItemDebug", "Product: " + item.getName() + ", Quantity: " + item.getQuantity());
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Failure: ", e);
                                        }
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failure: ", e);
                    }
                });
    }
}
