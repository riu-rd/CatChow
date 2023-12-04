package com.mobdeve.s17.catchow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PaymentMethodActivity extends AppCompatActivity {

    BottomNavigationView navbar;
    EditText userInput;
    Button linkButton;
    TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        navbar = findViewById(R.id.navbar);
        userInput = findViewById(R.id.user_input);
        linkButton = findViewById(R.id.link_button);
        resultText = findViewById(R.id.result_text);

        //Setup Bottom Navigation View
        setupBottomNavigationView();

        linkButton.setOnClickListener(v -> {
            String enteredText = userInput.getText().toString().trim();
            resultText.setText(enteredText);
            //startActivity(new Intent(getApplicationContext(), ReviewFormActivity.class));
            //finish();
        });
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
                startActivity(new Intent(getApplicationContext(),AddressActivity.class));
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

    public void goBack (View v) {
        finish();
    }

    public void profile(View v) {
        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        finish();
    }
}