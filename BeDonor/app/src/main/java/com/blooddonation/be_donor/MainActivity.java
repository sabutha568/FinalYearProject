package com.blooddonation.be_donor;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private FloatingActionButton addPostBtn;

    private  String current_user_id;

    private BottomNavigationView mainBottomNav;

    private HomeFragment homeFragment;
    private ChatFragment notificationFragment;
    private AccountFragment accountFragment;
    private SearchFragment searchFragment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Be Donor");

        if (mAuth.getCurrentUser() != null){

            mainBottomNav = findViewById(R.id.mainBottomNav);

        homeFragment = new HomeFragment();
        notificationFragment = new ChatFragment();
        accountFragment = new AccountFragment();
        searchFragment = new SearchFragment();


        initializeFragment();


        mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.bottom_action_home:
                        replaceFragment(homeFragment);
                        return true;

                    case R.id.bottom_action_search:
                        replaceFragment(searchFragment);
                        return true;

                    case R.id.bottom_action_account:
                        replaceFragment(accountFragment);
                        return true;

                    case R.id.bottom_action_notification:
                        replaceFragment(notificationFragment);
                        return true;

                    default:
                        return false;
                }

            }
        });


        addPostBtn = findViewById(R.id.add_post_btn);

        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(intent);
            }
        });

    }
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser == null){
           sendTOLogin();
        }else{
            current_user_id = mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        if(!task.getResult().exists()){
                                Intent setupIntent = new Intent(MainActivity.this,SetupActivity.class);
                                startActivity(setupIntent);
                                finish();
                        }

                    }else{
                        String error = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Firestore Retrive Error : "+error, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_settings_btn:
                Intent intent = new Intent(MainActivity.this,SetupActivity.class);
                startActivity(intent);

                return true;

            case R.id.action_logout_btn:
                logOut();
                return true;

            default:
                return false;

        }

    }

    private void logOut() {
        mAuth.signOut();
        sendTOLogin();

    }

    private void sendTOLogin() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void replaceFragment(Fragment fragment){

          FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

          if(fragment == homeFragment){

              addPostBtn.show();
            fragmentTransaction.hide(searchFragment);
            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(notificationFragment);

        }
        if(fragment == searchFragment){

            addPostBtn.hide();
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(notificationFragment);

        }

        if(fragment == accountFragment){
            addPostBtn.show();
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(searchFragment);
            fragmentTransaction.hide(notificationFragment);

        }

        if(fragment == notificationFragment){

            addPostBtn.hide();
            fragmentTransaction.hide(accountFragment);
            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(searchFragment);


        }

        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
//        fragmentTransaction.replace(R.id.main_container,fragment);
//        fragmentTransaction.commit();

    }

    private void initializeFragment() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container,homeFragment);
        fragmentTransaction.add(R.id.main_container,searchFragment);
        fragmentTransaction.add(R.id.main_container,notificationFragment);
        fragmentTransaction.add(R.id.main_container,accountFragment);


        fragmentTransaction.hide(searchFragment);
        fragmentTransaction.hide(notificationFragment);
        fragmentTransaction.hide(accountFragment);

        fragmentTransaction.commit();


    }


    @Override
    public void onBackPressed() {
        finishAffinity();

    }
}
