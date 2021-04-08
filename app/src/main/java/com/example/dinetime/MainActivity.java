package com.example.dinetime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.dinetime.ui.dashboard.DashboardFragment;
import com.example.dinetime.ui.home.EmptyActivity;
import com.example.dinetime.ui.home.HomeFragment;
import com.example.dinetime.ui.home.ProfileActivity;
import com.example.dinetime.ui.notifications.NotificationsFragment;
import com.example.dinetime.ui.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements  BottomNavigationView.OnNavigationItemSelectedListener {

    //initialiserer variabler
    private static final String TAG = "MainActivity";
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private BottomNavigationView navView;
    FragmentManager fm;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // denne setter opp de funksjonene som alltid er tilstede på MainActivity skjermen samt muligheten til å kunne hoppe mellom de forskjellige skjermene

        navView = findViewById(R.id.nav_view);
        fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            // hvis det ikke er noen tidligere saved instance skal man starte på "hjem"
            Log.d(TAG, "savedInstanceState = null");
            FragmentTransaction t = fm.beginTransaction();
            fragment = new HomeFragment();
            t.replace(R.id.nav_host_fragment, fragment);
            t.commit();
        } else {
            // Hvis det var et saved instance henter den det fra tidligere
            Log.d(TAG, "savedInstanceState != null");
            fragment = (Fragment) fm.findFragmentById(R.id.nav_host_fragment);
        }

        // Setter en listener som hører etter når det blir byttet mellom hver side
        navView.setOnNavigationItemSelectedListener(this);

        // setter opp basic knapper
        Button loginButton = findViewById(R.id.loginButton);
        Button addDinner = findViewById(R.id.addDinnerButton);
        ImageButton profileButton = findViewById(R.id.profileButton);
        TextView profileText = findViewById(R.id.profileText);

        // sjekker om noen er logget inn
        try {
            if (user != null) {
                // User is signed in
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

        //setter opp onClickListener til knappene
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
    } // end of onCreate

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Denne metoden hører etter om en av knappene i bottBar blir trykket på og handler etter det
        Log.d(TAG, "onNavigationItemSelected is called");
        Log.d(TAG, "item = " + item.toString());

        fm = getSupportFragmentManager();

        switch (item.getItemId()){
            // Switch som hører etter når de forskjellige sidene blir startet opp og hopper mellom dem
            case R.id.navigation_home:
                // hjemme siden over alle middager
                FragmentTransaction hjem = fm.beginTransaction();
                fragment = new HomeFragment();
                hjem.replace(R.id.nav_host_fragment, fragment);
                hjem.commit();
                Log.w(TAG, "Bytter til hjem fragment");
                return true;
            case R.id.navigation_dashboard:
                //side for mine middager
                FragmentTransaction mineMiddager = fm.beginTransaction();
                fragment = new DashboardFragment();
                mineMiddager.replace(R.id.nav_host_fragment, fragment);
                mineMiddager.commit();
                Log.w(TAG, "Bytter til mine middager fragment");
                return true;
            case R.id.navigation_notifications:
                // side for varsler
                FragmentTransaction varsler = fm.beginTransaction();
                fragment = new NotificationsFragment();
                varsler.replace(R.id.nav_host_fragment, fragment);
                varsler.commit();
                Log.w(TAG, "Bytter til varsler fragment");
                return true;
        }
        // dette skal egentlig ikke skje, den skal fanges opp av det over
        Log.wtf(TAG, "onNavigationItemSelected returning false -- THIS IS NOT SUPPOSED TO HAPPEN!!!");
        return false;
    }
}
