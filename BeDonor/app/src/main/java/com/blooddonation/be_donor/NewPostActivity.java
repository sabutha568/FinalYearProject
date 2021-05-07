package com.blooddonation.be_donor;

import android.content.Intent;
import android.net.Uri;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;




public class NewPostActivity extends AppCompatActivity {

    private Toolbar newPostToolbar;
    private ImageView newPostImage;
    private EditText newPostTitle,newPostDesc;
    private Button newPostBtn;
    private ProgressBar newPostProgress;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;





    private Uri postImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);


        newPostToolbar = findViewById(R.id.new_post_toolbar);

        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newPostImage = findViewById(R.id.new_post_image);
        newPostTitle = findViewById(R.id.new_post_title);
        newPostDesc = findViewById(R.id.new_post_desc);
        newPostBtn = findViewById(R.id.post_btn);
        newPostProgress = findViewById(R.id.new_post_progress);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id =firebaseAuth.getCurrentUser().getUid();


        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(3,2)
                        .start(NewPostActivity.this);
            }
        });


        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newPostBtn.setEnabled(false);

            final String title = newPostTitle.getText().toString();
            final String desc = newPostDesc.getText().toString();

            if(!TextUtils.isEmpty(title.trim()) && !TextUtils.isEmpty(desc.trim()) && postImageUri != null ){

                newPostProgress.setVisibility(View.VISIBLE);

                final String randomName = UUID.randomUUID().toString();

                final StorageReference filePath = storageReference.child("post_images").child(randomName + ".jpg");


                final UploadTask uploadTask = filePath.putFile(postImageUri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            String downloadUri = task.getResult().toString();
                            String title_lower = title.toLowerCase();



                            Map<String,Object> postMap = new HashMap<>();
                            postMap.put("image_url",downloadUri);
                            postMap.put("title",title);
                            postMap.put("title_lower",title_lower);
                            postMap.put("desc",desc);
                            postMap.put("user_id",current_user_id);
                            postMap.put("timestamp",FieldValue.serverTimestamp());


                            firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {

                                    if(task.isSuccessful()){

                                        Toast.makeText(NewPostActivity.this, "Post added successfully", Toast.LENGTH_SHORT).show();
                                        Intent mainIntent = new Intent(NewPostActivity.this,MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();

                                    }else{
                                        String error = task.getException().getMessage();
                                        newPostBtn.setEnabled(true);
                                        Toast.makeText(NewPostActivity.this, "Image Error : " + error, Toast.LENGTH_SHORT).show();
                                    }
                                    newPostProgress.setVisibility(View.INVISIBLE);
                                }
                            });





                        } else {
                            // Handle failures
                            // ...
                            String error = task.getException().getMessage();
                            newPostBtn.setEnabled(true);
                            Toast.makeText(NewPostActivity.this, "Image Error : " + error, Toast.LENGTH_SHORT).show();
                            newPostProgress.setVisibility(View.INVISIBLE);
                        }

                    }
                });



            }else{
                newPostBtn.setEnabled(true);
                Toast.makeText(NewPostActivity.this, "All Fields are required", Toast.LENGTH_SHORT).show();
            }


            }
        });


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
