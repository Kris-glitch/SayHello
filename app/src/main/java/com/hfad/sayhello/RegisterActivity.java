package com.hfad.sayhello;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText email, username, password;
    private Button registerBtn;

    private FirebaseAuth auth;
    private DatabaseReference myDBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        registerBtn = findViewById(R.id.registerBtn);

        auth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username_txt = username.getText().toString().trim();
                String email_txt = email.getText().toString().trim();
                String password_txt = password.getText().toString().trim();

                try{
                    signUpValidation(username_txt, email_txt, password_txt);
                    registerNow (username_txt, email_txt, password_txt);
                }catch(InvalidRegistrationException e){
                    Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerNow (final String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String userID = getCurrentUserID();
                    HashMap<String, String> hashMap = createHash(userID, username);

                    myDBRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(userID);
                    myDBRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }
                        }
                    });

                }else{
                    Toast.makeText(RegisterActivity.this, "Something went wrong. Try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getCurrentUserID() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String userID = firebaseUser.getUid();
        return userID;
    }

    private HashMap createHash(String userID, String username) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", userID);
        hashMap.put("username", username);
        hashMap.put("imageURL", "default");

        return hashMap;
    }

    public static class InvalidRegistrationException extends Exception {
        public InvalidRegistrationException(String message) {
            super(message);
        }
    }

    public boolean passwordHasDigit (String password) {
        for (int i = 0; i < password.length(); i++){
            char a = password.charAt(i);
            if (Character.isDigit(a)){
                return true;
            }
        }
        return false;
    }

    public boolean passwordHasUppercase (String password) {
        for (int i = 0; i < password.length(); i++){
            char a = password.charAt(i);
            if (Character.isUpperCase(a)){
                return true;
            }
        }
        return false;
    }

    public void signUpValidation(String name, String email, String password) throws InvalidRegistrationException {
        if (TextUtils.isEmpty(name)) {
            throw new InvalidRegistrationException("Please enter your username");
        }
        if (TextUtils.isEmpty(email)) {
            throw new InvalidRegistrationException("Please enter your email");
        }
        if (TextUtils.isEmpty(password)) {
            throw new InvalidRegistrationException("Please enter a password");
        }
        if (password.length() < 8) {
            throw new InvalidRegistrationException("Password should be at least 8 characters long");
        }
        if (!passwordHasUppercase(password)){
            throw new InvalidRegistrationException("Password must contain at least one uppercase letter");
        }
        if (!passwordHasDigit(password)){
            throw new InvalidRegistrationException("Password must contain at least one number");
        }
    }


}