package com.mobdeve.s17.catchow;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s17.catchow.adapters.Order_RVAdapter;
import com.mobdeve.s17.catchow.models.Order;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    ArrayList<Order> orderList = new ArrayList<>();
    RecyclerView cart_rv;
    Order_RVAdapter order_adapter;
    LinearLayoutManager verticalLayoutManager;
    TextView total_price;

    FirebaseFirestore db;
    FirebaseAuth auth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cart_rv = findViewById(R.id.cart_rv);
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
        setupCartRecyclerView();
    }

    private void setupCartRecyclerView() {
        Log.d(TAG, "THIS IS WORKING");
        db.collection("users")
                .whereEqualTo("email", currentEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot: snapshotList) {
                            DocumentReference userRef = snapshot.getReference();
                            userRef.collection("orders")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot orderQuerySnapshot) {
                                            Log.d(TAG, "Success: Get USER ORDERS Request Successful!");
                                            List<DocumentSnapshot> orderSnapshotList = orderQuerySnapshot.getDocuments();

                                            orderList.clear();
                                            for (DocumentSnapshot orderSnapshot : orderSnapshotList) {
                                                Log.d(TAG, "Order: " + orderSnapshot.getData());
                                                String name = orderSnapshot.getString("name");
                                                Double price = orderSnapshot.getDouble("price");
                                                Double quantity = orderSnapshot.getDouble("quantity");

                                                Order order = new Order(name, price, quantity);
                                                orderList.add(order);
                                            }

                                            // Initialize Recycler View
                                            order_adapter = new Order_RVAdapter(CartActivity.this, orderList);
                                            cart_rv.setAdapter(order_adapter);
                                            verticalLayoutManager = new LinearLayoutManager(CartActivity.this, LinearLayoutManager.VERTICAL, false);
                                            cart_rv.setLayoutManager(verticalLayoutManager);

                                            calculateTotalPrice(orderList);
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



    public void goBack (View v) {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
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

    private void calculateTotalPrice(ArrayList<Order> orderList) {
        Double tempTotal = 0.0;
        for (Order order : orderList) {
            tempTotal += (order.getPrice() * order.getQuantity());
        }
        total_price.setText("₱ "+ String.format("%.2f", tempTotal));
    }
}
