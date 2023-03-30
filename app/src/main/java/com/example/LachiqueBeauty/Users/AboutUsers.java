package com.example.LachiqueBeauty.Users;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.LachiqueBeauty.Login;
import com.example.LachiqueBeauty.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class AboutUsers extends AppCompatActivity {
    private ViewPager2 viewPager;
    private ImageAdapter imageAdapter;
    Button servicesAbtbtn, logout, aboutus, settingsbtn;
    private Handler handler;
    private int delay = 3000; // 3 seconds
    private int page = 0;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_users);


            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            logout = findViewById(R.id.logoutbtn);
            aboutus = findViewById(R.id.aboutbtn);


            aboutus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AboutUsers.this, AboutUsers.class);
                    startActivity(intent);
                }
            });
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fAuth.signOut();
                    Toast.makeText(AboutUsers.this,"Successful Log Out",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AboutUsers.this, Login.class));
                    finish();

                }
            });

            settingsbtn = findViewById(R.id.settingsbtn);
            settingsbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AboutUsers.this, SettingsUser.class);
                    startActivity(intent);
                }
            });

        servicesAbtbtn = findViewById(R.id.servicesabout);
        servicesAbtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutUsers.this, Services.class);
                startActivity(intent);
            }
        });


            viewPager = findViewById(R.id.view_pager);
            getImageItems();
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (page < imageAdapter.getItemCount() - 1) {
                        viewPager.setCurrentItem(page++);
                    } else {
                        viewPager.setCurrentItem(0);
                        page = 0;
                    }
                    handler.postDelayed(this, delay);
                }
            }, delay);



        }
    private void getImageItems() {
        List<String> imageUrls = new ArrayList<>();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("service_images");
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrls.add(uri.toString());
                            if (imageUrls.size() == listResult.getItems().size()) {
                                imageAdapter = new ImageAdapter(imageUrls);
                                viewPager.setAdapter(imageAdapter);

                            }
                        }
                    });
                }
            }
        });
    }
}
