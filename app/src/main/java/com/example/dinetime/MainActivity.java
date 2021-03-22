package com.example.dinetime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.dinetime.ui.home.AdminActivity;
import com.example.dinetime.ui.home.EmptyActivity;
import com.example.dinetime.ui.home.ProfileActivity;
import com.example.dinetime.ui.home.UserActivity;
import com.example.dinetime.ui.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class MainActivity extends AppCompatActivity {

  private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
  private String userID;
  private DatabaseReference myRef;
  private FirebaseDatabase firebaseDatabase;
  private static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    BottomNavigationView navView = findViewById(R.id.nav_view);
    // Passing each menu ID as a set of Ids because each
    // menu should be considered as top level destinations.
    AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
            .build();
    NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    NavigationUI.setupWithNavController(navView, navController);

    firebaseDatabase = FirebaseDatabase.getInstance();
    myRef = firebaseDatabase.getReference("UserData");

    Button loginButton = findViewById(R.id.loginButton);
    Button addDinner = findViewById(R.id.addDinnerButton);
    ImageButton profileButton = findViewById(R.id.profileButton);
    TextView profileText = findViewById(R.id.profileText);
    final Button adminButton = findViewById(R.id.adminButton);

    adminButton.setVisibility(View.INVISIBLE);

    try {
      if (user != null) {
        // User is signed in
        userID = user.getUid();
        loginButton.setText("Logg ut");
        profileButton.setVisibility(View.VISIBLE);
        profileText.setVisibility(View.VISIBLE);
        addDinner.setVisibility(View.VISIBLE);
        profileText.setText(user.getEmail());
        System.out.println("Logged in");
      } else {
        // No user is signed in
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        loginButton.setText("Logg inn");
        profileButton.setVisibility(View.INVISIBLE);
        profileText.setVisibility(View.INVISIBLE);
        addDinner.setVisibility(View.INVISIBLE);
        System.out.println("No user is signed in");
      }
    } catch (NullPointerException e) {
      System.out.println("yepp");
    }

    loginButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        if (user != null) {
          FirebaseAuth.getInstance().signOut();
        }
        Intent myIntent = new Intent(view.getContext(), LoginActivity.class);
        startActivityForResult(myIntent, 0);
      }
    });

    addDinner.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Intent myIntent = new Intent(view.getContext(), EmptyActivity.class);
        startActivityForResult(myIntent, 0);
      }
    });

    profileButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Intent myIntent = new Intent(view.getContext(), ProfileActivity.class);
        startActivityForResult(myIntent, 0);
      }
    });

    adminButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Intent myIntent = new Intent(view.getContext(), AdminActivity.class);
        startActivityForResult(myIntent, 0);
      }
    });

    myRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        // This method is called once with the initial value and again
        // whenever data at this location is updated.
        try {
          boolean isAdmin = snapshot.child(userID).child("isAdmin").getValue(Boolean.class);
          if (isAdmin) {
            adminButton.setVisibility(View.VISIBLE);
          }
        } catch (Exception e) {
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        // Failed to read value
        Log.w(TAG, "Failed to read value.", error.toException());
      }

    });
  }
}
