package com.example.atal_jbernardes_jfinalproject.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atal_jbernardes_jfinalproject.R;

public class UserViewHolder extends RecyclerView.ViewHolder {
    TextView username;
    ImageView profilePicture;
    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.friendUsernameTextView);
        profilePicture = itemView.findViewById(R.id.friendProfilePictureImage);
    }
}
