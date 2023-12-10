package com.mobdeve.s17.catchow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReviewFormActivity extends AppCompatActivity {

    BottomNavigationView navbar;
    Button submitReview;

    FirebaseFirestore db;
    FirebaseAuth auth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    String currentEmail;

    EditText multilineTextView;

    RatingBar ratingBar;

    TextView nameUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_form);

        navbar = findViewById(R.id.navbar);

        submitReview = findViewById(R.id.submitReview);

        ratingBar = findViewById(R.id.ratingBar);
        multilineTextView = findViewById(R.id.multilineTextView);

        nameUser = findViewById(R.id.nameUser);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setup Firestore Database and Auth
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            currentEmail = acct.getEmail();
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            currentEmail = currentUser.getEmail();
        }

        //Setup Bottom Navigation View
        setupBottomNavigationView();


        String storeName = getIntent().getStringExtra("storeName");

        if (currentEmail != null) {
            db.collection("users")
                    .whereEqualTo("email", currentEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String name = documentSnapshot.getString("name");

                            nameUser.setText(name);

                            break;
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ReviewFormActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the rating from the RatingBar
                float rating = ratingBar.getRating();

                // Get the text from the multilineTextView
                String reviewText = multilineTextView.getText().toString();

                // Check if the rating is less than 1
                if (rating < 1.0) {
                    // Show a toast indicating that a minimum rating is required
                    Toast.makeText(getApplicationContext(), "Please provide a rating of at least 1 star.", Toast.LENGTH_SHORT).show();
                } else if (reviewText.trim().isEmpty()) {
                    // Check if the review text is empty
                    // Show a toast indicating that a review is required
                    Toast.makeText(getApplicationContext(), "Please provide a review.", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle the submission of the review (e.g., save it to a database)
                    // ...
                    savedRatingsAndReview();
                    finish();
                }
            }
        });


    }




    public void savedRatingsAndReview() {
        // Get the rating from the RatingBar
        float rating = ratingBar.getRating();

        // Get the text from the multilineTextView
        String reviewText = multilineTextView.getText().toString();

        String nameText = nameUser.getText().toString();

        // Check if both the rating and review text are empty
        if (rating == 1.0 && reviewText.trim().isEmpty()) {
            // Both rating and review text are empty, show a toast
            Toast.makeText(getApplicationContext(), "Please provide a rating and a review.", Toast.LENGTH_SHORT).show();
        } else {
            // Create a new rating object
            Map<String, Object> ratingData = new HashMap<>();
            ratingData.put("rating", rating);
            ratingData.put("review", reviewText);
            ratingData.put("name", nameText);

            // Access the Firestore database
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Get the restaurant ID (storeName) from the Intent
            String storeName = getIntent().getStringExtra("storeName");

            // Add the rating data to the 'ratings' subcollection of the restaurant
            db.collection("restaurants")
                    .document(storeName) // Use storeName as the document ID
                    .collection("ratings")
                    .add(ratingData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Rating and review successfully added
                            Toast.makeText(getApplicationContext(), "Rating and review saved.", Toast.LENGTH_SHORT).show();

                            // Clear the RatingBar and multilineTextView if needed
                            ratingBar.setRating(0.0f);
                            multilineTextView.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the error
                            Toast.makeText(getApplicationContext(), "Failed to save rating and review.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }



    private void setupBottomNavigationView() {
        navbar.setSelectedItemId(R.id.menu_profile);
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