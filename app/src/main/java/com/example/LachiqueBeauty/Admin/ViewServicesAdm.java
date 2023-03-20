package com.example.LachiqueBeauty.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.LachiqueBeauty.Login;
import com.example.LachiqueBeauty.R;
import com.example.LachiqueBeauty.Users.Services;
import com.example.LachiqueBeauty.Users.viewAppointments;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewServicesAdm extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ViewServiceAdmAdapter viewServiceAdmAdapter;
    ArrayList<ViewServiceClass> list;
    private Button homebtn, logout, aboutus;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_adm);

        aboutus = findViewById(R.id.aboutUpdatebtn);
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewServicesAdm.this, AboutUsUpdate.class);
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
                Toast.makeText(ViewServicesAdm.this,"Successful Log Out",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewServicesAdm.this, Login.class));
                finish();
            }
        });
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewServicesAdm.this, AdminHome.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        recyclerView = findViewById(R.id.srvRecycler);
        databaseReference = FirebaseDatabase.getInstance().getReference("Services");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        viewServiceAdmAdapter = new ViewServiceAdmAdapter(this,list);

        recyclerView.setAdapter(viewServiceAdmAdapter);



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ViewServiceClass viewServiceClass = dataSnapshot.getValue(ViewServiceClass.class);
                    list.add(viewServiceClass);
                }
                viewServiceAdmAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        viewServiceAdmAdapter.setOnItemClickListener(new ViewServiceAdmAdapter.OnItemClickListener() {
            @Override
            public void onButton1Click(int position) {
                viewServiceAdmAdapter.delete(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewServicesAdm.this);
                builder.setMessage("Deleted");
                final AlertDialog alert = builder.create();
                alert.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alert.dismiss();
                        finish();
                        overridePendingTransition( 0, 0);
                    }
                }, 3000);

                overridePendingTransition( 0, 0);
                startActivity(getIntent());
                overridePendingTransition( 0, 0);
            }
            @Override
            public void onButton2Click(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewServicesAdm.this);
                builder.setTitle("Enter new price");

                final EditText input = new EditText(ViewServicesAdm.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPrice = input.getText().toString();
                        viewServiceAdmAdapter.update(position, newPrice);
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewServicesAdm.this);
                        builder.setTitle("Price updated");
                        final AlertDialog alert = builder.create();
                        alert.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alert.dismiss();
                                finish();
                                overridePendingTransition( 0, 0);
                            }
                        }, 3000);

                        overridePendingTransition( 0, 0);
                        startActivity(getIntent());
                        overridePendingTransition( 0, 0);
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

        });



    }
    @Override
    public void onBackPressed() {
        // Call the finish() method to close the activity
        finish();
    }

}

