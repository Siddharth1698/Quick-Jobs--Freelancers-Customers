package com.quickjobs.quickjobs_freelancercustomers;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class VerifyActivity extends AppCompatActivity {
    private Button jobverifiedcash,jobverifiedgooglepay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        jobverifiedcash = (Button)findViewById(R.id.jobverifiedcash);
        jobverifiedgooglepay = (Button)findViewById(R.id.jobverifiedgooglepay);

        jobverifiedcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(VerifyActivity.this,CustomerMapsActivity.class));
                finish();
            }
        });

    }
}
