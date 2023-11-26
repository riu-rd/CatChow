package com.mobdeve.s17.catchow;

import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));

        Button placeOrder = findViewById(R.id.button);
        placeOrder.setOnClickListener(view -> startActivity(new Intent(this, OrderPlacedActivity.class)));
    }

}