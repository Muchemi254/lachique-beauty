package com.example.LachiqueBeauty.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LachiqueBeauty.R;
import com.example.LachiqueBeauty.Users.ServiceUserAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.MyViewHolder>{
    Context context;
    ArrayList<AdminUserClass> list;

    public AdminUserAdapter(Context context, ArrayList<AdminUserClass> list) {
        this.context = context;
        this.list = list;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, phoneNo;
        Button book;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameAdminUserID);
            email = itemView.findViewById(R.id.emailAdminUserID);
            phoneNo = itemView.findViewById(R.id.phoneNoAdminUserID);
        }
    }

    @NonNull
    @Override
    public AdminUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_users, parent, false);
        return new AdminUserAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserAdapter.MyViewHolder holder, int position) {
        AdminUserClass adminUserClass = list.get(position);
        holder.name.setText(adminUserClass.getName());
        holder.email.setText(adminUserClass.getEmail());
        holder.phoneNo.setText(adminUserClass.getPhoneNo());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

