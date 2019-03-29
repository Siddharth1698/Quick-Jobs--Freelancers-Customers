package com.quickjobs.quickjobs_freelancercustomers;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quickjobs.quickjobs_freelancercustomers.Adapters.FreelancerHistoryLJAdapter;
import com.quickjobs.quickjobs_freelancercustomers.Objects.FreelancerHistoryLJObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LocalJobsFreelancersActivity extends AppCompatActivity {


    String uidd;
    Spinner areaSpinner,catSpinner;
    Button sbmtbtn,sbmtbtn2,searchagainbtn;
    LinearLayout linlayfree,linlayfirst;
    RecyclerView freerecyclerview;
    private String areaName,loc,cat;

    private RecyclerView mHistoryRecyclerView;
    private RecyclerView.Adapter mHistoryAdapter;
    private RecyclerView.LayoutManager mHistoryLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_jobs_freelancers);





        uidd =  FirebaseAuth.getInstance().getCurrentUser().getUid();
        sbmtbtn = (Button)findViewById(R.id.sbmtbtn);
        sbmtbtn2 = (Button)findViewById(R.id.sbmtbtn2);
        linlayfree = (LinearLayout)findViewById(R.id.linlayfree);
        linlayfirst = (LinearLayout)findViewById(R.id.linlayfirst);
        freerecyclerview = (RecyclerView)findViewById(R.id.freeljrecyclerview);
        searchagainbtn = (Button)findViewById(R.id.searchagainbtn);


        mHistoryRecyclerView = (RecyclerView) findViewById(R.id.freeljrecyclerview);
        mHistoryRecyclerView.setNestedScrollingEnabled(false);
        mHistoryRecyclerView.setHasFixedSize(true);
        mHistoryLayoutManager = new LinearLayoutManager(LocalJobsFreelancersActivity.this);
        mHistoryRecyclerView.setLayoutManager(mHistoryLayoutManager);
        mHistoryAdapter = new FreelancerHistoryLJAdapter(getDataSetHistory(), LocalJobsFreelancersActivity.this);
        mHistoryRecyclerView.setAdapter(mHistoryAdapter);




        FirebaseDatabase.getInstance().getReference().child("LocalJobs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> areas = new ArrayList<String>();


                for (DataSnapshot var: dataSnapshot.getChildren()) {
                    areaName = var.getKey().toString();
                    areas.add(areaName);
                }


                areaSpinner = (Spinner) findViewById(R.id.locspinner);
                catSpinner = (Spinner) findViewById(R.id.catspinner);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(LocalJobsFreelancersActivity.this, android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                areaSpinner.setAdapter(areasAdapter);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sbmtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                catSpinner.setVisibility(View.VISIBLE);

                loc = String.valueOf(areaSpinner.getSelectedItem());

                FirebaseDatabase.getInstance().getReference().child("LocalJobs").child(loc).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Is better to use a List, because you don't know the size
                        // of the iterator returned by dataSnapshot.getChildren() to
                        // initialize the array
                        final List<String> areas = new ArrayList<String>();


                        for (DataSnapshot loc: dataSnapshot.getChildren()) {
                            String catName = loc.getKey().toString();
                            areas.add(catName);
                        }



                        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(LocalJobsFreelancersActivity.this, android.R.layout.simple_spinner_item, areas);
                        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        catSpinner.setAdapter(areasAdapter);
                        sbmtbtn.setVisibility(View.GONE);
                        sbmtbtn2.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                areaSpinner.setEnabled(false);
            }

        });

        sbmtbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                catSpinner.setVisibility(View.VISIBLE);

                areaSpinner.setEnabled(true);
                sbmtbtn2.setVisibility(View.GONE);
                freerecyclerview.setVisibility(View.VISIBLE);
                linlayfree.setVisibility(View.VISIBLE);
                linlayfirst.setVisibility(View.GONE);
                cat = String.valueOf(catSpinner.getSelectedItem());
                getUserHistoryIds();


      }

        });

        searchagainbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultsHistory.clear();
                sbmtbtn.setVisibility(View.VISIBLE);
                linlayfree.setVisibility(View.GONE);
                linlayfirst.setVisibility(View.VISIBLE);
                freerecyclerview.setVisibility(View.GONE);
                catSpinner.setAdapter(null);
                catSpinner.setVisibility(View.GONE);
            }
        });



    }


    private void getUserHistoryIds() {
        uidd =  FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("LocalJobs").child(loc).child(cat);
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot localjobshistory : dataSnapshot.getChildren()){
                        FetchRideInformation(localjobshistory.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void FetchRideInformation(String rideKey) {
        DatabaseReference historyDatabase = FirebaseDatabase.getInstance().getReference().child("LocalJobsHistory").child(rideKey);
        historyDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String title = "",desc="",location="",cat="",loc="";

                    title = dataSnapshot.child("title").getValue().toString();
                    desc = dataSnapshot.child("desc").getValue().toString();
                    cat = dataSnapshot.child("Cat").getValue().toString();
                    loc = dataSnapshot.child("loc").getValue().toString();
                    Long timestamp = 0L;



                    if(dataSnapshot.child("timestamp").getValue() != null){
                        timestamp = Long.valueOf(dataSnapshot.child("timestamp").getValue().toString());
                    }




                    FreelancerHistoryLJObject obj = new FreelancerHistoryLJObject(title,desc,location,cat,getDate(timestamp));
                    resultsHistory.add(obj);
                    mHistoryAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private String getDate(Long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time*1000);
        String date = DateFormat.format("MM-dd-yyyy hh:mm", cal).toString();
        return date;
    }

    private ArrayList resultsHistory = new ArrayList<FreelancerHistoryLJObject>();
    private ArrayList<FreelancerHistoryLJObject> getDataSetHistory() {
        return resultsHistory;
    }
}
