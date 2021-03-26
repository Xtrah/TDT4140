package com.example.dinetime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.dinetime.ui.home.EmptyActivity;
import com.example.dinetime.ui.home.ProfileActivity;
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

    try {
      if (user != null) {
        // User is signed in
        userID = user.getUid();
        loginButton.setText("Logg ut");
        profileButton.setVisibility(View.VISIBLE);
        profileText.setVisibility(View.VISIBLE);
        addDinner.setVisibility(View.VISIBLE);
        profileText.setText(user.getEmail());
      } else {
        // No user is signed in
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        loginButton.setText("Logg inn");
        profileButton.setVisibility(View.INVISIBLE);
        profileText.setVisibility(View.INVISIBLE);
        addDinner.setVisibility(View.INVISIBLE);
        Log.w(TAG, "No user is signed in.");
      }
    } catch (NullPointerException e) {
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

  }
}
