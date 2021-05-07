package com.blooddonation.be_donor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SinglePostActivity extends AppCompatActivity {

    private TextView singlePostTitle,singlePostDesc;
    private ImageView singlePostImage;
    private ProgressBar singlePostProgress;

    private String blogId;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        Toolbar singlePostToolbar = findViewById(R.id.single_post_Toolbar);
        setSupportActionBar(singlePostToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        blogId = getIntent().getStringExtra("POST_ID");

        singlePostTitle = findViewById(R.id.single_post_title);
        singlePostDesc = findViewById(R.id.single_post_desc);
        singlePostImage = findViewById(R.id.single_post_image);
        singlePostProgress = findViewById(R.id.single_post_progress);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();




        if(firebaseAuth.getCurrentUser() != null){
            getData();
    }


    }


    //Retrive data from firestore

    public void getData(){
        singlePostProgress.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("Posts").document(blogId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {

                        String title = task.getResult().getString("title");
                        String desc = task.getResult().getString("desc");
                        String imageUrl = task.getResult().getString("image_url");

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.post_img);
                        Glide.with(SinglePostActivity.this).setDefaultRequestOptions(placeholderRequest).load(imageUrl).into(singlePostImage);

                        singlePostTitle.setText(title);
                        singlePostDesc.setText(desc);
                    }

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(SinglePostActivity.this, "Firestore Retrive Error : " + error, Toast.LENGTH_SHORT).show();
                }

                singlePostProgress.setVisibility(View.INVISIBLE);

            }
        });

    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

    }
}
