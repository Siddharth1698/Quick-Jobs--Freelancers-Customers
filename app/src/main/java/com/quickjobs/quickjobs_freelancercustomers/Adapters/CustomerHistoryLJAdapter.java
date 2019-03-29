package com.quickjobs.quickjobs_freelancercustomers.Adapters;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.quickjobs.quickjobs_freelancercustomers.CustomerMapsActivity;
import com.quickjobs.quickjobs_freelancercustomers.Objects.CustomerHistoryLJObject;
import com.quickjobs.quickjobs_freelancercustomers.Objects.HistoryObject;
import com.quickjobs.quickjobs_freelancercustomers.R;
import com.quickjobs.quickjobs_freelancercustomers.ViewHolders.CustomerHistoryLJViewHolders;
import com.quickjobs.quickjobs_freelancercustomers.ViewHolders.HistoryViewHolders;

import java.util.List;
import java.util.zip.Inflater;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class CustomerHistoryLJAdapter  extends RecyclerView.Adapter<CustomerHistoryLJViewHolders> {

    private List<CustomerHistoryLJObject> itemList;
    private Context context;
    View layoutView;

    public CustomerHistoryLJAdapter(List<CustomerHistoryLJObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public CustomerHistoryLJViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_history_local_jobs_item, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        CustomerHistoryLJViewHolders rcv = new CustomerHistoryLJViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final CustomerHistoryLJViewHolders holder, final int position) {

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String location = itemList.get(position).getLocation();
                String category = itemList.get(position).getCat();
                String ridekey = itemList.get(position).getRidekey();
                Toast.makeText(context,"hello",Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference().child("LocalJobsHistory").child(ridekey).removeValue();
                FirebaseDatabase.getInstance().getReference().child("LocalJobs").child(location).child(category).child(ridekey).removeValue();

                itemList.remove(position);
                notifyItemRemoved(position);
                return false;
            }
        });


        holder.title.setText(itemList.get(position).getTitle());
        holder.desc.setText(itemList.get(position).getDesc());
        holder.status.setText(itemList.get(position).getStatus());
        holder.location.setText(itemList.get(position).getLocation());
        holder.cat.setText(itemList.get(position).getCat());
        if (itemList.get(position).getTime() != null) {
            holder.time.setText(itemList.get(position).getTime());
        }
    }



    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

}