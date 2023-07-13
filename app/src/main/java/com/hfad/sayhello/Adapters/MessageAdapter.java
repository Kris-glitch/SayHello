package com.hfad.sayhello.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.hfad.sayhello.MessageActivity;
import com.hfad.sayhello.Model.Chat;
import com.hfad.sayhello.Model.Users;
import com.hfad.sayhello.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private Context context;
    private ArrayList<Chat> myChat;

    private FirebaseUser currentUser;


    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;


    public MessageAdapter(Context context, ArrayList<Chat> myChat) {
        this.context = context;
        this.myChat = myChat;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT){
            view = LayoutInflater.from(context).inflate(R.layout.message_item_right, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.message_item_left, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        Chat chat = myChat.get(position);
        holder.show_msg.setText(chat.getMessage());


    }

    @Override
    public int getItemCount() {
        return myChat.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView show_msg;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            show_msg = itemView.findViewById(R.id.show_msg);

        }
    }


    @Override
    public int getItemViewType(int position) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (myChat.get(position).getSender().equals(currentUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
