package com.example.dinetime.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.dinetime.R;
import com.example.dinetime.ui.home.DinnerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private static String TAG = "NotificationsFragment.java";
    private String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_notifications, container,false);
        Log.i(TAG, "Ankommet NotificationsFragment.java");

        // Create a LinearLayout element to add buttons to
        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // liste over alle varlser
        final ArrayList<ArrayList<String>> varselList = new ArrayList<>();


        // leser av data fra database - lokasjon er ~/userData/userID/varsler
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.i(TAG, "Leser av data fra databasen");
                // Henter liste over middager brukeren skylder penger for
                // data lagret slik: varsler/<userID til en man skylder penger>/<dinnerID til middagen man skylder penger for>.getValue(hvor mange kroner man faktisk skylder)
                Log.w(TAG, "Skylder brukeren noen noe? " + String.valueOf(dataSnapshot.child("UserData").child(userID).child("varsler").child("skylder").exists()));
                for (DataSnapshot oppgjoer : dataSnapshot.child("UserData").child(userID).child("varsler").child("skylder").getChildren()) {
                    // for hvert oppgjør som har blitt lagt inn legges det inn i en større total liste
                    Log.w(TAG, "Kjører for Loekke");
                    ArrayList<String> oppgjoerList = new ArrayList<>();
                    //henter og legger til navn på den man skylder penger
                    String navn = dataSnapshot.child("UserData").child(oppgjoer.child("toUser").getValue(String.class)).child("firstName").getValue(String.class);
                    oppgjoerList.add(navn);
                    //henter og legger til hva slags rett man spiste
                    String rett = dataSnapshot.child("dinners").child(oppgjoer.child("forDinner").getValue(String.class)).child("typeRett").getValue(String.class);
                    oppgjoerList.add(rett);
                    //legger til referanse til dette spesifikke oppgjøret
                    String oppgjoerID = oppgjoer.getKey();
                    oppgjoerList.add(oppgjoerID);

                    Log.d(TAG, "skylder bruker med userID: " + oppgjoer.child("toUser").getValue(String.class));
                    varselList.add(oppgjoerList);
                }


                // tar inn liste og gjør det om til en rekke knapper som leder til PaybackActivity.java hvor brukeren får basic info og kan bekrefte at de har betalt tilbake
                for (final ArrayList<String> oppgjoerList : varselList) {
                    Button skylderButton = new Button(view.getContext());
                    LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layPar.setMargins(20, 20, 20, 20);
                    skylderButton.setLayoutParams(layPar);
                    skylderButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                    // setter opp teksten

                    String txt = "";
                    txt += "<b>" + oppgjoerList.get(0) + "</b>";
                    txt += " har delt utgitene for ";
                    txt += oppgjoerList.get(1) + " ";
                    skylderButton.setText(Html.fromHtml(txt));

                    // flytter deg til split expenses middagsside
                    skylderButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            Intent myIntent = new Intent(view.getContext(), PaybackActivity.class);
                            Log.d(TAG, "Moving to PaybackActivity.java");
                            myIntent.putExtra("paybackID", oppgjoerList.get(2));
                            startActivityForResult(myIntent, 1);
                        }
                    });

                    // for debugging: checks if button is null
                    if (skylderButton == null) {
                        Log.d(TAG, "Button is NULL");
                    } else {
                        Log.i(TAG, "Knapp lagt til: " + txt);
                    }
                    linearLayout.addView(skylderButton, layPar);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // søkemotor for varselsiden
        final EditText searchField = view.findViewById(R.id.searchField);
        Button searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                linearLayout.removeAllViews();
                String searchword = searchField.getText().toString().toLowerCase();

                // returns all dinner events in database if nothing is searched for
                if (searchword.equals(null)) {
                    return;
                }

                // removes all events that is not searched for
                ArrayList<ArrayList<String>> newList = new ArrayList<>();
                for (ArrayList<String> dlist : varselList) {
                    Log.i(TAG, dlist.toString());
                    if (dlist.get(0).toLowerCase().contains(searchword) ||
                            dlist.get(1).toLowerCase().contains(searchword)) {
                        newList.add(dlist);
                    }
                }
                Log.i(TAG, newList.toString());

                // tar inn liste og gjør det om til en rekke knapper som leder til PaybackActivity.java hvor brukeren får basic info og kan bekrefte at de har betalt tilbake
                for (final ArrayList<String> oppgjoerList : newList) {
                    Button skylderButton = new Button(view.getContext());
                    LinearLayout.LayoutParams layPar = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layPar.setMargins(20, 20, 20, 20);
                    skylderButton.setLayoutParams(layPar);
                    skylderButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                    // setter opp teksten

                    String txt = "";
                    txt += "<b>" + oppgjoerList.get(0) + "</b>";
                    txt += " har delt utgitene for ";
                    txt += oppgjoerList.get(1) + " ";
                    skylderButton.setText(Html.fromHtml(txt));

                    // flytter deg til split expenses middagsside
                    skylderButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            Intent myIntent = new Intent(view.getContext(), DinnerActivity.class);
                            Log.d(TAG, "Moving to PaybackActivity.java");
                            myIntent.putExtra("paybackID", oppgjoerList.get(2));
                            startActivityForResult(myIntent, 1);
                        }
                    });

                    // for debugging: checks if button is null
                    if (skylderButton == null) {
                        Log.d(TAG, "Button is NULL");
                    }
                    linearLayout.addView(skylderButton, layPar);
                }
            }
        });

        return view;
    }
}