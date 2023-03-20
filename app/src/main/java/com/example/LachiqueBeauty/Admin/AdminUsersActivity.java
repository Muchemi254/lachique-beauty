package com.example.LachiqueBeauty.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.LachiqueBeauty.Login;
import com.example.LachiqueBeauty.R;
import com.example.LachiqueBeauty.Users.Home;
import com.example.LachiqueBeauty.Users.Services;
import com.example.LachiqueBeauty.Users.viewAppointments;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdminUsersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    AdminUserAdapter AdminUserAdapter;
    ArrayList<AdminUserClass> list;
    private Button homebtn, logout, aboutus;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        aboutus = findViewById(R.id.aboutUpdatebtn);
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminUsersActivity.this, AboutUsUpdate.class);
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
                Toast.makeText(AdminUsersActivity.this,"Successful Log Out",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AdminUsersActivity.this, Login.class));
                finish();
            }
        });
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminUsersActivity.this, AdminHome.class);
                startActivity(intent);
            }
        });

        fAuth = FirebaseAuth.getInstance();



        recyclerView = findViewById(R.id.AdminUserRecyclerID);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AdminUsersActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        recyclerView.setHasFixedSize(true);

        list = new ArrayList<>();
        AdminUserAdapter = new AdminUserAdapter(this,list);

        recyclerView.setAdapter(AdminUserAdapter);




        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    AdminUserClass adminUserClass = dataSnapshot.getValue(AdminUserClass.class);
                    list.add(adminUserClass);
                }
                AdminUserAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Picasso.get().setLoggingEnabled(true);
    }
}