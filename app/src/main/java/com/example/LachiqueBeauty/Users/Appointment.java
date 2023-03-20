package com.example.LachiqueBeauty.Users;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.LachiqueBeauty.Login;
import com.example.LachiqueBeauty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.commons.lang3.text.WordUtils;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Calendar;

public class Appointment extends AppCompatActivity {

    EditText date_in;
    TextView textViewTime;
    Spinner spinnerNailist;
    Spinner spinnerService;
    Spinner spinnerTime;
    Button btnInsertData,buttonTime, logout;
    FirebaseAuth fAuth;

    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);


        logout = findViewById(R.id.logoutbtn);


        fAuth = FirebaseAuth.getInstance();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                Toast.makeText(Appointment.this,"Successful Log Out",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Appointment.this, Login.class));
                finish();

            }
        });


        if (getSupportActionBar() != null)  //remove top actionbar
        {
            getSupportActionBar().hide();
        }
        String textViewValue = getIntent().getStringExtra("textViewValue");
        List<String> services = new ArrayList<>();
        List<String> nailists = new ArrayList<>();
        date_in = findViewById(R.id.date_input);
        spinnerTime = findViewById(R.id.spinnerTime);
        btnInsertData = findViewById(R.id.book_btn);
        spinnerService = findViewById(R.id.spinnerService);
        spinnerNailist = findViewById(R.id.spinnerNailist);

        DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference().child("Services");
        servicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get a list of services from the dataSnapshot
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot serviceSnapshot : dataSnapshot.getChildren()) {
                        String service = serviceSnapshot.child("service").getValue(String.class);
                        service = WordUtils.capitalize(service);
                        services.add(service);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Appointment.this);
                    builder.setTitle("Alert");
                    builder.setMessage("No Services Available at the moment");
                    builder.setCancelable(false);
                    builder.setCancelable(true);
                    AlertDialog dialog =builder.create();
                    dialog.show();
                }

                // Set up the spinner with the list of services
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Appointment.this, android.R.layout.simple_spinner_item, services);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerService.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
        DatabaseReference nailistRef = FirebaseDatabase.getInstance().getReference().child("admin");
        nailistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get a list of services from the dataSnapshot
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot serviceSnapshot : dataSnapshot.getChildren()) {
                        String nailist = serviceSnapshot.child("name").getValue(String.class);
                        nailist = WordUtils.capitalize(nailist);
                        nailists.add(nailist);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Appointment.this);
                    builder.setTitle("Alert");
                    builder.setMessage("No Nailist Available at the moment");
                    builder.setCancelable(false);
                    builder.setCancelable(true);
                    AlertDialog dialog =builder.create();
                    dialog.show();
                }

                // Set up the spinner with the list of services
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Appointment.this, android.R.layout.simple_spinner_item, nailists);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerNailist.setAdapter(adapter);
                spinnerNailist.setSelection(0, false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
                Toast.makeText(Appointment.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });







        db = FirebaseFirestore.getInstance();
        btnInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerService.getCount() == 0) {
                    Toast.makeText(getApplicationContext(), "Services can,t be Empty",
                            Toast.LENGTH_LONG).show();
                } else if (spinnerNailist.getCount() == 0) {
                    Toast.makeText(getApplicationContext(), "Nailist can,t be Empty",
                            Toast.LENGTH_LONG).show();
                } else {
                    FirebaseAuth fAuth = FirebaseAuth.getInstance();
                    String id = fAuth.getCurrentUser().getUid();
                    String date = date_in.getText().toString().trim();
                    String time = spinnerTime.getSelectedItem().toString().trim().toLowerCase();
                    String nailist = spinnerNailist.getSelectedItem().toString().trim().toLowerCase();
                    String service = spinnerService.getSelectedItem().toString().trim().toLowerCase();


                    if (TextUtils.isEmpty(date)) {
                        date_in.setError("Add a Appointment Date");
                        Toast.makeText(Appointment.this, "Date Can't be Empty", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(Appointment.this);
                        builder.setTitle("Invalid Date");
                        builder.setMessage("Date can't be Empty");
                        builder.setCancelable(true);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return;
                    }

                    ProgressDialog progressDialog = new ProgressDialog(Appointment.this);
                    progressDialog.setTitle("Checking Availability");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    // Initialize Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // Get the current user's ID
                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();


                    // Create a variable to check if a match is found
                    final boolean[] matchFound = {false};

                    // Create a query to get all documents in the "Appointments" collection
                    db.collection("Appointments").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            // Check if the current document is not the current user's document
                                            if (!document.getId().equals(currentUserId)) {
                                                // Get the values of "nailist", "date", and "time" from the document
                                                String inputNailist = document.getString("nailist");
                                                String inputDate = document.getString("date");
                                                String inputTime = document.getString("time");
                                                // Compare the input strings with the values from the document


                                                if (inputNailist != null && inputNailist.equals(nailist) && inputDate != null && inputDate.equals(date) && inputTime != null && inputTime.equals(time)) {
                                                    // The input strings match the values from the document
                                                    matchFound[0] = true;
                                                    break;
                                                }
                                            }
                                        }
                                        // Check if a match was found
                                        if (matchFound[0]) {
                                            // A match was found, show a toast message
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Match found!", Toast.LENGTH_SHORT).show();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Appointment.this);
                                            builder.setTitle(nailist + " already booked on " + date + " at " + time);
                                            builder.setMessage("Try another Date or Time");
                                            builder.setNegativeButton("Try Again", null);
                                            AlertDialog dialog = builder.create();
                                            dialog.show();
                                        } else {
                                            // No match was found, show a different toast message
                                            progressDialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Appointment.this);
                                            builder.setTitle("Confirm Details");
                                            builder.setCancelable(false);

                                            LinearLayout layout = new LinearLayout(Appointment.this);
                                            layout.setOrientation(LinearLayout.VERTICAL);
                                            int margin = 20;
                                            layout.setPadding(margin, 0, margin, 0);

                                            String[] titles = {"Date:", "Time:", "Service:", "Nailist:"};
                                            String[] variables = {date, time, service, nailist};

                                            for (int i = 0; i < titles.length; i++) {
                                                TextView textView = new TextView(Appointment.this);
                                                textView.setText(titles[i] + " " + variables[i]);
                                                layout.addView(textView);
                                            }

                                            builder.setView(layout);

                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // Save the details including price


                                                    Timestamp myTimestamp = null;
                                                    try {
                                                        // Define the date format and time format
                                                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd-MM-yyyy");
                                                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

// Parse the date and time strings into Date objects
                                                        // Parse the date and time strings into Date objects
                                                        Date dateStamp = dateFormat.parse(date);
                                                        Date timeStamp = timeFormat.parse(time);

// Combine the date and time into a single Date object
                                                        Calendar calendar = Calendar.getInstance();
                                                        calendar.setTime(dateStamp);
                                                        calendar.set(Calendar.HOUR_OF_DAY, timeStamp.getHours());
                                                        calendar.set(Calendar.MINUTE, timeStamp.getMinutes());
                                                        calendar.set(Calendar.SECOND, timeStamp.getSeconds());
                                                        Date timestamp = calendar.getTime();

// Convert the Date object to a Firestore Timestamp object
                                                        Timestamp fbTimestamp = new Timestamp(timestamp);
                                                        // Assign the Firestore Timestamp to the variable declared outside of the try block
                                                        myTimestamp = fbTimestamp;

                                                    } catch (ParseException e) {
                                                        // Handle the exception here
                                                        e.printStackTrace();
                                                    }
                                                    // Use the variable outside of the try block
                                                    if (myTimestamp != null) {
                                                        // The variable is not null, so it can be used
                                                        // Get a reference to the Firebase database
                                                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                                        String servicedb = service.toLowerCase();

// Get a reference to the child node
                                                        DatabaseReference childRef = database.child("Services").child(servicedb).child("price");


// Attach a listener to read the value of the child field
                                                        Timestamp finalMyTimestamp = myTimestamp;
                                                        childRef.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                // Get the value of the child field
                                                                String price = dataSnapshot.getValue(String.class);


                                                                saveToFirestore(id, date, time, nailist, service, finalMyTimestamp, price);
                                                                Toast.makeText(Appointment.this,price,Toast.LENGTH_SHORT).show();
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError error) {
                                                                // Handle errors
                                                            }
                                                        });

                                                    }


                                                }
                                            });

                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });

                                            builder.show();
                                        }
                                    } else {
                                        // Handle error
                                    }
                                }
                            });

                    // check for staff availability


                }
            }
        });


        date_in.setInputType(InputType.TYPE_NULL);


        date_in.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showDateDialog(date_in);
            }
        });
        }



    private void saveToFirestore(String id, String date, String time, String nailist, String service, Timestamp myTimestamp, String price){
        if(!date.isEmpty() && !time.isEmpty()){
            HashMap<String,Object>map = new HashMap<>();
            map.put("id",id);
            map.put("date",date);
            map.put("time",time);
            map.put("nailist",nailist);
            map.put("service",service);
            map.put("myTimestamp", myTimestamp);
            map.put("price",price);


            db.collection("Appointments").document(id).set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Appointment.this,"Congrats!, your appointment is booked successfully",Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(Appointment.this);
                                builder.setTitle("Booking Succesful");
                                builder.setMessage("Go to Main Page");
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Save the details
                                        Intent intent = new Intent(Appointment.this, Home.class);
                                        startActivity(intent);
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Appointment.this,"Appointment booking Failed",Toast.LENGTH_SHORT).show();
                }
            });

        }
        else {
            Toast.makeText(this,"Empty Field not allowed ",Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDateDialog(final EditText date_in){
       final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Calendar today = Calendar.getInstance();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.SUNDAY) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Appointment.this);
                    builder.setTitle("Lachhiq is Closed on a sunday");
                    builder.setMessage("Try a week day or Saturday");
                    builder.setNegativeButton("Try Again", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else if (calendar.before(today) || calendar.equals(today)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Appointment.this);
                    builder.setTitle("Date Error");
                    builder.setMessage("Select a Date in the future");
                    builder.setNegativeButton("Try Again", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    String dayOfWeekStr = new DateFormatSymbols().getWeekdays()[dayOfWeek];
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    String date = simpleDateFormat.format(calendar.getTime());
                    date_in.setText(dayOfWeekStr + " " + date);
                }
            }


        };

          new DatePickerDialog(Appointment.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }
}
