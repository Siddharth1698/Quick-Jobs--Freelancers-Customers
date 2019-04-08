package com.quickjobs.quickjobs_freelancercustomers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class CustomerLoginActivity extends AppCompatActivity {
    private EditText mEmail,mPassword;
    private Button mLogin,mRegister,phlogin;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;
    private FirebaseUser user;
    private String email,password,uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        auth = FirebaseAuth.getInstance();

        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){
                    Intent intent = new Intent(CustomerLoginActivity.this,CustomerMapsActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CALL_PHONE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_NETWORK_STATE).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();


        mEmail = (EditText)findViewById(R.id.emailCustomer);
        mPassword = (EditText) findViewById(R.id.passwordCustomer);
        mLogin = (Button)findViewById(R.id.loginCustomerBtn);
        mRegister = (Button)findViewById(R.id.registerCustomerBtn);
        phlogin = (Button)findViewById(R.id.phonelogin);




        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();

                if (email.isEmpty() && password.isEmpty() || email.isEmpty() || password.isEmpty()){

                    Toast.makeText(CustomerLoginActivity.this,"Please enter details",Toast.LENGTH_SHORT).show();
                }else {

                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                uid = auth.getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance()
                                        .getReference().child("Users").child("Customers").child(uid);
                                current_user_db.setValue(true);
                                Toast.makeText(CustomerLoginActivity.this,"Succesfully Registered",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CustomerLoginActivity.this, CustomerProfileRegistrationActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(CustomerLoginActivity.this,"Error signing in",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }
        });

        phlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomerLoginActivity.this,OTPVerificationActivity.class));
                return;
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                if (email.isEmpty() && password.isEmpty() || email.isEmpty() || password.isEmpty()){
                    Toast.makeText(CustomerLoginActivity.this,"Please enter details",Toast.LENGTH_SHORT).show();
                }else {
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(CustomerLoginActivity.this,"Succesfully Signed in",Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(CustomerLoginActivity.this,"Error signing in",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }}
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(firebaseAuthListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(firebaseAuthListner);
    }

}