package com.quickjobs.quickjobs_freelancercustomers.Fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

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
private DatabaseReference historyDatabase;


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
        mHistoryAdapter.notifyDataSetChanged();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mHistoryRecyclerView);




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

    private String loc,cat,RideKey;
    private void FetchRideInformation(final String rideKey) {
        historyDatabase = FirebaseDatabase.getInstance().getReference().child("LocalJobsHistory").child(rideKey);
        historyDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String title = "",desc="",phno="",status="";
                    loc="";
                    cat="";
                    RideKey = rideKey;

                   title = dataSnapshot.child("title").getValue().toString();
                    desc = dataSnapshot.child("desc").getValue().toString();
                    cat = dataSnapshot.child("Cat").getValue().toString();
                    phno = dataSnapshot.child("phno").getValue().toString();
                    loc = dataSnapshot.child("loc").getValue().toString();
                    status = dataSnapshot.child("Status").getValue().toString();
                    Long timestamp = 0L;



                    if(dataSnapshot.child("timestamp").getValue() != null){
                        timestamp = Long.valueOf(dataSnapshot.child("timestamp").getValue().toString());
                    }




                    CustomerHistoryLJObject obj = new CustomerHistoryLJObject(title,desc,phno,cat,getDate(timestamp),status);
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
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Toast.makeText(getActivity(), "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }




        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            Toast.makeText(getActivity(), "on Swiped ", Toast.LENGTH_SHORT).show();
            //Remove swiped item from list and notify the RecyclerView

            
            int position = viewHolder.getAdapterPosition();
            resultsHistory.remove(position);
            mHistoryAdapter.notifyDataSetChanged();

        }
    };

    private ArrayList resultsHistory = new ArrayList<CustomerHistoryLJObject>();
    private ArrayList<CustomerHistoryLJObject> getDataSetHistory() {
        return resultsHistory;
    }



}