package com.blooddonation.be_donor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private EditText chatText;
    private ImageButton chatBtn;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;
    private RecyclerView chatView;
    private MessageRecyclerAdapter messageRecyclerAdapter;
    private List<Message> message_list;
    private Boolean isFirst = true;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllMsg();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        chatText = view.findViewById(R.id.chat_text);
        chatBtn = view.findViewById(R.id.chat_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        current_user_id = firebaseAuth.getCurrentUser().getUid();

        message_list = new ArrayList<>();

        messageRecyclerAdapter = new MessageRecyclerAdapter(message_list);


        chatView = view.findViewById(R.id.chat_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
//       linearLayoutManager.setReverseLayout(true);
 //       linearLayoutManager.setStackFromEnd(true);
        chatView.setLayoutManager(linearLayoutManager);

        chatView.setAdapter(messageRecyclerAdapter);



        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = chatText.getText().toString();
                String checkText = text.trim();
                if(!TextUtils.isEmpty(checkText)) {
                    sendMsg(text);
                    messageRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });



        // Inflate the layout for this fragment
        return view;
    }

    private void getAllMsg() {
        Query firstQuery = firebaseFirestore.collection("Message").orderBy("timestamp",Query.Direction.ASCENDING).limitToLast(100);

        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {

                    Log.d("TAG", "Error:" + e.getMessage());
                }else{
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                        if (doc.getType() == DocumentChange.Type.ADDED) {


                            Message msgList = doc.getDocument().toObject(Message.class);

                            message_list.add(msgList);
                            messageRecyclerAdapter.notifyDataSetChanged();
                            chatView.scrollToPosition(message_list.size()-1);
                        }


                    }

                }

            }
        });
    }




    private void sendMsg(String text) {
        if(firebaseAuth.getCurrentUser() != null){
            Map<String,Object> sendMap = new HashMap<>();

            sendMap.put("user_id",current_user_id);
            sendMap.put("msg",text);
            sendMap.put("timestamp", FieldValue.serverTimestamp());


            firebaseFirestore.collection("Message").add(sendMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {

                    if(task.isSuccessful()){

                        chatText.setText("");


                    }else{
                        String error = task.getException().getMessage();
                        Toast.makeText(getContext(), "Message Error : " + error, Toast.LENGTH_SHORT).show();
                    }
                }
            });



        }

    }
}
