package com.example.LachiqueBeauty.Admin;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.LachiqueBeauty.Login;
import com.example.LachiqueBeauty.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ViewAppointmentsAdm extends AppCompatActivity {
    private String userId;
    TextView countID, NailistSelected, monthSelected;
    private Button homebtn, logout, aboutus, filterbtn, Datebtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointments_adm);
        NailistSelected = findViewById(R.id.NailistSelected);
        monthSelected = findViewById(R.id.monthID);

        Datebtn = findViewById(R.id.DateSelectedId);
        Datebtn.setOnClickListener(new View.OnClickListener() {
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewAppointmentsAdm.this);
                        builder.setTitle("Select Nailist");
                        builder.setItems(nailistNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Prompt the user to select a year
                                final String[] years = {"2023", "2024", "2025", "2026", "2027", "2028"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(ViewAppointmentsAdm.this);
                                builder.setTitle("Select Year");
                                builder.setItems(years, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int whichYear) {
                                        // Update the query to retrieve documents for the selected nailist and month
                                        final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewAppointmentsAdm.this);
                                        builder.setTitle("Select Month");
                                        builder.setItems(months, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int whichMonth) {
                                                // Update the query to retrieve documents for the selected nailist and month
                                                String selectedNailist = nailistNames.get(which);
                                                String selectedYearStr = years[whichYear];
                                                int selectedYear = Integer.parseInt(selectedYearStr);
                                                // Months in Firestore are represented as integers from 1 to 12
                                                // Prompt the user to select a month
                                                int selectedMonth = whichMonth + 1; // Months in Firestore are represented as integers from 1 to 12
                                                String monthstr = months[whichMonth];
                                                filterByNailist(selectedNailist,selectedYear, selectedMonth);
                                                NailistSelected.setText(selectedNailist);
                                                monthSelected.setText(monthstr);
                                            }
                                        });
                                        builder.show();
                                    }
                                });
                                builder.show();
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


            private void filterByNailist(String nailistName, int year, int month) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Query query = db.collection("Appointments")
                        .whereEqualTo("nailist", nailistName).whereEqualTo("year", year).whereEqualTo("month", month);
                Toast.makeText(getApplicationContext(), "Loading successful!! " + month + year, Toast.LENGTH_LONG).show();
                // Get the documents and attach a listener
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        // Set up the RecyclerView with the adapter
                        RecyclerView recyclerView = findViewById(R.id.admin);
                        recyclerView.setHasFixedSize(true);
                        CompletedAppointmentAdapter adapter = new CompletedAppointmentAdapter(documents);
                        recyclerView.setAdapter(adapter);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(layoutManager);

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
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewAppointmentsAdm.this);
                        builder.setTitle("Select Nailist");
                        builder.setItems(nailistNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Update the query to retrieve documents for the selected nailist
                                String selectedNailist = nailistNames.get(which);
                                showDatePicker(selectedNailist);
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

            private void showDatePicker(String selectedNailist) {
                // Get the current date
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a date formatter with the desired format
                SimpleDateFormat sdf = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    sdf = new SimpleDateFormat("EEEE dd-MM-yyyy", Locale.getDefault());
                }

                // Create a date picker dialog with the formatted date
                SimpleDateFormat finalSdf = sdf;
                DatePickerDialog datePickerDialog = new DatePickerDialog(ViewAppointmentsAdm.this,
                        (view, yearSelected, monthSelected, dayOfMonthSelected) -> {
                            // Convert the selected date to the desired format
                            Calendar selectedCalendar = Calendar.getInstance();
                            selectedCalendar.set(yearSelected, monthSelected, dayOfMonthSelected);
                            String selectedDate = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                selectedDate = finalSdf.format(selectedCalendar.getTime());
                            }

                            // Filter appointments by selected nailist and date
                            filterByNailistAndDate(selectedNailist, selectedDate);
                        },
                        year, month, dayOfMonth);

                // Show the date picker dialog
                datePickerDialog.show();
            }


            private void filterByNailistAndDate(String nailistName, String selectedDate) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Query query = db.collection("Appointments")
                        .whereEqualTo("nailist", nailistName.toLowerCase())
                        .whereEqualTo("date", selectedDate);

                // Get the documents and attach a listener
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                        // Set up the RecyclerView with the adapter
                        RecyclerView recyclerView = findViewById(R.id.admin);
                        recyclerView.setHasFixedSize(true);
                        ViewAppointmentsAdapter adapter = new ViewAppointmentsAdapter(documents);
                        recyclerView.setAdapter(adapter);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(layoutManager);

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