package com.blooddonation.be_donor;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.ViewHolder> {

    public List<Message> message_list;
    public Context context;
    private FirebaseAuth firebaseAuth;


    public MessageRecyclerAdapter(List<Message> message_list) {
        this.message_list = message_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item,parent,false);

        context = parent.getContext();
        firebaseAuth = FirebaseAuth.getInstance();

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String textData = message_list.get(position).getMsg();
        holder.setMsg(textData);

        String current_user = firebaseAuth.getCurrentUser().getUid();
        String msg_user = message_list.get(position).getUser_id();
        if(current_user.equals(msg_user)){
            holder.itemMsg.setBackgroundColor(Color.parseColor("#F5A18F"));
            holder.itemMsg.setTextColor(Color.parseColor("#382F2F"));
            holder.chatItemHolder.setGravity(Gravity.RIGHT);

        }else{
            holder.itemMsg.setBackgroundColor(Color.WHITE);
            holder.itemMsg.setTextColor(Color.parseColor("#382F2F"));
            holder.chatItemHolder.setGravity(Gravity.LEFT);
        }




    }

    @Override
    public int getItemCount() {
        return message_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView itemMsg;
        private LinearLayout chatItemHolder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            chatItemHolder = mView.findViewById(R.id.chat_item_holder);

        }

        public void setMsg(String text){

            itemMsg = mView.findViewById(R.id.item_msg);
            itemMsg.setText(text);

        }
    }
}
