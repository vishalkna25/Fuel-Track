package com.example.fueltrackv1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;

import io.paperdb.Paper;

public class profile extends AppCompatActivity {

    TextInputEditText fullName, mobile, password, district, sex;
    TextView picture;
    Button logout;
    String username, District, work;
    DatabaseReference reference;
    StorageTask uploadTask;
    BottomNavigationView bottomNavigationView;
    ImageView profile;
    StorageReference fileref;
    private Uri imageUri,imgUri;
    private String myurl = "";
    private StorageReference storageReference;
    private String checker = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile pictures");
        final Intent intent = getIntent();
        username = intent.getStringExtra("username");
        logout = findViewById(R.id.logoutbtn);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem= menu.getItem(2);
        menuItem.setChecked(true);

        //init text
        fullName = findViewById(R.id.profileUserName);
        mobile = findViewById(R.id.profileMobile);
        password = findViewById(R.id.profilePassword);
        district = findViewById(R.id.profileEmail);
        sex = findViewById(R.id.profileSex);
        logout = findViewById(R.id.logoutbtn);
        profile = findViewById(R.id.profile_logo);
        picture = findViewById(R.id.change);


        reference = FirebaseDatabase.getInstance().getReference().child("users").child(username);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("image").exists()) {
                    String image = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(image).into(profile);
                }
                String firstName = dataSnapshot.child("firstName").getValue().toString();
                String lastName = dataSnapshot.child("lastName").getValue().toString();
                String Mobile = dataSnapshot.child("mobile").getValue().toString();
                String Password = dataSnapshot.child("password").getValue().toString();
                District = dataSnapshot.child("District").getValue().toString();
                String Sex = dataSnapshot.child("sex").getValue().toString();
                work = dataSnapshot.child("userStatus").getValue().toString();


                String name = firstName+" "+lastName;

                fullName.setText(name);
                mobile.setText(Mobile);
                password.setText(Password);
                district.setText(District);
                sex.setText(Sex);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionMangement sessionMangement = new SessionMangement(profile.this);
                sessionMangement.removeSession();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                Intent intent = new Intent(profile.this,MainActivity.class);
                startActivity(intent);

            }
        });

       picture.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               CropImage.activity(imageUri).setAspectRatio(1,1).start(profile.this);

           }
       });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            //profile.setImageURI(imageUri);
            fileref = storageReference.child(username + ".jpg");
            uploadTask = fileref.putFile(imageUri);
            myurl = imageUri.toString();

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        myurl = downloadUri.toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                        ref.child(username).child("image").setValue(myurl);
                        Picasso.get().load(myurl).into(profile);

                        if(!ref.child(username).child("userSatus").equals("Others"))
                        {
                            DatabaseReference reff = FirebaseDatabase.getInstance().getReference("Professionals");
                            reff.child(District).child(work).child(username).child("image").setValue(myurl);
                        }

                    }
                }
            });
        }else{
            Toast.makeText(this,"Error, Try Again...",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(profile.this, profile.class));
            finish();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    //Log.e("CurrentTAG", String.valueOf(Thread.currentThread()));
                    switch (item.getItemId()) {
                        case R.id.profileNavigation:
                            break;
                        case R.id.helpNavigation:
                            Thread helpThread =new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent2 = new Intent(profile.this, Help_actv.class);
                                    intent2.putExtra("username",username);
                                    startActivity(intent2);
                                    profile.this.finish();
                                }
                            });
                            helpThread.start();
                            break;
                        case R.id.homeNavigation:
                            Thread homeThread =new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent2 = new Intent(profile.this, home.class);
                                    intent2.putExtra("username",username);
                                    startActivity(intent2);
                                    profile.this.finish();
                                }
                            });
                            homeThread.start();
                            break;
                    }
                    return false;
                }
            };

}

