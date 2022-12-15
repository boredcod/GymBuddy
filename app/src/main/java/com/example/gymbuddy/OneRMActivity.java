package com.example.gymbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.checkerframework.common.subtyping.qual.Bottom;

public class OneRMActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    EditText InKilos;
    TextView Bench,Dead,Squat;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_rmactivity);
        InKilos = findViewById(R.id.oneRMEditText);
        Bench = findViewById(R.id.BenchTextViewChange);
        Dead = findViewById(R.id.DeadTextViewChange);
        Squat = findViewById(R.id.SquatTextViewChange);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_onermcalculator);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        InKilos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Calculates Bench Deadlift Squat maxes with according edittext values.
                if (InKilos.getText().toString().equals("")) {
                    Bench.setText("");
                    Dead.setText("");
                    Squat.setText("");
                } else {
                    float squatanddead = (float) ((Float.valueOf(InKilos.getText().toString())) * 1.097 + 14.2546);
                    float bench = (float) ((Float.valueOf(InKilos.getText().toString())) * 1.1307 + 0.7);
                    Bench.setText(Float.toString(bench));
                    Dead.setText(Float.toString(squatanddead));
                    Squat.setText(Float.toString(squatanddead));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    //Signs out the user
    private void SignOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                //Takes the user to the log in page
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                return true;
            case R.id.menu_randomMatching:
                startActivity(new Intent(getApplicationContext(), RandomMatchingActivity.class));
                return true;
            case R.id.menu_onermcalculator:
                startActivity(new Intent(getApplicationContext(), OneRMActivity.class));
                return true;
            case R.id.menu_addFriends:
                return true;
            case R.id.menu_logOut:
                SignOut();
                return true;
        }
        return false;
    }
}