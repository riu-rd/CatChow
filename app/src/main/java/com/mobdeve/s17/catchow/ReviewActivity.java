package com.mobdeve.s17.catchow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobdeve.s17.catchow.models.Address;

public class ReviewActivity extends AppCompatActivity {
    ImageView backButton;
    BottomNavigationView navbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_reviews);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> onBackPressed());
        navbar = findViewById(R.id.navbar);
        TextView addressTextView = findViewById(R.id.addressTextView);
        TextView foodRestaurant = findViewById(R.id.foodRestaurant);
        Intent intent = getIntent();
        Address userAddress = (Address) intent.getSerializableExtra("userAddress");
        String restaurantName = intent.getStringExtra("restaurantName");
        if (restaurantName != null) {
            foodRestaurant.setText(restaurantName);
        }
        if (userAddress != null) {
            String addressText = userAddress.getLabelAs() + "\n" +
                    userAddress.getStreetInfo() + "\n" +
                    userAddress.getRegionInfo() + " " + userAddress.getPostalCode();
            addressTextView.setText(addressText);
        }


        backButton.setOnClickListener(view -> onBackPressed());
        setupBottomNavigationView();
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

}
