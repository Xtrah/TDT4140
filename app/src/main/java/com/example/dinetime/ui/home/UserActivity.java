package com.example.dinetime.ui.home;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.dinetime.MainActivity;
import com.example.dinetime.R;
import com.example.dinetime.ui.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private String firstName, lastName, address;
    private ArrayList<String> allergies = new ArrayList<>();

    private static final String TAG = "UserActivity";
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("UserData");

        final EditText emailEditText = findViewById(R.id.userEmail);
        final EditText passwordEditText = findViewById(R.id.userPassword);
        final EditText firstNameEditText = findViewById(R.id.userFirstName);
        final EditText lastNameEditText = findViewById(R.id.userLastName);
        final EditText addressEditText = findViewById(R.id.userAddress);

        final CheckBox gluten = findViewById(R.id.checkBoxGluten);
        final CheckBox nuts = findViewById(R.id.checkBoxNuts);
        final CheckBox milk = findViewById(R.id.checkBoxMilk);
        final CheckBox egg = findViewById(R.id.checkBoxEgg);
        final CheckBox soya = findViewById(R.id.checkBoxSoya);
        final CheckBox shellfish = findViewById(R.id.checkBoxShellfish);
        final CheckBox other = findViewById(R.id.checkBoxOther);
        final EditText otherAllergy = findViewById(R.id.editTextAllergies);

        final Button create = findViewById(R.id.createProfileButton);
        final ImageButton back = findViewById(R.id.backButtonSignUp);

        create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //createAccount(email, password);
                //Intent myIntent = new Intent(view.getContext(), LoginActivity.class);
                //startActivityForResult(myIntent, 0);

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                firstName = firstNameEditText.getText().toString();
                lastName = lastNameEditText.getText().toString();
                address = addressEditText.getText().toString();

                addAllergy(gluten);
                addAllergy(nuts);
                addAllergy(milk);
                addAllergy(egg);
                addAllergy(soya);
                addAllergy(shellfish);
                if (other.isChecked()) {
                    allergies.add(otherAllergy.getText().toString());
                }
                if (allergies.isEmpty()) {
                    allergies.add("Ingen");
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(UserActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    UserData data = new UserData(firstName, lastName, address, allergies);

                                    FirebaseDatabase.getInstance().getReference("UserData")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(data).
                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(UserActivity.this, "Successful Registered", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(UserActivity.this, MainActivity.class);
                                                    startActivity(intent);

                                                    Log.d(TAG, "createUserWithEmail:success");
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    updateUI(user);
                                                }
                                            });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(UserActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null);
                                }
                            }
                        });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), LoginActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            // TODO
        }
    }

    private void addAllergy(CheckBox checkBox) {
        if (checkBox.isChecked()) {
            allergies.add(checkBox.getText().toString());
        }
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(UserActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

    }
}