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

import java.util.List;

public class SearchRecyclerAdapter extends RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>{

    public List<PostStory> blog_list;
    public Context context;


    public SearchRecyclerAdapter(List<PostStory> blog_list){

        this.blog_list = blog_list;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_list,parent,false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String blogPostId = blog_list.get(position).PostId;

        String title_data = blog_list.get(position).getTitle();
        holder.setTitleText(title_data);

        String image_url = blog_list.get(position).getImage_url();
        holder.setBlogImage(image_url);

        holder.searchBlogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,SinglePostActivity.class);
                intent.putExtra("BLOG_POST_ID", blogPostId);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView searchBlogTitle;
        private ImageView searchBlogImage;
        private Button searchBlogBtn;

        private View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            searchBlogBtn = mView.findViewById(R.id.search_item_btn);
        }

        public void setTitleText(String titleText){

            searchBlogTitle = mView.findViewById(R.id.search_item_title);
            searchBlogTitle.setText(titleText);
        }

        public void setBlogImage(String downloadUri){

            searchBlogImage = mView.findViewById(R.id.search_item_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.post_img);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).into(searchBlogImage);


        }
    }
}
