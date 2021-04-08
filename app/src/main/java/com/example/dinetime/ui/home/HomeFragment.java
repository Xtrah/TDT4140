package com.example.dinetime.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.dinetime.R;

import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.widget.*;
import androidx.annotation.RequiresApi;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private String TAG = "HomeFragment.java";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home,container,false);


        // Create a LinearLayout element to add buttons to
        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // list with all dinners in
        final ArrayList<ArrayList<String>> list = new ArrayList<>();

        // reading from database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("dinners");
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try{
                        ArrayList<String> dinnerList = new ArrayList<>();
                        dinnerList.add(snapshot.getRef().getKey());
                        dinnerList.add(snapshot.child("typeRett").getValue().toString());
                        dinnerList.add(snapshot.child("dato").getValue().toString());
                        dinnerList.add(snapshot.child("klokkeslett").getValue().toString());
                        dinnerList.add(snapshot.child("sted").getValue().toString());

                        list.add(dinnerList);
                        Log.w(TAG, "Value is: " + snapshot.getValue().toString());
                    }
                    catch(NullPointerException e){
                        Log.d(TAG, "NullPointerException on dataSnapshot dinnerList");
                    }
                }


                // checks if dinner-event date is in the past, and removes if so
                ArrayList<ArrayList<String>> newList = new ArrayList<>();
                for (int l = 0; l < list.size(); l++) {

                    String[] dateList = new String[2];
                    dateList[0] = list.get(l).get(2).split("\\.")[0];
                    dateList[1] = list.get(l).get(2).split("\\.")[1];

                    String[] timeList = new String[2];
                    timeList[0] = list.get(l).get(3).split(":")[0];
                    timeList[1] = list.get(l).get(3).split(":")[1];


                    Calendar date = Calendar.getInstance();
                    date.set(Calendar.YEAR, 2021);
                    date.set(Calendar.MONTH, Integer.parseInt(dateList[1]));
                    date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateList[0]));
                    date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeList[0]));
                    date.set(Calendar.MINUTE, Integer.parseInt(timeList[1]));

                    if (date.before(Calendar.getInstance())) {
                        Log.w(TAG, "Removed from list: " + list.get(l));
                    } else {
                        newList.add(list.get(l));
                        Log.w(TAG, "Value stays: " + list.get(l));
                        continue;
                    }
                }

                // to get all dinner events in the future sorted by date by default
                Collections.sort(newList, new Comparator<ArrayList<String>>() {
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

                // to get all dinner events searched for sorted by date
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


                // Add Buttons
                for (ArrayList<String> l : newList) {
                    Button myButton = new Button(view.getContext());
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

                    // giving buttons functionality
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
                        Log.d(TAG, "Button is NULL");
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

        // ######################### START OF SEARCH ENGINE #######################################

        // giving main-page search functionality
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
                    Button myButton = new Button(view.getContext());
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
        });

        return view;
    }
}