package com.quickjobs.quickjobs_freelancercustomers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button mCustomer,mFreelancer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCustomer = (Button)findViewById(R.id.customer);
        mFreelancer =(Button)findViewById(R.id.freelancer);

        mFreelancer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FreelancerLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        mCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CustomerLoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}
