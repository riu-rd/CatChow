package com.mobdeve.s17.catchow;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s17.catchow.adapters.Menu_RVAdapter;
import com.mobdeve.s17.catchow.models.Food;
import com.mobdeve.s17.catchow.models.Rating;

import java.util.ArrayList;
import java.util.List;

public class ResMenuActivity extends AppCompatActivity {

    ArrayList<Food> menu = new ArrayList<>();
    ArrayList<Rating> ratingList = new ArrayList<>();
    FirebaseFirestore db;
    BottomNavigationView navbar;
    ImageView res_img;
    TextView resname_txt;
    TextView menugreeting_txt;
    TextView info_txt;
    TextView minimum_txt;
    TextView duration_txt;
    TextView rating_txt;
    Button all_btn;
    Button popular_btn;
    Button recommended_btn;

    TextView see_txt;

    private int selected;
    private int not_selected;

    // Restaurant Recycler View Variables
    RecyclerView menu_rv;
    Menu_RVAdapter menu_adapter;
    GridLayoutManager gridLayoutManager;

    // Intent Variables
    private String imageurl;
    private String name;
    private String distance;
    private Double fee;
    private Double minimum;
    private String duration;
    private String storename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_menu);

        navbar = findViewById(R.id.navbar);
        res_img = findViewById(R.id.res_img);
        resname_txt = findViewById(R.id.resname_txt);
        menugreeting_txt = findViewById(R.id.menugreeting_txt);
        info_txt = findViewById(R.id.info_txt);
        minimum_txt = findViewById(R.id.minimum_txt);
        duration_txt = findViewById(R.id.duration_txt);
//        rating_txt = findViewById(R.id.rating_txt);
        menu_rv = findViewById(R.id.menu_rv);
        all_btn = findViewById(R.id.all_btn);
        popular_btn = findViewById(R.id.popular_btn);
        recommended_btn = findViewById(R.id.recommended_btn);
        selected = ContextCompat.getColor(this, R.color.orange);
        not_selected = ContextCompat.getColor(this, R.color.hard_text);
        see_txt = findViewById(R.id.see_txt);

        // Setup Bottom Navigation View
        setupBottomNavigationView();

        // Setup Activity values
        setupActivityValues();

        // Setup Firestore Database
        db = FirebaseFirestore.getInstance();

        // Setup Restaurant Recycler View
        setupMenuRecyclerView();

        // Setup Ratings (Under Construction)
//        setupRatings();

        see_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String restaurantName = resname_txt.getText().toString();
                Intent intent = new Intent(getApplicationContext(), RatingListActivity.class);
                intent.putExtra("restaurantName", restaurantName);
                startActivity(intent);
            }
        });
    }

    private void setupBottomNavigationView() {
        navbar.setSelectedItemId(R.id.menu_home);
        navbar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
                return true;
            }
            else if (id == R.id.menu_cart) {
                startActivity(new Intent(getApplicationContext(),CartActivity.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
                return true;
            }
            else if (id == R.id.menu_address) {
                startActivity(new Intent(getApplicationContext(), AddressActivity.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
                return true;
            }
            else if (id == R.id.menu_profile) {
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                finish();
                return true;
            }
            return false;
        });
    }

    private void setupActivityValues() {
        Intent intent = getIntent();
        this.storename = intent.getStringExtra("storename");
        this.imageurl = intent.getStringExtra("imageurl");
        this.name = intent.getStringExtra("name");
        this.distance = intent.getStringExtra("distance");
        this.fee = intent.getDoubleExtra("fee", -1);
        this.minimum = intent.getDoubleExtra("minimum", -1);
        this.duration = intent.getStringExtra("duration");

        Glide.with(this).load(this.imageurl).placeholder(R.drawable.mcdo_logo).into(res_img);
        resname_txt.setText(this.name);
        menugreeting_txt.setText("Welcome to " + this.name + "!");
        info_txt.setText(this.distance + " away | ₱ " + String.format("%.2f", this.fee) + " Delivery Fee");
        minimum_txt.setText("Minimum Order: ₱ " + String.format("%.2f", this.minimum));
        duration_txt.setText("Approx. Delivery: " + this.duration);
    }

    private void setupMenuRecyclerView() {
        db.collection("restaurants")
                .document(this.name)
                .collection("menu")
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "Success: Get Menu for " + name + " Request Successful!");
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot: snapshotList) {
                            Log.d(TAG, "Food: " + snapshot.getData());
                            Food food = new Food(
                                    snapshot.getString("imageurl"),
                                    snapshot.getString("name"),
                                    snapshot.getDouble("price"),
                                    snapshot.getString("type"),
                                    name
                            );
                            menu.add(food);
                        }

                        // Initialize Recycler View
                        menu_adapter = new Menu_RVAdapter(ResMenuActivity.this, menu);
                        menu_rv.setAdapter(menu_adapter);
                        gridLayoutManager = new GridLayoutManager(ResMenuActivity.this,2,GridLayoutManager.VERTICAL,false);
                        menu_rv.setLayoutManager(gridLayoutManager);
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

    public void getAllFood(View v) {
        menu_adapter.setMenu(menu);
        all_btn.setTextColor(selected);
        popular_btn.setTextColor(not_selected);
        recommended_btn.setTextColor(not_selected);
    }


    public void getPopularFood (View v) {
        ArrayList<Food> filteredMenu = new ArrayList<>();
        for (Food food: menu) {
            if (food.getType().toLowerCase().contains("Popular".toLowerCase())) {
                filteredMenu.add(food);
            }
            menu_adapter.setMenu(filteredMenu);
        }
        all_btn.setTextColor(not_selected);
        popular_btn.setTextColor(selected);
        recommended_btn.setTextColor(not_selected);
    }

    public void getRecommendedFood (View v) {
        ArrayList<Food> filteredMenu = new ArrayList<>();
        for (Food food: menu) {
            if (food.getType().toLowerCase().contains("Recommended".toLowerCase())) {
                filteredMenu.add(food);
            }
            menu_adapter.setMenu(filteredMenu);
        }
        all_btn.setTextColor(not_selected);
        popular_btn.setTextColor(not_selected);
        recommended_btn.setTextColor(selected);
    }

    public void goBack (View v) {
        finish();
    }

    public void totalRatingReviews() {

    }
}