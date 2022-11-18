package com.example.gymbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class FriendProfileActivity extends AppCompatActivity {
    TextView passed_email, profileName, profileGym, profileLocation;
    Button back_button, removeFriend_button, chat_button;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String p_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        passed_email = findViewById(R.id.friendProfileEmail);
        back_button = findViewById(R.id.friendProfileBackButton);
        Intent i = new Intent(this,AddFriendActivity.class);
        Intent passed_intent = getIntent();
        p_email = passed_intent.getStringExtra("user_email");
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(i);
            }
        });
        passed_email.setText(p_email);
        profileName = findViewById(R.id.friendProfileName);
        profileLocation = findViewById(R.id.friendProfileLocation);
        profileGym = findViewById(R.id.friendProfileGym);
        removeFriend_button = findViewById(R.id.removeFriendButton);
        removeFriend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFriend();

            }
        });

        chat_button = findViewById(R.id.friendProfileChatButton);

        chat_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startChat();
            }
        });
        getProfileInformation();

    }
    private void startChat(){
        Intent i = new Intent(this, MessageActivity.class);
        i.putExtra("sendEmail",p_email);
        startActivity(i);
    }
    private void removeFriend(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        DocumentReference docRef = db.collection("users").document(account.getEmail());
        docRef.update("friendlist", FieldValue.arrayRemove(p_email));
        Intent i = new Intent(this,AddFriendActivity.class);
        startActivity(i);
    }
    private void getProfileInformation(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(p_email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        profileName.setText(document.getData().get("name").toString());
                        profileLocation.setText(document.getData().get("location").toString());
                        profileGym.setText(document.getData().get("gym").toString());
                    } else {
                        Log.d("d", "No such document");
                    }
                } else {
                    Log.d("d", "get failed with ", task.getException());
                }
            }
        });
    }
}