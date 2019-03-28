package com.quickjobs.quickjobs_freelancercustomers.ViewHolders;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.quickjobs.quickjobs_freelancercustomers.HistorySingleActivity;
import com.quickjobs.quickjobs_freelancercustomers.R;

public class CustomerHistoryLJViewHolders  extends RecyclerView.ViewHolder{

    public TextView title,desc,location,cat;
    public TextView time;
    public CustomerHistoryLJViewHolders(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.histtitle);
        time = (TextView) itemView.findViewById(R.id.histtime);
        cat = (TextView) itemView.findViewById(R.id.histcat);
        location = (TextView) itemView.findViewById(R.id.histloc);
        desc = (TextView) itemView.findViewById(R.id.histdesc);

    }

}