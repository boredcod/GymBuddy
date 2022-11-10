package com.example.gymbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    TextView name;
    Button saveButton;
    EditText location, gym;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        name = findViewById(R.id.welcome_name);
        saveButton = findViewById(R.id.saveProfile_button);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String Name = account.getDisplayName();
            name.setText(Name);
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEdit();
            }
        });
        location = findViewById(R.id.welcome_location);
        gym = findViewById(R.id.welcome_gym);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        getCurrentDetails();


    }
    private void getCurrentDetails(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String Name = account.getDisplayName();
            name.setText(Name);
        }
        DocumentReference docRef = db.collection("users").document(account.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        location.setText(document.getData().get("location").toString());
                        gym.setText(document.getData().get("gym").toString());
                        Log.d("d", "DocumentSnapshot data: " + document.getData());

                        Log.d("d","friendlist: " + document.getData().get("friendlist"));
                    } else {
                        createNewUser();
                        Log.d("d", "No such document");
                    }
                } else {
                    Log.d("d", "get failed with ", task.getException());
                }
            }
        });
    }
    private void saveEdit() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);



        db.collection("users").
                document(account.getEmail()).update("gym", gym.getText().toString(),
                        "location",location.getText().toString()).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("hi", "DocumentSnapshot successfully written!");
                        Toast.makeText(getApplicationContext(),"Editted Successfully!",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("hi", "Error writing document", e);
                    }
                });

        if (account != null) {
            String Name = account.getDisplayName();
            name.setText(Name);
        }
    }
    private void createNewUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String Name = account.getDisplayName();
            name.setText(Name);
        }

        User newUser = new User(account.getDisplayName(), account.getId(),
                location.getText().toString(), gym.getText().toString(), account.getEmail(), new ArrayList<String>());

        db.collection("users").document(newUser.getEmail()).set(newUser).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("hi", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("hi", "Error writing document", e);
                    }
                });

    }

    private void SignOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_home:
                System.out.println("menu_home");
                return true;
            case R.id.menu_addFriends:
                startActivity(new Intent(getApplicationContext(), AddFriendActivity.class));
                System.out.println("menu_addFriends");
                return true;
            case R.id.menu_logOut:
                SignOut();
                return true;
        }
        return false;
    }
}