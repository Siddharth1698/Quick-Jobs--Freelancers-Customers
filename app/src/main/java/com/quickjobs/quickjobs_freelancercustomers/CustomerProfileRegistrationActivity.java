package com.quickjobs.quickjobs_freelancercustomers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerProfileRegistrationActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private CircleImageView profileImage;
    private ProgressDialog loadingBar;
    private EditText profilePhone;
    private EditText profileName,profileAddress;
    private Button createAccountButton;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private static int galleryPic = 1;
    private FirebaseUser user;
    private StorageReference UserProfileImageRef;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;
    private FirebaseAuth auth;
    private String currentUserId;
    private CircleImageView prof_reg_pic;
    private String imageurl;
    private String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile_registration);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profile_images");

        createAccountButton = (Button)findViewById(R.id.profile_register_button);
//        myToolbar = findViewById(R.id.my_profile_toolbar);
//        if (myToolbar!=null) {
//            setSupportActionBar(myToolbar);
//            getSupportActionBar().setTitle("Register");
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            myToolbar.setNavigationIcon(R.drawable.back_btn);
//            myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    onBackPressed();
//                }
//            });
//
//        }

        profileImage = (CircleImageView) findViewById(R.id.profile_register_picture);
        profileName = (EditText)findViewById(R.id.profile_register_name);
        profileAddress = (EditText)findViewById(R.id.profile_register_address);
        profilePhone = (EditText) findViewById(R.id.profile_register_phone);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,galleryPic);
            }
        });



        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });



    }




    private void CreateNewAccount() {

        if (TextUtils.isEmpty(profilePhone.getText().toString())){
            Toast.makeText(this,"Feild Cannot be empty",Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(profileAddress.getText().toString())){
            Toast.makeText(this,"Feild Cannot be empty",Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(profileName.getText().toString())){
            Toast.makeText(this,"Feild Cannot be empty",Toast.LENGTH_SHORT);
        }
        else{



            String currentUserId = mAuth.getCurrentUser().getUid();

            HashMap<String,String> profileMap = new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("name",profileName.getText().toString());
            profileMap.put("phone",profilePhone.getText().toString());
            profileMap.put("address",profileAddress.getText().toString());
            profileMap.put("profileImageUrl",imageurl);


            RootRef.child("Users").child("Customers").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Intent registerIntent = new Intent(CustomerProfileRegistrationActivity.this,CustomerMapsActivity.class);
                        startActivity(registerIntent);
                        Toast.makeText(CustomerProfileRegistrationActivity.this,"Account Created Succesfully",Toast.LENGTH_SHORT);

                    }else {
                        String error = task.getException().toString();
                        Toast.makeText(CustomerProfileRegistrationActivity.this,error,Toast.LENGTH_SHORT);
                    }
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        prof_reg_pic = ((CircleImageView) findViewById(R.id.profile_register_picture));
        if (requestCode==galleryPic && resultCode==RESULT_OK && data!=null){
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {


                final Uri resultUri = result.getUri();
                StorageReference filepath = UserProfileImageRef.child(currentUserId + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CustomerProfileRegistrationActivity.this, "Image uploaded succesfully...", Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            RootRef.child("Users").child("Customers").child(currentUserId).child("profileImageUrl").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(CustomerProfileRegistrationActivity.this, "Image saved in db succesfully", Toast.LENGTH_SHORT).show();
                                        ((CircleImageView) findViewById(R.id.profile_register_picture)).setImageURI(resultUri);
                                        RootRef.child("Users").child("Customers").child(currentUserId).child("profileImageUrl").setValue(downloadUrl);
                                        Picasso.get().load(resultUri).into(prof_reg_pic);
                                        imageurl = downloadUrl;


                                    } else {
                                        Toast.makeText(CustomerProfileRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        } else {
                            Toast.makeText(CustomerProfileRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();


                        }
                    }
                });
            }}
    }


}
