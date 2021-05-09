package com.example.familyshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    private EditText fullname, username, password;
    private Button signup;
    private FirebaseFirestore database;
    private CollectionReference usersReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullname = findViewById(R.id.name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        signup = findViewById(R.id.signup);

        database = FirebaseFirestore.getInstance();
        usersReference = database.collection("users");
        mAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addData();
            }
        });
    }

    private void addData() {
        final String usrname = username.getText().toString() + "@gmail.com".trim();
        final String psswrd = password.getText().toString().trim();
        final String fname = fullname.getText().toString().trim();
        final String id = username.getText().toString();
        
        if (!TextUtils.isEmpty(usrname) && !TextUtils.isEmpty(psswrd)) {
            mAuth.createUserWithEmailAndPassword(usrname, psswrd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        ProfileValues data = new ProfileValues(fname, username.getText().toString(), psswrd);
                        usersReference.document(id).set(data);

                        Toast.makeText(Register.this, "Registration Successful !", Toast.LENGTH_LONG).show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Register.this, Login.class);
                                startActivity(intent);
                            }
                        }, 3000);
                    } else {
                        Toast.makeText(Register.this, "Enter the values", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
