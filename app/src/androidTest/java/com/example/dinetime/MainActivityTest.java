package com.example.dinetime;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import static org.junit.Assert.*;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;

    private static final String TAG = "MainActivityTest.java";

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLaunch() {
        View view = mActivity.findViewById(R.id.container);
        assertNotNull(view);
    }

    @Test
    public void testSortByDate() {
        LinearLayout layout = (LinearLayout)mActivity.findViewById(R.id.linearLayout);
        int count = layout.getChildCount();
        ArrayList<ArrayList<String>> testingList = new ArrayList<>();
        for (int i= 0; i < count; i++) {
            View v = layout.getChildAt(i);
            Button b = (Button)v;
            String buttonText = b.getText().toString();
            System.out.println("-------------------------");
            Log.w(TAG, "Value: " + buttonText);
            String[] content = buttonText.split(" ");
            ArrayList<String> date = new ArrayList<>();
            date.add(content[1]);
            date.add(content[2]);
            testingList.add(date);
        }

        ArrayList<ArrayList<String>> sortedTestingList = (ArrayList<ArrayList<String>>) testingList.clone();
        Collections.sort(sortedTestingList,new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> l1, ArrayList<String> l2) {

                String[] dateList1 = new String[2];
                String[] dateList2 = new String[2];
                dateList1[0] = l1.get(0).split("\\.")[0];
                dateList1[1] = l1.get(0).split("\\.")[1];
                dateList2[0] = l2.get(0).split("\\.")[0];
                dateList2[1] = l2.get(0).split("\\.")[1];
                String[] timeList1 = new String[2];
                String[] timeList2 = new String[2];
                timeList1[0] = l1.get(1).split(":")[0];
                timeList1[1] = l1.get(1).split(":")[1];
                timeList2[0] = l2.get(1).split(":")[0];
                timeList2[1] = l2.get(1).split(":")[1];


                Calendar date1 = Calendar.getInstance();
                date1.set(Calendar.YEAR, 2021);
                date1.set(Calendar.MONTH,Integer.parseInt(dateList1[1]));
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
        assertEquals(testingList, sortedTestingList);
    }

    @Test
    public void testSortByMeal() throws Throwable {

        mActivityTestRule.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                // Stuff that updates the UI
                EditText searchField = (EditText)mActivity.findViewById(R.id.searchField);
                searchField.setText("pizza");
                Button searchButton = (Button)mActivity.findViewById(R.id.searchButton);
                searchButton.performClick();

            }
        });


        LinearLayout layout = (LinearLayout)mActivity.findViewById(R.id.linearLayout);
        int count = layout.getChildCount();

        for (int i= 0; i < count; i++) {
            View v = layout.getChildAt(i);
            Button b = (Button)v;
            String buttonText = b.getText().toString();
            System.out.println("-------------------------");
            Log.w(TAG, "Value: " + buttonText);
            String[] content = buttonText.split(" ");
            assertTrue(content[0].toLowerCase().contains("pizza"));
        }

    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }
}