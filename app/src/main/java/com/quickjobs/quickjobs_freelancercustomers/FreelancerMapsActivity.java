package com.quickjobs.quickjobs_freelancercustomers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreelancerMapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, RoutingListener, NavigationView.OnNavigationItemSelectedListener {


    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private Button logoutfreelancer,mSettings,AcceptJobBtn,DeclineJobBtn;
    private String userid,uid;
    private Boolean isLoggingOut = false;
    public LinearLayout mCustomerInfo,startstopbtns;
    private ImageView mCustomerProfileImage;
    private float rideDistance;
    private TextView mCustomerName,mCustomerPhone,mCustomerAddress;
    private List<Polyline> polylines;
    private int distance = 0;
    private Button mJobStatus,freelancerDeclineBtn,fchat,freelancerhistorybtn;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    private String customerId = "", desc = "";
    private int status = 0;
    private TextView userNameNav,userPhoneNav;
    private ImageView userImageNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancers_maps);





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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        polylines = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);

        updateFreelancerNavHeader();
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_NETWORK_STATE).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

        String uidd =  FirebaseAuth.getInstance().getCurrentUser().getUid();


        FirebaseDatabase.getInstance().getReference().child("Users").child("Freelancers").child(uidd).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()<3){

                    startActivity(new Intent(FreelancerMapsActivity.this,FreelancerProfileRegisterActivity.class));

                    return;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        startstopbtns = (LinearLayout)findViewById(R.id.startstopbtns);
        mJobStatus =(Button)findViewById(R.id.fsb) ;
        freelancerDeclineBtn = (Button)findViewById(R.id.fdb) ;
        fchat = (Button)findViewById(R.id.fchat);

        mCustomerInfo = (LinearLayout)findViewById(R.id.customerInfo);
        mCustomerProfileImage = (ImageView) findViewById(R.id.customerProfileImage);
        mCustomerName = (TextView) findViewById(R.id.customerName);
        mCustomerPhone = (TextView) findViewById(R.id.customerPhone);
        mCustomerAddress = (TextView)findViewById(R.id.customerDescription);


        AcceptJobBtn = (Button)findViewById(R.id.AcceptJobBtn);
        DeclineJobBtn = (Button)findViewById(R.id.DeclineJobBtn);



        AcceptJobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Chats").child(userid).child(customerId).setValue(true);
                startstopbtns.setVisibility(View.VISIBLE);
                mCustomerInfo.setVisibility(View.GONE);
                Intent intent = new Intent(FreelancerMapsActivity.this, FreelancerChatActivity.class);
                startActivity(intent);
                return;


            }
        });

        fchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FreelancerMapsActivity.this, FreelancerChatActivity.class);
                startActivity(intent);
                return;
            }
        });

        DeclineJobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                declineJob();
               }
        });




        mJobStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCustomerInfo.setVisibility(View.GONE);
                freelancerDeclineBtn.setVisibility(View.GONE);

                switch (status){
                    case 1:
                        status = 2;
                        freelancerDeclineBtn.setVisibility(View.GONE);
                        FirebaseDatabase.getInstance().getReference().child("JobStatus").child(userid).setValue("0");
                        mJobStatus.setText("Job Finished");
                        break;
                    case 2:
                        FirebaseDatabase.getInstance().getReference().child("JobStatus").child(userid).setValue("1");
                        recordJob();
                        endJob();
                        break;
                }

            }
        });

        freelancerDeclineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                declineJob();

            }
        });




        getAssignedCustomer();




    }

    private void updateFreelancerNavHeader() {

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("Users").child("Freelancers").child(uid).addValueEventListener(new ValueEventListener() {
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
        getMenuInflater().inflate(R.menu.freelancer_map, menu);
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

            Intent intent=new Intent(FreelancerMapsActivity.this,HistoryActivity.class);
            intent.putExtra("customerOrDriver", "Freelancers");
            startActivity(intent);
        }
        else if (id == R.id.nav_gallery) {
            Intent intent=new Intent(FreelancerMapsActivity.this,LocalJobsFreelancersActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

            Intent intent=new Intent(FreelancerMapsActivity.this,FreelancerSettingsActivity.class);
            startActivity(intent);
        } else if ((id == R.id.nav_about)){

        }else if ((id == R.id.nav_logout)){
            isLoggingOut = true;

            dissconnectFreelancer();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(FreelancerMapsActivity.this,MainActivity.class);
            startActivity(intent);
            finish();


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void recordJob() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference freelancerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Freelancers").child(userId).child("history");
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("history");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("History");
        String requestId = historyRef.push().getKey();
        freelancerRef.child(requestId).setValue(true);
        customerRef.child(requestId).setValue(true);


        HashMap map = new HashMap();
        map.put("driver", userId);
        map.put("customer", customerId);
        map.put("rating", 0);
        map.put("timestamp", getCurrentTimestamp());
        map.put("destination",desc);
        map.put("location/from/lat", pickupLatLng.latitude);
        map.put("location/from/lng", pickupLatLng.longitude);
        map.put("location/to/lat", lastLatLng.latitude);
        map.put("location/to/lng", lastLatLng.longitude);
        map.put("distance", distance);
        historyRef.child(requestId).updateChildren(map);
    }

    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;
    }


    private void endJob() {

            mJobStatus.setText("Start");
            startstopbtns.setVisibility(View.GONE);
        FirebaseDatabase.getInstance().getReference().child("Chats").child(userid).removeValue();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference freelancerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Freelancers").child(userId).child("CustomerRequest");
            freelancerRef.setValue(true);
        rideDistance = 0;




        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("CustomerRequests").child(customerId).child("location");
        FirebaseDatabase.getInstance().getReference("CustomerRequests").child(customerId).child("CustomerRequestDescs").removeValue();

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerId);
        customerId = "";

        if (pickUpMarker != null){
            pickUpMarker.remove();
        }


        erasePolyLines();
        customerId = "";

        if (pickUpMarker!=null){
            pickUpMarker.remove();
        }

        if (assignedCustomerPickUpLocationRefListner != null){
            assignedCustomerPickUpLocationRef.removeEventListener(assignedCustomerPickUpLocationRefListner);
        }
        mCustomerInfo.setVisibility(View.GONE);
        mCustomerName.setText("");
        mCustomerPhone.setText("");

    }


        public void declineJob() {
        erasePolyLines();
        customerId = "";
        if (pickUpMarker!=null){
            pickUpMarker.remove();
        }
        FirebaseDatabase.getInstance().getReference("CustomerRequests").child(customerId).removeValue();
        mCustomerInfo.setVisibility(View.GONE);
        startstopbtns.setVisibility(View.GONE);
        FirebaseDatabase.getInstance().getReference().child("Users").child("Freelancers").child(userid).child("CustomerRideId").removeValue();
        FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("CustomerRequestDescs").setValue("");
        FirebaseDatabase.getInstance().getReference().child("Chats").child(userid).removeValue();
    }

    private void getAssignedCustomer() {

        String freelancerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Freelancers").child(freelancerId).child("CustomerRideId");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    status = 1;
                    customerId = dataSnapshot.getValue().toString();
                    getAssignedCustomerPickUpLocation();
                    getAssignedCustomerDesc();
                    getAssignedCustomerInfo();
                }else {
                   endJob();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void getAssignedCustomerDesc(){
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("CustomerRequestDescs");
        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    desc = dataSnapshot.getValue().toString();
                }
                    else{

                    }
                }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getAssignedCustomerInfo() {
        mCustomerInfo.setVisibility(View.VISIBLE);




        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    final Map<String,Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name")!=null){
                        mCustomerName.setText(map.get("name").toString());
                    }
                    if (map.get("phone")!=null){
                        mCustomerPhone.setText(map.get("phone").toString());
                    }

                    if (map.get("CustomerRequestDescs")!=null){
                        mCustomerPhone.setText(map.get("CustomerRequestDescs").toString());
                    }

                    if (map.get("profileImageUrl")!=null){
                        Picasso.get().load(map.get("profileImageUrl").toString()).into(mCustomerProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    Marker pickUpMarker;
    DatabaseReference assignedCustomerPickUpLocationRef;
    private ValueEventListener assignedCustomerPickUpLocationRefListner;
    LatLng pickupLatLng,lastLatLng;
    private void getAssignedCustomerPickUpLocation() {

        assignedCustomerPickUpLocationRef = FirebaseDatabase.getInstance().getReference().child("CustomerRequests").child(customerId).child("location").child(customerId).child("l");

        assignedCustomerPickUpLocationRefListner = assignedCustomerPickUpLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && !customerId.equals("")){

                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if (map.get(0) != null){
                        locationLat = Double.parseDouble(map.get(0).toString());

                    }
                    if (map.get(1) != null){
                        locationLng = Double.parseDouble(map.get(1).toString());

                    }


                    pickupLatLng = new LatLng(locationLat,locationLng);

                    pickUpMarker = mMap.addMarker(new MarkerOptions().position(pickupLatLng).title("Pickup Location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pickupp)));

                    getRouteToMarkder(pickupLatLng);

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void getRouteToMarkder(LatLng pickupLatLng) {

        if( pickupLatLng!= null) {

            lastLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            if (pickupLatLng != null && mLastLocation != null){
            Routing routing = new Routing.Builder()
                    .key("AIzaSyAUHvnkNCxNZgu4FiCTPV4AxZRzyZltPYU")
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), pickupLatLng)
                    .build();
            routing.execute();
        }}
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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

        if (getApplicationContext()!= null){



            mLastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));


            if(!customerId.equals("") && mLastLocation!=null && location != null){
                rideDistance += mLastLocation.distanceTo(location)/1000;
            }

            DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("FreelancersAvailable");
            DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("FreelancersWorking");
            GeoFire geoFireAvaiable = new GeoFire(refAvailable);
            GeoFire geoFireWorking = new GeoFire(refWorking);

             switch (customerId){
                case "":
                    geoFireWorking.removeLocation(userid);
                    geoFireAvaiable.setLocation(userid,new GeoLocation(location.getLatitude(),location.getLongitude()));
                    break;

                default:
                    geoFireAvaiable.removeLocation(userid);
                    geoFireWorking.setLocation(userid,new GeoLocation(location.getLatitude(),location.getLongitude()));
                    break;
            }

        }


    }

    private void dissconnectFreelancer(){
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("FreelancersAvailable");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userid);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isLoggingOut){
           dissconnectFreelancer();
        }


    }




    @Override
    public void onRoutingFailure(RouteException e) {

        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }


        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            distance = route.get(i).getDistanceValue();

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onRoutingCancelled() {

    }

    private void erasePolyLines(){
        for (Polyline line: polylines){
            line.remove();
        }
        polylines.clear();
    }
}
