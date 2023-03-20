package com.example.LachiqueBeauty.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.LachiqueBeauty.Login;
import com.example.LachiqueBeauty.R;
import com.example.LachiqueBeauty.Users.Home;
import com.example.LachiqueBeauty.Users.Services;
import com.example.LachiqueBeauty.Users.viewAppointments;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHome extends AppCompatActivity {

    private Button view_appId, viewAdmin, addAdm, addService, viewService, completedbtn, usersAdmin;
    private Button userHome, logout, aboutus;
    private TextView regHere;
    FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        auth = FirebaseAuth.getInstance();
        completedbtn = findViewById(R.id.completeID);
        usersAdmin = findViewById(R.id.userAdminId);
        aboutus = findViewById(R.id.aboutUpdatebtn);
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, AboutUsUpdate.class);
                startActivity(intent);
            }
        });
        userHome = findViewById(R.id.userHomebtn);
        logout = findViewById(R.id.logoutbtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Toast.makeText(AdminHome.this,"Successful Log Out",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AdminHome.this, Login.class));
                finish();
            }
        });
        userHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, Home.class);
                startActivity(intent);
            }
        });
        usersAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHome.this, AdminUsersActivity.class);
            startActivity(intent);
        });

        completedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, CompletedAppointments.class);
                startActivity(intent);
            }
        });


        view_appId = findViewById(R.id.view_appId);
        viewAdmin = findViewById(R.id.viewAdnin);
        addAdm = findViewById(R.id.addAdm);
        addService = findViewById(R.id.addService);
        viewService = findViewById(R.id.viewService);


        viewAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, ViewAdmin.class);
                startActivity(intent);
            }
        });


        viewService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, ViewServicesAdm.class);
                startActivity(intent);
            }
        });

        addService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, AddService.class);
                startActivity(intent);
            }
        });

        addAdm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, AddAdmin.class);
                startActivity(intent);
            }
        });

        //view all upcoming appointments
        view_appId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, ViewAppointmentsAdm.class);
                startActivity(intent);
            }
        });
        //view completed appointments
        view_appId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, ViewAppointmentsAdm.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        // Call the finish() method to close the activity
        finish();
    }
}