package com.quickjobs.quickjobs_freelancercustomers.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quickjobs.quickjobs_freelancercustomers.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerSettingsFragment extends Fragment {


    public CustomerSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_settings, container, false);
    }

}