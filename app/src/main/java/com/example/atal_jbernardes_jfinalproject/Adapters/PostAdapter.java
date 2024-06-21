package com.example.atal_jbernardes_jfinalproject.Adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.atal_jbernardes_jfinalproject.Activities.ProfileActivity;
import com.example.atal_jbernardes_jfinalproject.Elements.Pigeon;
import com.example.atal_jbernardes_jfinalproject.Elements.User;
import com.example.atal_jbernardes_jfinalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {
    private List<Pigeon> myPosts;

    private FirebaseStorage storage;


    public PostAdapter(@NonNull List<Pigeon> myPosts) {
        this.myPosts = myPosts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post, parent,
                false);
        return new PostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Pigeon currentPost = myPosts.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Users").document(
                currentPost.getUserId());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                User user = documentSnapshot.toObject(User.class);
                holder.username.setText(user.getUsername());
            }
        });

        holder.postInformation.setText(currentPost.getContent()); //change once post class has been implemented
        holder.likeCount.setText("Likes: " + currentPost.getLikeCount());
        holder.postDate.setText(currentPost.getTimestamp().toString()); //change once post class has been implemented
        storage = FirebaseStorage.getInstance();

        holder.userAccountInfo.setOnClickListener(v -> {
            Intent intent = new Intent(holder.userAccountInfo.getContext(), ProfileActivity.class);
            startActivity(holder.userAccountInfo.getContext(), intent, new Bundle());
        });

        StorageReference httpsReference;
        try {
            Log.d("PostHere", "first");
            try {
                httpsReference = storage.getReferenceFromUrl("gs://pigeoner-a0dab.appspot.com/" + currentPost.getUserId());
                extractImage(httpsReference, holder.profilePicture);
            } catch (Exception e) {

            }
            Log.d("PostHere", "second");

        } catch (Exception e) {
            Log.e("ImageEror", e.getMessage(), e.getCause());
        }

        holder.itemView.setOnClickListener(v -> {
            //Intent postIntent = new Intent(v.getContext(), /* new class to go to */ );
            //add post id
            //v.getContext().startActivity(/*postIntent*/);
        });
    }

    private void extractImage(StorageReference httpsReference, ImageView imageView) {
        try {
            httpsReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Log.d("PostHere", "third");
                    task.addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            if (!task.isSuccessful() || task.getResult() == null) {
                                StorageReference httpsReference = storage.getReferenceFromUrl("gs://pigeoner-a0dab.appspot.com/default.jpeg");
                                extractImage(httpsReference, imageView);
                                return;
                            }
                            imageView.setImageURI(task.getResult());
                            Glide.with(imageView.getContext()).load(task.getResult()).into(imageView);
//                            Glide.with(getActivity().getApplicationContext()).load(task.getResult()).into(imageView);
//                                   Picasso.get().load(task.getResult()).into(imageView);
//                            Picasso.with()
//                                    .load(task.getResult())
//                                    .into(imageView);
                        }
                    });
                    Log.d("PostHere", "fourth");
                }
            });
        } catch (Exception e) {

        }
    }


    @Override
    public int getItemCount() {
        Log.d("POST_COUNT", "Size: " + myPosts.size());
        return myPosts.size();
    }
}
