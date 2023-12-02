package com.mobdeve.s17.catchow;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mobdeve.s17.catchow.R;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_form);

        ImageView backButton = findViewById(R.id.backButton);
        RatingBar rb_ratingBar = findViewById(R.id.ratingBar);

        EditText multilineTextView = findViewById(R.id.multilineTextView);

        int cornerRadius = 20;
        int borderColor = Color.BLACK;
        int orangeColor = getResources().getColor(android.R.color.holo_orange_dark);
        rb_ratingBar.setProgressTintList(android.content.res.ColorStateList.valueOf(orangeColor));

        // Set other properties as needed
        multilineTextView.setText("");

        rb_ratingBar.setRating(2.5f);
        rb_ratingBar.setStepSize(0.5f);

        rb_ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });
        Button reviewButton = findViewById(R.id.submitReview);
        reviewButton.setOnClickListener(view -> startActivity(new Intent(this, MainActivity.class)));
        backButton.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }
}
