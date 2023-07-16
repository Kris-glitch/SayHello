package com.hfad.sayhello.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hfad.sayhello.Adapters.UserAdapter;
import com.hfad.sayhello.Model.ChatList;
import com.hfad.sayhello.Model.Users;
import com.hfad.sayhello.R;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {
    private UserAdapter userAdapter;
    private ArrayList<Users> usersList;

    FirebaseUser firebaseUser;
    DatabaseReference dbReference;

    private List<ChatList> listOfChats;
    private RecyclerView chatRecyclerView;


    public ChatsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chats, container, false);
        chatRecyclerView = view.findViewById(R.id.chats_recyclerView);
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        listOfChats = new ArrayList<>();

        dbReference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());

        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listOfChats.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
                    assert chatList != null;
                    listOfChats.add(chatList);
                }
                getRecentChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    public void getRecentChats(){
        usersList = new ArrayList<>();
        dbReference = FirebaseDatabase.getInstance().getReference("MyUsers");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users user = dataSnapshot.getValue(Users.class);
                    assert user != null;
                    for (ChatList chat : listOfChats){
                        if (user.getId().equals(chat.getId())){
                            usersList.add(user);
                        }
                    }

                }
                userAdapter = new UserAdapter(getContext(), usersList, true);
                chatRecyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}