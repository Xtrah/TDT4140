package com.example.dinetime.ui.home;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.dinetime.MainActivity;
import com.example.dinetime.R;
import com.example.dinetime.ui.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;


public class DinnerActivity extends AppCompatActivity {

    // Initialize TAG
    private static final String TAG = "DinnerActivity.java";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //debug message
        Log.w(TAG, "DinnerActivity.java has been opened");

        //create layout
        setContentView(R.layout.activity_dinner);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // henter instans av innlogget bruker
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // melder ifra om at man ikke er logget på
            String error = "Massive error encountered. Not logged in!";
            Log.wtf(TAG, error);
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();

            // sender deg tilbake til LoginActivity.class
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(myIntent, 0);
        }
        final String userID = user.getUid();

        Log.w(TAG, "lager knapper");

        final ImageButton back = (ImageButton) findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        // lager påmeldingsknapp
        final Button registration = (Button) findViewById(R.id.registerForDinnerButton);
        final TextView titletxt = (TextView) findViewById(R.id.title);
        final TextView typeRetttxt = (TextView) findViewById(R.id.typeRett);
        final TextView datotxt = (TextView) findViewById(R.id.dato);
        final TextView tidspunkttxt = (TextView) findViewById(R.id.tidspunkt);
        final TextView stedtxt = (TextView) findViewById(R.id.sted);
        final TextView gjestertxt = (TextView) findViewById(R.id.gjester);
        final TextView vegetartxt = (TextView) findViewById(R.id.vegetar);
        final TextView deleUtgiftertxt = (TextView) findViewById(R.id.deleUtgifter);

        // getting dinnerID from last activity
        Intent intent = getIntent();
        final String dinnerID = intent.getStringExtra("dinnerID");
        Log.w(TAG, "Hentet dinnerID: " + dinnerID);
        //starter database med riktig middagsreferanse:
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        // myRef er referanse til denne spesifikke middagen
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Log.w(TAG, "henter informasjon om middagen fra databasen");

                    //lager to snapshots, en for bruker og en for middag
                    final DataSnapshot dsDinner = dataSnapshot.child("dinners").child(dinnerID);
                    DataSnapshot dsEier = dataSnapshot.child("UserData").child(dsDinner.child("eier").getValue(String.class));

                    //henter inn tekst for brødtekst:
                    String typeRett = dsDinner.child("typeRett").getValue(String.class);
                    String dato= dsDinner.child("dato").getValue(String.class);
                    String tidspunkt= dsDinner.child("klokkeslett").getValue(String.class);
                    String sted= dsDinner.child("sted").getValue(String.class);
                    String gjesterRatio;
                    String vegetarYN = "Nei";
                    String deleUtgifterYN = "Nei";
                    String eier = dsDinner.child("eier").getValue(String.class);
                    boolean paameldt = false;

                    // setter ja eller nei utifra true or false på deling og vegetar
                    if (dsDinner.child("vegetar").getValue(Boolean.class).equals(Boolean.TRUE)) {
                        vegetarYN = "Ja";
                    }
                    if (dsDinner.child("deleUtgifter").getValue(Boolean.class).equals(Boolean.TRUE)) {
                        deleUtgifterYN = "Ja";
                    }


                    // henter maks antall gjester som int
                    int maksGjest = Integer.parseInt(dsDinner.child("gjester").getValue(String.class));
                    Log.d(TAG, "Maks gjester: " + maksGjest);
                    // tell antall påmeldte
                    int paameldte = 0;
                    // dersom stien ikke eksisterer har ingen meldt seg på
                    if (!dsDinner.child("paameldte").exists()){
                        Log.w(TAG, "paameldte.getChildren = null. Ingen påmeldte");
                    } else {
                        for (DataSnapshot snapshot : dsDinner.child("paameldte").getChildren()){
                            paameldte++;
                            // registrerer at brukeren er påmeldt
                            if (userID.equals(snapshot.getValue(String.class))) {
                                paameldt = true;
                            }
                        }
                    }


                    gjesterRatio = String.valueOf(paameldte) + "/" + String.valueOf(maksGjest);

                    Log.w(TAG, "oppdaterer brødtekst");
                    // oppdaterer textview brødtekst med innsamlet data:
                    titletxt.setText(dsEier.child("firstName").getValue(String.class) + " sin middag");
                    typeRetttxt.setText("Type Rett: " + typeRett);
                    datotxt.setText("Dato: " + dato);
                    tidspunkttxt.setText("Tidspunkt: " + tidspunkt);
                    stedtxt.setText("Sted: " + sted);
                    gjestertxt.setText("Påmeldte Gjester: " + gjesterRatio);
                    vegetartxt.setText("Vegetar: " + vegetarYN);
                    deleUtgiftertxt.setText("Dele På Utgifter: " + deleUtgifterYN);


                    // oppdaterer onclick metoden til knappen:
                    if (paameldt) {
                        registration.setText("Meld deg av");
                        // legge inn avmeldings funksjonalitet?
                        registration.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                // finn frem referanse til bruker i liste over påmeldte
                                for (DataSnapshot removeDs : dsDinner.child("paameldte").getChildren()){
                                    if (removeDs.getValue(String.class).equals(userID)) {
                                        // pop/slett den fra eksistens
                                        myRef.child("dinners").child(dinnerID).child("paameldte").child(removeDs.getKey()).removeValue();
                                        Log.w(TAG, "removed user " + userID + " from dinner");
                                    }
                                }
                                // endrer tekst til "meld deg på"
                                registration.setText("Meld deg på");
                                Toast.makeText(DinnerActivity.this, "Du er nå avmeldt middagen", Toast.LENGTH_SHORT).show();
                                // Endring i database vil medføre en ny gjennomgang av denne metoden.
                            }
                        });
                    } else if (eier.equals(userID)) {
                        registration.setText("Endre middag");
                        // legger inn klikkefunksjonalitet
                        registration.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                // ###############################################
                                // her kan man legge inn noe for endring av middag
                                // ###############################################
                                Toast.makeText(DinnerActivity.this, "Det er din middag, antar du kommer", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (paameldte >= maksGjest) {
                        registration.setText("Fullt");
                        // legge inn avmeldings funksjonalitet?
                        registration.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                Toast.makeText(DinnerActivity.this, "Det er fullt", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Knappen er nå for å melde seg på arrangementet
                        registration.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                // legger brukerid hos middagsoversikt
                                myRef.child("dinners").child(dinnerID).child("paameldte").push().setValue(userID);
                                // legger middagen til hos brukeren
                                myRef.child("UserData").child(userID).child("dinners").push().setValue(dinnerID);
                                Log.d(TAG, "ferdig registrering i database");

                                Toast.makeText(DinnerActivity.this, "Da er du påmeldt!", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                } catch (NullPointerException e) {
                    Log.w(TAG, "Dinner was deleted.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        final Button deleteDinnerButton = findViewById(R.id.deleteDinnerButton);
        deleteDinnerButton.setVisibility(View.INVISIBLE);

        deleteDinnerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                myRef.child("dinners").child(dinnerID).removeValue();
                Toast.makeText(DinnerActivity.this, "Middagen er slettet!",
                        Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        DatabaseReference userRef = database.getReference("UserData");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    boolean isAdmin = snapshot.child(userID).child("isAdmin").getValue(Boolean.class);
                    if (isAdmin) {
                        deleteDinnerButton.setVisibility(View.VISIBLE);
                    }
                } catch (NullPointerException e) {
                    Log.w(TAG, "Could not find isAdmin.");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        DatabaseReference dinnerRef = database.getReference("dinners");
        dinnerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    //if (userID.equals(snapshot.getValue(String.class))) {
                    String dinnerUserID = snapshot.child(dinnerID).child("eier").getValue(String.class);
                    if (userID.equals(dinnerUserID)) {
                        deleteDinnerButton.setVisibility(View.VISIBLE);
                    }
                } catch (NullPointerException e) {
                    Log.w(TAG, "Could not compare userIDs.");
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
