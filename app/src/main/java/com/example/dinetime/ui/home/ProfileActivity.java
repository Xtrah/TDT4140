package com.example.dinetime.ui.home;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.dinetime.MainActivity;
import com.example.dinetime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final ImageButton back = findViewById(R.id.backButtonProfile);
        final TextView profileEmail = findViewById(R.id.profileEmail);

        try {
            if (user != null) {
                // User is signed in
                profileEmail.setText(user.getEmail());
                System.out.println("Logged in");
            } else {
                // No user is signed in
                System.out.println("No user is signed in");
            }
        } catch (NullPointerException e) {
            System.out.println("yepp");
        }

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

    }
}