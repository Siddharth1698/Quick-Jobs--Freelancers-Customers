package com.quickjobs.quickjobs_freelancercustomers.Fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quickjobs.quickjobs_freelancercustomers.Adapters.CustomerHistoryLJAdapter;
import com.quickjobs.quickjobs_freelancercustomers.Adapters.HistoryAdapter;
import com.quickjobs.quickjobs_freelancercustomers.HistoryActivity;
import com.quickjobs.quickjobs_freelancercustomers.Objects.CustomerHistoryLJObject;
import com.quickjobs.quickjobs_freelancercustomers.Objects.HistoryObject;
import com.quickjobs.quickjobs_freelancercustomers.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerHistoryFragment extends Fragment {


    private RecyclerView mHistoryRecyclerView;
    private RecyclerView.Adapter mHistoryAdapter;
    private RecyclerView.LayoutManager mHistoryLayoutManager;


    public CustomerHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_customer_history, container, false);

        mHistoryRecyclerView = (RecyclerView) view.findViewById(R.id.custhistoryRecyclerView);
        mHistoryRecyclerView.setNestedScrollingEnabled(false);
        mHistoryRecyclerView.setHasFixedSize(true);
        mHistoryLayoutManager = new LinearLayoutManager(getContext());
        mHistoryRecyclerView.setLayoutManager(mHistoryLayoutManager);
        mHistoryAdapter = new CustomerHistoryLJAdapter(getDataSetHistory(), getActivity());
        mHistoryRecyclerView.setAdapter(mHistoryAdapter);



    getUserHistoryIds();

        return view;


}
String uidd;

    private void getUserHistoryIds() {
        uidd =  FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(uidd).child("localjobshistory");
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
                    String title = "",desc="",location="",cat="",phno="";

                   title = dataSnapshot.child("title").getValue().toString();
                    desc = dataSnapshot.child("desc").getValue().toString();
                    cat = dataSnapshot.child("Cat").getValue().toString();
                    phno = dataSnapshot.child("phno").getValue().toString();
                    Long timestamp = 0L;



                    if(dataSnapshot.child("timestamp").getValue() != null){
                        timestamp = Long.valueOf(dataSnapshot.child("timestamp").getValue().toString());
                    }




                    CustomerHistoryLJObject obj = new CustomerHistoryLJObject(title,desc,phno,cat,getDate(timestamp));
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

    private ArrayList resultsHistory = new ArrayList<CustomerHistoryLJObject>();
    private ArrayList<CustomerHistoryLJObject> getDataSetHistory() {
        return resultsHistory;
    }


}