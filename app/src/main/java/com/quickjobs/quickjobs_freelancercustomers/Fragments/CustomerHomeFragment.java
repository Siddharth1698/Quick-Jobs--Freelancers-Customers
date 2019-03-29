package com.quickjobs.quickjobs_freelancercustomers.Fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.quickjobs.quickjobs_freelancercustomers.Adapters.HistoryAdapter;
import com.quickjobs.quickjobs_freelancercustomers.HistoryActivity;
import com.quickjobs.quickjobs_freelancercustomers.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerHomeFragment extends Fragment {
    private String Custtitle,Custdesc,Custloc,Custimg,Custspin;
    private Spinner spinner1;
    private EditText title,desc,location,phno;
    //private ImageView jobimg;
    private Button jobbtn;


    Long timestamp = System.currentTimeMillis()/1000;


    public CustomerHomeFragment() {
        // Required empty public constructor
    }

    private String jobid,phnum;
    private String uidd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_customer_home, container, false);
        title = view.findViewById(R.id.custjobtitle);
        desc = view.findViewById(R.id.custjobdetails);
        location = view.findViewById(R.id.custlocjob);
        spinner1 = view.findViewById(R.id.spinner1);
        phno = view.findViewById(R.id.custphonenumber);
        //jobimg = view.findViewById(R.id.custjobimg);
        jobbtn = view.findViewById(R.id.btnSubmit);
        uidd =  FirebaseAuth.getInstance().getCurrentUser().getUid();







        jobbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Custtitle = title.getText().toString();
                Custdesc = desc.getText().toString();
                Custloc = location.getText().toString();
                phnum = phno.getText().toString();
                Custspin = String.valueOf(spinner1.getSelectedItem());
                jobid = FirebaseDatabase.getInstance().getReference().child("LocalJobs").child(Custloc).child(Custspin).push().getKey();
                DatabaseReference jobdata = FirebaseDatabase.getInstance().getReference().child("LocalJobs").child(Custloc).child(Custspin).child(jobid);
                jobdata.child("title").setValue(Custtitle);
                jobdata.child("desc").setValue(Custdesc);
                jobdata.child("loc").setValue(Custloc);
                jobdata.child("Cat").setValue(Custspin);
                jobdata.child("Customer").setValue(uidd);
                jobdata.child("timestamp").setValue(timestamp);
                jobdata.child("phno").setValue(phnum);
                jobdata.child("ridekey").setValue(jobid);
                jobdata.child("Status").setValue("Posted");

                DatabaseReference jobdata2 = FirebaseDatabase.getInstance().getReference().child("LocalJobsHistory").child(jobid);
                jobdata2.child("title").setValue(Custtitle);
                jobdata2.child("desc").setValue(Custdesc);
                jobdata2.child("loc").setValue(Custloc);
                jobdata2.child("Cat").setValue(Custspin);
                jobdata2.child("ridekey").setValue(jobid);
                jobdata2.child("phno").setValue(phnum);
                jobdata2.child("timestamp").setValue(timestamp);
                jobdata2.child("Customer").setValue(uidd);
                jobdata2.child("Status").setValue("Posted");

                FirebaseDatabase.getInstance().getReference()
                        .child("Users").child("Customers").child(uidd).child("localjobshistory").child(jobid).setValue(true);
//                jobHistory.child("title").setValue(Custtitle);
//                jobHistory.child("desc").setValue(Custdesc);
//                jobHistory.child("loc").setValue(Custloc);
//                jobHistory.child("Cat").setValue(Custspin);
//                jobHistory.child("Customer").setValue(uidd);
//                jobHistory.child("Status").setValue("Posted");

                Toast.makeText(getActivity(),"Job posted succesfully",Toast.LENGTH_SHORT).show();

                title.setText("");
                desc.setText("");
                location.setText("");
                phno.setText("");



            }
        });


        return view;

    }


}
