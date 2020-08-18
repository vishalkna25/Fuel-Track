package com.example.fueltrackv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class proffActv extends AppCompatActivity {

    String firstName, lastName, phoneNo, Name,workType, district, username,val;
    TextView name, phone, work, dstrict;
    Button call, done;
    TextInputLayout descrip;
    ImageView profile;
    DatabaseReference ref;
    //TextInputEditText desc;
    BottomNavigationView bottomNavigationView;
    private static final int REQUEST_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proff_actv);
        final Intent intent = getIntent();
        //username = intent.getStringExtra("username");
        firstName = intent.getStringExtra("fname");
        lastName = intent.getStringExtra("lname");
        phoneNo = intent.getStringExtra("phoneNo");
        workType = intent.getStringExtra("work");
        district = intent.getStringExtra("district");
        Name = firstName + " " + lastName;

        profile = findViewById(R.id.prf);

        ref = FirebaseDatabase.getInstance().getReference("users").child(phoneNo);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("image").getValue().toString();
                Picasso.get().load(image).into(profile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        work = findViewById(R.id.workType);
        dstrict = findViewById(R.id.mDistrict);
        call = findViewById(R.id.Call);
        done = findViewById(R.id.done);

        final TextInputEditText desc =findViewById(R.id.descr);
        descrip = findViewById(R.id.editTextTextMultiLine);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem= menu.getItem(1);
        menuItem.setChecked(true);

        name.setText(Name);
        phone.setText(phoneNo);
        work.setText(workType);
        dstrict.setText(district);


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(proffActv.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(proffActv.this,
                            new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                } else {
                    String dial = "tel:"+phoneNo;
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                }
                return;
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val = descrip.getEditText().getText().toString();
                if(val.isEmpty()==false)
                {
                    if(!username.equals(phoneNo)) {
                        DatabaseReference personRef = FirebaseDatabase.getInstance().getReference();
                        personRef.child("Reviews").child(phoneNo).child(username).setValue(val);
                        desc.setText("");
                    }else{
                        Toast.makeText(proffActv.this,"You can't write review for yourself",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(proffActv.this,"Write any Reviews...",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        });
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void makePhoneCall()
    {
        if (ContextCompat.checkSelfPermission(proffActv.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(proffActv.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:9443992644";
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    //Log.e("CurrentTAG", String.valueOf(Thread.currentThread()));
                    switch (item.getItemId()) {
                        case R.id.profileNavigation:
                            Thread profileThread =new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent3 = new Intent(proffActv.this, profile.class);
                                    intent3.putExtra("username",username);
                                    startActivity(intent3);
                                    proffActv.this.finish();
                                }
                            });
                            profileThread.start();
                            break;
                        case R.id.helpNavigation:
                            Thread helpThread =new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent2 = new Intent(proffActv.this, Help_actv.class);
                                    intent2.putExtra("username",username);
                                    startActivity(intent2);
                                    proffActv.this.finish();
                                }
                            });
                            helpThread.start();
                            break;
                        case R.id.homeNavigation:
                            Thread homeThread =new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent1 = new Intent(proffActv.this, home.class);
                                    intent1.putExtra("username",username);
                                    startActivity(intent1);
                                    proffActv.this.finish();
                                }
                            });
                            homeThread.start();
                            break;
                    }
                    return false;
                }
            };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(proffActv.this,Display.class);
        intent.putExtra("Address",district);
        intent.putExtra("Work",workType);
        startActivity(intent);
        proffActv.this.finish();
    }
}