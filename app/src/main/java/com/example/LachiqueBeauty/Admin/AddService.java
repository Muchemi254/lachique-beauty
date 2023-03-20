package com.example.LachiqueBeauty.Admin;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.LachiqueBeauty.Login;
import com.example.LachiqueBeauty.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddService extends AppCompatActivity {
    //Variables
    TextInputLayout service_Name,service_price;
    Button saveBtn;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    private FirebaseAuth auth;
    private static final int PICK_IMAGE_REQUEST = 2;
    private Uri imageUri;
    private Button homebtn, logout, aboutus, usersAdmin;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);
        //Hooks to all xml elements in activity_sign_up.xml
        ImageButton uploadButton = findViewById(R.id.upload_image);
        service_Name = findViewById(R.id.srv_name);
        service_price = findViewById(R.id.srv_price);
        saveBtn = findViewById(R.id.save_btn);
        auth = FirebaseAuth.getInstance();
        usersAdmin = findViewById(R.id.userAdminId);


        aboutus = findViewById(R.id.aboutUpdatebtn);
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddService.this, AboutUsUpdate.class);
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
                Toast.makeText(AddService.this,"Successful Log Out",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddService.this, Login.class));
                finish();
            }
        });
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddService.this, AdminHome.class);
                startActivity(intent);
                finish();
            }
        });
        usersAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(AddService.this, AdminUsersActivity.class);
            startActivity(intent);
        });


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String service = service_Name.getEditText().getText().toString().toLowerCase().trim();
                String price = service_price.getEditText().getText().toString().toLowerCase().trim();
                if (TextUtils.isEmpty(service)) {
                    Toast.makeText(getApplicationContext(),"Please enter Service Name!!",Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(price)) {
                    Toast.makeText(getApplicationContext(), "Please enter price!!",Toast.LENGTH_LONG).show();
                    return;
                }
                // Open the device's gallery to select an image
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        // upload image



        //Save data in FireBase on button click
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                String service = service_Name.getEditText().getText().toString().toLowerCase().trim();
                ProgressDialog progressDialog = new ProgressDialog(AddService.this);
                progressDialog.setTitle("Uploading");
                progressDialog.setMessage("please wait...");
                progressDialog.setCancelable(false);



                if (TextUtils.isEmpty(service)) {
                    Toast.makeText(getApplicationContext(),"Please enter Service Name!!",Toast.LENGTH_LONG).show();
                    return;
                }
                // Create a reference to the image location in Firebase Storage
                StorageReference imageRef = storageRef.child("service_images/" + service);

                if (imageUri== null){
                    Toast.makeText(getApplicationContext(), "No photo selected!!",Toast.LENGTH_LONG).show();
                } else {

                    // Upload the image to Firebase Storage
                    UploadTask uploadTask = imageRef.putFile(imageUri);
                    progressDialog.show();

                    // Get the download URL of the image
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return imageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                // Get the download URL of the image
                                Uri downloadUri = task.getResult();
                                String service = service_Name.getEditText().getText().toString().toLowerCase().trim();
                                if (service != null) {
                                    // Save the download URL to the Realtime Database
                                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("Services").child(service);
                                    databaseRef.child("image").setValue(downloadUri.toString());
                                    progressDialog.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AddService.this);
                                    builder.setTitle("Successful");
                                    builder.setMessage("Service saved");
                                    builder.setIcon(R.drawable.ic_baseline_cloud_done_24);
                                    builder.setCancelable(true);
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AddService.this);
                                    // Set the message show for the Alert time
                                    builder.setTitle("Upload Failed");
                                    builder.setCancelable(true);
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }


                            } else {
                                // Handle the error
                            }
                        }
                    });
                    progressDialog.dismiss();
                    NewService();
                }
            }
        });//Register Button method end


    }//onCreate Method End
    @Override
    public void onBackPressed() {
        // Call the finish() method to close the activity
        finish();
    }



    // Handle the result of the image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Get the selected image URI and display a preview
            imageUri = data.getData();
            // TODO: Display preview of selected image (optional)
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

// Display the image in the ImageView
            ImageView imageView = findViewById(R.id.image_preview);
            imageView.setImageBitmap(bitmap);
        }
    }



    private void NewService() {
        DatabaseReference servicePricesRef = FirebaseDatabase.getInstance().getReference().child("Services");
        String service = service_Name.getEditText().getText().toString().toLowerCase().trim();
        String price = service_price.getEditText().getText().toString().toLowerCase().trim();

        // Validations for input email and password
        if (TextUtils.isEmpty(service)) {
            Toast.makeText(getApplicationContext(),"Please enter Service Name!!",Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(price)) {
            Toast.makeText(getApplicationContext(), "Please enter price!!",Toast.LENGTH_LONG).show();
            return;
        }
        ServiceHelperClass helperClass = new ServiceHelperClass( service,price);
        Map<String, Object> updates = helperClass.toMap();
        servicePricesRef.child(service).updateChildren(updates);
        Toast.makeText(getApplicationContext(), "Service Added",Toast.LENGTH_LONG).show();
    }



    public static class ServiceHelperClass {
        String service, price;
        public ServiceHelperClass(String service, String price) {
            this.service = service;
            this.price = price;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("service", service);
            result.put("price", price);
            return result;
        }
    }

}



