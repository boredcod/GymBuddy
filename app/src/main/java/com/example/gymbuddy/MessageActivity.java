package com.example.gymbuddy;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private String chatRoomUid;
    private String myEmail;
    private String otherEmail;
    private String otherEmailClean;
    private String myEmailClean;

    private RecyclerView recyclerView;
    private Button sendButton;
    private EditText messageEditText;
    private FirebaseDatabase firebaseDatabase;

    private chatUser destUser;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy.MM.dd HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        init();
        sendMsg();
    }
    private void sendMsg(){
        //Sends the Message.
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Puts my email and the other person's email to the chatmessage class.
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.users.put(myEmailClean,true);
                chatMessage.users.put(otherEmailClean,true);
                if(chatRoomUid == null){
                    //Creates a chatroom with random Uid.
                    Toast.makeText(MessageActivity.this,"created chatroom",Toast.LENGTH_SHORT).show();
                    sendButton.setEnabled(false);
                    firebaseDatabase.getReference().child("chatrooms").push().setValue(chatMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            checkChatRoom();
                        }
                    });
                }else {
                    //If there is a chatroom uid, simply send the message to the database.
                    sendMsgToDatabase();
                }
            }
        });
    }
    private void init(){
        //Initializes the activity.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        myEmail = account.getEmail();
        int indexAt = myEmail.indexOf("@");
        myEmailClean = myEmail.substring(0,indexAt);
        otherEmail = getIntent().getStringExtra("sendEmail");
        int indexAt1 = otherEmail.indexOf("@");
        otherEmailClean = otherEmail.substring(0,indexAt1);
        recyclerView = findViewById(R.id.recyclerView);
        sendButton = findViewById(R.id.sendMessageButton);
        messageEditText = findViewById(R.id.messageEditText);

        firebaseDatabase = FirebaseDatabase.getInstance();
        if(messageEditText.getText().toString()==null) sendButton.setEnabled(false);
        else sendButton.setEnabled(true);
        checkChatRoom();
    }
    private void sendMsgToDatabase(){
        //Send the msg to the database if there is a message.
        if (!messageEditText.getText().toString().equals("")){
            ChatMessage.Chat chat = new ChatMessage.Chat();
            chat.userEmail = myEmailClean;
            chat.message = messageEditText.getText().toString();
            chat.timestamp = ServerValue.TIMESTAMP;
            firebaseDatabase.getReference().child("chatrooms").child(chatRoomUid).child("chats").push().setValue(chat)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //sends the message and cleans out the edit text.
                            Log.d("d", "successfully sent message");
                            messageEditText.setText("");
                        }
                    });
        }
    }
    private void checkChatRoom() {
        //Check the chatroom in the firebase and updates the view accordingly.
        firebaseDatabase.getReference().child("chatrooms").orderByChild("users/"+myEmailClean).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                            if(chatMessage.users.containsKey(otherEmailClean)){
                                chatRoomUid = dataSnapshot.getKey();
                                sendButton.setEnabled(true);

                                recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                                recyclerView.setAdapter(new RvAdapter());

                                sendMsgToDatabase();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder>{
        //Recycler View adapter for the chatroom.
        List<ChatMessage.Chat> chats;
        public RvAdapter(){
            chats = new ArrayList<>();
            getDestEmail();
        }
        private void getDestEmail(){
            firebaseDatabase.getReference().child("users").child(otherEmailClean).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    destUser = snapshot.getValue(chatUser.class);
                    getMessageList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        private void getMessageList(){
            //Gets all the messages stored in the chatroom.
            firebaseDatabase.getReference().child("chatrooms").child(chatRoomUid).child("chats").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chats.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        chats.add(dataSnapshot.getValue(ChatMessage.Chat.class));
                    }
                    notifyDataSetChanged();
                    recyclerView.scrollToPosition(chats.size()-1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        @NonNull
        @Override
        public RvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //Inflates the view.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RvAdapter.ViewHolder holder, int position) {
            ViewHolder viewHolder = holder;
            if(chats.get(position).userEmail.equals(myEmailClean)){
                viewHolder.tvMsg.setText(chats.get(position).message);
                viewHolder.LLholder.setVisibility(View.INVISIBLE);
                viewHolder.LLRoot.setGravity(Gravity.RIGHT);
                viewHolder.LLTimeStamp.setGravity(Gravity.RIGHT);
            }else {
                viewHolder.tvName.setText(otherEmailClean);
                viewHolder.LLholder.setVisibility(View.VISIBLE);
                viewHolder.tvMsg.setText(chats.get(position).message);
                viewHolder.LLRoot.setGravity(Gravity.LEFT);
                viewHolder.LLTimeStamp.setGravity(Gravity.LEFT);
            }
            viewHolder.tvTimeStamp.setText(getDateTime(position));
        }
        public String getDateTime(int position){
            //Gets the current time.
            long unixTime = (long) chats.get(position).timestamp;
            Date date = new Date(unixTime);
            String time = simpleDateFormat.format(date);
            return time;
        }

        @Override
        public int getItemCount() {
            return chats.size();
        }
        private class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tvMsg;
            public TextView tvName;
            public TextView tvTimeStamp;
            public ImageView ivProfile;
            public LinearLayout LLholder;
            public LinearLayout LLRoot;
            public LinearLayout LLTimeStamp;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                tvMsg = itemView.findViewById(R.id.messagebox_textview_msg);
                tvName = itemView.findViewById(R.id.messagebox_TextView_name);
                tvTimeStamp = itemView.findViewById(R.id.messagebox_textview_timestamp);
                ivProfile = itemView.findViewById(R.id.messagebox_ImageView_profile);
                LLholder = itemView.findViewById(R.id.messagebox_LinearLayout);
                LLRoot = itemView.findViewById(R.id.messagebox_root);
                LLTimeStamp = itemView.findViewById(R.id.messagebox_layout_timestamp);
            }
        }
    }
}
