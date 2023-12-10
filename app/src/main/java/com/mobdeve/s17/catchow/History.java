package com.mobdeve.s17.catchow;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class History extends AppCompatActivity {

    BottomNavigationView navbar;
    FirebaseFirestore db;
    FirebaseAuth auth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    String currentEmail;

    LinearLayout historyLayout; // Assuming a LinearLayout to display order history

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

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

        navbar = findViewById(R.id.navbar);
        setupBottomNavigationView();

        historyLayout = findViewById(R.id.historyLayout); // Replace with your actual layout ID

        fetchAndDisplayOrders();
    }

    private void setupBottomNavigationView() {
        navbar.setSelectedItemId(R.id.menu_profile);
        navbar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_profile) {
                return true;
            } else if (id == R.id.menu_cart) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (id == R.id.menu_address) {
                startActivity(new Intent(getApplicationContext(), AddressActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            } else if (id == R.id.menu_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
            return false;
        });
    }

    private void fetchAndDisplayOrders() {

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentEmail != null) {
            db.collection("users")
                    .whereEqualTo("email", currentEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String currentUserId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            CollectionReference ordersCollection = db.collection("users").document(currentUserId).collection("orders");

                            ordersCollection.get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String storeName = document.getString("storename");
                                        String imageUrl = getImageUrlByStoreName(storeName);
                                        String orderDetails = formatOrderDetails(document);

                                        addToHistoryLayout(storeName, imageUrl, orderDetails);
                                    }
                                } else {
                                    Log.e(TAG, "Error getting orders: ", task.getException());
                                }
                            });


                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error querying user document: ", e));
        }
    }

    private String formatOrderDetails(QueryDocumentSnapshot orderDocument) {
        // Format the order details into a String
        // Add your formatting logic here
        // Example:
        String itemName = orderDocument.getString("name");
        Double price = orderDocument.getDouble("price");
        Long quantity = orderDocument.getLong("quantity");

        return "Item: " + itemName + "\nPrice: " + price + "\nQuantity: " + quantity + "\n\n";
    }

//    private void addToHistoryLayout(String storeName, String imageUrl, String orderDetails) {
//        LinearLayout imageTextContainer = new LinearLayout(this);
//        imageTextContainer.setOrientation(LinearLayout.HORIZONTAL);
//        imageTextContainer.setLayoutParams(new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));
//
//        // Store image view
//        ImageView storeImageView = new ImageView(this);
//        int desiredWidth = 200; // Set your desired width
//        int desiredHeight = 200; // Set your desired height
//        Glide.with(this)
//                .load(imageUrl)
//                .override(desiredWidth, desiredHeight) // Resize the image
//                .error(R.drawable.profile_icon) // Fallback image
//                .into(storeImageView);
//
//        // Add some margin to the ImageView
//        LinearLayout.LayoutParams storeImageViewParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        storeImageViewParams.setMargins(0, 0, 20, 0); // Adjust the margin as needed
//        storeImageView.setLayoutParams(storeImageViewParams);
//
//        imageTextContainer.addView(storeImageView);
//
//        // Order text view
//        TextView orderTextView = new TextView(this);
//        orderTextView.setText(orderDetails);
//
//        // Add some margin to the TextView
//        LinearLayout.LayoutParams orderTextViewParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//        orderTextViewParams.setMargins(20, 0, 0, 0); // Adjust the margin as needed
//        orderTextView.setLayoutParams(orderTextViewParams);
//
//        imageTextContainer.addView(orderTextView);
//
//        // Set click listener to the container
//        imageTextContainer.setOnClickListener(v -> {
//            // Start ReviewFormActivity when the container is clicked
//            Intent intent = new Intent(History.this, ReviewFormActivity.class);
//            // Pass the storeName to ReviewFormActivity
//            intent.putExtra("storeName", storeName);
//            startActivity(intent);
//        });
//
//        // Add the container layout to the history layout
//        historyLayout.addView(imageTextContainer);
//    }

    private void addToHistoryLayout(String storeName, String imageUrl, String orderDetails) {
        // Create a CardView for the store image and order text views
        CardView cardView = new CardView(this);
        CardView.LayoutParams cardViewParams = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.MATCH_PARENT
        );

        // Set the margin at the bottom of the CardView in pixels (adjust the value as needed)
        int marginBottomInPixels = 16; // Change this value as needed
        cardViewParams.setMargins(0, 0, 0, marginBottomInPixels);
        cardView.setLayoutParams(cardViewParams);

        // Set the corner radius for the CardView (adjust the value as needed)
        float cornerRadiusInPixels = 25.0f; // Change this value as needed
        cardView.setRadius(cornerRadiusInPixels);

        // Set the background color for the CardView (orange color)
        cardView.setCardBackgroundColor(getResources().getColor(R.color.item_background)); // Replace with your desired color resource

        // Create a LinearLayout to hold the store image and order text views
        LinearLayout imageTextContainer = new LinearLayout(this);
        imageTextContainer.setOrientation(LinearLayout.HORIZONTAL);
        imageTextContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        int marginInPixels = 15; // Change this value as needed
        ImageView storeImageView = new ImageView(this);

