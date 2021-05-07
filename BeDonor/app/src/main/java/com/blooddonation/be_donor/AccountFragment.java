package com.blooddonation.be_donor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {


    private ImageView account_image,accont_btn;
    private TextView account_name;
    private TextView account_number;
    private TextView account_blood_group;
    private String user_id;

    private List<PostStory> post_list;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private PostUserRecyclerAdapter postUserRecyclerAdapter;

    private DocumentSnapshot lastVisible;

    private Boolean isFirst = true;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        account_image = view.findViewById(R.id.account_image);
        account_name = view.findViewById(R.id.account_name);
        account_number = view.findViewById( R.id.account_number );
        account_blood_group = view.findViewById( R.id.account_blood_group );

        accont_btn = view.findViewById(R.id.account_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        post_list = new ArrayList<>();
        RecyclerView list_user_view = view.findViewById( R.id.list_user );
        user_id = Objects.requireNonNull( firebaseAuth.getCurrentUser() ).getUid();

        getProfileDetails();

        postUserRecyclerAdapter = new PostUserRecyclerAdapter(post_list);

        list_user_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        list_user_view.setAdapter( postUserRecyclerAdapter );

        accont_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(container.getContext(),SetupActivity.class));
            }
        });



        if(firebaseAuth.getCurrentUser() != null) {


            list_user_view.addOnScrollListener( new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if(reachedBottom){

                        loadMorePost();

                    }
                }
            });


            Query firstQuery = firebaseFirestore.collection("Posts").whereEqualTo("user_id",user_id).orderBy("timestamp",Query.Direction.DESCENDING).limit(3);




            firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                    if (e != null) {

                        Log.d("TAG", "Error:" + e.getMessage());
                    } else {

                        if (isFirst) {

                            if(!queryDocumentSnapshots.isEmpty()) {
                                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                            }
                            else{
                                Toast.makeText(container.getContext(), "No post available", Toast.LENGTH_SHORT).show();
                            }

                        }

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId();

                                PostStory blogPost = doc.getDocument().toObject( PostStory.class).withId(blogPostId);

                                if (isFirst) {
                                    post_list.add(blogPost);
                                } else {
                                    post_list.add(0, blogPost);
                                }

                                postUserRecyclerAdapter.notifyDataSetChanged();
                            }

                        }
                        isFirst = false;
                    }

                }
            });


        }

        // Inflate the layout for this fragment
        return view;
    }



    public void loadMorePost(){


        Query nextQuery = firebaseFirestore.collection("Posts").whereEqualTo("user_id",user_id).orderBy("timestamp",Query.Direction.DESCENDING).startAfter(lastVisible).limit(3);

        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e!=null){

                    Log.d("TAG","Error:"+e.getMessage());
                }else {

                    if(!queryDocumentSnapshots.isEmpty()) {


                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);


                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String PostId =doc.getDocument().getId();

                                PostStory Post = doc.getDocument().toObject( PostStory.class).withId(PostId);
                                post_list.add(Post);

                                postUserRecyclerAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                }

            }
        });

    }




    private void getProfileDetails() {

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    String user_name = task.getResult().getString("name");
                    String user_number = task.getResult().getString("number");
                    String user_blood_group= task.getResult().getString("blood group");
                    String user_image = task.getResult().getString("image");

                    account_name.setText(user_name);
                    account_number.setText(user_number);
                    account_blood_group.setText(user_blood_group);
                    Glide.with(getContext()).load(user_image).into(account_image);

                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(getContext(), "Image Error : " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
