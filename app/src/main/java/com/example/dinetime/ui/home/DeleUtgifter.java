package com.example.dinetime.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dinetime.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleUtgifter extends AppCompatActivity {

    final static String TAG = "DeleUtgifter.java";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //debug message
        Log.w(TAG, "DeleUtgifter.java has been opened");

        //create layout
        setContentView(R.layout.activity_deleutgifter);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        final TextView titletxt = findViewById(R.id.title);
        final TextView typeRetttxt = findViewById(R.id.typeRett);
        final TextView gjestertxt = findViewById(R.id.gjester);
        final EditText utgifterEd = findViewById(R.id.utgifter);
        final Button deleUtgifter = (Button) findViewById(R.id.deleUtgifter);


        // getting dinnerID from last activity
        Intent intent = getIntent();
        final String dinnerID = intent.getStringExtra("dinnerID");
        Log.w(TAG, "Hentet dinnerID: " + dinnerID);
        // getting userID from current user
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // finner frem riktig middagsreferanse:
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseRef = database.getReference();
        final DatabaseReference dinnerRef = database.getReference().child("dinners").child(dinnerID);

        //lager knappene
        final ImageButton back = (ImageButton) findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), DinnerActivity.class);
                Log.d(TAG, "Moving to DinnerActivity.class");
                myIntent.putExtra("dinnerID", dinnerID);
                startActivityForResult(myIntent, 0);
            }
        });

        // Henter antall gjester og poster til dem
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                // oppdaterer brødtekst:
                //lager to snapshots, en for bruker og en for middag
                final DataSnapshot dsDinner = dataSnapshot.child("dinners").child(dinnerID);
                final DataSnapshot dsEier = dataSnapshot.child("UserData").child(userID);

                //henter inn tekst for brødtekst:
                String typeRett = dsDinner.child("typeRett").getValue(String.class);
                String gjesterRatio;

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
                    }
                }


                gjesterRatio = String.valueOf(paameldte) + "/" + String.valueOf(maksGjest);

                Log.w(TAG, "oppdaterer brødtekst");
                // oppdaterer textview brødtekst med innsamlet data:
                titletxt.setText(dsEier.child("firstName").getValue(String.class) + " sin middag");
                typeRetttxt.setText("Type Rett: " + typeRett);
                gjestertxt.setText("Påmeldte Gjester: " + gjesterRatio);


                final int finalPaameldte = paameldte;
                deleUtgifter.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Log.w(TAG, "Del utgifter aktivert");
                        // henter tekst fra dele UtigfterEd
                        String utgifterString = utgifterEd.getText().toString();

                        int totalUtgift = Integer.parseInt(utgifterEd.getText().toString());
                        int utgiftPerPers = totalUtgift/(finalPaameldte + 1);

                        // deler opp utgiftene og sender varsel til de som var påmeldte om hvor mange penger de skylder.
                        for (DataSnapshot personRef : dsDinner.child("paameldte").getChildren()){
                            DatabaseReference skylderRef = databaseRef.child("UserData").child(personRef.getValue().toString()).child("varsler")
                                    .child("skylder").push();
                            skylderRef.child("toUser").setValue(userID);
                            skylderRef.child("forDinner").setValue(dinnerID);
                            skylderRef.child("expenses").setValue(String.valueOf(utgiftPerPers));
                        }

                        // Registrerer at utgiftene for middagen har blitt delt
                        dinnerRef.child("delt").child("betaltTilbake").setValue(String.valueOf(0));
                        dinnerRef.child("delt").child("delesPaa").setValue(String.valueOf(finalPaameldte));
                        // Sender brukeren tilbake til oversikten over middagen
                        Intent myIntent = new Intent(view.getContext(), DinnerActivity.class);
                        Log.d(TAG, "Moving to DinnerActivity.class");
                        myIntent.putExtra("dinnerID", dinnerID);
                        startActivityForResult(myIntent, 0);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }
}
