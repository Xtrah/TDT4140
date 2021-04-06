package com.example.dinetime.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dinetime.MainActivity;

public class DinnerActivity extends AppCompatActivity {
    // Initialize TAG
    private static final String TAG = "DinnerActivity.java ";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Entered DinnerActivity");
        // getting dinnerID from last activity
        Intent intent = getIntent();
        final String dinnerID = intent.getStringExtra("dinnerID");
        Toast.makeText(DinnerActivity.this, "Gikk inn på middag", Toast.LENGTH_SHORT).show();
        Context context = getApplicationContext();
        Intent myIntent = new Intent(context, MainActivity.class);
        startActivityForResult(myIntent, 0);
    }
}
