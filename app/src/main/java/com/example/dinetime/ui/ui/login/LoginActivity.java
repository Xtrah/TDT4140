package com.example.dinetime.ui.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dinetime.MainActivity;
import com.example.dinetime.R;
import com.example.dinetime.ui.home.UserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

  private static final String TAG = "LoginActivity";
  private FirebaseAuth mAuth;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    final EditText usernameEditText = findViewById(R.id.username);
    final EditText passwordEditText = findViewById(R.id.password);
    final Button loginButton = findViewById(R.id.login);

    loginButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
          Toast.makeText(LoginActivity.this, "Fyll ut feiltene for å logge inn.",
                  Toast.LENGTH_SHORT).show();
        } else {
          signIn(username, password);
        }
      }
    });

    Button register = findViewById(R.id.register);
    register.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        Intent myIntent = new Intent(view.getContext(), UserActivity.class);
        startActivityForResult(myIntent, 0);
      }
    });
    mAuth = FirebaseAuth.getInstance();
  };


  private void signIn(String email, String password) {
    mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              // Sign in success, update UI with the signed-in user's information
              Log.d(TAG, "signInWithEmail:success");
              FirebaseUser user = mAuth.getCurrentUser();
              Toast.makeText(LoginActivity.this, "Velkommen!",
                  Toast.LENGTH_SHORT).show();
              Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
              startActivityForResult(myIntent, 0);
            } else {
              // If sign in fails, display a message to the user.
              Log.w(TAG, "signInWithEmail:failure", task.getException());
              Toast.makeText(LoginActivity.this, "Feil e-post eller passord.",
                  Toast.LENGTH_SHORT).show();
              FirebaseAuth.getInstance().signOut();
            }
          }
        });
  }

}

