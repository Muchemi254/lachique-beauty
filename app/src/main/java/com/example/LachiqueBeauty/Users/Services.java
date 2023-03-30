package com.example.LachiqueBeauty.Users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.LachiqueBeauty.Admin.ViewServiceAdmAdapter;
import com.example.LachiqueBeauty.Admin.ViewServiceClass;
import com.example.LachiqueBeauty.Login;
import com.example.LachiqueBeauty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.BreakIterator;
import java.util.ArrayList;

public class Services extends AppCompatActivity {

    RecyclerView recyclerView;
    private Button logout, aboutus,settingsbtn;
    DatabaseReference databaseReference;
    ServiceUserAdapter ServiceUserAdapter;
    ArrayList<ViewServiceClass> list;
    private FirebaseAuth fAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        logout = findViewById(R.id.logoutbtn);
        settingsbtn = findViewById(R.id.settingsbtn);
        aboutus = findViewById(R.id.aboutbtn);
        settingsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Services.this, SettingsUser.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
            }
        });
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Services.this, AboutUsers.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
            }
        });


        fAuth = FirebaseAuth.getInstance();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                Toast.makeText(Services.this,"Successful Log Out",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Services.this, Login.class));
                finish();

            }
        });



        recyclerView = findViewById(R.id.srvUserRecycler);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        databaseReference = FirebaseDatabase.getInstance().getReference("Services");
        recyclerView.setHasFixedSize(true);

        list = new ArrayList<>();
        ServiceUserAdapter = new ServiceUserAdapter(this,list);

        recyclerView.setAdapter(ServiceUserAdapter);

        ServiceUserAdapter.setOnItemClickListener(new ServiceUserAdapter.OnItemClickListener() {
            @Override
            public void onButton1Click(int position) {
                Intent intent = new Intent(Services.this, Appointment.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);
            }
        });






        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ViewServiceClass viewServiceClass = dataSnapshot.getValue(ViewServiceClass.class);
                    list.add(viewServiceClass);
                }
                ServiceUserAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Picasso.get().setLoggingEnabled(true);






    }



    @Override
    public void onBackPressed() {
        // Call the finish() method to close the activity
        finish();
    }

}

