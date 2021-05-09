package com.example.familyshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private TextView profilename;
    private EditText about, postDescription;
    private ImageView profilepic, postpic;
    private Button addprofilepic, addbio, logout, publish, postImage, activities;
    private String id, bio, postImageUrl, profileImageUrl;
    private static final int PICK_IMAGE = 1;
    private static final int PICK_POST_IMAGE = 2;
    private int i;
    private Uri mImageUri;
    private Uri mPostImageUri;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Post> items = new ArrayList<>();
    private Date currentTime;
    private ProgressBar postpicprogress;

    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private DocumentReference usersReference;
    private StorageReference usersStorageReference;
    private DocumentReference postReference;
    private DatabaseReference realtimeDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profilename = findViewById(R.id.pname);
        about = findViewById(R.id.about);
        profilepic = findViewById(R.id.profilepic);
        addprofilepic = findViewById(R.id.addppic);
        addbio = findViewById(R.id.addbio);
        logout = findViewById(R.id.logout);
        postDescription = findViewById(R.id.desc);
        publish = findViewById(R.id.publish);
        postImage = findViewById(R.id.postimage);
        postpic = findViewById(R.id.postpic);
        activities = findViewById(R.id.viewact);
        recyclerView = findViewById(R.id.listViewProfile);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ListAdapter(Profile.this, items);
        postpicprogress = findViewById(R.id.postPicProgress);

        Intent idIntent = getIntent();
        id = idIntent.getStringExtra("id");

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        usersReference = database.collection("users").document(id);
        realtimeDatabase = FirebaseDatabase.getInstance().getReference("Posts");
        usersStorageReference = FirebaseStorage.getInstance().getReference("users").child(id);

        profileAction();
    }

    private void profileAction() {

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Profile.this, Login.class));
                finish();
            }
        });

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProfilepic();
            }
        });

        addprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProfilePic();
            }
        });

        addbio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadBio();
            }
        });

        profilename.setText("@" + id);

        usersReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                about.setText(documentSnapshot.getString("bio"));
                Glide.with(getApplicationContext())
                        .load(documentSnapshot.getString("imageUrl"))
                        .override(100, 100)
                        .into(profilepic);
            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPost();
            }
        });

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPostImage();
            }
        });

        activities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, Activities.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        realtimeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot resultSnapshot : dataSnapshot.getChildren()) {
                    String description = resultSnapshot.child("description").getValue(String.class);
                    String imageUrl = resultSnapshot.child("postImageUrl").getValue(String.class);
                    String usnme = resultSnapshot.child("username").getValue(String.class);
                    Post post = new Post(description, imageUrl, usnme, String.valueOf(currentTime));
                    items.add(post);
                }
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadBio() {
        bio = about.getText().toString();
        Map<String, Object> biog = new HashMap<>();
        biog.put("bio", bio);
        usersReference.update(biog);
    }

    private void selectProfilepic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void addPostImage() {
        Intent postImageIntent = new Intent();
        postImageIntent.setType("image/*");
        postImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(postImageIntent, PICK_POST_IMAGE);
        postpicprogress.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null & data.getData() != null) {
            mImageUri = data.getData();
            Glide.with(getApplicationContext())
                    .load(mImageUri)
                    .fitCenter()
                    .into(profilepic);
        } else if (requestCode == PICK_POST_IMAGE && resultCode == RESULT_OK && data != null & data.getData() != null) {
            mPostImageUri = data.getData();
            uploadPostpic();
            Glide.with(getApplicationContext())
                    .load(mPostImageUri)
                    .fitCenter()
                    .into(postpic);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadProfilePic() {
        if (mImageUri != null) {
            final StorageReference fileReference = usersStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            profileImageUrl = String.valueOf(uri);
                            Map<String, Object> url = new HashMap<>();
                            url.put("imageUrl", profileImageUrl);
                            usersReference.update(url);
                        }
                    });
                    Toast.makeText(Profile.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Profile.this, "Upload unsuccessful", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadPostpic() {
        currentTime = Calendar.getInstance().getTime();
        if (mPostImageUri != null) {
            final StorageReference fileReference = usersStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(mPostImageUri));

            fileReference.putFile(mPostImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            postImageUrl = String.valueOf(uri);
                        }
                    });
                    postpicprogress.setVisibility(ProgressBar.INVISIBLE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Profile.this, "Photo adding unsuccessful", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadPost() {
        postReference = database.collection("users").document(id).collection("posts").document(String.valueOf(currentTime));
        Post post = new Post(postDescription.getText().toString(), postImageUrl, id, String.valueOf(currentTime));
        postReference.set(post);
        realtimeDatabase.child(String.valueOf(currentTime)).setValue(post);

        postDescription.setText("");
    }
}