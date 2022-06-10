package com.h5200006_burak_oztas_yonlendirilmiscalisma;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    EditText userName, userStatus;
    CircleImageView userProfileImage;
    Button btnUpdateProfileSettings;

    private DatabaseReference rootRef;
    private FirebaseAuth firebaseAuth;


    private static final int GalleryPick = 1;

    private StorageReference usersProfileImagesRef;
    private ProgressDialog loadingBar;
    private StorageTask uploadTask;
    private Uri fileUri;
    private String myUrl = "";

    private Toolbar profileToolBar;

    private String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // init firebase auth and fireStore


        firebaseAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef = FirebaseDatabase.getInstance().getReference();
        usersProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        currentUserID = firebaseAuth.getCurrentUser().getUid();

        initializeFields();

        btnUpdateProfileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateSettings();
            }
        });

        userName.setVisibility(View.INVISIBLE);

        retrieveUserInfo();

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
    }


    private void initializeFields() {

        userName = findViewById(R.id.txtUsername);
        userStatus = findViewById(R.id.txtStatus);
        userProfileImage = findViewById(R.id.imgUser);
        btnUpdateProfileSettings = findViewById(R.id.btnUpdateProfileSettings);


        loadingBar = new ProgressDialog(this);

        profileToolBar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(profileToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Profile Settings");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();


            StorageReference filePath = usersProfileImagesRef.child(currentUserID + ".jpg");

            uploadTask = filePath.putFile(ImageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }


                    return filePath.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        rootRef.child("Users").child(currentUserID).child("userImage")
                                .setValue(myUrl)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            //Toast.makeText(ProfileActivity.this, "Image save in Database, Successfully...", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        } else {
                                            String message = task.getException().toString();
                                            Toast.makeText(ProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                });
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(ProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                    }
                }
            });


        }
    }


    private void updateSettings() {

        String setUserName = userName.getText().toString();
        String setStatus = userStatus.getText().toString();

        if (TextUtils.isEmpty(setUserName)) {

            Toast.makeText(this, "Please write your username..", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setStatus)) {
            Toast.makeText(this, "Please write your status", Toast.LENGTH_SHORT).show();
        } else {

            HashMap<String, Object> profileMap = new HashMap<>();

            profileMap.put("uid", currentUserID);
            profileMap.put("username", setUserName);
            profileMap.put("status", setStatus);


            rootRef.child("Users").child(currentUserID).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {

                        sendUserToHomeScreenActivity();
                        Toast.makeText(ProfileActivity.this, "Profile Updated Succesfully", Toast.LENGTH_SHORT).show();
                    } else {

                        String message = task.getException().toString();
                        Toast.makeText(ProfileActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }


    private void retrieveUserInfo() {


        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("username") && (dataSnapshot.hasChild("userImage")))) {


                    String retrieveUserName = dataSnapshot.child("username").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                    String retrieveProfileImage = dataSnapshot.child("userImage").getValue().toString();

                    userName.setText(retrieveUserName);
                    userStatus.setText(retrieveStatus);
                    Picasso.get().load(retrieveProfileImage).into(userProfileImage);

                } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("username"))) {
                    String retrieveUserName = dataSnapshot.child("username").getValue().toString();
                    String retrievesStatus = dataSnapshot.child("status").getValue().toString();

                    userName.setText(retrieveUserName);
                    userStatus.setText(retrievesStatus);
                } else {
                    userName.setVisibility(View.VISIBLE);
                    Toast.makeText(ProfileActivity.this, "Please set & update your profile ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void sendUserToHomeScreenActivity() {

        Intent homeScreenIntent = new Intent(ProfileActivity.this, HomeScreenActivity.class);
        homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeScreenIntent);
        finish();
    }


}
