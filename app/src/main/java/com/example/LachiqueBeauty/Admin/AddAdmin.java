package com.example.LachiqueBeauty.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.LachiqueBeauty.Login;
import com.example.LachiqueBeauty.R;
import com.example.LachiqueBeauty.Users.Home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddAdmin extends AppCompatActivity {
    //Variables
    TextInputLayout admregName,admregEmail, admregPhoneNo, admregPassword;
    Button admregBtn;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    private FirebaseAuth auth;
    private Button homebtn, logout, aboutus, usersAdmin;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);
        //Hooks to all xml elements in activity_sign_up.xml
        admregName = findViewById(R.id.admreg_name);
        admregEmail = findViewById(R.id.admreg_email);
        usersAdmin = findViewById(R.id.userAdminId);
        admregPhoneNo = findViewById(R.id.admreg_phoneNo);
        admregPassword = findViewById(R.id.admreg_password);
        admregBtn = findViewById(R.id.admreg_btn);
        auth = FirebaseAuth.getInstance();
        //Save data in FireBase on button click

        aboutus = findViewById(R.id.aboutUpdatebtn);
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAdmin.this, AboutUsUpdate.class);
                startActivity(intent);
            }
        });
        homebtn = findViewById(R.id.homebtn);
        logout = findViewById(R.id.logoutbtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Toast.makeText(AddAdmin.this,"Successful Log Out",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddAdmin.this, Login.class));
                finish();
            }
        });
        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAdmin.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
        usersAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(AddAdmin.this, AdminUsersActivity.class);
            startActivity(intent);
        });
        admregBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewUser();
            }
        });//Register Button method end


    }//onCreate Method End
    @Override
    public void onBackPressed() {
        // Call the finish() method to close the activity
        finish();
    }


    private void registerNewUser() {
        String email = admregEmail.getEditText().getText().toString();
        String password = admregPassword.getEditText().getText().toString();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),"Please enter email!!",Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!!",Toast.LENGTH_LONG).show();
            return;
        }


        // validate if user exists
        // create new user or register new user
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(getApplicationContext(), "ViewAppointmentsAdm Added successful!", Toast.LENGTH_LONG).show();

                                // save data into database
                                savedata();

                            } else {
                                // Registration failed
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddAdmin.this);

                                // Set the message show for the Alert time
                                builder.setMessage(task.getException().getMessage());

                                // Set Alert Title
                                builder.setTitle("Registration Failed");

                                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                                builder.setCancelable(true);


                                // Create the Alert dialog
                                AlertDialog alertDialog = builder.create();
                                // Show the Alert Dialog box
                                alertDialog.show();
                            }
                        }
                    });


    }
    private void savedata() {
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("admin");
        //Get all the values
        String name = admregName.getEditText().getText().toString().toLowerCase();
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = admregEmail.getEditText().getText().toString();
        String phoneNo = admregPhoneNo.getEditText().getText().toString();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),"Please enter email!!",Toast.LENGTH_LONG).show();
            return;
        }
        UserHelperClass helperClass = new UserHelperClass( email,name, phoneNo);
        reference.child(currentuser).setValue(helperClass);
        finish();

    }

    public static class UserHelperClass {
        String email, name,  phoneNo;
        public UserHelperClass(String email, String name,String phoneNo) {
            this.email = email;
            this.name = name;
            this.phoneNo = phoneNo;

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNo() {
            return phoneNo;
        }

        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }


    }

}



