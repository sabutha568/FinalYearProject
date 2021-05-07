package com.blooddonation.be_donor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SetupActivity extends AppCompatActivity {


    private ImageView setupImage;
    private Uri mainImageURI = null;
    private EditText setupName, setupNumber , setupBlood_group;
    private Button setupBtn;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private ProgressBar setupProgress;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;

    private boolean isChanged = false;
    private String user_name, user_number, user_blood_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Setup");


        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        setupProgress = findViewById(R.id.setup_progress);

        setupProgress.setVisibility(View.VISIBLE);
        setupBtn = findViewById(R.id.setup_btn);
        setupBtn.setEnabled(false);

        setupImage = findViewById(R.id.setup_image);
        setupName = findViewById(R.id.setup_name);
        setupNumber = findViewById( R.id.setup_number );
        setupBlood_group = findViewById( R.id.setup_blood_group );

        user_id = Objects.requireNonNull( firebaseAuth.getCurrentUser() ).getUid();


        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                if(task.isSuccessful()){

                    if(task.getResult().exists()){

                        String name = task.getResult().getString("name");
                        String number = task.getResult().getString("number");
                        String blood_group = task.getResult().getString("blood group");
                        String image = task.getResult().getString("image");

                        mainImageURI = Uri.parse(image);

                        setupName.setText(name);
                        setupNumber.setText(number);
                        setupBlood_group.setText(blood_group);

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.user);
                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);


                    }


                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "Firestore Retrive Error : "+error, Toast.LENGTH_SHORT).show();
                }

                setupProgress.setVisibility(View.INVISIBLE);
                setupBtn.setEnabled(true);


            }
        });





        //for btn
        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setupBtn.setEnabled(false);

                final String user_name = setupName.getText().toString().trim();
                final String user_number = setupNumber.getText().toString().trim();
                final String user_blood_group = setupBlood_group.getText().toString().trim();
                if (!TextUtils.isEmpty(user_name) && mainImageURI != null) {
                setupProgress.setVisibility(View.VISIBLE);

                if(isChanged) {


                        final StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                        UploadTask uploadTask = image_path.putFile(mainImageURI);

                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return image_path.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {

                                    storeFirestore(task, user_name);
                                    storeFirestore(task, user_number);
                                    storeFirestore(task, user_blood_group);

                                } else {
                                    // Handle failures
                                    // ...
                                    String error = task.getException().getMessage();
                                    setupBtn.setEnabled(true);
                                    Toast.makeText(SetupActivity.this, "Image Error : " + error, Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    } else {

                    storeFirestore(null, user_name);
                    storeFirestore( null, user_number);
                    storeFirestore( null, user_blood_group);
                    }


                }else{
                    setupBtn.setEnabled(true);
                    Toast.makeText(SetupActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }


            }
        });


        //for image
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(SetupActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(SetupActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }else{
                       BringImagePicker();
                    }

                }else{
                    BringImagePicker();
                }



            }
        });
    }

    private void storeFirestore(Task<Uri> task,String user_name) {

        Uri downloadUri;

        if(task != null) {
            downloadUri = task.getResult();
        }else{

            downloadUri = mainImageURI;

        }
        Map<String,String> userMap = new HashMap<>();
        userMap.put("name",user_name);
        userMap.put("number",user_number);
        userMap.put("blood_group",user_blood_group);

        userMap.put("image",downloadUri.toString());
        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SetupActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    Intent mainIntent =new Intent(SetupActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "Firestore Error : "+error, Toast.LENGTH_SHORT).show();
                    setupProgress.setVisibility(View.INVISIBLE);
                }
                setupProgress.setVisibility(View.INVISIBLE);
            }
        });
    }


    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3,2)
                .start(SetupActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                setupImage.setImageURI(mainImageURI);

                isChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
