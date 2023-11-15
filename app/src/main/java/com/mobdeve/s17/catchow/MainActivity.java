package com.mobdeve.s17.catchow;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s17.catchow.adapters.Restaurant_RVAdapter;
import com.mobdeve.s17.catchow.models.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<Restaurant> restaurantList = new ArrayList<Restaurant>();
    FirebaseFirestore db;

    // Restaurant Recycler View Variables
    RecyclerView restaurant_rv;
    Restaurant_RVAdapter adapter;
    LinearLayoutManager horizontalLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declarations
        restaurant_rv = findViewById(R.id.restaurant_rv);

        // Setup Firestore Database
        db = FirebaseFirestore.getInstance();

        // Setup Restaurant Recycler View
        setupRestaurantRecyclerView();
    }

    private void setupRestaurantRecyclerView() {
        db.collection("restaurants")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "Success: Get Request Successful!");
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot snapshot: snapshotList) {
                    Log.d(TAG, "Item: " + snapshot.getData());
                    Restaurant res = new Restaurant(
                            snapshot.getString("imageurl"),
                            snapshot.getString("name"),
                            snapshot.getString("type"),
                            snapshot.getString("level"),
                            snapshot.getDouble("minimum"),
                            snapshot.getDouble("fee"),
                            snapshot.getString("duration"),
                            snapshot.getString("distance"),
                            new ArrayList<>(), // Initialize empty menu
                            new ArrayList<>()  // Initialize empty ratings
                    );
                    Log.d(TAG, "Is this working?");
                    restaurantList.add(res);
                }

                adapter = new Restaurant_RVAdapter(MainActivity.this, restaurantList);
                restaurant_rv.setAdapter(adapter);
                horizontalLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                restaurant_rv.setLayoutManager(horizontalLayoutManager);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failure: ", e);
            }
        });
    }
}