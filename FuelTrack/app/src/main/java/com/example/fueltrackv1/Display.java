package com.example.fueltrackv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fueltrackv1.Model.Products;
import com.example.fueltrackv1.ViewHolder.Product_View_Holder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Display extends AppCompatActivity {

    String address, userwork, username;
    DatabaseReference personRef, ref;
    BottomNavigationView bottomNavigationView;
    private RecyclerView recyclerView;
    private ArrayList<Products> arrayList;
    private FirebaseRecyclerOptions<Products> options;
    private FirebaseRecyclerAdapter<Products, Product_View_Holder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        address = intent.getStringExtra("Address");
        userwork = intent.getStringExtra("Work");
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem= menu.getItem(1);
        menuItem.setChecked(true);
        ref = FirebaseDatabase.getInstance().getReference().child("users");
        personRef = FirebaseDatabase.getInstance().getReference().child("Professionals").child(address).child(userwork);
        personRef.keepSynced(true);

        recyclerView = findViewById(R.id.recycle_menu);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<Products>();

        options = new FirebaseRecyclerOptions.Builder<Products>().setQuery(personRef, Products.class).build();
        adapter = new FirebaseRecyclerAdapter<Products, Product_View_Holder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull Product_View_Holder holder, int position, @NonNull final Products model)
            {
                holder.pfName.setText(model.getFirstName());
                holder.plName.setText(model.getLastName());
                holder.pNumber.setText(model.getPhoneNo());

                //Picasso.get().load(model.getProfile()).into(holder.profile);
                //ref.child("buck").setValue(model.getProfile());
                //Toast.makeText(Display.this,""+model.getProfile(),Toast.LENGTH_SHORT).show();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent =new Intent(Display.this, proffActv.class);
                        //intent.putExtra("username",username);
                        intent.putExtra("fname",model.getFirstName());
                        intent.putExtra("lname",model.getLastName());
                        intent.putExtra("phoneNo",model.getPhoneNo());
                        intent.putExtra("work",userwork);
                        intent.putExtra("district",address);
                        startActivity(intent);
                        Display.this.finish();
                        return;
                    }
                });
            }

            @NonNull
            @Override
            public Product_View_Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                return new Product_View_Holder(LayoutInflater.from(Display.this).inflate(R.layout.card_view,viewGroup,false));
            }
        };
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();

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
                                    Intent intent3 = new Intent(Display.this, profile.class);
                                    intent3.putExtra("username",username);
                                    startActivity(intent3);
                                    Display.this.finish();
                                }
                            });
                            profileThread.start();
                            break;
                        case R.id.helpNavigation:
                            Thread helpThread =new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent2 = new Intent(Display.this, Help_actv.class);
                                    intent2.putExtra("username",username);
                                    startActivity(intent2);
                                    Display.this.finish();
                                }
                            });
                            helpThread.start();
                            break;
                        case R.id.homeNavigation:
                            Thread homeThread =new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent1 = new Intent(Display.this, home.class);
                                    intent1.putExtra("username",username);
                                    startActivity(intent1);
                                    Display.this.finish();
                                }
                            });
                            homeThread.start();
                            break;
                    }
                    return false;
                }
            };
}