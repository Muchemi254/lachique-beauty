package com.example.LachiqueBeauty.Admin;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LachiqueBeauty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewAdminAdapter extends RecyclerView.Adapter<ViewAdminAdapter.MyViewHolder>{

    Context context;
    ArrayList<ViewAdminClass> list;

    public void delete(int position) {
        ViewAdminClass viewAdminClass = list.get(position);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (viewAdminClass != null) {
            Query query = FirebaseDatabase.getInstance().getReference().child("admin").orderByChild("email").equalTo(viewAdminClass.getEmail());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String adminId = dataSnapshot.getChildren().iterator().next().getKey();
                        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("admin").child(adminId);
                        adminRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // delete user from Firebase Authentication
                                    mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                list.remove(position);
                                                Toast.makeText(context, "Admin Deleted", Toast.LENGTH_SHORT).show();
                                                notifyItemRemoved(position);
                                            } else {
                                                Log.e(TAG, "Failed to delete user from Firebase Authentication", task.getException());
                                            }
                                        }
                                    });
                                } else {
                                    Log.e(TAG, "Failed to delete admin from Firebase Realtime Database", task.getException());
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });
        }
    }



    public interface OnItemClickListener {
        void onButton1Click(int position);
        void onButton2Click(int position);

    }
    private static ViewAdminAdapter.OnItemClickListener listener;
    public void setOnItemClickListener(ViewAdminAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public ViewAdminAdapter(Context context, ArrayList<ViewAdminClass> list) {
        this.context = context;
        this.list = list;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, phoneNo;
        Button update, delete, book;
        private ViewAdminAdapter.OnItemClickListener mListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameID);
            email = itemView.findViewById(R.id.emailID);
            phoneNo = itemView.findViewById(R.id.phoneNoID);
            delete = itemView.findViewById(R.id.deleteAdminViewID);
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


        }
    }




    @NonNull
    @Override
    public ViewAdminAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_admin, parent, false);
        return new ViewAdminAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAdminAdapter.MyViewHolder holder, int position) {
        ViewAdminClass viewAdminClass = list.get(position);
        holder.name.setText(viewAdminClass.getName());
        holder.email.setText(viewAdminClass.getEmail());
        holder.phoneNo.setText(viewAdminClass.getPhoneNo());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
