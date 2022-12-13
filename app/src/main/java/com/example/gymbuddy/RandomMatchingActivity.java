package com.example.gymbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class RandomMatchingActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    BottomNavigationView bottomNavigationView;
    String location;
    List<User> sameLocationUsers;
    TextView profile_name, profile_location, profile_gym, profile_description;
    String curr_user_email = "";
    Button profile_yes, profile_no;
    List<String> curr_friendlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_matching);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.menu_randomMatching);
        gsc = GoogleSignIn.getClient(this, gso);
        sameLocationUsers = new ArrayList<>();
        profile_name = findViewById(R.id.random_match_profile_name);
        profile_location = findViewById(R.id.random_match_profile_location);
        profile_gym = findViewById(R.id.random_match_profile_gym);
        profile_description = findViewById(R.id.random_match_profile_description);
        profile_yes = findViewById(R.id.random_match_profile_yes);
        profile_no = findViewById(R.id.random_match_profile_no);

        profile_yes.setVisibility(View.INVISIBLE);
        profile_no.setVisibility(View.INVISIBLE);
        curr_friendlist = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        db.collection("users").document(account.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    curr_friendlist = (ArrayList<String>) document.getData().get("friendlist");
                }
            }
        });
        getUsersInSameLocation();
        profile_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Curr User email " + curr_user_email + " My email " + account.getEmail());
                if (curr_user_email.equals(account.getEmail()) || (curr_friendlist.contains(curr_user_email))) {
                    Toast.makeText(getApplicationContext(), "You cannot add yourself or " +
                            "The person is already in the friends list", Toast.LENGTH_LONG).show();
                } else {
                    addUser(curr_user_email);
                }
                if (sameLocationUsers.size() == 0) {
                    Toast.makeText(getApplicationContext(), "No more users in the area", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } else {
                    User curr_user = sameLocationUsers.remove(0);
                    curr_user_email = curr_user.getEmail();
                    System.out.println("curr user email: " + curr_user_email);
                    profile_name.setText(curr_user.getName());
                    profile_location.setText(curr_user.getLocation());
                    profile_gym.setText(curr_user.getGym());
                    profile_description.setText(curr_user.getDescription());
                }

            }
        });
        profile_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sameLocationUsers.size() == 0) {
                    Toast.makeText(getApplicationContext(), "No more users in the area", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } else {
                    User curr_user = sameLocationUsers.remove(0);
                    curr_user_email = curr_user.getEmail();
                    profile_name.setText(curr_user.getName());
                    profile_location.setText(curr_user.getLocation());
                    profile_gym.setText(curr_user.getGym());
                    profile_description.setText(curr_user.getDescription());
                    curr_user_email = curr_user.getEmail();
                }
            }
        });

    }

    private void addUser(String addUserEmail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        db.collection("users").
                document(account.getEmail()).update("pendingRequests", FieldValue.arrayUnion(addUserEmail)).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Sent Friend Request!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("d", "Error writing document", e);
                    }
                });
        db.collection("users").document(addUserEmail).update("pendingInvites", FieldValue.arrayUnion(account.getEmail())).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Sent Friend Request to the user", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("d", "Error writing document", e);
                    }
                });
    }

    private void getUsersInSameLocation() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(account.getEmail());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        location = document.getData().get("location").toString();
                        db.collection("users").whereEqualTo("location", location).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot d : task.getResult()) {
                                        ArrayList<String> fl = new ArrayList<>();
                                        fl = (ArrayList<String>) d.getData().get("friendlist");
                                        ArrayList<String> pi = new ArrayList<>();
                                        pi = (ArrayList<String>) d.getData().get("pendingInvites");
                                        ArrayList<String> pr = new ArrayList<>();
                                        pr = (ArrayList<String>) d.getData().get("pendingRequests");
                                        User curr = new User(d.getData().get("name").toString(), d.getData().get("uID").toString(), d.getData().get("location").toString(),
                                                d.getData().get("gym").toString(),
                                                d.getData().get("email").toString(),
                                                fl,
                                                pi,
                                                pr, d.getData().get("description").toString());
                                        sameLocationUsers.add(curr);

                                        System.out.println(sameLocationUsers.get(0).getEmail());
                                    }
                                    profile_yes.setVisibility(View.VISIBLE);
                                    profile_no.setVisibility(View.VISIBLE);
                                    User curr_user = sameLocationUsers.remove(0);
                                    profile_name.setText(curr_user.getName());
                                    profile_location.setText(curr_user.getLocation());
                                    profile_gym.setText(curr_user.getGym());
                                    profile_description.setText(curr_user.getDescription());
                                    curr_user_email = curr_user.getEmail();
                                    System.out.println("curr user email is: " + curr_user_email);

                                } else {
                                    Toast.makeText(getApplicationContext(), "No more users in the area", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    Log.d("D", "Error getting documents: ", task.getException());
                                }
                            }
                        });
                    }
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
                startActivity(new Intent(getApplicationContext(), AddFriendActivity.class));
                System.out.println("menu_addFriends");
                return true;
            case R.id.menu_randomMatching:
                return true;
            case R.id.menu_logOut:
                SignOut();
                return true;
        }
        return false;
    }
}