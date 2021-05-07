package com.blooddonation.be_donor;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private RecyclerView list_view;
    private List<PostStory> list;

    private FirebaseFirestore firebaseFirestore;

    private PostRecyclerAdapter postRecyclerAdapter;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot lastVisible;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Boolean checkPostExist = true;

    private Boolean isFirst = true;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        list_view = view.findViewById(R.id.list_view);
        list = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.blue, R.color.green);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shuffle();
                    }
                }, 2500);
            }
        });





        postRecyclerAdapter = new PostRecyclerAdapter(list);

        list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        list_view.setAdapter( postRecyclerAdapter );




        if(firebaseAuth.getCurrentUser() != null) {

            firebaseFirestore = FirebaseFirestore.getInstance();

            list_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    Boolean reachedBottom = !recyclerView.canScrollVertically(1);

                    if(reachedBottom){

                        loadMorePost();

                    }
                }
            });


            loadPost();





        }

        // Inflate the layout for this fragment
        return view;
    }

    private void loadPost() {



        Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING).limit(3);



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
                            Toast.makeText(getContext(), "No post available", Toast.LENGTH_SHORT).show();
                            checkPostExist = false;
                        }

                    }

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String PostId = doc.getDocument().getId();

                            PostStory Post = doc.getDocument().toObject( PostStory.class).withId(PostId);

                            if (isFirst) {
                                list.add(Post);
                            } else {
                                list.add(0, Post);
                            }

                            postRecyclerAdapter.notifyDataSetChanged();
                        }

                    }
                    isFirst = false;
                }

            }
        });
    }


    private void shuffle() {


        list.clear();

        isFirst = true;

        loadPost();
        swipeRefreshLayout.setRefreshing(false);
    }


    public void loadMorePost(){


        Query nextQuery = firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING).startAfter(lastVisible).limit(3);

        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots,FirebaseFirestoreException e) {
                if (e!=null){

                    Log.d("TAG","Error:"+e.getMessage());
                }else {

                    if(!queryDocumentSnapshots.isEmpty()) {


                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);


                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {
                                String PostId =doc.getDocument().getId();

                                PostStory Post = doc.getDocument().toObject( PostStory.class).withId(PostId);
                                list.add(Post);

                                postRecyclerAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                }

            }
        });

    }


}
