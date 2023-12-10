package com.mobdeve.s17.catchow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s17.catchow.adapters.RatingAdapter;
import com.mobdeve.s17.catchow.models.Rating;

import java.util.ArrayList;
import java.util.List;

public class RatingListActivity extends AppCompatActivity {

    BottomNavigationView navbar;

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private RatingAdapter adapter;
    private List<Rating> ratingList;

    RatingBar overallratingbar;

    TextView reviews_count, overallratingtext, rating1count, rating2count, rating3count, rating4count, rating5count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_list);

        navbar = findViewById(R.id.navbar);

        // Initialize FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView and ratingList
        recyclerView = findViewById(R.id.ratings_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ratingList = new ArrayList<>();
        adapter = new RatingAdapter(this, ratingList); // Pass the context as the first parameter
        recyclerView.setAdapter(adapter);

        reviews_count = findViewById(R.id.reviews_count);
        overallratingtext = findViewById(R.id.overallratingtext);
        overallratingbar = findViewById(R.id.overallratingbar);

        rating1count = findViewById(R.id.rating1count);
        rating2count = findViewById(R.id.rating2count);
        rating3count = findViewById(R.id.rating3count);
        rating4count = findViewById(R.id.rating4count);
        rating5count = findViewById(R.id.rating5count);

        // Call the ratingReview method
        ratingReview();

        setupBottomNavigationView();

}

    public void ratingReview() {
        Intent intent = getIntent();

        if (intent.hasExtra("restaurantName")) {
            String restaurantName = intent.getStringExtra("restaurantName");
            Log.d("RatingListActivity", "Restaurant Name: " + restaurantName);

            db.collection("restaurants")
                    .document(restaurantName)
                    .collection("ratings")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                            int ratingsCount = snapshotList.size(); // Get the count of ratings

                            double totalRating = 0.0; // Initialize total rating
                            int[] ratingCounts = new int[5]; // Array to store counts for ratings 1 to 5

                            for (DocumentSnapshot snapshot : snapshotList) {
                                Double ratingValue = snapshot.getDouble("rating");

                                if (ratingValue != null) {
                                    totalRating += ratingValue;

                                    // Count the number of ratings for each value (1 to 5)
                                    int rating = ratingValue.intValue();
                                    if (rating >= 1 && rating <= 5) {
                                        ratingCounts[rating - 1]++;
                                    }
                                }
                            }

                            // Calculate the average rating
                            double averageRating = (ratingsCount > 0) ? totalRating / ratingsCount : 0.0;

                            // Update the RatingBar's rating value
                            RatingBar overallratingbar = findViewById(R.id.overallratingbar);
                            overallratingbar.setRating((float) averageRating);

                            // Update the TextView with the average rating (optional)
                            overallratingtext.setText(String.format("%.1f", averageRating));


                            // Update the count TextViews for each rating
                            rating1count.setText(String.valueOf(ratingCounts[0]));
                            rating2count.setText(String.valueOf(ratingCounts[1]));
                            rating3count.setText(String.valueOf(ratingCounts[2]));
                            rating4count.setText(String.valueOf(ratingCounts[3]));
                            rating5count.setText(String.valueOf(ratingCounts[4]));

                            reviews_count.setText(ratingsCount + " Reviews");

                            for (DocumentSnapshot snapshot : snapshotList) {
                                String name = snapshot.getString("name");
                                Double ratingValue = snapshot.getDouble("rating");
                                String review = snapshot.getString("review");

                                Log.d("RatingListActivity", "Rating Name: " + name);
                                Log.d("RatingListActivity", "Rating Value: " + ratingValue);
                                Log.d("RatingListActivity", "Rating Review: " + review);

                                if (ratingValue != null) {
                                    double rating = ratingValue;
                                    Rating rate = new Rating(name, rating, review);
                                    ratingList.add(rate);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            Log.d("RatingListActivity", "Ratings retrieval completed.");
                        }
                    });
        }
    }


    private void setupBottomNavigationView() {
        navbar.setSelectedItemId(R.id.menu_profile);
        navbar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_profile) {
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
            else if (id == R.id.menu_home) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                return true;
            }
            return false;
        });
    }

    public void profile(View v) {
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }

    public void goBack (View v) {
        finish();
    }
}