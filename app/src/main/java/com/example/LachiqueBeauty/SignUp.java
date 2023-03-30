package com.example.LachiqueBeauty;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    //Variables
    TextInputLayout regName, regUsername, regEmail, regPhoneNo, regPassword;
    Button regBtn;
    TextView regToLoginBtn;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //Hooks to all xml elements in activity_sign_up.xml
        regName = findViewById(R.id.reg_name);
        regUsername = findViewById(R.id.reg_username);
        regEmail = findViewById(R.id.reg_email);
        regPhoneNo = findViewById(R.id.reg_phoneNo);
        regPassword = findViewById(R.id.reg_password);
        regBtn = findViewById(R.id.reg_btn);
        auth = FirebaseAuth.getInstance();
        regToLoginBtn = findViewById(R.id.reg_login_btn);
        //Save data in FireBase on button click
        regToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });



        regBtn.setOnClickListener(new View.OnClickListener() {
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
        String email = regEmail.getEditText().getText().toString();
        String password = regPassword.getEditText().getText().toString();
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
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
        Matcher matcher = pattern.matcher(password);

        if (matcher.matches()) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();

                        // save data into database
                        savedata();


                        // if the user created intent to login activity

                        Intent intent = new Intent(SignUp.this, Login.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // Registration failed
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);

                        // Set the message show for the Alert time
                        builder.setMessage(task.getException().getMessage());

                        // Set Alert Title
                        builder.setTitle("Login Failed");

                        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                        builder.setCancelable(true);


                        // Create the Alert dialog
                        AlertDialog alertDialog = builder.create();
                        // Show the Alert Dialog box
                        alertDialog.show();
                    }
                }
            });

            // Password meets the requirements
        } else {

            // Password does not meet the requirements
            // Show an error message to the user
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error")
                    .setMessage("Password must be at least 8 characters long and contain at least one digit, one letter, and one special character")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Do nothing, just close the dialog
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        
        // validate if user exists
        // create new user or register new user


    }
    private void savedata() {
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        //Get all the values
        String name = regName.getEditText().getText().toString();
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String username = regUsername.getEditText().getText().toString();
        String email = regEmail.getEditText().getText().toString();
        String phoneNo = regPhoneNo.getEditText().getText().toString();
        String password = regPassword.getEditText().getText().toString();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),"Please enter email!!",Toast.LENGTH_LONG).show();
            return;
        }
        UserHelperClass helperClass = new UserHelperClass(name, username, email, phoneNo);
        reference.child(currentuser).setValue(helperClass);

    }

    public static class UserHelperClass {
        String name, username, email, phoneNo;
        public UserHelperClass(String name, String username, String email, String phoneNo) {
            this.name = name;
            this.username = username;
            this.email = email;
            this.phoneNo = phoneNo;

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
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

