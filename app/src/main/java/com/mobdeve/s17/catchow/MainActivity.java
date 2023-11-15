package com.mobdeve.s17.catchow;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

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
    ArrayList<Restaurant> restaurantList = new ArrayList<>();
    FirebaseFirestore db;
    SearchView res_sv;
    Button res_btn;
    Button coffee_btn;
    Button des_btn;

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
        res_sv = findViewById(R.id.restaurant_search);
        res_btn = findViewById(R.id.res_btn);
        coffee_btn = findViewById(R.id.coffee_btn);
        des_btn = findViewById(R.id.dessert_btn);

        // Setup Search View
        res_sv.clearFocus();
        res_sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });

        // Setup Firestore Database
        db = FirebaseFirestore.getInstance();

        // Setup Restaurant Recycler View
        setupRestaurantRecyclerView();
    }

    private void search(String newText) {
        ArrayList<Restaurant> filteredList = new ArrayList<>();
        for (Restaurant res: restaurantList) {
            if (res.getName().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(res);
            }
            adapter.setFilteredList(filteredList);
        }
    }

    public void filterAll(View v) {
        adapter.setFilteredList(restaurantList);
    }

    public void filterRestaurant(View v) {
        ArrayList<Restaurant> filteredList = new ArrayList<>();
        for (Restaurant res: restaurantList) {
            if (res.getType().toLowerCase().contains("Restaurant".toLowerCase())) {
                filteredList.add(res);
            }
            adapter.setFilteredList(filteredList);
        }
    }

    public void filterCoffee(View v) {
        ArrayList<Restaurant> filteredList = new ArrayList<>();
        for (Restaurant res: restaurantList) {
            if (res.getType().toLowerCase().contains("Café".toLowerCase())) {
                filteredList.add(res);
            }
            adapter.setFilteredList(filteredList);
        }
    }

    public void filterDessert(View v) {
        ArrayList<Restaurant> filteredList = new ArrayList<>();
        for (Restaurant res: restaurantList) {
            if (res.getType().toLowerCase().contains("Dessert".toLowerCase())) {
                filteredList.add(res);
            }
            adapter.setFilteredList(filteredList);
        }
    }

    private void setupRestaurantRecyclerView() {
        db.collection("restaurants")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "Success: Get Restaurants Request Successful!");
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot snapshot: snapshotList) {
                    Log.d(TAG, "Restaurant: " + snapshot.getData());
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
                    restaurantList.add(res);
                }

                // Initialize Recycler View
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