package com.example.dinetime.ui.home;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.*;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.dinetime.MainActivity;
import com.example.dinetime.R;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class EmptyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

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
            //Creates dropdown menu for months
            final Spinner monthEd = findViewById(R.id.months_spinner);
            final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.months, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            monthEd.setAdapter(adapter);
            monthEd.setOnItemSelectedListener(this);
            //Creates dropdown menu for days
            final Spinner dagEd = findViewById(R.id.days_spinner);
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.days, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dagEd.setAdapter(adapter2);
            dagEd.setOnItemSelectedListener(this);
            //Creates dropdown menu for hours
            final Spinner timeEd = findViewById(R.id.hour_spinner);
            ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this, R.array.hours, android.R.layout.simple_spinner_item);
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            timeEd.setAdapter(adapter3);
            timeEd.setOnItemSelectedListener(this);
            //Creates dropdown menu for minutes
            final Spinner minuttEd = findViewById(R.id.minutes_spinner);
            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(this, R.array.minutes, android.R.layout.simple_spinner_item);
            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            minuttEd.setAdapter(adapter4);
            minuttEd.setOnItemSelectedListener(this);

            final Spinner guestsEd = findViewById(R.id.guests_spinner);
            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(this, R.array.guests, android.R.layout.simple_spinner_item);
            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            guestsEd.setAdapter(adapter5);
            guestsEd.setOnItemSelectedListener(this);






        // initializing objects from form (no user input read)
            final EditText rettEd = findViewById(R.id.editTextDinner);
            final EditText stedEd = findViewById(R.id.editTextPlace);
            final Switch deleEd = findViewById(R.id.switchSplitExpenses);
            final Switch vegetarEd = findViewById(R.id.switchVegetarian);

            Log.i(TAG, "Opening dinner form");

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

                    String month = monthEd.getSelectedItem().toString();
                    // gjoer om maaned til tall
                    for (int i = 0; i < adapter.getCount(); i++){
                        if (month.equals(adapter.getItem(i))){
                            month = String.valueOf(i);
                        }
                    }
                    // creating Strings of the user input:
                    String rett = rettEd.getText().toString().trim();
                    String dato = dagEd.getSelectedItem().toString().trim() + "." + month;
                    String klokke = timeEd.getSelectedItem().toString().trim() + ":" + minuttEd.getSelectedItem().toString().trim();
                    String sted = stedEd.getText().toString().trim();
                    String gjester = guestsEd.getSelectedItem().toString().trim();

                    boolean notfilled = rett.equals("") || sted.equals("") || dato.contains("Dag") || dato.contains("Måned") || klokke.contains("Minutt") || klokke.contains("Time");
                    Log.d(TAG, "notfilled = " + notfilled);
                    if (notfilled){
                        Log.i(TAG, "Form not filled. Nothing happens");
                        // add toast to implement feedback
                        Toast.makeText(getApplicationContext(), "Klarte ikke å lage arrangement. Skjema ikke utfylt", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i(TAG, "Form Filled. Saving Dinner to Database");

                        // creating intent to move to MainActivity
                        Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                        startActivityForResult(myIntent, 0);

                        // Toast message to give feedback to user that the action was completed
                        Toast.makeText(getApplicationContext(), "Publiserer...", Toast.LENGTH_SHORT).show();

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}