// Create layout parameters with margins
        LinearLayout.LayoutParams storeImageViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        storeImageViewParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);

// Add top margin to the storeImageView
        storeImageViewParams.topMargin = 45; // Adjust the margin value as needed

        storeImageView.setLayoutParams(storeImageViewParams);

        int desiredWidth = 200; // Set your desired width
        int desiredHeight = 200; // Set your desired height
        Glide.with(this)
                .load(imageUrl)
                .override(desiredWidth, desiredHeight) // Resize the image
                .error(R.drawable.profile_icon) // Fallback image
                .into(storeImageView);

// Add the ImageView with margin to the container
        imageTextContainer.addView(storeImageView);

        // Order text view
        TextView orderTextView = new TextView(this);
        orderTextView.setText(orderDetails);
        orderTextView.setTextColor(ContextCompat.getColor(this, R.color.black));

        // Add some margin to the TextView
        LinearLayout.LayoutParams orderTextViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        orderTextViewParams.setMargins(20, 60, 0, 0); // Adjust the margin as needed
        orderTextView.setLayoutParams(orderTextViewParams);

        imageTextContainer.addView(orderTextView);

        // Add the LinearLayout (containing store image and order text views) to the CardView
        cardView.addView(imageTextContainer);

        // Set click listener to the CardView
        cardView.setOnClickListener(v -> {
            // Start ReviewFormActivity when the CardView is clicked
            Intent intent = new Intent(History.this, ReviewFormActivity.class);
            // Pass the storeName to ReviewFormActivity
            intent.putExtra("storeName", storeName);
            startActivity(intent);
        });

        // Add the CardView to the history layout
        historyLayout.addView(cardView);
    }

    private String getImageUrlByStoreName(String storeName) {
        switch (storeName) {
            case "Burger King":
                return "https://firebasestorage.googleapis.com/v0/b/catchow-app.appspot.com/o/Burger%20King.png?alt=media&token=c5277e91-9274-44e6-ac34-b51623016fbd";
            case "Dairy Queen":
                return "https://firebasestorage.googleapis.com/v0/b/catchow-app.appspot.com/o/Dairy%20Queen.png?alt=media&token=6f604453-301f-4a54-b203-be09eb8de511";
            case "Gong cha":
                return "https://firebasestorage.googleapis.com/v0/b/catchow-app.appspot.com/o/Gong%20cha.png?alt=media&token=1657e3f6-6eb4-43cc-b497-7a9bdfb0abe7";
            case "McDonalds":
                return "https://firebasestorage.googleapis.com/v0/b/catchow-app.appspot.com/o/McDonalds.png?alt=media&token=1f9d75ed-f65e-41a3-a6c2-22ecb76e29d5";
            case "Pancake House":
                return "https://firebasestorage.googleapis.com/v0/b/catchow-app.appspot.com/o/Pancake%20House.png?alt=media&token=7f2dfb46-0fa6-47f9-93fe-0bae06b4f20a";
            case "Starbucks":
                return "https://firebasestorage.googleapis.com/v0/b/catchow-app.appspot.com/o/Starbucks.png?alt=media&token=cd745c9e-8d25-471f-85f8-e65919460e4b";
            default:
                return null;
        }
    }

    public void profile(View v) {
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }

    public void goBack (View v) {
//        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }
}
