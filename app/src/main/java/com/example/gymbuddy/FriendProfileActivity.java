package com.example.gymbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FriendProfileActivity extends AppCompatActivity {
    TextView passed_email;
    Button back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        passed_email = findViewById(R.id.friendProfileEmail);
        back_button = findViewById(R.id.friendProfileBackButton);
        Intent i = new Intent(this,AddFriendActivity.class);
        Intent passed_intent = getIntent();
        String p_email = passed_intent.getStringExtra("user_email");
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(i);
            }
        });
        passed_email.setText(p_email);
    }
}