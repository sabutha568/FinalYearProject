package com.blooddonation.be_donor;

import android.content.Intent;
import android.os.Bundle;
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

import static android.text.TextUtils.isEmpty;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailText, loginPasswordText;
    private ProgressBar loginProgress;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loginEmailText = findViewById(R.id.login_email);
        loginPasswordText = findViewById(R.id.login_password);
        ImageButton loginBtn = findViewById( R.id.login_btn );
        loginProgress = findViewById(R.id.login_progress);


        loginBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String loginEmail = loginEmailText.getText().toString().trim();
                String loginPassword = loginPasswordText.getText().toString();

                if (!isEmpty( loginPassword )) {
                    if (!isEmpty( loginEmail )) {

                        loginProgress.setVisibility( View.VISIBLE );

                        mAuth.signInWithEmailAndPassword( loginEmail, loginPassword )
                                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information

                                            sentToMain();


                                        } else {
                                            // If sign in fails, display a message to the user.

                                            Toast.makeText( LoginActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT ).show();

                                        }
                                        loginProgress.setVisibility( View.INVISIBLE );

                                        // ...
                                    }
                                } );

                    } else {
                        Toast.makeText( LoginActivity.this, "Fields Cannot be Empty",
                                Toast.LENGTH_SHORT ).show();
                    }
                } else {
                    Toast.makeText( LoginActivity.this, "Fields Cannot be Empty",
                            Toast.LENGTH_SHORT ).show();
                }

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
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();

    }

    public void gotoReset(View view) {
//        Intent resetIntent = new Intent(LoginActivity.this,PasswordReset.class);
//        startActivity(resetIntent);
        startActivity(new Intent(LoginActivity.this,PasswordReset.class));

    }

    public void gotoSignUp(View view) {
        Intent regIntent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(regIntent);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();

    }
}
