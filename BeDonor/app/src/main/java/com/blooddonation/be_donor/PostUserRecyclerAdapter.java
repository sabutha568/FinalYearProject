package com.blooddonation.be_donor;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class PostUserRecyclerAdapter extends RecyclerView.Adapter<PostUserRecyclerAdapter.ViewHolder> {


    public List<PostStory> post_list;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storage;
    private StorageReference storageReference;



    public PostUserRecyclerAdapter(List<PostStory> post_list){

        this.post_list = post_list;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item_list_user,parent,false);

        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();





        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final String post_id = post_list.get(position).PostId;

        final String title_data = post_list.get(position).getTitle();
        holder.setTitleText(title_data);

        final String image_url = post_list.get(position).getImage_url();
        holder.setBlogImage(image_url);


        holder.account_blog_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context,EditBlogActivity.class);
                intent.putExtra("POST_ID", post_id);
                context.startActivity(intent);
            }
        });

        holder.accountBlogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(context,SinglePostActivity.class);
                intent.putExtra("POST_ID", post_id);
                context.startActivity(intent);

            }
        });


        holder.accountBlogMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // Setting Alert Dialog Title
                alertDialogBuilder.setTitle(title_data);

                // Setting Alert Dialog Message
                alertDialogBuilder.setMessage("Are you sure,You want to delete");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        firebaseFirestore.collection("Posts").document(post_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                        post_list.remove(position);

                        storage = FirebaseStorage.getInstance();

                        storageReference = storage.getReferenceFromUrl(image_url);

                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Could not Delete", Toast.LENGTH_SHORT).show();
                            }
                        });




                    }
                });

                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });



    }



    @Override
    public int getItemCount() {
        return post_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView accountBlogTitleView;
        private ImageView accountBlogImageView,accountBlogMoreBtn;
        private Button account_blog_edit_btn, accountBlogBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;


            accountBlogMoreBtn = mView.findViewById(R.id.account_blog_more_btn);

            accountBlogBtn = mView.findViewById(R.id.account_blog_btn);

            account_blog_edit_btn = mView.findViewById(R.id.account_blog_edit_btn);



        }

        public void setTitleText(String titleText){

            accountBlogTitleView = mView.findViewById(R.id.account_blog_title);
           accountBlogTitleView.setText(titleText);
        }

        public void setBlogImage(String downloadUri){

            accountBlogImageView = mView.findViewById(R.id.account_blog_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.post_img);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).into(accountBlogImageView);


        }
    }



}
