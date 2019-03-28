package com.quickjobs.quickjobs_freelancercustomers;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.quickjobs.quickjobs_freelancercustomers.Fragments.CustomerChatsFragment;
import com.quickjobs.quickjobs_freelancercustomers.Fragments.CustomerHistoryFragment;
import com.quickjobs.quickjobs_freelancercustomers.Fragments.CustomerHomeFragment;
import com.quickjobs.quickjobs_freelancercustomers.Fragments.CustomerSettingsFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;
import android.widget.TextView;

public class LocalJobsCustomersActivity extends AppCompatActivity {


    Fragment HomeFragment = new CustomerHomeFragment();
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        Fragment fragment = null;

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new CustomerHomeFragment();
                    if (fragment != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container_cust, fragment)
                                .commit();
                        return true;
                    }

                    return true;
                case R.id.navigation_dashboard:
                    fragment = new CustomerChatsFragment();
                    if (fragment != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container_cust, fragment)
                                .commit();
                        return true;
                    }
                    return true;
                case R.id.navigation_notifications:
                    fragment = new CustomerHistoryFragment();
                    if (fragment != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container_cust, fragment)
                                .commit();
                        return true;
                    }
                    return true;
                case R.id.navigation_settings:
                    fragment = new CustomerSettingsFragment();
                    if (fragment != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container_cust, fragment)
                                .commit();
                        return true;
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_jobs_customers);


        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
