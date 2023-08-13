package com.hfad.sayhello;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hfad.sayhello.Model.Users;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Token";
    private static final String NOTIFICATION_PERMISSION = "notification_permission";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseUser currentUser;
    private DatabaseReference dbRef;
    private GridLayout gridLayout;
    private Button allow_btn, deny_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(currentUser.getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        gridLayout = findViewById(R.id.notification_permission);
        allow_btn = findViewById(R.id.allow);
        deny_btn = findViewById(R.id.deny);


        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_chat_bubble_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_perm_contact_calendar_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.baseline_person_24);

        Intent i = getIntent();
        boolean isRegistered = i.getBooleanExtra(RegisterActivity.EXTRA_IS_REGISTERED, false);

        if (isRegistered) {
            gridLayout.setVisibility(View.VISIBLE);
        }

        allow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridLayout.setVisibility(View.GONE);
                getFCMToken();
            }
        });
        deny_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridLayout.setVisibility(View.GONE);
            }
        });


    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.d(TAG, "Token is " + token);
                        setNotificationPermissionStatus(token);
                    }
                });
    }

    private void setNotificationPermissionStatus(String fcmToken) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("fcm_token", fcmToken);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return false;
    }

    private void CheckStatus(String status){

        dbRef  = FirebaseDatabase.getInstance().getReference("MyUsers").child(currentUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        dbRef.updateChildren(hashMap);
    }


    @Override
    protected void onResume() {
        super.onResume();
        CheckStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        CheckStatus("Offline");
    }

}