package com.example.dinetime.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dinetime.MainActivity;
import com.example.dinetime.R;
import com.example.dinetime.ui.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class EmptyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Initialiserer verdier som trengs senere
    private static final String TAG = "EmptyActivity.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        BottomNavigationView navView = findViewById(R.id.nav_view);

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
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (month.equals(adapter.getItem(i))) {
                        month = String.valueOf(i).trim();
                    }
                }
                // creating Strings of the user input:

                String dag = dagEd.getSelectedItem().toString().trim();
                Log.d(TAG, "Value of dag: " + dag);
                Log.d(TAG, "Value of month: " + month);

                final String rett = rettEd.getText().toString().trim();
                final String dato = dag + "." + month;
                final String klokke = timeEd.getSelectedItem().toString().trim() + ":" + minuttEd.getSelectedItem().toString().trim();
                final String sted = stedEd.getText().toString().trim();
                final String gjester = guestsEd.getSelectedItem().toString().trim();

                // array for validering av dato
                String[] badMonthArray = new String[]{
                        "2", "4", "6", "9", "11"
                };
                List<String> badMonthList = Arrays.asList(badMonthArray);

                boolean notfilled = rett.equals("") || sted.equals("") || dato.contains("Dag") || dato.contains("Måned") || klokke.contains("Minutter") || klokke.contains("Timer") || gjester.contains("gjester");
                Log.i(TAG, "notfilled = " + notfilled);
                if (notfilled) {
                    Log.i(TAG, "Form not filled. Nothing happens");
                    // add toast to implement feedback
                    Toast.makeText(getApplicationContext(), "Skjema ikke utfylt", Toast.LENGTH_SHORT).show();
                } else if (!rett.matches("[a-zæøåA-ZÆØÅ\\s\\-]+")) {
                    Log.i(TAG, "Form not filled. Ugyldig rett");
                    Toast.makeText(EmptyActivity.this, "Ugyldig rett", Toast.LENGTH_SHORT).show();
                } else if (!sted.matches("^[a-zæøåA-ZÆØÅ\\s\\-]{2,}(\\s[0-9]+){0,1}$")) {
                    Log.i(TAG, "Form not filled. Ugyldig sted");
                    Toast.makeText(EmptyActivity.this, "Ugyldig sted", Toast.LENGTH_SHORT).show();
                } else if ((badMonthList.contains(month) && dag.equals("31")) || (month.equals("2") && (dag.equals("29") || dag.equals("30")))) {
                    Log.i(TAG, "Form not filled. Ugyldig dato");
                    Toast.makeText(EmptyActivity.this, "Ugyldig dato", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Form Filled. Saving Dinner to Database");

                    // creating intent to move to MainActivity
                    Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                    startActivityForResult(myIntent, 0);


                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        Log.wtf(TAG, "User == null");
                        Toast.makeText(EmptyActivity.this, "Nå har det skjedd en massiv feil", Toast.LENGTH_SHORT).show();
                        myIntent = new Intent(view.getContext(), LoginActivity.class);
                        startActivityForResult(myIntent, 0);
                    } else {
                        // Toast message to give feedback to user that the action was completed
                        Toast.makeText(getApplicationContext(), "Publiserer...", Toast.LENGTH_SHORT).show();

                        // get database object from the digital database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        // create new dinner object in dinners path
                        String dinnerID = "";
                        if (getIntent().getStringExtra("dinnerID") != null) {
                            Intent intent = getIntent();
                            dinnerID = intent.getStringExtra("dinnerID");
                            Log.i(TAG, "Fant dinnerID: " + dinnerID);
                        }
                        else{
                            dinnerID = UUID.randomUUID().toString();
                        }
                        final DatabaseReference myRef = database.getReference("dinners").child(dinnerID);
                        final String userID = user.getUid();
                        int counter = 1;

                        // Dette er ikke pent, men det er det eneste som har fungert hittil og det vil ikke være særlig merkbart for bruker så vi lar det gå
                        while (counter <= 3) {
                            Log.w(TAG, "Laster opp. Forsøk nummer " + counter);
                            myRef.child("typeRett").setValue(rett);
                            Log.wtf(TAG, "rett: " + rett);
                            myRef.child("dato").setValue(dato);
                            Log.wtf(TAG, "dato: " + dato);
                            myRef.child("klokkeslett").setValue(klokke);
                            Log.wtf(TAG, "klokkeslett: " + klokke);
                            myRef.child("sted").setValue(sted);
                            Log.wtf(TAG, "sted: " + sted);
                            myRef.child("gjester").setValue(gjester);
                            Log.wtf(TAG, "gjester: " + gjester);
                            myRef.child("vegetar").setValue(vegetarEd.isChecked());
                            Log.wtf(TAG, "vegetarEd: " + String.valueOf(vegetarEd.isChecked()));
                            myRef.child("deleUtgifter").setValue(deleEd.isChecked());
                            Log.wtf(TAG, "deleEd: " + String.valueOf(deleEd.isChecked()));
                            myRef.child("eier").setValue(userID);
                            Log.w(TAG, "lagt til eier med userID: " + userID);
                            counter++;
                        }
                    }
                }
            }
        });
        // getting dinnerID from last activity if it exists
        try {
            if (getIntent().getStringExtra("dinnerID") != null) {
                Intent intent = getIntent();
                final String dinnerID = intent.getStringExtra("dinnerID");
                Log.i(TAG, "Fant dinnerID: " + dinnerID);

                // TODO: Hent fra databasen
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final EmptyActivity context = this;
                final DatabaseReference myRef = database.getReference();

                // Attach a listener to read the data at our posts reference
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final DataSnapshot dsDinner = dataSnapshot.child("dinners").child(dinnerID);
                        try {
                            String typeRett = dsDinner.child("typeRett").getValue(String.class);
                            String dato = dsDinner.child("dato").getValue(String.class);

                            String tidspunkt = dsDinner.child("klokkeslett").getValue(String.class);
                            String sted = dsDinner.child("sted").getValue(String.class);
                            String antGjester = dsDinner.child("gjester").getValue(String.class);
                            Boolean vegetarYN = dsDinner.child("vegetar").getValue(Boolean.class);
                            Boolean deleUtgifter = dsDinner.child("deleUtgifter").getValue(Boolean.class);

                            rettEd.setText(dsDinner.child("typeRett").getValue(String.class));

                            // sett dato og måned
                            String[] dateList = dato.split("\\.");
                            String compareValue = dateList[0];
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.days, android.R.layout.simple_spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            dagEd.setAdapter(adapter);
                            if (compareValue != null) {
                                int spinnerPosition = adapter.getPosition(compareValue);
                                dagEd.setSelection(spinnerPosition);
                            }

                            adapter = ArrayAdapter.createFromResource(context, R.array.months, android.R.layout.simple_spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            monthEd.setAdapter(adapter);
                            monthEd.setSelection(Integer.parseInt(dateList[1]));

                            // sett timer og minutter
                            dateList = tidspunkt.split(":");
                            compareValue = dateList[0];
                            adapter = ArrayAdapter.createFromResource(context, R.array.hours, android.R.layout.simple_spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            timeEd.setAdapter(adapter);
                            if (compareValue != null) {
                                int spinnerPosition = adapter.getPosition(compareValue);
                                timeEd.setSelection(spinnerPosition);
                            }
                            compareValue = dateList[1];
                            adapter = ArrayAdapter.createFromResource(context, R.array.minutes, android.R.layout.simple_spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            minuttEd.setAdapter(adapter);
                            if (compareValue != null) {
                                int spinnerPosition = adapter.getPosition(compareValue);
                                minuttEd.setSelection(spinnerPosition);
                            }

                            // sett sted
                            stedEd.setText(dsDinner.child("sted").getValue(String.class));

                            // sett antall gjester
                            compareValue = dsDinner.child("gjester").getValue(String.class);;
                            adapter = ArrayAdapter.createFromResource(context, R.array.guests, android.R.layout.simple_spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            guestsEd.setAdapter(adapter);
                            if (compareValue != null) {
                                int spinnerPosition = adapter.getPosition(compareValue);
                                guestsEd.setSelection(spinnerPosition);
                            }
                            // sett vegetar og dele utgifter
                            vegetarEd.setChecked(dsDinner.child("vegetar").getValue(Boolean.class));
                            deleEd.setChecked(dsDinner.child("deleUtgifter").getValue(Boolean.class));
                        }
                        catch (NullPointerException e){
                            Log.d(TAG, "NullPointerException");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            }

        } catch (NullPointerException e) {
            Log.d(TAG, "No dinnerID from former intent");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}