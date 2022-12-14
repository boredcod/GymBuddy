package com.example.gymbuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FriendInviteAdapter extends ArrayAdapter<User> implements View.OnClickListener{
    private ArrayList<User> users;
    Context mContext;
    private static class ViewHolder {
        //View Holder for the Friend invites
        TextView txtName;
        TextView txtGym;
        ImageView info;
    }
    public FriendInviteAdapter(ArrayList<User> data, Context context) {
        //Initializes the list of users and Context of this adapter.
        super(context, R.layout.friend_row, data);
        this.users = data;
        this.mContext = context;
    }
    @Override
    public void onClick(View view) {

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User currUser = getItem(position);
        FriendInviteAdapter.ViewHolder viewHolder;
        final View result;
        if (convertView == null){
            //Creates a Inflater and inflate the view with the inflater if the view is present.
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.friend_row, parent, false);
            //Creates a reference for each items in the row.
            viewHolder.txtName = convertView.findViewById(R.id.friendRowName);
            viewHolder.txtGym = convertView.findViewById(R.id.friendRowGym);
            viewHolder.info = convertView.findViewById(R.id.friendRowImage);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            //If the View is already present, simply get the view with previous tag.
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }
        viewHolder.txtName.setText(currUser.getName());
        viewHolder.txtGym.setText(currUser.getGym());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        return convertView;
    }
}
