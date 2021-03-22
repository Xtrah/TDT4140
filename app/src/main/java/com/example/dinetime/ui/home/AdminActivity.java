package com.example.dinetime.ui.home;

import android.content.Intent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.dinetime.MainActivity;
import com.example.dinetime.R;
import com.example.dinetime.ui.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        final ImageButton back = findViewById(R.id.backButtonAdmin);
        final Button search = findViewById(R.id.searchButtonAdmin);
        final Button delete = findViewById(R.id.deleteUserButton);
        final EditText searchField = findViewById(R.id.searchEditTextAdmin);
        final TextView result = findViewById(R.id.searchResultAdmin);
        delete.setVisibility(View.INVISIBLE);

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String searchString = searchField.getText().toString();
                try {
                    //String uid = FirebaseAuth.getInstance().getUserByEmail(serachString).getUid();
                    //result.setText(uid);
                    delete.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Toast.makeText(AdminActivity.this, "Finner ikke bruker.",
                            Toast.LENGTH_SHORT).show();
                    delete.setVisibility(View.INVISIBLE);
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    //String uid = result.getText().toString();
                    //FirebaseAuth.getInstance().deleteUser(uid);
                } catch (Exception e) {
                    Toast.makeText(AdminActivity.this, "Kan ikke slette bruker.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}