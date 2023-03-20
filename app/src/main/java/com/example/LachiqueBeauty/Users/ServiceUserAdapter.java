package com.example.LachiqueBeauty.Users;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LachiqueBeauty.Admin.ViewServiceClass;
import com.example.LachiqueBeauty.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ServiceUserAdapter extends RecyclerView.Adapter<ServiceUserAdapter.MyViewHolder>{

    Context context;
    ArrayList<ViewServiceClass> list;

    public ServiceUserAdapter(Context context, ArrayList<ViewServiceClass> list) {
        this.context = context;
        this.list = list;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView service, price;
        Button book;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.service_image);
            service = itemView.findViewById(R.id.srvUserID);
            price = itemView.findViewById(R.id.prcUserID);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_services_users, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ViewServiceClass viewServiceClass = list.get(position);
        Picasso.get().load(viewServiceClass.getImage()).into(holder.image);
        holder.service.setText(viewServiceClass.getService());
        holder.price.setText(viewServiceClass.getPrice());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
