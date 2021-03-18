package com.example.dinetime.ui.home;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.dinetime.MainActivity;
import com.example.dinetime.R;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class EmptyActivity extends AppCompatActivity {

    // Initialize TAG
    private static final String TAG = "EmptyActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // initializing objects from form (no user input read)
        final EditText rettEd = findViewById(R.id.editTextDinner);
        final EditText datoEd = findViewById(R.id.editTextDate);
        final EditText klokkeEd = findViewById(R.id.editTextTime);
        final EditText stedEd = findViewById(R.id.editTextPlace);
        final EditText gjesterEd = findViewById(R.id.editTextGuests);
        final Switch deleEd = findViewById(R.id.switchSplitExpenses);
        final Switch vegetarEd = findViewById(R.id.switchVegetarian);

        Log.w(TAG, "Opening dinner form");

        final ImageButton back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        final Button publish = findViewById(R.id.publishDinnerButton);
        publish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // creating Strings of the user input:
                String rett = rettEd.getText().toString().trim();
                String dato = datoEd.getText().toString().trim();
                String klokke = klokkeEd.getText().toString().trim();
                String sted = stedEd.getText().toString().trim();
                String gjester = gjesterEd.getText().toString().trim();

                boolean notfilled = rett.equals("") || dato.equals("") || klokke.equals("") || sted.equals("") || gjester.equals("");
                Log.w(TAG, "notfilled = " + notfilled);
                if (notfilled){
                    Log.w(TAG, "Form not filled. Nothing happens");
                    // add toast to implement feedback
                    Toast.makeText(getApplicationContext(), "Klarte ikke å lage arrangement. Skjema ikke utfylt", Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "Form Filled. Saving Dinner to Database");

                    // creating intent to move to MainActivity
                    Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                    startActivityForResult(myIntent, 0);

                    // Toast message to give feedback to user that the action was completed
                    Toast.makeText(getApplicationContext(), "Publiserer...", Toast.LENGTH_SHORT).show();

                    // ######## Saving dinner to database ########

                    // get database object from the digital database
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    // create new dinner object in dinners path
                    DatabaseReference myRef = database.getReference("dinners").child(UUID.randomUUID().toString());
                    // save values to database
                    myRef.child("typeRett").setValue(rett);
                    myRef.child("dato").setValue(dato);
                    myRef.child("klokkeslett").setValue(klokke);
                    myRef.child("sted").setValue(sted);
                    myRef.child("gjester").setValue(gjester);
                    myRef.child("vegetar").setValue(vegetarEd.isChecked());
                    myRef.child("deleUtgifter").setValue(deleEd.isChecked());
                }
            }
        });


    }
}