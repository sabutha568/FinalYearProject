package com.blooddonation.be_donor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText reg_email_field, reg_pass_field,reg_confirm_pass_field;
    private ProgressBar reg_progress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        reg_email_field = findViewById(R.id.reg_email);
        reg_pass_field = findViewById(R.id.reg_pass);
        reg_confirm_pass_field = findViewById(R.id.reg_confirm_pass);
        ImageButton reg_btn = findViewById( R.id.reg_btn );
        reg_progress = findViewById(R.id.reg_progress);


        reg_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = reg_email_field.getText().toString();
                String pass = reg_pass_field.getText().toString();
                String confirm_pass = reg_confirm_pass_field.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_pass) ){

                    if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                        if (pass.equals(confirm_pass)) {

                            reg_progress.setVisibility(View.VISIBLE);

                            registerUser(email, pass);

                        } else {
                            Toast.makeText(RegisterActivity.this, "Confirm Password do not match",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(RegisterActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(RegisterActivity.this, "Fields cannot be empty",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });





    }

    private void registerUser(String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent setupIntent = new Intent(RegisterActivity.this,SetupActivity.class);
                            startActivity(setupIntent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        reg_progress.setVisibility(View.INVISIBLE);
                        // ...
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            sentToMain();

        }
    }

    private void sentToMain() {
        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();

    }

    public void gotoSignIn(View view) {
        finish();
    }
}
