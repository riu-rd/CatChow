package com.mobdeve.s17.catchow;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectedMenuItem extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth auth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    String currentEmail;

    private int quantity = 1;
    private double originalPrice;
    private String name;
    private double price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_menu_item);


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

                    if (name.equals("") || originalPrice == 0 || quantity == 0) {
                        Log.e(TAG, "Fill up all fields");
                    } else {
                        db.collection("users")
                                .whereEqualTo("email", currentEmail)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        Log.d(TAG, "Success: User found!");

                                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                                        for (DocumentSnapshot snapshot : snapshotList) {
                                            DocumentReference userRef = snapshot.getReference();
                                            CollectionReference ordersCollection = userRef.collection("orders");

                                            // Check if the item already exists in the "orders" subcollection
                                            ordersCollection.whereEqualTo("name", name)
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot querySnapshot) {
                                                            if (querySnapshot.isEmpty()) {
                                                                // The name does not exist, so add the new order
                                                                Map<String, Object> newOrder = new HashMap<>();
                                                                newOrder.put("name", name);
                                                                newOrder.put("price", originalPrice);
                                                                newOrder.put("quantity", quantity);

                                                                ordersCollection.add(newOrder)
                                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentReference documentReference) {
                                                                                Log.d(TAG, "Order added with ID: " + documentReference.getId());
                                                                                setResult(RESULT_OK, new Intent());
                                                                                finish();
                                                                                Toast.makeText(getApplicationContext(), "New order Added", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.w(TAG, "Error adding address", e);
                                                                            }
                                                                        });
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Order already exists", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e(TAG, "Failure checking if label exists", e);
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
}
