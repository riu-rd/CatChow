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
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    ImageButton profile_btn;
    SearchView res_sv;
    Button res_btn;
    Button coffee_btn;
    Button des_btn;
    BottomNavigationView navbar;

    // Restaurant Recycler View Variables
    RecyclerView restaurant_rv;
    Restaurant_RVAdapter adapter;
    LinearLayoutManager horizontalLayoutManager;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView greeting_text;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Declarations
        profile_btn = findViewById(R.id.top_profile_btn);
        restaurant_rv = findViewById(R.id.restaurant_rv);
        res_sv = findViewById(R.id.restaurant_search);
        res_btn = findViewById(R.id.res_btn);
        coffee_btn = findViewById(R.id.coffee_btn);
        des_btn = findViewById(R.id.dessert_btn);
        navbar = findViewById(R.id.navbar);
        greeting_text = findViewById(R.id.greeting_txt);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        //Setup Bottom Navigation View
        setupBottomNavigationView();

        // Setup Search View
        setupSearchView();

        // Setup Firestore Database
        db = FirebaseFirestore.getInstance();

        // Setup Restaurant Recycler View
        setupRestaurantRecyclerView();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            greeting_text.setText("Hello, " + personName + "!");
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            firestore.collection("users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String name = documentSnapshot.getString("name");
                            String firstName = extractFirstName(name);

                            greeting_text.setText("Hello, " + firstName + "!");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private String extractFirstName(String fullName) {
        if (fullName != null && fullName.contains(" ")) {
            return fullName.substring(0, fullName.indexOf(" "));
        }
        return fullName;
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

    private void setupSearchView() {
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
                            snapshot.getString("distance")
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

    public void profile(View v) {
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
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
            if (res.getType().toLowerCase().contains("Cafe".toLowerCase())) {
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
//        clearSharedPreferencesData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Activity resumed");
//        clearSharedPreferencesData();
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