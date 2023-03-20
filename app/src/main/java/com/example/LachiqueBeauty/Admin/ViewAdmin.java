package com.example.LachiqueBeauty.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.LachiqueBeauty.Login;
import com.example.LachiqueBeauty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAdmin extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ViewAdminAdapter viewAdminAdapter;
    ArrayList<ViewAdminClass> list;
    private Button homebtn, logout, aboutus;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_admin);

        aboutus = findViewById(R.id.aboutUpdatebtn);
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewAdmin.this, AboutUsUpdate.class);
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
                Toast.makeText(ViewAdmin.this,"Successful Log Out",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ViewAdmin.this, Login.class));
                finish();
            }
        });
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewAdmin.this, AdminHome.class);
                startActivity(intent);
                finish();
            }
        });

        recyclerView = findViewById(R.id.admRecycler);
        databaseReference = FirebaseDatabase.getInstance().getReference("admin");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        viewAdminAdapter = new ViewAdminAdapter(this,list);

        recyclerView.setAdapter(viewAdminAdapter);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ViewAdminClass viewAdminClass = dataSnapshot.getValue(ViewAdminClass.class);
                    list.add(viewAdminClass);
                }
                viewAdminAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        viewAdminAdapter.setOnItemClickListener(new ViewAdminAdapter.OnItemClickListener() {
            @Override
            public void onButton1Click(int position) {
                viewAdminAdapter.delete(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewAdmin.this);
                builder.setMessage("Deleted");
                final AlertDialog alert = builder.create();
                alert.show();
                overridePendingTransition( 0, 0);
                startActivity(getIntent());
                overridePendingTransition( 0, 0);
            }

            @Override
            public void onButton2Click(int position) {

            }

        });

    }
}