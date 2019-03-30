package com.quickjobs.quickjobs_freelancercustomers.Adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.firebase.database.FirebaseDatabase;
import com.quickjobs.quickjobs_freelancercustomers.Objects.FreelancerHistoryLJObject;
import com.quickjobs.quickjobs_freelancercustomers.R;

import com.quickjobs.quickjobs_freelancercustomers.ViewHolders.FreelancerHistoryLJViewHolders;


import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class FreelancerHistoryLJAdapter extends RecyclerView.Adapter<FreelancerHistoryLJViewHolders> {

    private List<FreelancerHistoryLJObject> itemList;
    private Context context;

    public FreelancerHistoryLJAdapter(List<FreelancerHistoryLJObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public FreelancerHistoryLJViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.free_history_local_jobs_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        FreelancerHistoryLJViewHolders rcv = new FreelancerHistoryLJViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(FreelancerHistoryLJViewHolders holder, final int position) {

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String phno = itemList.get(position).getPhno();

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phno));
                context.startActivity(intent);
                return false;
            }
        });

        holder.title.setText(itemList.get(position).getTitle());
        holder.desc.setText(itemList.get(position).getDesc());
        holder.location.setText(itemList.get(position).getLocation());
        holder.cat.setText(itemList.get(position).getCat());
        if(itemList.get(position).getTime()!=null){
            holder.time.setText(itemList.get(position).getTime());
        }
    }
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

}