package com.example.gymbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    EditText gym;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    BottomNavigationView bottomNavigationView;
    AutoCompleteTextView chooseCityView;
    String [] cities = {"","Boston",
            "Cambridge",
            "Waltham",
            "Burlington",
            "Woburn",
            "Newton",
            "Lexington",
            "Wellesley",
            "Framingham",
            "Marlborough",
            "Bedford",
            "Somerville",
            "Andover",
            "Portsmouth",
            "Needham",
            "Watertown",
            "Concord",
            "Quincy",
            "Lowell",
            "Chelmsford",
            "Billerica",
            "Brookline",
            "Natick",
            "Acton",
            "Beverly",
            "Medford",
            "Westford",
            "Wilmington",
            "Littleton",
            "Canton",
            "Wakefield",
            "Maynard",
            "Hopkinton",
            "North Andover",
            "Arlington Heights",
            "Salem",
            "Westwood",
            "Salem",
            "Braintree",
            "Tewksbury",
            "Charlestown",
            "Danvers",
            "Norwood",
            "Sudbury",
            "Boxborough",
            "Marblehead",
            "Franklin",
            "Dedham",
            "Belmont",
            "Allston",
            "Newburyport",
            "Lawrence",
            "Newton Center",
            "North Billerica",
            "Wayland",
            "Hingham",
            "Peabody",
            "Weymouth",
            "Brockton",
            "Lincoln",
            "Brighton",
            "Dover",
            "Hampstead",
            "Haverhill",
            "Holliston",
            "Gloucester",
            "North Reading",
            "Malden",
            "Durham",
            "Foxboro",
            "Hudson",
            "Norwell",
            "Reading",
            "Auburndale",
            "West Newton",
            "Stoneham",
            "Walpole",
            "Scituate",
            "Jamaica Plain",
            "Saugus",
            "Tyngsboro",
            "Rockland",
            "Stoughton",
            "Chelsea",
            "West Roxbury",
            "Medfield",
            "Winchester",
            "Rochester",
            "Hampton Falls",
            "Melrose",
            "Ayer",
            "Chestnut Hill",
            "Methuen",
            "Avon",
            "Middleton",
            "Pembroke",
            "Newbury",
            "Weston",
            "Ashland",
            "Derry",
            "West Bridgewater",
            "Bridgewater",
            "Groton",
            "Boxford",
            "Wenham",
            "Stow",
            "Medway",
            "Devens",
            "Arlington",
            "North Chelmsford",
            "Milton",
            "Lynn",
            "Ipswich",
            "Needham Heights",
            "Cohasset",
            "Plymouth",
            "Seabrook",
            "Londonderry",
            "Windham",
            "Marshfield",
            "Amesbury",
            "Millis",
            "White Horse Beach",
            "East Boston",
            "Wrentham",
            "Lynnfield",
            "Revere",
            "Stratham",
            "Georgetown",
            "Rockport",
            "Swampscott",
            "Holbrook",
            "Newtonville",
            "Hanover",
            "Hanson",
            "Middleboro",
            "Topsfield",
            "Hyde Park",
            "Rowley",
            "Randolph",
            "Everett",
            "Dracut",
            "Carlisle",
            "Duxbury",
            "Auburn",
            "Plaistow",
            "Exeter",
            "Atkinson",
            "Marion",
            "Merrimac",
            "Sandown",
            "Nahant",
            "North Waltham",
            "Chester",
            "Babson Park",
            "South Hamilton",
            "Dorchester",
            "Wellesley Hills",
            "Sharon",
            "East Walpole",
            "South Weymouth",
            "Abington",
            "Kingston",
            "Manomet",
            "Hampton",
            "Rye",
            "Newmarket",
            "Barrington",
            "New Castle",
            "Newton Junction",
            "Center Strafford",
            "Newfields",
            "Raymond",
            "Kingston",
            "Rollinsford",
            "East Candia",
            "Northwood",
            "Farmington",
            "Newington",
            "Candia",
            "Epping",
            "Newton",
            "Wareham",
            "East Wareham",
            "Carver",
            "Mattapoisett",
            "Lakeville",
            "Whitman",
            "Pepperell",
            "Townsend",
            "Bellingham",
            "Salisbury",
            "Norfolk",
            "Greenbush",
            "Brookline Village",
            "West Medford",
            "Waverley",
            "Sheldonville",
            "North Hampton",
            "North Salem",
            "Roslindale",
            "Milton",
            "Ocean Bluff",
            "Hathorne",
            "Readville",
            "Byfield",
            "Minot",
            "East Hampstead",
            "Waban",
            "Pinehurst",
            "South Walpole",
            "West Nottingham",
            "Rye Beach",
            "East Kingston",
            "West Groton",
            "Ashby",
            "Marshfield Hills",
            "West Boxford",
            "West Townsend",
            "North Weymouth",
            "Plainville",
            "Madbury",
            "Fremont",
            "Danville",
            "West Newbury",
            "Humarock",
            "Brant Rock",
            "New Town",
            "Onset",
            "Bryantville",
            "Plympton",
            "Deerfield",
            "Dunstable",
            "Dover",
            "Accord",
            "Prides Crossing",
            "Elmwood",
            "North Pembroke",
            "Halifax",
            "East Derry",
            "Lee",
            "Nutting Lake",
            "North Scituate",
            "Newton Upper Falls",
            "Greenland",
            "North Carver",
            "West Wareham",
            "Monponsett",
            "Village Of Nagog Woods",
            "Newton Lower Falls",
            "North Marshfield",
            "New Durham",
            "Milton Mills",
            "Nonantum",
            "Rochester",
            "Woodville",
            "South Carver",
            "Green Harbor",
            "Somersworth",
            "Nottingham",
            "Mattapan",
            "Sherborn",
            "Strafford",
            "Milton Village",
            "Newton Highlands",
            "East Weymouth",
            "Winthrop",
            "Groveland",
            "Shirley",
            "East Bridgewater",
            "Essex",
            "Hull",
            "Hamilton"};
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
        ArrayAdapter<String> city_adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice, cities);
        gym = findViewById(R.id.welcome_gym);
        chooseCityView = findViewById(R.id.location_autocomplete);
        chooseCityView.setThreshold(1);
        chooseCityView.setAdapter(city_adapter);
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
                        chooseCityView.setText(document.getData().get("location").toString());
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
                        "location",chooseCityView.getText().toString()).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("hi", "DocumentSnapshot successfully written!");
                        Toast.makeText(getApplicationContext(),"Edited Successfully!",Toast.LENGTH_SHORT).show();
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
                "", gym.getText().toString(), account.getEmail(), new ArrayList<String>());

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