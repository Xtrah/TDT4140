package com.example.dinetime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.dinetime.ui.home.EmptyActivity;
import com.example.dinetime.ui.home.HomeFragment;
import com.example.dinetime.ui.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
        //        R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
        //        .build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(navView, navController);

        //Button next = (Button) findViewById(R.id.button2);
        //next.setOnClickListener(new View.OnClickListener() {
        //    public void onClick(View view) {
        //        Intent myIntent = new Intent(view.getContext(), EmptyActivity.class);
        //        startActivityForResult(myIntent, 0);
        //    }
        //});

        //Button loginButton = (Button) findViewById(R.id.loginButton);
        //loginButton.setOnClickListener(new View.OnClickListener() {
        //    public void onClick(View view) {
        //        Intent myIntent = new Intent(view.getContext(), LoginActivity.class);
        //        startActivityForResult(myIntent, 0);
        //    }
        //});

        Button mainScreenButton = (Button) findViewById(R.id.login);
        mainScreenButton .setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), HomeFragment.class);
                startActivityForResult(myIntent, 0);
            }
        });

    }

    //public void buttonClick(View v) {
    //    EditText text = (EditText) findViewById (R.id.editTextTextPersonName);
    //    text.setText("Welcome to android");
    //}

}
