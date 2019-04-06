package com.quickjobs.quickjobs_freelancercustomers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InviteActivity extends AppCompatActivity {
    private Button inviteBtnl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        inviteBtnl = (Button)findViewById(R.id.invitebtn);

        inviteBtnl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Get your things done quickly or freelance locally now. Download our app: https://github.com/QuickJobs-getThingsDone");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }
}
