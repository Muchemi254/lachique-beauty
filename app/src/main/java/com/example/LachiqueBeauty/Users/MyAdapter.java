package com.example.LachiqueBeauty.Users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LachiqueBeauty.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private viewAppointments activity;
    private List<Booking> mList;



    public MyAdapter(viewAppointments activity, List<Booking> mList){
        this.mList = mList;
        this.activity = activity;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {

        holder.date.setText(mList.get(position).getDate());
        holder.time.setText(mList.get(position).getTime());
        holder.nailist.setText(mList.get(position).getNailist());
        holder.service.setText(mList.get(position).getService());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
       TextView date,time,nailist, service;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date_text);
            time = itemView.findViewById(R.id.time_text);
            nailist = itemView.findViewById(R.id.nailist_text);
            service = itemView.findViewById(R.id.service_text);
        }
    }
}
