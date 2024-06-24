package com.example.atal_jbernardes_jfinalproject.Adapters;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atal_jbernardes_jfinalproject.R;


public class PostViewHolder extends RecyclerView.ViewHolder {
    TextView username;
    ImageView profilePicture;
    TextView postInformation;
    TextView likeCount;
    TextView postDate;
    View userAccountInfo;
    Button likeButton;
    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.userUsernameTextView);
        profilePicture = itemView.findViewById(R.id.userProfilePictureImage);
        postInformation = itemView.findViewById(R.id.userPostTextView);
        likeCount = itemView.findViewById(R.id.likeCountTextView);
        postDate = itemView.findViewById(R.id.timeStampTextView);
        userAccountInfo = itemView.findViewById(R.id.userAccountInfo);
        likeButton = itemView.findViewById(R.id.likeButton);
    }
}
