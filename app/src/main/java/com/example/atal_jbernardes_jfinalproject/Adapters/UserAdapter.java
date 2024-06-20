package com.example.atal_jbernardes_jfinalproject.Adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private List<User> myFriends;
    private FirebaseStorage storage;

    public UserAdapter(@NonNull List<User> myFriends) {
        this.myFriends = myFriends;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_profile,
                parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User currentFriend = myFriends.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference  = db.collection("Users").document(
                currentFriend.getUserId());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                User user = documentSnapshot.toObject(User.class);
                holder.username.setText(user.getUsername());
            }
        });

        storage = FirebaseStorage.getInstance();

        StorageReference httpsReference;
        try {
            httpsReference = storage.getReferenceFromUrl("gs://pigeoner-a0dab.appspot.com/"+ currentFriend.getUserId());
            extractImage(httpsReference, holder.profilePicture);
        } catch (Exception e) {
            Log.e("ImageEror", e.getMessage(), e.getCause());
        }

        holder.itemView.setOnClickListener(v -> {
            //Intent postIntent = new Intent(v.getContext(), /* new class to go to */ );
            //add user id
            //v.getContext().startActivity(/*userIntent*/);
        });
    }

    private void extractImage (StorageReference httpsReference, ImageView imageView) {
        try {
            httpsReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Log.d("FriendHere", "third");
                    task.addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            if (!task.isSuccessful() || task.getResult() == null) {
                                StorageReference httpsReference = storage.getReferenceFromUrl(
                                        "gs://pigeoner-a0dab.appspot.com/default.jpeg");
                                extractImage(httpsReference, imageView);
                                return;
                            }
                            imageView.setImageURI(task.getResult());
                            Glide.with(imageView.getContext()).load(task.getResult()).into(imageView);
                        }
                    });
                    Log.d("FriendHere", "fourth");
                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        Log.d("FRIEND_COUNT", "Size: " + myFriends.size());
        return myFriends.size();
    }
}
