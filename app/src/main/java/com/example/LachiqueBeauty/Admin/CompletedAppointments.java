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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.LachiqueBeauty.Login;
import com.example.LachiqueBeauty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompletedAppointments extends AppCompatActivity {
    private String userId;
    Button filterbtn;
    TextView countID, totalprice, NailistSelected, NailistCommission;
    private Button homebtn, logout, aboutus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_appointments);

        ProgressDialog progressDialog = new ProgressDialog(CompletedAppointments.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading Appointments...");
        progressDialog.show();

        NailistSelected = findViewById(R.id.NailistSelected);
        NailistCommission = findViewById(R.id.NailistSelectedCommision);

        aboutus = findViewById(R.id.aboutUpdatebtn);
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompletedAppointments.this, AboutUsUpdate.class);
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
                Toast.makeText(CompletedAppointments.this,"Successful Log Out",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CompletedAppointments.this, Login.class));
                finish();
            }
        });
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompletedAppointments.this, AdminHome.class);
                startActivity(intent);
                finish();
            }
        });

        filterbtn = findViewById(R.id.filterID);
        filterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the dialog with a list of all nailists
                showNailistDialog();
            }

            private void showNailistDialog() {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("admin");
                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> nailistNames = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String name = dataSnapshot.child("name").getValue(String.class).toLowerCase();
                            nailistNames.add(name);
                        }
                        // Show the dialog with the list of nailists
                        AlertDialog.Builder builder = new AlertDialog.Builder(CompletedAppointments.this);
                        builder.setTitle("Select Nailist");
                        builder.setItems(nailistNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Update the query to retrieve documents for the selected nailist
                                String selectedNailist = nailistNames.get(which);
                                filterByNailist(selectedNailist);
                                NailistSelected.setText(selectedNailist);
                            }
                        });
                        builder.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle any errors
                    }
                });
            }

            private void filterByNailist(String nailistName) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Query query = db.collection("CompletedAppointments").whereEqualTo("nailist", nailistName.toLowerCase());

                // Get the documents and attach a listener
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        // Set up the RecyclerView with the adapter
                        RecyclerView recyclerView = findViewById(R.id.completeAdminRecycler);
                        recyclerView.setHasFixedSize(true);
                        CompletedAppointmentAdapter adapter = new CompletedAppointmentAdapter(documents);
                        recyclerView.setAdapter(adapter);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(layoutManager);

                        double sum = 0;
                        double commission = 0.0;
                        for (DocumentSnapshot document : documents) {
                            String priceString = document.getString("price");
                            double price = Double.parseDouble(priceString);
                            sum += price;
                        }
                        commission = sum * 0.2;

                        TextView totalprice = findViewById(R.id.totalAmountID);
                        totalprice.setText(String.valueOf(sum));
                        NailistCommission.setText("Commission:  " + String.valueOf(commission));

                        int count = documents.size();
                        // update TextView with count
                        countID = findViewById(R.id.countID);
                        countID.setText(String.valueOf(count));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }




            });



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("CompletedAppointments");

// Initialize a variable to hold the sum of all values
        final AtomicDouble sum = new AtomicDouble(0);

// Query all documents in the collection
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Loop through all documents in the query result
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String valueString = document.getString("price");
                        if (valueString != null && !valueString.isEmpty()) {
                            double price = Double.parseDouble(valueString);
                            sum.getAndAdd(price);
                            totalprice = findViewById(R.id.totalAmountID);
                            totalprice.setText(String.valueOf(sum));
                        }

                    }

                    // Print out the sum
                } else {
                    System.out.println("Error getting documents: " + task.getException());
                }
            }
        });


        //get count of number of appointments
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int count = queryDocumentSnapshots.size();
                // update TextView with count
                countID = findViewById(R.id.countID);
                countID.setText(String.valueOf(count));
                Toast.makeText(getApplicationContext(), "Loading successful!!", Toast.LENGTH_LONG).show();
                if (countID== null){
                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(CompletedAppointments.this);
                    builder.setTitle("No Completed Appointments");
                    builder.setIcon(R.drawable.ic_baseline_event_note_24);
                    // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                    builder.setCancelable(true);
                    builder.setPositiveButton("Home", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(CompletedAppointments.this, AdminHome.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    });
                    // Create the Alert dialog
                    AlertDialog alertDialog = builder.create();
                    // Show the Alert Dialog box
                    alertDialog.show();


                } else {
                    progressDialog.dismiss();
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

                RecyclerView recyclerView = findViewById(R.id.completeAdminRecycler);
                recyclerView.setHasFixedSize(true);
                CompletedAppointmentAdapter adapter = new CompletedAppointmentAdapter(documents);
                recyclerView.setAdapter(adapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error getting documents: ", e);
            }
        });
    }
}