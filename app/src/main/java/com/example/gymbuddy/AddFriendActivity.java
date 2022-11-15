package com.example.gymbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddFriendActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    EditText searchFriends;
    Button searchButton;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    BottomNavigationView bottomNavigationView;
    LinearLayout fragContainer;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        searchFriends = findViewById(R.id.searchFriendsByEmail);
        searchButton = findViewById(R.id.completeSearchFriendsButton);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriendsToFB();
            }
        });
        fragContainer = findViewById(R.id.friendFragmentList);
        FriendListFragment curr_frag = new FriendListFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentFriendList, curr_frag).commit();
    }

    private void addFriendsToFB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


        String c_v = searchFriends.getText().toString();

        DocumentReference docRef = db.collection("users").document(c_v);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        db.collection("users").
                                document(account.getEmail()).update("friendlist", FieldValue.arrayUnion(c_v)).
                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Added Successfully!", Toast.LENGTH_SHORT).show();
                                        FriendListFragment curr_frag = new FriendListFragment();
                                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.fragmentFriendList, curr_frag).commit();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("d", "Error writing document", e);
                                    }
                                });
                        Log.d("d", "DocumentSnapshot data in onComplete: " + document.getData());
                    } else {
                        Log.d("d", "Document doesn't exist");
                    }
                } else {
                    Log.d("d", "get failed with ", task.getException());
                }
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
        switch (item.getItemId()) {
            case R.id.menu_home:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                System.out.println("menu_home");
                return true;
            case R.id.menu_addFriends:
                System.out.println("menu_addFriends");
                return true;
            case R.id.menu_logOut:
                SignOut();
                return true;
        }
        return false;
    }
}