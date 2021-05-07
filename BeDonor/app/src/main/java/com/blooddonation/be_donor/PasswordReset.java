package com.blooddonation.be_donor;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordReset extends AppCompatActivity {

    EditText reset_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        reset_email = findViewById(R.id.reset_email_id);
    }

    public void resetPassword(View view) {
        String email = reset_email.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email field Empty", Toast.LENGTH_SHORT).show();
        }
        else{
            reset(email);
        }
    }

    private void reset(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PasswordReset.this, "Email sent", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(PasswordReset.this, LoginActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(PasswordReset.this, "Failed try again", Toast.LENGTH_SHORT).show();
                            reset_email.setText("");
                        }
                    }
                });

    }

    public void gotoSignUP(View view) {
        startActivity(new Intent(PasswordReset.this,RegisterActivity.class));
        finish();
    }
}
