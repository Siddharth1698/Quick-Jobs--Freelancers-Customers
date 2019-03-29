package com.quickjobs.quickjobs_freelancercustomers.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quickjobs.quickjobs_freelancercustomers.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FreelancerHomeFragment extends Fragment {
    String uidd;
    Spinner areaSpinner,catSpinner;
    Button sbmtbtn,sbmtbtn2;
    private String areaName,loc,cat;


    public FreelancerHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_freelancer_home, container, false);

        uidd =  FirebaseAuth.getInstance().getCurrentUser().getUid();
        sbmtbtn = (Button)view.findViewById(R.id.sbmtbtn);
        sbmtbtn2 = (Button)view.findViewById(R.id.sbmtbtn2);


        FirebaseDatabase.getInstance().getReference().child("LocalJobs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> areas = new ArrayList<String>();


                for (DataSnapshot Allepy: dataSnapshot.getChildren()) {
                    areaName = Allepy.getKey().toString();
                    areas.add(areaName);
                }


                 areaSpinner = (Spinner) view.findViewById(R.id.locspinner);
                catSpinner = (Spinner) view.findViewById(R.id.catspinner);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areas);
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



                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                catSpinner.setAdapter(areasAdapter);
                sbmtbtn.setVisibility(View.GONE);
                sbmtbtn2.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

            }
        });

        sbmtbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cat = String.valueOf(catSpinner.getSelectedItem());



            }
        });



        // Inflate the layout for this fragment
        return view;
    }

}
