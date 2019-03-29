package com.quickjobs.quickjobs_freelancercustomers.ViewHolders;

import android.view.View;
import android.widget.TextView;

import com.quickjobs.quickjobs_freelancercustomers.R;

import androidx.recyclerview.widget.RecyclerView;

public class FreelancerHistoryLJViewHolders  extends RecyclerView.ViewHolder{

    public TextView title,desc,location,cat;
    public TextView time;
    public FreelancerHistoryLJViewHolders(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.histtitle);
        time = (TextView) itemView.findViewById(R.id.histtime);
        cat = (TextView) itemView.findViewById(R.id.histcat);
        location = (TextView) itemView.findViewById(R.id.histloc);
        desc = (TextView) itemView.findViewById(R.id.histdesc);

    }

}