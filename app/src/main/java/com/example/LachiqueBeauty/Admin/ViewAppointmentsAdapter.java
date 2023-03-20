package com.example.LachiqueBeauty.Admin;
import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.LachiqueBeauty.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class ViewAppointmentsAdapter extends RecyclerView.Adapter<ViewAppointmentsAdapter.MyViewHolder> {
    private List<DocumentSnapshot> mDataset;
    private DatabaseReference mDatabase;
    private String mUserId;




    public interface OnItemClickListener {
        void onButton1Click(int position);
        void onButton2Click(int position);
    }
    private static OnItemClickListener listener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }



    public ViewAppointmentsAdapter(List<DocumentSnapshot> myDataset) {
        mDataset = myDataset;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        DocumentSnapshot document = mDataset.get(position);
        String userId = document.getId();
        String service = document.getString("service");
        String date = document.getString("date");
        String time = document.getString("time");
        String nailist = document.getString("nailist");
        String price = document.getString("price");
        holder.mServiceView.setText(service);
        holder.mDateView.setText(date);
        holder.mTimeView.setText(time);
        holder.mNailistView.setText(nailist);
        holder.mPriceView.setText(price);


        mDatabase.child(userId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                holder.mNameView.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameView;
        public TextView mServiceView;
        public TextView mDateView;
        public TextView mTimeView;
        public TextView mNailistView;
        public TextView mPriceView;
        private OnItemClickListener mListener;
        Button complete, cancel;

        public MyViewHolder(View v) {
            super(v);
            mNameView = (TextView) v.findViewById(R.id.firstname);
            mServiceView = (TextView) v.findViewById(R.id.serviceId);
            mDateView = (TextView) v.findViewById(R.id.dateId);
            mTimeView = (TextView) v.findViewById(R.id.timeId);
            mNailistView = (TextView) v.findViewById(R.id.nailist);
            mPriceView = (TextView) v.findViewById(R.id.priceId);


            complete = itemView.findViewById(R.id.completeApp);
            cancel = itemView.findViewById(R.id.cancelID);
            mListener = listener;


            complete.setOnClickListener(new View.OnClickListener() {
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

            cancel.setOnClickListener(new View.OnClickListener() {
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
}
