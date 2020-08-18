package com.example.fueltrackv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.fueltrackv1.Model.Products;
import com.example.fueltrackv1.ViewHolder.Product_View_Holder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;

public class Help_actv extends AppCompatActivity {

    private GoogleMap mMap;
    LocationListener locationListener;
    LocationManager locationManager;
    LatLng userLatlng;
    SupportMapFragment mapFragment;
    FusedLocationProviderClient client;
    String workType = "Select", val, hosipital = "hosipital", Fuel_Stations = "FuelStations", url, username, work, dist;
    Double lat, lng;
    RadioGroup regwork;
    RadioButton radioWorkType;
    DatabaseReference personRef, Node, dref,ref;
    Button btn,search;
    int ProximityRadius = 10000;
    AutoCompleteTextView regAddress;
    FloatingActionButton fuel, hosp;
    private static final String[] States = new String[]{"Ariyalur", "Chennai", "Coimbatore", "Cuddalore", "Dharmapuri", "Dindigul", "Erode", "Kanchipuram", "Kanniyakumari", "Karur", "Krishnagiri", "Madurai", "Nagapattinam", "Namakkal", "Nilgiris", "Perambular", "Pudukkottai", "Ramnathapuram", "Salem", "Sivagangai", "Thanjavur", "Theni", "Thoothukodi", "Tiruvarur", "Tirunelveli", "Tiruchirappalli", "Tiruvallur", "Tiruppur", "Tiruvannamalai", "Vellore", "Villupuram", "Virudhnagar"};
    BottomNavigationView bottomNavigationView;
    //com.example.fueltrackv1.FuelStation_fragment help = new com.example.fueltrackv1.FuelStation_fragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_actv);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.container);
        client = LocationServices.getFusedLocationProviderClient(this);
        ActivityCompat.requestPermissions(Help_actv.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);

        regwork = findViewById(R.id.workType);
        regAddress = findViewById(R.id.text_list);
        btn = findViewById(R.id.search);
        fuel = findViewById(R.id.fuel);
        hosp = findViewById(R.id.hospital);
        search = findViewById(R.id.order);

