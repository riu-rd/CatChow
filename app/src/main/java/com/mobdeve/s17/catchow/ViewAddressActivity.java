package com.mobdeve.s17.catchow;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewAddressActivity extends AppCompatActivity {
    BottomNavigationView navbar;
    TextView title;
    EditText region_input;
    EditText postal_input;
    EditText street_input;
    TextView error_txt;

    FirebaseFirestore db;
    FirebaseAuth auth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String currentEmail;

    String selRegion;
    String selPostal;
    String selStreet;
    String selLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_address);

        navbar = findViewById(R.id.navbar);
        title = findViewById(R.id.view_address_title);
        region_input = findViewById(R.id.view_region_input);
        postal_input = findViewById(R.id.view_postal_input);
        street_input = findViewById(R.id.view_street_input);
        error_txt = findViewById(R.id.view_addr_error);

        //Setup Bottom Navigation View
        setupBottomNavigationView();

        //Setup Activity Values
        setupActivityValues();

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

    private void setupActivityValues() {
        Intent intent = getIntent();
        selLabel = intent.getStringExtra("label");
        selRegion = intent.getStringExtra("region");
        selPostal = intent.getStringExtra("postal");
        selStreet = intent.getStringExtra("street");

        title.setText(selLabel);
        region_input.setText(selRegion);
        postal_input.setText(selPostal);
        street_input.setText(selStreet);
    }

    public void profile(View v) {
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }

    public void goBack (View v) {
        finish();
    }

    public void update(View v) {
        String newRegion = region_input.getText().toString().trim();
        String newPostal = postal_input.getText().toString().trim();
        String newStreet = street_input.getText().toString().trim();

        if (newRegion.equals("") || newPostal.equals("") || newStreet.equals("")) {
            error_txt.setText("Error: Fill up all fields.");
        } else {
            error_txt.setText("Updating address. . .");
            error_txt.setTextColor(getResources().getColor(R.color.orange, getTheme()));

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
                                CollectionReference addressesCollection = userRef.collection("addresses");

                                addressesCollection.whereEqualTo("label", selLabel)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot querySnapshot) {
                                                if (!querySnapshot.isEmpty()) {
                                                    // The label exists, update the existing address
                                                    DocumentSnapshot addressSnapshot = querySnapshot.getDocuments().get(0);

                                                    Map<String, Object> updatedAddress = new HashMap<>();
                                                    updatedAddress.put("region", newRegion);
                                                    updatedAddress.put("postalcode", newPostal);
                                                    updatedAddress.put("street", newStreet);

                                                    addressesCollection.document(addressSnapshot.getId())
                                                            .update(updatedAddress)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d(TAG, "Address updated successfully");
                                                                    setResult(RESULT_OK, new Intent());
                                                                    finish();
                                                                    Toast.makeText(getApplicationContext(), "Address Updated", Toast.LENGTH_SHORT).show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.e(TAG, "Error updating address", e);
                                                                    error_txt.setText("Error updating address");
                                                                    error_txt.setTextColor(getResources().getColor(R.color.red, getTheme()));
                                                                }
                                                            });
                                                } else {
                                                    error_txt.setText("Error: Label not found.");
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


    public void delete(View v) {
        String labelToDelete = selLabel;

        if (labelToDelete.equals("")) {
            error_txt.setText("Error: Enter a label to delete.");
        } else {
            error_txt.setText("Deleting address. . .");
            error_txt.setTextColor(getResources().getColor(R.color.orange, getTheme()));

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
                                CollectionReference addressesCollection = userRef.collection("addresses");

                                // Check if the address with the specified label exists
                                addressesCollection.whereEqualTo("label", labelToDelete)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot querySnapshot) {
                                                if (!querySnapshot.isEmpty()) {
                                                    // Address with the specified label found, delete it
                                                    DocumentSnapshot addressSnapshot = querySnapshot.getDocuments().get(0);
                                                    String addressIdToDelete = addressSnapshot.getId();

                                                    addressesCollection.document(addressIdToDelete)
                                                            .delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d(TAG, "Address deleted successfully!");
                                                                    setResult(RESULT_OK, new Intent());
                                                                    finish();
                                                                    Toast.makeText(getApplicationContext(), "Address Deleted", Toast.LENGTH_SHORT).show();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.e(TAG, "Error deleting address", e);
                                                                    error_txt.setText("Error deleting address");
                                                                    error_txt.setTextColor(getResources().getColor(R.color.red, getTheme()));
                                                                }
                                                            });
                                                } else {
                                                    // No address with the specified label found
                                                    error_txt.setText("Error: Address not found.");
                                                    error_txt.setTextColor(getResources().getColor(R.color.red, getTheme()));
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "Failure checking if address exists", e);
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


    public void cancel (View v) { finish(); }
}