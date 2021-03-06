package com.quickjobs.quickjobs_freelancercustomers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class OTPVerificationActivityTwo extends AppCompatActivity {
    private Button sendVerificationButton,VerifyButton;
    private EditText InputPhoneNumber,InputVerificationCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog loadingBar;
    CountryCodePicker ccp;
    Pinview pinview1;
    String verificationCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification_two);

        mAuth = FirebaseAuth.getInstance();
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        pinview1= (Pinview) findViewById(R.id.pinview1);
        sendVerificationButton = (Button)findViewById(R.id.send_verification_button);
        VerifyButton = (Button)findViewById(R.id.verify_button);
        InputPhoneNumber = (EditText)findViewById(R.id.phone_number_input);
//        InputVerificationCode = (EditText)findViewById(R.id.verification_code_input);
        loadingBar = new ProgressDialog(this);
        ccp.registerCarrierNumberEditText(InputPhoneNumber);



        pinview1.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                verificationCode = pinview.getValue();
            }
        });




        sendVerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String phoneNumber = InputPhoneNumber.getText().toString();
                phoneNumber = "+91" + phoneNumber;
                if (TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(OTPVerificationActivityTwo.this,"Phone number is required",Toast.LENGTH_SHORT);
                }else {

                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Rolling up soon...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    Intent i = new Intent(OTPVerificationActivityTwo.this, FreelancerMapsActivity.class);
                    i.putExtra("phone", "99");

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            OTPVerificationActivityTwo.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks

                }
            }
        });



        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationButton.setVisibility(View.INVISIBLE);
                ccp.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);
                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(OTPVerificationActivityTwo.this,"Empty Code",Toast.LENGTH_SHORT);

                }else {
                    loadingBar.setTitle("Code Verification");
                    loadingBar.setMessage("Rolling up soon...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(OTPVerificationActivityTwo.this,"Invalid Code",Toast.LENGTH_SHORT);
                sendVerificationButton.setVisibility(View.VISIBLE);
                InputPhoneNumber.setVisibility(View.VISIBLE);
                ccp.setVisibility(View.VISIBLE);

                VerifyButton.setVisibility(View.INVISIBLE);
                pinview1.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {

                mVerificationId = verificationId;
                mResendToken = token;

                loadingBar.dismiss();
                Toast.makeText(OTPVerificationActivityTwo.this,"Code have been sent to your mobile",Toast.LENGTH_SHORT);
                sendVerificationButton.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);

                VerifyButton.setVisibility(View.VISIBLE);
                pinview1.setVisibility(View.VISIBLE);
                ccp.setVisibility(View.INVISIBLE);
            }
        };
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(OTPVerificationActivityTwo.this,"You are in...",Toast.LENGTH_SHORT);

                            String uidd =  FirebaseAuth.getInstance().getCurrentUser().getUid();


                            FirebaseDatabase.getInstance().getReference().child("Users").child("Freelancers").child(uidd).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()<3){

                                        startActivity(new Intent(OTPVerificationActivityTwo.this,FreelancerProfileRegisterActivity.class));
                                        finish();
                                    }else {
                                        SendUserToMainActivity();
                                        finish();
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });





                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(OTPVerificationActivityTwo.this,message,Toast.LENGTH_SHORT);


                        }
                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(OTPVerificationActivityTwo.this,FreelancerMapsActivity.class);
        startActivity(mainIntent);
        finish();
    }


}



