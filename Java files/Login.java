package com.example.familyshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    private EditText username, password;
    private Button login, register;
    private String usname, pswrd, radio, id;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private CollectionReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        database = FirebaseFirestore.getInstance();
        usersReference = database.collection("users");
        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Register.class);
                startActivity(intent);
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logindata();
            }
        });
    }

    private void logindata(){
        usname = username.getText().toString() + "@gmail.com".trim();
        pswrd = password.getText().toString().trim();
        id = username.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(usname, pswrd)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            usersReference.document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists())
                                    {
                                        Intent usersid = new Intent(getApplicationContext(), Profile.class);
                                        usersid.putExtra("id",id);
                                        startActivity(usersid);
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Credentials mismatch",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
