package com.quickjobs.quickjobs_freelancercustomers.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.quickjobs.quickjobs_freelancercustomers.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerHomeFragment extends Fragment {

    private Spinner spinner1;


    public CustomerHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_home, container, false);
    }

}
