package com.example.LachiqueBeauty.Users;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.LachiqueBeauty.Login;
import com.example.LachiqueBeauty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class viewAppointments extends AppCompatActivity {

   /* DatabaseReference studentDbRef;
    private MyAdapter adapter;
    private ArrayList<Booking> list;
    */
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private MyAdapter adapter;
    private List<Booking> list;
    private Button cancelAppointment, logout, settingsbtn;
    private TextView emptyView;
    FirebaseAuth fAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment_user);

        logout = findViewById(R.id.logoutbtn);
        settingsbtn = findViewById(R.id.settingsbtn);
        settingsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewAppointments.this, SettingsUser.class);
                startActivity(intent);
            }
        });


        fAuth = FirebaseAuth.getInstance();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                Toast.makeText(viewAppointments.this,"Successful Log Out",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(viewAppointments.this, Login.class));
                finish();

            }
        });

        ProgressDialog progressDialog = new ProgressDialog(viewAppointments.this);
        progressDialog.setTitle("Loading Appointments");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();



        if (getSupportActionBar() != null)  //remove top actionbar
        {
            getSupportActionBar().hide();
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(viewAppointments.this));

        db = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        cancelAppointment = findViewById(R.id.cancelAppointment);
        list = new ArrayList<>();
        adapter = new MyAdapter(viewAppointments.this,list);
        recyclerView.setAdapter(adapter);
        String id = fAuth.getCurrentUser().getUid();

        cancelAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(viewAppointments.this);
                builder.setTitle("Cancel Appointment");
                builder.setMessage("Are you sure you want to continue?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform positive action here
                        db.collection("Appointments").document(id)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(getIntent());
                                        overridePendingTransition(0, 0);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });
                    }
                });
                builder.setNegativeButton("No", null);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        CollectionReference collectionRef = FirebaseFirestore.getInstance().collection("Appointments");
        DocumentReference docRef = collectionRef.document(id);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        showData();
                        progressDialog.dismiss();
                        // document exists
                    } else {
                        // document does not exist
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(viewAppointments.this);
                        builder.setTitle("No Appointment booked");
                        builder.setMessage("Book a New Appointment with us");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Perform positive action here
                                startActivity(new Intent(viewAppointments.this,Appointment.class));
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Perform positive action here
                                startActivity(new Intent(viewAppointments.this,Home.class));
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();



                    }
                } else {
                    // handle error
                    Toast.makeText(viewAppointments.this,"OOPS something went wrong",Toast.LENGTH_SHORT).show();
                }
            }
        });







        showData();


    }
    private void showData(){
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String id = fAuth.getCurrentUser().getUid();
        db.collection("Appointments").whereEqualTo("id",id).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        for(DocumentSnapshot snapshot : task.getResult()){
                            Booking book = new Booking(snapshot.getString("id"),snapshot.getString("date"),snapshot.getString("time"),snapshot.getString("nailist"), snapshot.getString("service"));
                            list.add(book);

                        }
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(viewAppointments.this,"OOPS something went wrong",Toast.LENGTH_SHORT).show();

            }
        });
    }

}
