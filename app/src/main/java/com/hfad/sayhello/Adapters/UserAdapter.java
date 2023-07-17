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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.sayhello.MessageActivity;
import com.hfad.sayhello.Model.Chat;
import com.hfad.sayhello.Model.Users;
import com.hfad.sayhello.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final Context context;
    private final ArrayList<Users> myUsers;
    FirebaseUser firebaseUser;
    private final boolean isChat;

    public UserAdapter(Context context, ArrayList<Users> myUsers, boolean isChat) {
        this.context = context;
        this.myUsers = myUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final Users users = myUsers.get(position);
        holder.username.setText(users.getUsername());

        if (users.getImageURL().equals("default")) {
            holder.userPic.setImageResource(R.drawable.profile);
        } else {
            Glide.with(context).load(users.getImageURL()).into(holder.userPic);
        }

        String status = users.getStatus();

        if (isChat) {

            setMsgSnippet(users.getId(), holder.msgSnippet);

            if (status.equals("online")) {
                holder.imageViewON.setVisibility(View.VISIBLE);
                holder.imageViewOFF.setVisibility(View.GONE);
            } else {
                holder.imageViewON.setVisibility(View.GONE);
                holder.imageViewOFF.setVisibility(View.VISIBLE);
            }
        } else {
            holder.imageViewON.setVisibility(View.GONE);
            holder.imageViewOFF.setVisibility(View.GONE);
            holder.msgSnippet.setText("");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userid", users.getId());
                context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return myUsers.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView userPic;
        public TextView msgSnippet;
        public ImageView imageViewON;
        public ImageView imageViewOFF;


        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username_item);
            userPic = itemView.findViewById(R.id.profilePicture);
            msgSnippet = itemView.findViewById(R.id.msg_item);
            imageViewON = itemView.findViewById(R.id.statusimageON);
            imageViewOFF = itemView.findViewById(R.id.statusimageOFF);

        }
    }

    private void setMsgSnippet(String userID, TextView msgSnippet) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String lastMessage = "";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat == null) {
                        continue;
                    }

                    if ((chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userID)) ||
                            (chat.getReceiver().equals(userID) && chat.getSender().equals(firebaseUser.getUid()))) {
                        lastMessage = chat.getMessage();
                    }
                }
                msgSnippet.setText(lastMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