        //String[] disticts=getResources().getStringArray(R.array.states);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Help_actv.this, android.R.layout.simple_list_item_1, States);
        regAddress.setAdapter(adapter);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                LatLng latLng = new LatLng(9.262221, 77.508473);
                MarkerOptions options = new MarkerOptions().position(latLng).title("vishal");
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                mMap.addMarker(options);
                */
                ref = FirebaseDatabase.getInstance().getReference("Professionals").child("Kanniyakumari").child("Mobile Puncher");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        //Log.e("Count " ,""+snapshot.getChildrenCount());
                        //Toast.makeText(Help_actv.this,""+snapshot.getChildren(),Toast.LENGTH_SHORT).show();
                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            final String name = (String) postSnapshot.child("FirstName").getValue();
                            final String lname = (String) postSnapshot.child("LastName").getValue();
                            final String profnum = (String) postSnapshot.child("PhoneNo").getValue();
                            Double latit = (Double) postSnapshot.child("lat").getValue();
                            Double longi = (Double) postSnapshot.child("lng").getValue();
                            LatLng latLng = new LatLng(latit, longi);
                            final MarkerOptions options = new MarkerOptions().position(latLng).title(""+profnum)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                            mMap.addMarker(options);
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    Intent intent =new Intent(Help_actv.this, proffActv.class);
                                    //intent.putExtra("username",username);
                                    intent.putExtra("fname",name);
                                    intent.putExtra("lname",lname);
                                    intent.putExtra("phoneNo",marker.getTitle());
                                    intent.putExtra("work","Mobile Puncher");
                                    intent.putExtra("district","Kanniyakumari");
                                    startActivity(intent);
                                    Help_actv.this.finish();
                                    return false;
                                }
                            });
                            //Toast.makeText(Help_actv.this,""+latit+""+longi,Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
            }
        });
        fuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                /*
                Object transferData[] = new Object[2];
                NearByPlaces getNearByPlaces = new NearByPlaces();
                url = getURL(lat, lng, Fuel_Stations);
                transferData[0] = mMap;
                transferData[1] = url;
                getNearByPlaces.execute(transferData);
                 */
                Toast.makeText(Help_actv.this, "Searching for NearBy FuelStations", Toast.LENGTH_SHORT).show();
                // Map point based on address
                String val = "geo:"+lat+","+lng+"?z=10+fuel";
                String val1= "http://maps.google.com/maps?&saddr=" + lat + "," + lng + "&daddr=nearby Petrol";
                Uri location = Uri.parse(val1);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                Toast.makeText(Help_actv.this, "Showing NearBy FuelStations", Toast.LENGTH_SHORT).show();
                startActivity(mapIntent);
            }
        });

        hosp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Object transferData[] = new Object[2];
                NearByPlaces getNearByPlaces = new NearByPlaces();
                url = getURL(lat, lng, hosipital);
                transferData[0] = mMap;
                transferData[1] = url;
                getNearByPlaces.execute(transferData);
                 */
                Toast.makeText(Help_actv.this, "Searching for NearBy Hosipitals", Toast.LENGTH_SHORT).show();
                String val = "geo:"+lat+","+lng+"?z=10+hospital";
                String val1= "http://maps.google.com/maps?&saddr=" + lat + "," + lng + "&daddr=nearby hospitals";
                Uri location = Uri.parse(val1);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                Toast.makeText(Help_actv.this, "Showing NearBy Hosipitals", Toast.LENGTH_SHORT).show();
                startActivity(mapIntent);
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                val = regAddress.getText().toString();
                if (val.isEmpty() == false && (workType.isEmpty() == false && workType.equals("Select") == false)) {
                    Node = FirebaseDatabase.getInstance().getReference("Professionals");
                    Node.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if ((dataSnapshot.child(val).exists()) == true && (dataSnapshot.child(val).child(workType).exists()) == true) {
                                Intent intent = new Intent(Help_actv.this, Display.class);
                                regAddress.setText("");
                                intent.putExtra("username",username);
                                intent.putExtra("Address", val);
                                personRef = FirebaseDatabase.getInstance().getReference().child("Professionals");
                                intent.putExtra("Work", workType);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Help_actv.this, workType + "s in the district you have entered were not Registered Here.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(Help_actv.this, "The above Fields can't be Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLatlng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.clear();
                MarkerOptions options = new MarkerOptions().position(userLatlng).title("HERE");
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatlng,10));
                mMap.addMarker(options);
            }
        };
        askLocationPermission();
    }

    private void askLocationPermission() {
        Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                userLatlng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(userLatlng).title("Here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatlng));
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }
    */
    private String getURL(Double lat, Double lng, String nearByplace)
    {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" +lat+ "," +lng);
        googleURL.append("&radius=" +ProximityRadius);
        googleURL.append("&sensor=true");
        googleURL.append("&keyword=hotel");
        googleURL.append("&key=" + "AIzaSyCY89tN4mMsxR1vmJSJSIpTU6mSqp5Omik");

        Log.d("Help_actv", "url=" +googleURL.toString());
        return googleURL.toString();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if(location!=null)
                {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = googleMap;
                            lat=location.getLatitude();
                            lng=location.getLongitude();


                            dref = FirebaseDatabase.getInstance().getReference();
                            dref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    work = dataSnapshot.child("users").child(username).child("userStatus").getValue().toString();
                                    if(!work.equals("Others"))
                                    {
                                        dist = dataSnapshot.child("users").child(username).child("District").getValue().toString();
                                        dref.child("Professionals").child(dist).child(work).child(username).child("lat").setValue(lat);
                                        dref.child("Professionals").child(dist).child(work).child(username).child("lng").setValue(lng);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            MarkerOptions options = new MarkerOptions().position(latLng).title("HERE");
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                            mMap.addMarker(options);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==44)
        {
            if(grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                getCurrentLocation();
            }
        }
    }
    public void radioClick(View view) {
        int workId = regwork.getCheckedRadioButtonId();
        radioWorkType = findViewById(workId);
        workType = radioWorkType.getText().toString();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    //Log.e("CurrentTAG", String.valueOf(Thread.currentThread()));
                    switch (item.getItemId()) {
                        case R.id.helpNavigation:
                            break;
                        case R.id.homeNavigation:
                            Thread homeThread =new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent2 = new Intent(Help_actv.this, home.class);
                                    intent2.putExtra("username",username);
                                    startActivity(intent2);
                                    Help_actv.this.finish();
                                }
                            });
                            homeThread.start();
                            break;
                        case R.id.profileNavigation:
                            Thread profilethread =new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent3 = new Intent(Help_actv.this, profile.class);
                                    intent3.putExtra("username",username);
                                    startActivity(intent3);
                                    Help_actv.this.finish();
                                }
                            });
                            profilethread.start();
                            break;
                    }
                    return false;
                }
            };

}