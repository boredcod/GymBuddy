package com.example.gymbuddy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList<User> users;
    ListView listView;
    ArrayList<String> friendlist;
    private static FriendListAdapter adapter;
    public FriendListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendListFragment newInstance(String param1, String param2) {
        FriendListFragment fragment = new FriendListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        friendlist = new ArrayList<>();
        users = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friend_list, container, false);
        listView = v.findViewById(R.id.friendFragmentList);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        Log.d("d", "Google account email: " + account.getEmail());
        DocumentReference docRef = db.collection("users").document(account.getEmail());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    System.out.println("debug this " + document.getData());
                    if (document.exists()) {
                        Log.d("d", "DocumentSnapshot data: " + document.getData());
                        friendlist = (ArrayList<String>) document.getData().get("friendlist");
                        Log.d("d", "List of friends hi: " + friendlist);
                        if (friendlist.size() == 0){
                            Log.d("d", "no friendlist");
                        }else {
                            final long[] pendingLoadCount = {friendlist.size()};
                            while (friendlist.size() > 0){
                                String currFriendEmail = friendlist.remove(0);
                                DocumentReference friendsDocRef = db.collection("users").document(currFriendEmail);
                                friendsDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                String curr_name = document.getData().get("name").toString();
                                                String curr_uid = document.getData().get("uID").toString();
                                                String curr_location = document.getData().get("location").toString();
                                                String curr_gym = document.getData().get("gym").toString();
                                                String curr_email = document.getData().get("email").toString();
                                                Log.d("d", "DocumentSnapshot dataaaaaaa: " + document.getData());
                                                Log.d("d", "List of friends: " + document.getData().get("friendlist"));
                                                users.add(new User(curr_name,curr_uid,curr_location,curr_gym,curr_email,new ArrayList<>()));
                                            } else {
                                                Log.d("d", "No such document");
                                            }
                                        } else {
                                            Log.d("d", "get failed with ", task.getException());
                                        }
                                        pendingLoadCount[0] = pendingLoadCount[0] - 1;
                                        if (pendingLoadCount[0] == 0){
                                            adapter = new FriendListAdapter(users,getActivity());
                                            listView.setAdapter(adapter);
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        Log.w("d", "No such document");
                    }

                } else {
                    Log.d("d", "get failed with ", task.getException());
                }

            }
        });



        return v;

    }
}