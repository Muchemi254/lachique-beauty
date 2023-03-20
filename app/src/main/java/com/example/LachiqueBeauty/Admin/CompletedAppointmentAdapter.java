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

public class CompletedAppointmentAdapter extends RecyclerView.Adapter<CompletedAppointmentAdapter.MyViewHolder> {
    private List<DocumentSnapshot> mDataset;
    private DatabaseReference mDatabase;
    private String mUserId;






    public interface OnItemClickListener {
    }
    private static CompletedAppointmentAdapter.OnItemClickListener listener;


    public CompletedAppointmentAdapter(List<DocumentSnapshot> myDataset) {
        mDataset = myDataset;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");


    }

    @Override
    public CompletedAppointmentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_complete_item, parent, false);
        CompletedAppointmentAdapter.MyViewHolder viewHolder = new CompletedAppointmentAdapter.MyViewHolder(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CompletedAppointmentAdapter.MyViewHolder holder, int position) {
        DocumentSnapshot document = mDataset.get(position);

        String userId = document.getString("id");
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
        public TextView mPriceView;
        public TextView mNailistView;
        private CompletedAppointmentAdapter.OnItemClickListener mListener;

        public MyViewHolder(View v) {
            super(v);
            mNameView = (TextView) v.findViewById(R.id.firstnameCA);
            mServiceView = (TextView) v.findViewById(R.id.serviceIdCA);
            mDateView = (TextView) v.findViewById(R.id.dateIdCA);
            mTimeView = (TextView) v.findViewById(R.id.timeIdCA);
            mPriceView = (TextView) v.findViewById(R.id.priceIdCA);
            mNailistView = (TextView) v.findViewById(R.id.nailistCA);

            mListener = listener;
        }
    }
}
