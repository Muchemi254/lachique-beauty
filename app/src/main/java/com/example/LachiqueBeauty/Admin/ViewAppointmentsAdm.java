package com.example.LachiqueBeauty.Admin;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.LachiqueBeauty.Login;
import com.example.LachiqueBeauty.R;
import com.example.LachiqueBeauty.Users.Home;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewAppointmentsAdm extends AppCompatActivity {
    private String userId;
    TextView countID;
    private Button homebtn, logout, aboutus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        aboutus = findViewById(R.id.aboutUpdatebtn);
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewAppointmentsAdm.this, AboutUsUpdate.class);
                startActivity(intent);
            }
        });
        homebtn = findViewById(R.id.homebtn);
        logout = findViewById(R.id.logoutbtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                Toast.makeText(ViewAppointmentsAdm.this,"Successful Log Out",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewAppointmentsAdm.this, Login.class));
                finish();
            }
        });
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewAppointmentsAdm.this, AdminHome.class);
                startActivity(intent);
                finish();
            }
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("Appointments");

        //get count of number of appointments
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int count = queryDocumentSnapshots.size();
                // update TextView with count
                countID = findViewById(R.id.countID);
                countID.setText(String.valueOf(count));
                ProgressDialog progressDialog = new ProgressDialog(ViewAppointmentsAdm.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Loading Appointments...");
                progressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 2000);
                Toast.makeText(getApplicationContext(), "Loading successful!!", Toast.LENGTH_LONG).show();
                if (countID== null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ViewAppointmentsAdm.this);
                    builder.setTitle("No Appointments Booked");
                    builder.setIcon(R.drawable.ic_baseline_event_note_24);
                    // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                    builder.setCancelable(true);
                    builder.setPositiveButton("Home", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ViewAppointmentsAdm.this, AdminHome.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    });
                    // Create the Alert dialog
                    AlertDialog alertDialog = builder.create();
                    // Show the Alert Dialog box
                    alertDialog.show();


                }
            }
        });






        //sort appointments by days
        Query query = collectionRef.orderBy("myTimestamp", Query.Direction.DESCENDING);


        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                List<String> userIds = new ArrayList<>();

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    userId = documentSnapshot.getId();
                    userIds.add(userId);
                }

                RecyclerView recyclerView = findViewById(R.id.admin);
                recyclerView.setHasFixedSize(true);
                ViewAppointmentsAdapter adapter = new ViewAppointmentsAdapter(documents);
                recyclerView.setAdapter(adapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);



                adapter.setOnItemClickListener(new ViewAppointmentsAdapter.OnItemClickListener() {
                    @Override
                    public void onButton1Click(int position) {
                        // Handle button1 click event for the item at the given position
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference appointmentRef = db.collection("Appointments").document(userId);

                        // Retrieve the appointment details from Firestore
                        appointmentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    // Create a new document in the "completed appointments" collection
                                    db.collection("CompletedAppointments").add(documentSnapshot.getData())
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(getApplicationContext(), "Task successful!!", Toast.LENGTH_LONG).show();
                                                    // Delete the appointment details from the original collection
                                                    appointmentRef.delete();
                                                    finish();
                                                    startActivity(getIntent());
                                                }
                                            });
                                }
                            }
                        });

                    }

                    @Override
                    public void onButton2Click(int position) {
                        // Handle button2 click event for the item at the given position
                        // Handle button1 click event for the item at the given position
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference appointmentRef = db.collection("Appointments").document(userId);

                        // Retrieve the appointment details from Firestore
                        appointmentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    appointmentRef.delete();
                                    finish();
                                    startActivity(getIntent());
                                }
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error getting documents: ", e);
            }
        });



    }

    @Override
    public void onBackPressed() {
        // Call the finish() method to close the activity
        finish();
    }
}