package com.mobdeve.s17.catchow;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mobdeve.s17.catchow.databinding.ActivitySignUpBinding;
import com.mobdeve.s17.catchow.models.Users;

import java.util.List;


public class SignUpActivity extends AppCompatActivity {

    GoogleSignInOptions gso; // google sign in
    GoogleSignInClient gsc; // google sign in
    ActivitySignUpBinding binding; // email/password sign in
    FirebaseAuth auth; // email/password sign in
    FirebaseFirestore firestore; // email/password sign in
    ProgressDialog progressDialog; // email/password sign in
    Button google_button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating account...");
        progressDialog.setMessage("Please wait.");

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

        binding.clickSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = binding.edittextFullnameSignup.getText().toString();
                String email = binding.edittextEmailSignup.getText().toString();
                String password = binding.edittextPassSignup.getText().toString();

                if(name.isEmpty()) {
                    binding.edittextFullnameSignup.setError("Enter your name");
                } else if(email.isEmpty()) {
                    binding.edittextEmailSignup.setError("Enter your email");
                } else if(password.isEmpty()) {
                    binding.edittextPassSignup.setError("Enter your password");
                } else {

                    firestore.collection("users")
                            .whereEqualTo("email", email)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                                    if (snapshotList.isEmpty()) {
                                        // User with the given email does not exist
                                        // Perform your specific task for the case when the user does not exist
                                        Log.d(TAG, "User does not exist. Can register");
                                        progressDialog.show();
                                        auth.createUserWithEmailAndPassword(binding.edittextEmailSignup.getText().toString(), binding.edittextPassSignup.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(task.isSuccessful()) {
                                                    Users model = new Users(name, email);

                                                    String id = task.getResult().getUser().getUid();
                                                    firestore.collection("users").document().set(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        binding.edittextEmailSignup.setError("Email already exists");
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Failure: ", e);
                                    // Handle failure, e.g., show an error message
                                }
                            });
                }
            }
        });

        binding.logInButton.setOnClickListener(new View.OnClickListener() {
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
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleGoogleSignInResult(account);
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleGoogleSignInResult(GoogleSignInAccount account) {
        String name = account.getDisplayName();
        String email = account.getEmail();

        firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                navigateToMainActivity();
                            } else {
                                addUserToDatabase(name, email); // Placeholder for password
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "Error checking user data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addUserToDatabase(String name, String email) {
        Users model = new Users(name, email);

        firestore.collection("users")
                .document(email)
                .set(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            navigateToMainActivity();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Failed to add user data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    void navigateToMainActivity() {
        finish();
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
    }
}