package com.blooddonation.be_donor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
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
public class SearchFragment extends Fragment {

    private SearchView searchText;
    private ProgressBar searchProgress;
    private RecyclerView searchResultView;
    private List<PostStory> blog_list;
    private SearchRecyclerAdapter searchRecyclerAdapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private TextView noSearchResult;


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchText = view.findViewById(R.id.search_text);
        searchProgress = view.findViewById(R.id.search_progress);

        noSearchResult = view.findViewById(R.id.search_noResult);

        blog_list = new ArrayList<>();
        searchRecyclerAdapter = new SearchRecyclerAdapter(blog_list);

        searchResultView = view.findViewById(R.id.search_result_view);
        searchResultView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        searchResultView.setAdapter(searchRecyclerAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                callData(query);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                blog_list.clear();
                noSearchResult.setVisibility(View.INVISIBLE);
                return false;
            }
        });










        // Inflate the layout for this fragment
        return view;
    }

    private void callData(String query) {
        searchProgress.setVisibility(View.VISIBLE);
        if(firebaseAuth.getCurrentUser() != null) {

            String query_lower = query.toLowerCase();
            Query searchQuery =firebaseFirestore.collection("Posts").orderBy("title_lower").startAt(query_lower).endAt(query_lower+"\uf8ff");

            searchQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        String blogPostId = doc.getDocument().getId();

                        PostStory blogPost = doc.getDocument().toObject( PostStory.class).withId(blogPostId);

                        blog_list.add(blogPost);

                        searchRecyclerAdapter.notifyDataSetChanged();
                    }
                    if(blog_list.size()<=0){
                        noSearchResult.setVisibility(View.VISIBLE);
                    }
                    searchProgress.setVisibility(View.INVISIBLE);

                }
            });



        }

    }


}
