package com.example.atal_jbernardes_jfinalproject.Adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atal_jbernardes_jfinalproject.Elements.Post;
import com.example.atal_jbernardes_jfinalproject.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {
    private List<Post> myPosts;

    public PostAdapter(@NonNull List<Post> myPosts) {this.myPosts = myPosts;}
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post, parent,
                false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post currentPost = myPosts.get(position);
        holder.username.setText("Username"); //change once post class has been implemented
        //change image to user pfp here
        //String iconURL = ""; //figure out how to see user icon online
        //Picasso.get().load(iconURL).fit().into(holder.profilePicture);
        holder.postInformation.setText("Post Info"); //change once post class has been implemented
        holder.postDate.setText("Post Date"); //change once post class has been implemented
        holder.itemView.setOnClickListener(v -> {
            //Intent postIntent = new Intent(v.getContext(), /* new class to go to */ );
            //add post id
            //v.getContext().startActivity(/*postIntent*/);
        });
    }

    @Override
    public int getItemCount() {
        Log.d("POST_COUNT", "Size: " + myPosts.size());
        return myPosts.size();
    }
}
