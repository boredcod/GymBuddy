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

import java.util.HashMap;
import java.util.Map;

public class AddFriendActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    EditText searchFriends;
    Button searchButton;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    BottomNavigationView bottomNavigationView;
    LinearLayout fragContainer, pendingRequestsContainer, pendingInvitesContainer;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        searchFriends = findViewById(R.id.searchFriendsByEmail);
        searchButton = findViewById(R.id.completeSearchFriendsButton);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menu_addFriends);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFriendsToFB();
            }
        });
        //Creates fragment for current friends
        fragContainer = findViewById(R.id.friendFragmentList);
        FriendListFragment curr_frag = new FriendListFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentFriendList, curr_frag).commit();
        //Creates fragment for Sent Friend Requests
        pendingRequestsContainer = findViewById(R.id.pendingFriendsFragmentList);
        PendingFriendsFragment curr_pendingRequest = new PendingFriendsFragment();
        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
        ft2.replace(R.id.fragmentPendingRequests, curr_pendingRequest).commit();
        //Creates fragment for Current Requests
        pendingInvitesContainer = findViewById(R.id.pendingInvitesFragmentList);
        PendingInvitesFragment curr_invites = new PendingInvitesFragment();
        FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
        ft3.replace(R.id.fragmentPendingInvites, curr_invites).commit();

    }
    //Sends a friend request
    private void addFriendsToFB() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        //Gets the email typed on the Search Box
        String c_v = searchFriends.getText().toString();
        if (c_v.equals(account.getEmail())){
            //Checks if the email is equal to your own email
            Toast.makeText(getApplicationContext(),"You cannot add yourself!", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference docRef = db.collection("users").document(c_v);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //If the user exists, send the friend request and refresh the fragment.
                        db.collection("users").
                                document(account.getEmail()).update("pendingRequests", FieldValue.arrayUnion(c_v)).
                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Sent Friend Request!", Toast.LENGTH_SHORT).show();
                                        FriendListFragment curr_frag = new FriendListFragment();
                                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.fragmentFriendList, curr_frag).commit();
                                        PendingFriendsFragment pending_frag = new PendingFriendsFragment();
                                        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                                        ft2.replace(R.id.fragmentPendingRequests, pending_frag).commit();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("d", "Error writing document", e);
                                    }
                                });
                        //Adds my own email to the pending invites of the user that you have just sent request to.
                        db.collection("users").document(c_v).update("pendingInvites", FieldValue.arrayUnion(account.getEmail())).
                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Sent Friend Request to the user", Toast.LENGTH_SHORT).show();
                                        FriendListFragment curr_frag = new FriendListFragment();
                                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.fragmentFriendList, curr_frag).commit();
                                        PendingInvitesFragment curr_invites = new PendingInvitesFragment();
                                        FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                                        ft3.replace(R.id.fragmentPendingInvites, curr_invites).commit();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("d", "Error writing document", e);
                                    }
                                });
                    } else {
                        Log.d("d", "Document doesn't exist");
                        Toast.makeText(getApplicationContext(),"No Such User !",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("d", "get failed with ", task.getException());
                }
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
    //Sets up the navigation to the bottom navigation.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                System.out.println("menu_home");
                return true;
            case R.id.menu_randomMatching:
                startActivity(new Intent(getApplicationContext(), RandomMatchingActivity.class));
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