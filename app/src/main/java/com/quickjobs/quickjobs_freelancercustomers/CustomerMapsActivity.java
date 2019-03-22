package com.quickjobs.quickjobs_freelancercustomers;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private Button logout,request,settings,customerChatBtn;
    private LatLng pickuplocation;
    private String customerId = "",requestService;
    private Boolean requestbol = false;
    private Marker pickupMarker;
    private String destination;
    private LinearLayout mFreelancerInfo;
    private ImageView mFreelancerProfileImage;
    private TextView mFreelancerName;
    private EditText jobDesc;
    private String job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFreelancerInfo = (LinearLayout)findViewById(R.id.freelancerInfo);
        mFreelancerProfileImage = (ImageView) findViewById(R.id.freelancerProfileImage);
        mFreelancerName = (TextView) findViewById(R.id.freelancerName);
        customerChatBtn = (Button)findViewById(R.id.customerChatBtn);

        logout = (Button)findViewById(R.id.logoutCustomer);
        request = (Button)findViewById(R.id.request);
        settings = (Button)findViewById(R.id.settings);
        final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();


//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(CustomerMapsActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();
//                return;
//            }
//        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                 postJob();


            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(CustomerMapsActivity.this,CustomerSettingsActivity.class);
                startActivity(intent);
                return;
            }
        });

        customerChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent=new Intent(CustomerMapsActivity.this,CustomerChatActivity.class);
                intent.putExtra("freelancerFoundId", freelancerFoundId);
                startActivity(intent);
                return;
            }
        });



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
                        jobDesc = (EditText)alertLayout.findViewById(R.id.job_desc);
                        job = jobDesc.getText().toString();
                        FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userid).child("CustomerRequestDescs").setValue(job);

                        if (requestbol){
                            requestbol = false;
                            freelancerLocationRef.removeEventListener(freelancerLocationRefListner);
                            geoQuery.removeAllListeners();

                            if (freelancerFoundId != null){
                                DatabaseReference freelancerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Freelancers").child(freelancerFoundId).child("CustomerRequest");
                                freelancerRef.setValue(true);
                                freelancerFoundId = null;

                            }
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequests").child(userid).child("location");
                            FirebaseDatabase.getInstance().getReference("CustomerRequests").child(userid).child("CustomerRequestDescs").removeValue();

                            GeoFire geoFire = new GeoFire(ref);
                            geoFire.removeLocation(userid);

                            if (pickupMarker != null){
                                pickupMarker.remove();
                            }
                            request.setText("Get Things Done");


                            mFreelancerInfo.setVisibility(View.GONE);
                            mFreelancerName.setText("");




                        }else {

                            requestbol = true;

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequests").child(userid).child("location");

                            GeoFire geoFire = new GeoFire(ref);
                            geoFire.setLocation(userid,new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));

                            pickuplocation = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                            pickupMarker = mMap.addMarker(new MarkerOptions().position(pickuplocation).title("I AM HERE"));

                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();



                            FirebaseDatabase.getInstance().getReference("CustomerRequests").child(userId).child("CustomerRequestDescs").setValue(job);
                            request.setText("Getting your freelancer...");
                            getClosestFreelancer();
                            FirebaseDatabase.getInstance().getReference().child("Chats").addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                        if (dataSnapshot.exists()){
                                            customerChatBtn.setVisibility(View.VISIBLE);
                                        }


                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                        if (dataSnapshot.exists()){
                                            customerChatBtn.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
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
                                    request.setText("Get Things Done");
                                    Toast.makeText(getApplicationContext(),"Freelancer cancelled your request...",Toast.LENGTH_SHORT);

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

        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickuplocation.latitude,pickuplocation.longitude),radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!freelancerFound && requestbol){
                    freelancerFound = true;
                    freelancerFoundId = key;

                    DatabaseReference freelancerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Freelancers").child(freelancerFoundId);
                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    HashMap map = new HashMap();
                    map.put("CustomerRideId",customerId);
                    freelancerRef.updateChildren(map);

                    getFreelancerLocation();
                    getFreelancerInfo();
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
                if (!freelancerFound){
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
        DatabaseReference mCustomerDatabase =  FirebaseDatabase.getInstance().getReference().child("Users").child("Freelancers").child(freelancerFoundId);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name")!=null){

                        mFreelancerName.setText(map.get("name").toString());
                    }

                    if (map.get("profileImageUrl")!=null){
                        Picasso.get().load(map.get("profileImageUrl").toString()).into(mFreelancerProfileImage);
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
                if (dataSnapshot.exists() && requestbol){
                List<Object> map = (List<Object>) dataSnapshot.getValue();
                double locationLat = 0;
                double locationLng = 0;
                request.setText("Freelancer Found");
                if (map.get(0) != null){
                    locationLat = Double.parseDouble(map.get(0).toString());
                    locationLng = Double.parseDouble(map.get(1).toString());
                }
                LatLng freelancerLatLng = new LatLng(locationLat,locationLng);

                if (freelancerMarker != null){
                    freelancerMarker.remove();
                }
                Location loc1 = new Location("");
                loc1.setLatitude(pickuplocation.latitude);
                loc1.setLongitude(pickuplocation.longitude);

                Location loc2 = new Location("");
                loc2.setLatitude(freelancerLatLng.latitude);
                loc2.setLongitude(freelancerLatLng.longitude);

                float distance = loc1.distanceTo(loc2);

                if (distance<100){
                    request.setText("freelancer is Here");
                }else{
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


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }



    @SuppressLint("RestrictedApi")
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


    }

    @Override
    protected void onStop() {
        super.onStop();
    }


}
