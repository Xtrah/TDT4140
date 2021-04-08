package com.example.dinetime.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class PaybackActivity extends AppCompatActivity {

    private static String TAG = "PaybackActivity.java";
    //starter database
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // intro message
        Log.i(TAG, "DinnerActivity.java has been opened");
        //create layout
        setContentView(R.layout.activity_payback);

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

        Log.d(TAG, "lager knapper");

        final ImageButton back = (ImageButton) findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        // lager påmeldingsknapp og brødtekst
        final Button paidBack = (Button) findViewById(R.id.paidBack);
        final TextView titletxt = (TextView) findViewById(R.id.title);
        final TextView typeRetttxt = (TextView) findViewById(R.id.typeRett);
        final TextView datotxt = (TextView) findViewById(R.id.dato);
        final TextView stedtxt = (TextView) findViewById(R.id.sted);
        final TextView gjestertxt = (TextView) findViewById(R.id.delesPaa);
        final TextView skyldertxt = (TextView) findViewById(R.id.skylder);

        // getting paybackID from last activity
        Intent intent = getIntent();
        final String paybackID = intent.getStringExtra("paybackID");
        Log.w(TAG, "Hentet paybackID: " + paybackID);


        // myRef er referanse til denne spesifikke middagen
        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                try {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    Log.w(TAG, "henter informasjon om middagen fra databasen");

                    //lager to snapshots, en for bruker og en for middag
                    final DataSnapshot dsOppgjoer = dataSnapshot.child("UserData").child(userID).child("varsler").child("skylder").child(paybackID);
                    DataSnapshot dsMiddag = dataSnapshot.child("dinners").child(dsOppgjoer.child("forDinner").getValue(String.class));

                    //henter inn tekst for brødtekst:
                    String expenseAmount = dsOppgjoer.child("expenses").getValue(String.class);
                    String typeRett = dsMiddag.child("typeRett").getValue(String.class);
                    String dato= dsMiddag.child("dato").getValue(String.class);
                    String sted= dsMiddag.child("sted").getValue(String.class);
                    String deltPaa = dsMiddag.child("delt").child("delesPaa").getValue(String.class);
                    final String eierID = dsMiddag.child("eier").getValue(String.class);
                    String eierNavn = dataSnapshot.child("UserData").child(eierID).child("firstName").getValue(String.class)
                            + " " + dataSnapshot.child("UserData").child(eierID).child("lastName").getValue(String.class);

                    Log.i(TAG, "oppdaterer brødtekst");
                    // oppdaterer textview brødtekst med innsamlet data:
                    titletxt.setText("Du skylder " + expenseAmount + "kr");
                    typeRetttxt.setText(Html.fromHtml("<b>Type Rett:</b> " + typeRett));
                    datotxt.setText(Html.fromHtml("<b>Dato:</b> " + dato));
                    stedtxt.setText(Html.fromHtml("<b>Sted:</b> " + sted));
                    gjestertxt.setText(Html.fromHtml("<b>Påmeldte gjester ved deling:</b> " + deltPaa));
                    skyldertxt.setText(Html.fromHtml("<b>Betal til:</b> " + eierNavn));

                    // oppdaterer onclick metoden til deleknappen
                    if (!dsOppgjoer.exists()){
                        // dersom den ikke eksisterer har dette nettopp blitt gjort og man skal egentlig ikke være her
                        Log.wtf(TAG, "oppgjoer eksisterer ikke likevel");
                    } else {
                        paidBack.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                // gjoer klar til å flytte tilbake til MainActivity så fort alt under er gjort
                                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                                startActivityForResult(myIntent, 0);

                                // oppdaterer antallet som har betalt i eierens database
                                int totalTilbake = Integer.parseInt(dataSnapshot.child("dinners").child(dsOppgjoer.child("forDinner").getValue(String.class)).child("delt").child("betaltTilbake").getValue(String.class)) + 1 ;
                                myRef.child("dinners").child(dsOppgjoer.child("forDinner").getValue(String.class)).child("delt").child("betaltTilbake").setValue(String.valueOf(totalTilbake));
                                Log.d(TAG, "oppdatert antall betalt tilbake hos middagen");

                                // sletter oppgjøret fra databasen
                                myRef.child("UserData").child(userID).child("varsler").child("skylder").child(paybackID).removeValue();
                                Log.d(TAG, "Sletter oppgjoer fra brukeren");
                            }
                        });

                    }

                } catch (NullPointerException e) {
                    Log.e(TAG, "Oppgjoer kan ikke bli nådd" + e);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}
