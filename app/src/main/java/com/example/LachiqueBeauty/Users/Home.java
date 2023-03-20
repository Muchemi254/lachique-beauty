package com.example.LachiqueBeauty.Users;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.LachiqueBeauty.Login;
import com.example.LachiqueBeauty.R;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    Button btn1,btn2,btn3, logout, aboutus, settings;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        logout = findViewById(R.id.logoutbtn);


        fAuth = FirebaseAuth.getInstance();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                Toast.makeText(Home.this,"Successful Log Out",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Home.this, Login.class));
                finish();

            }
        });
        //tool bar menu




        if (getSupportActionBar() != null)  //remove top actionbar
        {
            getSupportActionBar().hide();
        }


        btn1.setOnClickListener(v -> {
            Toast.makeText(Home.this,"Book an Appointment with our Nailist",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Home.this,Appointment.class));
        });
        btn2.setOnClickListener(v -> {
            Toast.makeText(Home.this,"Professional Based Services provided",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Home.this,Services.class));
        });
        btn3.setOnClickListener(v -> {
            Toast.makeText(Home.this,"Booking Details are present here",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Home.this, viewAppointments.class));
        });

    }




}
