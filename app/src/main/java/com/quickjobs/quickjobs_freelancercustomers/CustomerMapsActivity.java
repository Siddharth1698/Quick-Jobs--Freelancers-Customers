package com.quickjobs.quickjobs_freelancercustomers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import de.hdodenhof.circleimageview.CircleImageView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerMapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener  {


    GoogleMap mMap;

    Location mLastLocation;
    LocationRequest mLocationRequest;
    private Button logout, request, settings, customerChatBtn, customerVerifyBtn, history;
    private LatLng pickuplocation;
    private String customerId = "", requestService;
    private Boolean requestbol = false;
    private Marker pickupMarker;
    private LinearLayout mFreelancerInfo;
    String uid;
    private ImageView mFreelancerProfileImage;

    private RatingBar mRatingBar;
    private TextView mFreelancerName;
    private EditText jobDesc;
    private String job;
    private TextView userNameNav,userPhoneNav;
    private ImageView userImageNav;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        androidx.appcompat.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        userNameNav = (TextView)navigationView.getHeaderView(0).findViewById(R.id.navName);
        userImageNav = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.navImage);
        userPhoneNav = (TextView)navigationView.getHeaderView(0).findViewById(R.id.navPhone);
        updateCustomerNavHeader();

        mapFragment.getMapAsync(this);

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

        String uidd =  FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(uidd).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()<2){
                    startActivity(new Intent(CustomerMapsActivity.this,CustomerProfileRegistrationActivity.class));
                    return;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mFreelancerInfo = (LinearLayout) findViewById(R.id.freelancerInfo);
        mFreelancerProfileImage = (ImageView) findViewById(R.id.freelancerProfileImage);
        mFreelancerName = (TextView) findViewById(R.id.freelancerName);
        customerChatBtn = (Button) findViewById(R.id.customerChatBtn);


        request = (Button) findViewById(R.id.request);


        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);

        customerVerifyBtn = (Button) findViewById(R.id.customerVerifyBtn);
        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();




        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                postJob();


            }
        });

        customerVerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                customerVerifyBtn.setVisibility(View.GONE);
                FirebaseDatabase.getInstance().getReference().child("JobStatus").child(freelancerFoundId).setValue("0");
                FirebaseDatabase.getInstance().getReference().child("Users").child("Freelancers").child(freelancerFoundId).child("CustomerRideId").removeValue();
                startActivity(new Intent(CustomerMapsActivity.this, VerifyActivity.class));
                finish();

            }
        });



        customerChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(CustomerMapsActivity.this, CustomerChatActivity.class);
                intent.putExtra("freelancerFoundId", freelancerFoundId);
                mFreelancerInfo.setVisibility(View.GONE);
                startActivity(intent);
                return;
            }
        });


        FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(uidd).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               if (!dataSnapshot.exists()){
                     startActivity(new Intent(CustomerMapsActivity.this,CustomerProfileRegistrationActivity.class));
                     return;
               }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void updateCustomerNavHeader() {

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>3){
                    String name = dataSnapshot.child("name").getValue().toString();
                    String phone = dataSnapshot.child("phone").getValue().toString();
                    String image = dataSnapshot.child("profileImageUrl").getValue().toString();

                    userNameNav.setText(name);
                    userPhoneNav.setText(phone);
                    Picasso.get().load(image).into(userImageNav);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            Intent intent = new Intent(CustomerMapsActivity.this, HistoryActivity.class);
                    intent.putExtra("customerOrDriver", "Customers");
                    startActivity(intent);
        }
        else if (id == R.id.nav_gallery) {

            Intent intent = new Intent(CustomerMapsActivity.this, LocalJobsCustomersActivity.class);
            startActivity(intent);



        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

            Intent intent = new Intent(CustomerMapsActivity.this, CustomerSettingsActivity.class);
            startActivity(intent);
        } else if ((id == R.id.nav_about)){

        }else if ((id == R.id.nav_logout)){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(CustomerMapsActivity.this,MainActivity.class);
            startActivity(intent);
            finish();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    private void postJob() {


        LayoutInflater inflater = getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.job_dialog_post, null);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(CustomerMapsActivity.this);
        builder1.setMessage("Post your Job");
        builder1.setView(alertLayout);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Submit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        jobDesc = (EditText) alertLayout.findViewById(R.id.job_desc);
                        job = jobDesc.getText().toString();
                        FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userid).child("CustomerRequestDescs").setValue(job);

                        if (requestbol) {

                            endJob();


                        } else {

                            requestbol = true;

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequests").child(userid).child("location");

                            GeoFire geoFire = new GeoFire(ref);
                            geoFire.setLocation(userid, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                            pickuplocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                            pickupMarker = mMap.addMarker(new MarkerOptions().position(pickuplocation).title("I AM HERE"));

                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


                            FirebaseDatabase.getInstance().getReference("CustomerRequests").child(userId).child("CustomerRequestDescs").setValue(job);
                            request.setText("Getting your freelancer...");
                            getClosestFreelancer();
                            FirebaseDatabase.getInstance().getReference().child("Chats").addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                    if (dataSnapshot.exists()) {
                                        customerChatBtn.setVisibility(View.VISIBLE);
                                    }


                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    if (dataSnapshot.exists()) {
                                        customerChatBtn.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        customerChatBtn.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                            FirebaseDatabase.getInstance().getReference().child("CustomerRequests").child(customerId).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    mFreelancerInfo.setVisibility(View.GONE);
                                    freelancerMarker.remove();
                                    request.setText("Get Things Done");
                                    Toast.makeText(CustomerMapsActivity.this,"Sorry... Freelancer canceled your request",Toast.LENGTH_SHORT);
                                    Toast.makeText(getApplicationContext(), "Freelancer cancelled your request...", Toast.LENGTH_SHORT);

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            FirebaseDatabase.getInstance().getReference().child("JobStatus").addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    if (dataSnapshot.exists()) {
                                        if (dataSnapshot.getValue().toString().equals("1")) {
                                            customerVerifyBtn.setVisibility(View.VISIBLE);
                                        } else {
                                            if (dataSnapshot.exists()) {
                                                customerVerifyBtn.setVisibility(View.GONE);
                                            }

                                        }

                                    }
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    if (dataSnapshot.exists()) {
                                        if (dataSnapshot.getValue().toString().equals("1")) {
                                            customerVerifyBtn.setVisibility(View.VISIBLE);
                                        } else {
                                            customerVerifyBtn.setVisibility(View.GONE);
                                        }

                                    }
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }


                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Decline",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog jobpost = builder1.create();
        jobpost.show();


    }

    private int radius = 1;
    private Boolean freelancerFound = false;
    private String freelancerFoundId;
    private GeoQuery geoQuery;

    private void getClosestFreelancer() {
        DatabaseReference freelancerLocation = FirebaseDatabase.getInstance().getReference().child("FreelancersAvailable");

        GeoFire geoFire = new GeoFire(freelancerLocation);

        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickuplocation.latitude, pickuplocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!freelancerFound && requestbol) {
                    freelancerFound = true;
                    freelancerFoundId = key;

                    DatabaseReference freelancerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Freelancers").child(freelancerFoundId);
                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    HashMap map = new HashMap();
                    map.put("CustomerRideId", customerId);
                    freelancerRef.updateChildren(map);

                    getFreelancerLocation();
                    getFreelancerInfo();
                    gethasEndedJob();
                    request.setText("Looking for Freelancer Location");

                }


            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!freelancerFound) {
                    radius++;
                    getClosestFreelancer();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void getFreelancerInfo() {
        mFreelancerInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Freelancers").child(freelancerFoundId);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {

                        mFreelancerName.setText(map.get("name").toString());
                    }

                    if (map.get("profileImageUrl") != null) {
                        Picasso.get().load(map.get("profileImageUrl").toString()).into(mFreelancerProfileImage);
                    }

                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingsAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()) {
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if (ratingsTotal != 0) {
                        ratingsAvg = ratingSum / ratingsTotal;
                        mRatingBar.setRating(ratingsAvg);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private Marker freelancerMarker;
    private DatabaseReference freelancerLocationRef;
    private ValueEventListener freelancerLocationRefListner;

    private void getFreelancerLocation() {
        freelancerLocationRef = FirebaseDatabase.getInstance().getReference().child("FreelancersWorking").child(freelancerFoundId).child("l");
        freelancerLocationRefListner = freelancerLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && requestbol) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    request.setText("Freelancer Found");
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng freelancerLatLng = new LatLng(locationLat, locationLng);

                    if (freelancerMarker != null) {
                        freelancerMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickuplocation.latitude);
                    loc1.setLongitude(pickuplocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(freelancerLatLng.latitude);
                    loc2.setLongitude(freelancerLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    if (distance < 100) {
                        request.setText("freelancer is Here");
                    } else {
                        request.setText("freelancer Found: " + String.valueOf(distance));
                    }
                    freelancerMarker = mMap.addMarker(new MarkerOptions().position(freelancerLatLng).title("Your freelancer"));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private DatabaseReference freelancerhasEndedRef;
    private ValueEventListener freelancerhasEndedRefListner;

    private void gethasEndedJob() {


        freelancerhasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Freelancers").child(freelancerFoundId).child("CustomerRideId");
        freelancerhasEndedRefListner = freelancerhasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                } else {

                    endJob();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void endJob() {

        requestbol = false;
        freelancerLocationRef.removeEventListener(freelancerLocationRefListner);
        geoQuery.removeAllListeners();
        freelancerhasEndedRef.removeEventListener(freelancerhasEndedRefListner);

        if (freelancerFoundId != null) {
            DatabaseReference freelancerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Freelancers").child(freelancerFoundId).child("CustomerRequest");
            freelancerRef.setValue(true);
            freelancerFoundId = null;

        }

        freelancerFound = false;
        radius = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequests").child(userId).child("location");
        FirebaseDatabase.getInstance().getReference("CustomerRequests").child(userId).child("CustomerRequestDescs").removeValue();

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);

        if (pickupMarker != null) {
            pickupMarker.remove();
        }
        request.setText("Get Things Done");


        mFreelancerInfo.setVisibility(View.GONE);
        mFreelancerName.setText("");

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                checkLocationPermission();
            }
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);

    }
    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for(Location location : locationResult.getLocations()){
                if(getApplicationContext()!=null){
                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
//                    if(!getDriversAroundStarted)
//                        getDriversAround();
                }
            }
        }
    };


    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(CustomerMapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(CustomerMapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}



//    boolean getFreelancesAroundStarted = false;
//        List<Marker> markers = new ArrayList<Marker>();
//    private void getFreelancesAround(){
//        getFreelancesAroundStarted = true;
//        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("FreelancersAvailable");
//
//        GeoFire geoFire = new GeoFire(driverLocation);
//        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLongitude(), mLastLocation.getLatitude()), 15000);
//
//        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
//            @Override
//            public void onKeyEntered(String key, GeoLocation location) {
//
//                for(Marker markerIt : markers){
//                    if(markerIt.getTag().equals(key))
//                        return;
//                }
//
//                LatLng freelancerLocation = new LatLng(location.latitude, location.longitude);
//
//                Marker mFreelancerMarker = mMap.addMarker(new MarkerOptions().position(freelancerLocation).title(key));
//                mFreelancerMarker.setTag(key);
//
//                markers.add(mFreelancerMarker);
//
//
//            }
//
//            @Override
//            public void onKeyExited(String key) {
//                for(Marker markerIt : markers){
//                    if(markerIt.getTag().equals(key)){
//                        markerIt.remove();
//                    }
//                }
//            }
//
//            @Override
//            public void onKeyMoved(String key, GeoLocation location) {
//                for(Marker markerIt : markers){
//                    if(markerIt.getTag().equals(key)){
//                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
//                    }
//                }
//            }
//
//            @Override
//            public void onGeoQueryReady() {
//            }
//
//            @Override
//            public void onGeoQueryError(DatabaseError error) {
//
//            }
//        });
//    }
//}
//
//
