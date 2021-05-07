package com.blooddonation.be_donor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditBlogActivity extends AppCompatActivity {

    private TextView edit_blog_title,edit_blog_desc;
    private Button edit_blog_btn;
    private ProgressBar editBlogProgress;

    private String blogId;

    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post );

        blogId = getIntent().getStringExtra("POST_ID");


        Toolbar edit_blog_toolbar = findViewById(R.id.edit_blog_toolbar);
        setSupportActionBar(edit_blog_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edit_blog_title = findViewById(R.id.edit_blog_title);
        edit_blog_desc = findViewById(R.id.edit_blog_desc);
        edit_blog_btn = findViewById(R.id.edit_blog_btn);
        editBlogProgress = findViewById(R.id.edit_blog_progress);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getData();

        edit_blog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_blog_btn.setEnabled(false);
                editBlogProgress.setVisibility(View.VISIBLE);
                updateData();

            }
        });


    }

    private void updateData() {


        String title = edit_blog_title.getText().toString();
        String desc = edit_blog_desc.getText().toString();


        if(title.trim().equals("")){
            editBlogProgress.setVisibility(View.INVISIBLE);
            Toast.makeText(EditBlogActivity.this, "Title is required", Toast.LENGTH_SHORT).show();
            edit_blog_btn.setEnabled(true);

        }else if(desc.trim().equals("")){
            editBlogProgress.setVisibility(View.INVISIBLE);
            Toast.makeText(EditBlogActivity.this, "Description is required", Toast.LENGTH_SHORT).show();
            edit_blog_btn.setEnabled(true);
        }
        else {
            DocumentReference data = firebaseFirestore.collection("Posts").document(blogId);
            data.update("title", title);
            data.update("desc", desc).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    editBlogProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(EditBlogActivity.this, "Updated", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(EditBlogActivity.this, SinglePostActivity.class);
                    intent.putExtra("BLOG_POST_ID", blogId);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    startActivity(intent);
                    finish();

//                Intent intent = new Intent(EditBlogActivity.this,HomeFragment.class);
//                startActivity(intent);
//                finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    editBlogProgress.setVisibility(View.INVISIBLE);
                    edit_blog_btn.setEnabled(true);
                    Toast.makeText(EditBlogActivity.this, "Update Error", Toast.LENGTH_SHORT).show();
                }
            });

        }





    }

    private void getData() {
        editBlogProgress.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("Posts").document(blogId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult().exists()) {

                        String title = task.getResult().getString("title");
                        String desc = task.getResult().getString("desc");



                        edit_blog_title.setText(title);
                        edit_blog_desc.setText(desc);
                    }

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(EditBlogActivity.this, "Firestore Retrive Error : " + error, Toast.LENGTH_SHORT).show();
                }

                editBlogProgress.setVisibility(View.INVISIBLE);

            }
        });

    }
}
