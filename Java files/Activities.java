package com.example.familyshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class Activities extends AppCompatActivity {

    private ArrayList<Post> items = new ArrayList<>();
    private DatabaseReference postReference;
    private CollectionReference postreference;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String description, imageUrl, usnme, id, time;

//    @Override
//    public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
//        Post post = documentSnapshot.toObject(Post.class);
//        time = post.getTime();
//
//        Intent intent = new Intent(this, Comments.class);
//        intent.putExtra("time",time);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        recyclerView = findViewById(R.id.listView);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ListAdapter(getApplicationContext(), items);

        Intent idIntent = getIntent();
        id = idIntent.getStringExtra("id");

        postReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        //postreference = FirebaseFirestore.getInstance().collection("users").document(id).collection("posts");
        postReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot resultSnapshot: dataSnapshot.getChildren()) {

                    description = resultSnapshot.child("description").getValue(String.class);
                    imageUrl = resultSnapshot.child("postImageUrl").getValue(String.class);
                    usnme = resultSnapshot.child("username").getValue(String.class);
                    Post post = new Post(description, imageUrl, usnme, time);
                    items.add(post);
                }
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}

