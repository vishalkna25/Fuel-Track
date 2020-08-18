package com.example.fueltrackv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.eventbus.EventBus;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;


public class home extends AppCompatActivity{

    BottomNavigationView bottomNavigationView;
    TextInputEditText level;
    String username, fuellevel;
    TextView price, change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        ViewPager viewPager = findViewById(R.id.viewPager);
        price = findViewById(R.id.priceNew);
        change = findViewById(R.id.priceChange);

        imageAdapter adapter = new imageAdapter(this);
        viewPager.setAdapter(adapter);

        level = findViewById(R.id.flevel);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("fuellevel");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("users").child(username).exists()) {
                    String lvl = dataSnapshot.child("users").child(username).child("level").getValue().toString();
                    fuellevel = "FUEL LEVEL: " + lvl + "%";
                    level.setText(fuellevel);
                    int leevl = Integer.parseInt(lvl);
                    if (leevl <= 20) {
                        Toast.makeText(home.this, "Your fuel level getting low", Toast.LENGTH_SHORT).show();
                        notification();
                    }
                }else{
                    level.setText("No vehicles you've Registered Here");
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
            });
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("fuelPrice");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String newpetrol = dataSnapshot.child("petrolNew").getValue().toString();
                String old = dataSnapshot.child("petrolOld").getValue().toString();

                price.setText("Rs."+newpetrol);
                change.setText("Rs."+old);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void notification()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("n","n",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"n")
                .setContentText("Your Fuel level getting low! ! !, CHECK NOW")
                .setSmallIcon(R.drawable.mplogo)
                .setAutoCancel(true)
                .setContentTitle("FUEL TRACK");
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    //Log.e("CurrentTAG", String.valueOf(Thread.currentThread()));
                    switch (item.getItemId()) {
                        case R.id.homeNavigation:
                            break;
                        case R.id.helpNavigation:
                            Thread helpthread =new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent2 = new Intent(home.this, Help_actv.class);
                                    intent2.putExtra("username",username);
                                    startActivity(intent2);
                                    home.this.finish();
                                }
                            });
                            helpthread.start();
                            break;
                        case R.id.profileNavigation:
                            Thread profilethread =new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent3 = new Intent(home.this, profile.class);
                                    intent3.putExtra("username",username);
                                    startActivity(intent3);
                                    home.this.finish();
                                }
                            });
                            profilethread.start();
                            break;
                    }
                    return false;
                }
            };

}
