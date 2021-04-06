package com.example.dinetime;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.dinetime.ui.home.DinnerActivity;
import com.example.dinetime.ui.home.EmptyActivity;
import com.example.dinetime.ui.home.ProfileActivity;
import com.example.dinetime.ui.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String userID;
    private DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("UserData");

        Button loginButton = findViewById(R.id.loginButton);
        Button addDinner = findViewById(R.id.addDinnerButton);
        ImageButton profileButton = findViewById(R.id.profileButton);
        TextView profileText = findViewById(R.id.profileText);

        try {
            if (user != null) {
                // User is signed in
                userID = user.getUid();
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

        // Find the ScrollView
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);

        // Create a LinearLayout element
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

            // create context variable
            final MainActivity context = this;

            final ArrayList<ArrayList<String>> list = new ArrayList<>();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("dinners");
            reference.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        ArrayList<String> dinnerList = new ArrayList<>();
                        dinnerList.add(snapshot.getRef().getKey());
                        dinnerList.add(snapshot.child("typeRett").getValue().toString());
                        dinnerList.add(snapshot.child("dato").getValue().toString());
                        dinnerList.add(snapshot.child("klokkeslett").getValue().toString());

                        list.add(dinnerList);
                        Log.w(TAG, "Henter middag med info: " + snapshot.getValue().toString());
                    }
                    // assert !list.isEmpty();

                    Collections.sort(list, new Comparator<ArrayList<String>>() {
                        @Override
                        public int compare(ArrayList<String> l1, ArrayList<String> l2) {

                            String[] dateList1 = new String[2];
                            String[] dateList2 = new String[2];
                            dateList1[0] = l1.get(2).split("\\.")[0];
                            dateList1[1] = l1.get(2).split("\\.")[1];
                            dateList2[0] = l2.get(2).split("\\.")[0];
                            dateList2[1] = l2.get(2).split("\\.")[1];
                            String[] timeList1 = new String[2];
                            String[] timeList2 = new String[2];
                            timeList1[0] = l1.get(3).split(":")[0];
                            timeList1[1] = l1.get(3).split(":")[1];
                            timeList2[0] = l2.get(3).split(":")[0];
                            timeList2[1] = l2.get(3).split(":")[1];


                            Calendar date1 = Calendar.getInstance();
                            date1.set(Calendar.YEAR, 2021);
                            date1.set(Calendar.MONTH, Integer.parseInt(dateList1[1]));
                            date1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateList1[0]));
                            date1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeList1[0]));
                            date1.set(Calendar.MINUTE, Integer.parseInt(timeList1[1]));
                            Calendar date2 = Calendar.getInstance();
                            date2.set(Calendar.YEAR, 2021);
                            date2.set(Calendar.MONTH, Integer.parseInt(dateList2[1]));
                            date2.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateList2[0]));
                            date2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeList2[0]));
                            date2.set(Calendar.MINUTE, Integer.parseInt(timeList2[1]));
                            return date1.compareTo(date2);
                        }
                    });

                    //adapter.notifyDataSetChanged();

                    // Find the ScrollView
                    final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);

                    // Create a LinearLayout element
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);


                    // Add Buttons
                    for (ArrayList<String> l : list) {
                        Button myButton = new Button(context);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(20, 20, 20, 20);
                        myButton.setLayoutParams(lp);
                        myButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                        String txt = "";
                        txt += "<b>" + l.get(1) + "</b>";
                        txt += " --- Dato: ";
                        txt += l.get(2) + " ";
                        txt += "Klokkeslett: ";
                        txt += l.get(3) + " ";
                        myButton.setText(Html.fromHtml(txt));

                        final String id = l.get(0);

                        myButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                Intent myIntent = new Intent(view.getContext(), DinnerActivity.class);
                                Log.d(TAG, "Moving to DinnerActivity.class");
                                myIntent.putExtra("dinnerID", id);
                                startActivityForResult(myIntent, 1);
                            }
                        });

                        // for debugging: checks if button is null
                        if (myButton == null) {
                            System.out.println("Button is NULL");
                        }
                        linearLayout.addView(myButton, lp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });

            final EditText searchField = findViewById(R.id.searchField);
            Button searchButton = findViewById(R.id.searchButton);
            searchButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    linearLayout.removeAllViews();
                    String searchword = searchField.getText().toString().toLowerCase();
                    if (searchword.equals(null)) {
                        return;
                    }
                    ArrayList<ArrayList<String>> newList = new ArrayList<>();
                    for (ArrayList<String> dlist : list) {
                        System.out.println(dlist);
                        if (dlist.get(1).toLowerCase().contains(searchword) ||
                                dlist.get(4).toLowerCase().contains(searchword)) {
                            newList.add(dlist);
                        }
                    }
                    System.out.println(newList);

                    // Add Buttons
                    for (ArrayList<String> l : newList) {
                        Button myButton = new Button(context);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(20, 20, 20, 20);
                        myButton.setLayoutParams(lp);
                        myButton.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                        String txt = "";
                        txt += "<b>" + l.get(1) + "</b>";
                        txt += " --- Dato: ";
                        txt += l.get(2) + " ";
                        txt += "Klokkeslett: ";
                        txt += l.get(3) + " ";
                        myButton.setText(Html.fromHtml(txt));

                        final String id = l.get(0);

                        myButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                Intent myIntent = new Intent(view.getContext(), DinnerActivity.class);
                                myIntent.putExtra("dinnerID", id);
                                startActivityForResult(myIntent, 0);
                            }
                        });

                        // for debugging: checks if button is null
                        if (myButton == null) {
                            System.out.println("Button is NULL");
                        }
                        linearLayout.addView(myButton, lp);
                    }
                }
            });

    }
}


