package com.example.LachiqueBeauty.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LachiqueBeauty.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewServiceAdmAdapter extends RecyclerView.Adapter<ViewServiceAdmAdapter.MyViewHolder>{

    Context context;
    ArrayList<ViewServiceClass> list;

    public void update(int position, String newPrice) {
        ViewServiceClass viewServiceClass = list.get(position);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Services").child(viewServiceClass.getService());
        reference.child("price").setValue(newPrice);
        viewServiceClass.setPrice(newPrice); // Update the price in the ViewServiceClass object
        notifyItemChanged(position);
    }


    public void delete(int position) {

        ViewServiceClass viewServiceClass = list.get(position);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        if (viewServiceClass != null && viewServiceClass.getService() != null) {
            ref.child("Services").child(viewServiceClass.getService()).removeValue();
            list.remove(position);
            StorageReference imageRef = storageRef.child("service_images/" + viewServiceClass.getService());
            imageRef.delete();

            Toast.makeText(context, "Service Deleted", Toast.LENGTH_SHORT).show();

            notifyItemRemoved(position);
        }

    }


    public interface OnItemClickListener {
        void onButton1Click(int position);
        void onButton2Click(int position);
    }
    private static ViewServiceAdmAdapter.OnItemClickListener listener;
    public void setOnItemClickListener(ViewServiceAdmAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }


    public ViewServiceAdmAdapter(Context context, ArrayList<ViewServiceClass> list) {
        this.context = context;
        this.list = list;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView service, price;
        Button update, delete, book;
        private ViewServiceAdmAdapter.OnItemClickListener mListener;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            service = itemView.findViewById(R.id.srvID);
            price = itemView.findViewById(R.id.prcID);
            update = itemView.findViewById(R.id.updateID);
            delete = itemView.findViewById(R.id.deleteID);
            //book = itemView.findViewById(R.id.BookID);
            mListener = listener;


            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onButton1Click(position);
                        }
                    }
                }
            });

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onButton2Click(position);
                        }
                    }
                }
            });


        }
    }


    @NonNull
    @Override
    public ViewServiceAdmAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewServiceAdmAdapter.MyViewHolder holder, int position) {
        ViewServiceClass viewServiceClass = list.get(position);
        holder.service.setText(viewServiceClass.getService());
        holder.price.setText(viewServiceClass.getPrice());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
