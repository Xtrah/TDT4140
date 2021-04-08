package com.example.dinetime.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dinetime.MainActivity;
import com.example.dinetime.R;
import android.text.Html;
import com.example.dinetime.ui.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DinnerActivity extends AppCompatActivity {

    // Initialize TAG
    private static final String TAG = "DinnerActivity.java";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // intro message
        Log.i(TAG, "DinnerActivity.java has been opened");

        //create layout
        setContentView(R.layout.activity_dinner);

        // henter instans av innlogget bruker
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // melder ifra om at man ikke er logget på
            String error = "Massive error encountered. Not logged in!";
            Log.wtf(TAG, error);
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();

            // sender deg tilbake til LoginActivity.java
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

        // lager påmeldingsknapp og brødtekst
        final Button registration = (Button) findViewById(R.id.registerForDinnerButton);
        final TextView titletxt = (TextView) findViewById(R.id.title);
        final TextView typeRetttxt = (TextView) findViewById(R.id.typeRett);
        final TextView datotxt = (TextView) findViewById(R.id.dato);
        final TextView tidspunkttxt = (TextView) findViewById(R.id.tidspunkt);
        final TextView stedtxt = (TextView) findViewById(R.id.sted);
        final TextView gjestertxt = (TextView) findViewById(R.id.gjester);
        final TextView vegetartxt = (TextView) findViewById(R.id.vegetar);
        final TextView deleUtgiftertxt = (TextView) findViewById(R.id.deleUtgifter);
        // lager splitExpenses knapp og skjuler den
        final Button splitExpenses = (Button) findViewById(R.id.splitExpenses);
        splitExpenses.setVisibility(View.INVISIBLE);

        // getting dinnerID from last activity
        Intent intent = getIntent();
        final String dinnerID = intent.getStringExtra("dinnerID");
        Log.w(TAG, "Hentet dinnerID: " + dinnerID);
        // starter database med riktig middagsreferanse:
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();

        // myRef er referanse til denne spesifikke middagen
        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Log.w(TAG, "henter informasjon om middagen fra databasen");

                    // lager to snapshots, en for bruker og en for middag
                    final DataSnapshot dsDinner = dataSnapshot.child("dinners").child(dinnerID);
                    DataSnapshot dsEier = dataSnapshot.child("UserData").child(dsDinner.child("eier").getValue(String.class));

                    // henter inn tekst for brødtekst:
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
                    typeRetttxt.setText(Html.fromHtml("<b>Type Rett:</b> " + typeRett));
                    datotxt.setText(Html.fromHtml("<b>Dato:</b> " + dato));
                    tidspunkttxt.setText(Html.fromHtml("<b>Tidspunkt:</b> " + tidspunkt));
                    stedtxt.setText(Html.fromHtml("<b>Sted:</b> " + sted));
                    gjestertxt.setText(Html.fromHtml("<b>Påmeldte Gjester:</b> " + gjesterRatio));
                    vegetartxt.setText(Html.fromHtml("<b>Vegetar:</b> " + vegetarYN));
                    deleUtgiftertxt.setText(Html.fromHtml("<b>Dele På Utgifter:</b> " + deleUtgifterYN));


                    // oppdaterer onclick metoden til knappen:
                    if (eier.equals(userID)) {
                        //dersom det er eieren av middagen som ser på så kan eieren endre middagen
                        registration.setText("Endre middag");
                        // legger inn klikkefunksjonalitet
                        registration.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                Intent myIntent = new Intent(view.getContext(), EmptyActivity.class);
                                myIntent.putExtra("dinnerID", dinnerID);
                                Log.d(TAG, "Moving to EmptyActivity.class, remembering dinnerID " + dinnerID);
                                startActivityForResult(myIntent, 1);
                            }
                        });
                        // hvis den skulle splittes kan eieren dele på utgiftene
                        if (deleUtgifterYN.equals("Ja")){
                            splitExpenses.setVisibility(View.VISIBLE);
                            if (dsDinner.child("delt").exists()) {
                                String tilbakebetaltRatio = dsDinner.child("delt").child("betaltTilbake").getValue(String.class) + "/"
                                        + dsDinner.child("delt").child("delesPaa").getValue(String.class);
                                splitExpenses.setText(tilbakebetaltRatio + " har betalt deg tilbake");
                                splitExpenses.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // sjekker om utgiftene allerede har blitt delt, hvis ikke gjennomføres neste bit
                                        Toast.makeText(DinnerActivity.this, "Utgifter har allerede blitt delt", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Log.d(TAG, "Utgifter har allerede blitt delt");
                            } else {
                                splitExpenses.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // sjekker om utgiftene allerede har blitt delt, hvis ikke gjennomføres neste bit
                                        if (!dsDinner.child("paameldte").exists()) {
                                            Toast.makeText(DinnerActivity.this, "Ingen å dele utgiftene med", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "Ingen påmeldte -- Utgifter ikke delt");
                                        } else {
                                            Intent myIntent = new Intent(view.getContext(), DeleUtgifter.class);
                                            Log.d(TAG, "Moving to DeleUtgifter.class");
                                            myIntent.putExtra("dinnerID", dinnerID);
                                            startActivityForResult(myIntent, 0);
                                        }
                                    }
                                });
                            }
                        }
                    } else if (dsDinner.child("delt").exists()) {
                        // dersom denne stien eksisterer har utgiftene for arrangementet blitt delt og man kan ikke lenger melde seg på
                        registration.setText("Påmelding avsluttet");
                        registration.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                Toast.makeText(DinnerActivity.this, "Påmeldingsperioden er dessverre over", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (paameldt) {
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


        // Initializing delete button, it is invisible by default
        final Button deleteDinnerButton = findViewById(R.id.deleteDinnerButton);
        deleteDinnerButton.setVisibility(View.INVISIBLE);

        // Method for deleting the current dinner
        deleteDinnerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                myRef.child("dinners").child(dinnerID).removeValue();
                Toast.makeText(DinnerActivity.this, "Middagen er slettet!",
                        Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        // Delete button is visible if the user is an administrator
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

        // Delete button is visible if the current user is the creator of the dinner
        DatabaseReference dinnerRef = database.getReference("dinners");
        dinnerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    // if (userID.equals(snapshot.getValue(String.class))) {
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
