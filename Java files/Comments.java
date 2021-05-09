package com.example.familyshare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.BatchUpdateException;
import java.util.HashMap;
import java.util.Map;

public class Comments extends AppCompatActivity{

    private EditText comments;
    private Button postcomment;
    private String commentText;
    private RecyclerView commentRecyclerView;

    private FirebaseFirestore database;
    private CollectionReference commentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        comments = findViewById(R.id.entercomment);
        postcomment = findViewById(R.id.postcomment);

        Intent idIntent = getIntent();
        String id = idIntent.getStringExtra("id");

        Intent timeIntent = getIntent();
        String time = timeIntent.getStringExtra("time");

        database = FirebaseFirestore.getInstance();
        commentReference = database.collection("users").document(id).collection("posts").document(time).collection("comments");

        postcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentText = comments.getText().toString();
                Map<String, Object> comm = new HashMap<>();
                comm.put("comments", commentText);
                commentReference.add(comm);
            }
        });


    }
}
