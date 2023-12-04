package com.mobdeve.s17.catchow;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s17.catchow.adapters.Address_RVAdapter;
import com.mobdeve.s17.catchow.models.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity {

    BottomNavigationView navbar;
    FirebaseFirestore db;
    FirebaseAuth auth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ArrayList<Address> addressList = new ArrayList<>();
    RecyclerView address_rv;
    Address_RVAdapter address_adapter;
    LinearLayoutManager verticalLayoutManager;

    String currentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        navbar = findViewById(R.id.navbar);
        address_rv = findViewById(R.id.address_rv);

        //Setup Bottom Navigation View
        setupBottomNavigationView();

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
        setupAddressRecyclerView();
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

    private void setupAddressRecyclerView() {
        db.collection("users")
                .whereEqualTo("email", currentEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot: snapshotList) {
                            DocumentReference userRef = snapshot.getReference();
                            userRef.collection("addresses")
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot addressQuerySnapshot) {
                                            Log.d(TAG, "Success: Get USER ADDRESSES Request Successful!");
                                            List<DocumentSnapshot> addressSnapshotList = addressQuerySnapshot.getDocuments();
                                            addressList.clear();
                                            for (DocumentSnapshot addressSnapshot : addressSnapshotList) {
                                                Log.d(TAG, "Address: " + addressSnapshot.getData());
                                                Address address = new Address(
                                                        addressSnapshot.getString("label"),
                                                        addressSnapshot.getString("region"),
                                                        addressSnapshot.getString("postalcode"),
                                                        addressSnapshot.getString("street")
                                                );
                                                addressList.add(address);
                                            }

                                            // Initialize Recycler View
                                            address_adapter = new Address_RVAdapter(AddressActivity.this, addressList);
                                            address_rv.setAdapter(address_adapter);
                                            verticalLayoutManager = new LinearLayoutManager(AddressActivity.this, LinearLayoutManager.VERTICAL, false);
                                            address_rv.setLayoutManager(verticalLayoutManager);
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

    public void profile(View v) {
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }

    public void goBack (View v) {
        startActivity(new Intent(AddressActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 69) {
            setupAddressRecyclerView();
        }
    }
}