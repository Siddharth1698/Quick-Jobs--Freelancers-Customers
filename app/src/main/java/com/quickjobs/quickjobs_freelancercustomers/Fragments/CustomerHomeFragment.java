package com.quickjobs.quickjobs_freelancercustomers.Fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quickjobs.quickjobs_freelancercustomers.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerHomeFragment extends Fragment {
    private String Custtitle,Custdesc,Custloc,Custimg,Custspin;
    private Spinner spinner1;
    private EditText title,desc,location;
    //private ImageView jobimg;
    private Button jobbtn;




    public CustomerHomeFragment() {
        // Required empty public constructor
    }

    private String jobid;
    private String uidd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_customer_home, container, false);
        title = view.findViewById(R.id.custjobtitle);
        desc = view.findViewById(R.id.custjobdetails);
        location = view.findViewById(R.id.custlocjob);
        spinner1 = view.findViewById(R.id.spinner1);
        //jobimg = view.findViewById(R.id.custjobimg);
        jobbtn = view.findViewById(R.id.btnSubmit);
        uidd =  FirebaseAuth.getInstance().getCurrentUser().getUid();



        jobbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Custtitle = title.getText().toString();
                Custdesc = desc.getText().toString();
                Custloc = location.getText().toString();
                Custspin = String.valueOf(spinner1.getSelectedItem());
                jobid = FirebaseDatabase.getInstance().getReference().child("LocalJobs").child(Custloc).child(Custspin).push().getKey();
                DatabaseReference jobdata = FirebaseDatabase.getInstance().getReference().child("LocalJobs").child(Custloc).child(Custspin).child(jobid);
                jobdata.child("title").setValue(Custtitle);
                jobdata.child("desc").setValue(Custdesc);
                jobdata.child("loc").setValue(Custloc);
                jobdata.child("Cat").setValue(Custspin);
                jobdata.child("Customer").setValue(uidd);

                Toast.makeText(getActivity(),"Job posted succesfully",Toast.LENGTH_SHORT).show();



            }
        });




        return view;

    }


}
