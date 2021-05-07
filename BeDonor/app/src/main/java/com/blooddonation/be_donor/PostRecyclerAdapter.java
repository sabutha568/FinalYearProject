package com.blooddonation.be_donor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PostRecyclerAdapter extends RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder> {

    public List<PostStory> post_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;

    public PostRecyclerAdapter(List<PostStory> post_list){

        this.post_list = post_list;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);

        context = parent.getContext();

        firebaseFirestore = FirebaseFirestore.getInstance();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String PostId = post_list.get(position).PostId;

        String title_data = post_list.get(position).getTitle();
        holder.setTitleText(title_data);

        String desc_data = post_list.get(position).getDesc();

        int line = 250;
        if(desc_data.length() <= 250){
            line = desc_data.length();
        }
        String limited_desc_data = desc_data.substring(0,line)+"...";

        holder.setDescText(limited_desc_data);

        String image_url = post_list.get(position).getImage_url();
        holder.setBlogImage(image_url);

        //read more

        holder.blogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(context,SinglePostActivity.class);
                intent.putExtra("POST_ID", PostId);
                context.startActivity(intent);

            }
        });



    }

    @Override
    public int getItemCount() {
        return post_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;


        private TextView titleView,descView;
        private ImageView blogImageView;
        private Button blogBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            blogBtn = mView.findViewById(R.id.blog_btn);
        }

        public void setTitleText(String titleText){

            titleView = mView.findViewById(R.id.blog_title);
            titleView.setText(titleText);
        }

        public void setDescText(String descText){
            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(descText);

        }

        public void setBlogImage(String downloadUri){

            blogImageView = mView.findViewById(R.id.blog_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.post_img);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).into(blogImageView);


        }

    }
}
