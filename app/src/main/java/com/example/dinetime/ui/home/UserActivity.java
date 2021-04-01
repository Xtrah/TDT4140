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
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;

    private String firstName, lastName, address, email, password;
    private ArrayList<String> allergies;

    private static final String TAG = "UserActivity";
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("UserData");
        allergies = new ArrayList<>();

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

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), LoginActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                firstName = firstNameEditText.getText().toString();
                lastName = lastNameEditText.getText().toString();
                address = addressEditText.getText().toString();

                if (email == null || password == null || email.isEmpty() || password.isEmpty()
                        || firstName == null || lastName == null || address == null ||
                        firstName.isEmpty() || lastName.isEmpty() || address.isEmpty()) {
                    Log.w(TAG, "Text fields are not filled out");
                    Log.w(TAG, "createUserWithEmail:failure");
                    Toast.makeText(UserActivity.this,
                            "Fyll ut alle feltene.",
                            Toast.LENGTH_SHORT).show();
                }

                else if (!verifyUserData()) {
                    Log.w(TAG, "createUserWithEmail:failure");
                }

                else {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(UserActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, save user to database

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

                                        UserData data = new UserData(firstName, lastName, address, allergies, false);

                                        FirebaseDatabase.getInstance().getReference("UserData")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(data).
                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(UserActivity.this, "Vellykket registrering!",
                                                                Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(UserActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        Log.d(TAG, "createUserWithEmail:success");
                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                    }
                                                });
                                    } else {
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(UserActivity.this,
                                                "E-post er ikke gyldig.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                }
            }

            private boolean verifyUserData() {
                if (!firstName.matches("[a-zæøåA-ZÆØÅ\\s\\-]+") || !lastName.matches("[a-zæøåA-ZÆØÅ]+")) {
                    Log.w(TAG, "Invalid name");
                    Toast.makeText(UserActivity.this, "Navn kan bare inneholde bokstaver.",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (password.length() < 6) {
                    Log.w(TAG, "Invalid password");
                    Toast.makeText(UserActivity.this, "Passordet må ha minst 6 tegn.",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (!address.matches("^[a-zæøåA-ZÆØÅ\\s\\-]{2,}[0-9]+$")) {
                    Log.w(TAG, "Invalid address");
                    Toast.makeText(UserActivity.this, "Adressen er ikke gyldig.",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (other.isChecked() && (!otherAllergy.getText().toString().matches("[a-zæøåA-ZÆØÅ\\s\\-]+"))) {
                    Log.w(TAG, "Invalid allergy");
                    Toast.makeText(UserActivity.this, "Oppgitt allergi er ikke gyldig.",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            }

            private void addAllergy(CheckBox checkBox) {
                if (checkBox.isChecked()) {
                    allergies.add(checkBox.getText().toString());
                }
            }
    });

    }
}
