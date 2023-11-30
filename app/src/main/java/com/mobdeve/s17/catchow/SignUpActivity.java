package com.mobdeve.s17.catchow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SignUpActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    Button google_button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button logInButton = findViewById(R.id.log_in_button);
        TextView textView = findViewById(R.id.name_signup);

        String text = "CatChow";
        String buttonText = "Already have an account? Login";

        Spannable spannable = new SpannableString(text);
        SpannableString spannableString = new SpannableString(buttonText);

        ForegroundColorSpan catColorSpan = new ForegroundColorSpan(Color.parseColor("#FFE4B5"));
        spannable.setSpan(catColorSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ForegroundColorSpan chowColorSpan = new ForegroundColorSpan(Color.parseColor("#8B4513"));
        spannable.setSpan(chowColorSpan, 3, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannable);

        ForegroundColorSpan alreadyHaveAccountColor = new ForegroundColorSpan(Color.parseColor("#8B4513"));
        spannableString.setSpan(alreadyHaveAccountColor, 0, 22, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ForegroundColorSpan loginColor = new ForegroundColorSpan(Color.parseColor("#Ef8A07"));
        spannableString.setSpan(loginColor, 23, buttonText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        logInButton.setText(spannableString);

        google_button2 = findViewById(R.id.google_button2);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        google_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });

    }

    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void navigateToSecondActivity() {
        finish();
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }
}