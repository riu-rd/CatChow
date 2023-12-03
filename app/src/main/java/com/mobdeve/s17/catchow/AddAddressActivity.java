package com.mobdeve.s17.catchow;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    BottomNavigationView navbar;
    EditText region_input;
    EditText postal_input;
    EditText street_input;
    EditText label_input;
    TextView error_txt;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        navbar = findViewById(R.id.navbar);
        region_input = findViewById(R.id.region_input);
        postal_input = findViewById(R.id.postal_input);
        street_input = findViewById(R.id.street_input);
        label_input = findViewById(R.id.label_input);
        error_txt = findViewById(R.id.add_addr_error);

        //Setup Bottom Navigation View
        setupBottomNavigationView();

        // Setup Firestore Database and Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
    }

    private void setupBottomNavigationView() {
        navbar.setSelectedItemId(R.id.menu_address);
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
                startActivity(new Intent(getApplicationContext(),CartActivity.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
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

    public void confirm(View v) {
        String newLabel = label_input.getText().toString().trim().toLowerCase();
        String newRegion = region_input.getText().toString().trim();
        String newPostal = postal_input.getText().toString().trim();
        String newStreet = street_input.getText().toString().trim();

        if (newLabel.equals("") || newRegion.equals("") || newPostal.equals("") || newStreet.equals("")) {
            error_txt.setText("Error: Fill up all fields.");
        } else {
            error_txt.setText("Adding new address. . .");
            error_txt.setTextColor(getResources().getColor(R.color.orange, getTheme()));

            db.collection("users")
                    .whereEqualTo("email", currentUser.getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.d(TAG, "Success: User found!");

                            List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot snapshot : snapshotList) {
                                DocumentReference userRef = snapshot.getReference();
                                CollectionReference addressesCollection = userRef.collection("addresses");

                                // Check if the label already exists in the "addresses" subcollection
                                addressesCollection.whereEqualTo("label", newLabel)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot querySnapshot) {
                                                if (querySnapshot.isEmpty()) {
                                                    // The label does not exist, so add the new address
                                                    Map<String, Object> newAddress = new HashMap<>();
                                                    newAddress.put("label", newLabel);
                                                    newAddress.put("region", newRegion);
                                                    newAddress.put("postalcode", newPostal);
                                                    newAddress.put("street", newStreet);

                                                    addressesCollection.add(newAddress)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    Log.d(TAG, "Address added with ID: " + documentReference.getId());
                                                                    setResult(RESULT_OK, new Intent());
                                                                    finish();
                                                                    Toast.makeText(getApplicationContext(), "New Address Added", Toast.LENGTH_SHORT).show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.w(TAG, "Error adding address", e);
                                                                    error_txt.setText("Error adding address");
                                                                    error_txt.setTextColor(getResources().getColor(R.color.red, getTheme()));
                                                                }
                                                            });
                                                } else {
                                                    // The label already exists, show an error message
                                                    error_txt.setText("Error: Label already exists.");
                                                    error_txt.setTextColor(getResources().getColor(R.color.red, getTheme()));
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


    public void profile(View v) {
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }

    public void goBack (View v) {
        finish();
    }

    public void cancel (View v) { finish(); }
}