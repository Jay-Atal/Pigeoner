package com.example.atal_jbernardes_jfinalproject.Adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.atal_jbernardes_jfinalproject.Activities.ProfileActivity;
import com.example.atal_jbernardes_jfinalproject.Activities.Replies;
import com.example.atal_jbernardes_jfinalproject.Elements.Pigeon;
import com.example.atal_jbernardes_jfinalproject.Elements.User;
import com.example.atal_jbernardes_jfinalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostViewHolder> {
    private List<Pigeon> myPosts;

    private FirebaseStorage storage;
    List<String> likesByUser;


    public PostAdapter(@NonNull List<Pigeon> myPosts) {
        this.myPosts = myPosts;
        likesByUser = new ArrayList<>();
        getLikesByFollowers();
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
        holder.likeButton.setText((likesByUser.contains(currentPost.getPigeonId())) ? "Un Like" : "Like");
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
            intent.putExtra("userId", currentPost.getUserId());
            startActivity(holder.userAccountInfo.getContext(), intent, new Bundle());
        });

        holder.likeButton.setOnClickListener(v -> {
            updatePostLikeCollection("Likes", currentPost.getPigeonId(), FirebaseAuth.getInstance().getUid(), holder.likeButton);
            updateUserLikeCollection("LikesByUser", FirebaseAuth.getInstance().getUid(), currentPost.getPigeonId());
        });

        holder.repliesButton.setOnClickListener(v -> {
            Intent intent = new Intent(holder.repliesButton.getContext(), Replies.class);
            intent.putExtra("parentId", currentPost.getPigeonId());
            startActivity(holder.repliesButton.getContext(), intent, new Bundle());
        });

        holder.commentButton.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(holder.commentButton.getContext());
            View dialogLayout = inflater.inflate(R.layout.add_reply, null);
            final AlertDialog dialog = new AlertDialog.Builder(holder.commentButton.getContext())
                    .setView(dialogLayout)
                    .setTitle("Add Reply")
                    .setPositiveButton("Send", null) //Set to null. We override the onclick
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
            EditText contentField;
            contentField = dialogLayout.findViewById(R.id.contentField);

            dialog.setOnShowListener(dialog1 -> {
                Button ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                ok.setOnClickListener(v2 -> {
                    Pigeon reply = new Pigeon(FirebaseAuth.getInstance().getUid(), contentField.getText().toString());
                    reply.setParentId(currentPost.getPigeonId());
                    db.collection("Pigeons").add(reply).addOnCompleteListener(task -> {
                        if(!task.isSuccessful()) {
                            return;
                        }
                        DocumentReference documentReference1 = task.getResult();
                        reply.setPigeonId(documentReference1.getId());
                        db.collection("Pigeons").document(documentReference1.getId()).set(reply, SetOptions.merge()).addOnCompleteListener(task1 -> {
                            dialog.dismiss();
                            holder.repliesButton.callOnClick();
                        });
                    });

                });
            });
            dialog.show();
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

    private void updatePostLikeCollection(String collectionName, String document, String value, Button likeButton) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionName).document(document).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> data = new HashMap<>();
                List<String> list;
                if (!task.isSuccessful()) {
                    list = new ArrayList<>();
                } else {
                    if (task != null) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        data = documentSnapshot.getData();
                    }
                    if (data == null) {
                        data = new HashMap<>();
                    }
                    list = (List<String>) data.getOrDefault(collectionName, new ArrayList<>());
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                }
                boolean added;
                if (list.contains(value)) {
                    added = false;
                    list.remove(value);
                    likeButton.setText("Like");
                } else {
                    list.add(value);
                    added = true;
                    likeButton.setText("Un Like");
                }
                db.collection("Pigeons").document(document).get().addOnCompleteListener(task1 -> {
                    if (!task1.isSuccessful()) {
                        return;
                    }
                    DocumentSnapshot documentSnapshot = task1.getResult();
                    Pigeon pigeon1 = documentSnapshot.toObject(Pigeon.class);
                    if (pigeon1 == null) {
                        Log.d("PostAdapter", "pigeon is null" + document);
                        return;
                    }
                    int like = pigeon1.getLikeCount();
                    if (added) {
                        like++;
                    } else {
                        like--;
                    }
                    pigeon1.setLikeCount(like);
                    db.collection("Pigeons").document(document).set(pigeon1, SetOptions.merge());
                });

                data.put(collectionName, list);
                db.collection(collectionName).document(document).set(data, SetOptions.merge());
            }
        });
    }

    private void updateUserLikeCollection(String collectionName, String document, String value) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionName).document(document).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> data = new HashMap<>();
                List<String> list;
                if (!task.isSuccessful()) {
                    list = new ArrayList<>();
                } else {
                    if (task != null) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        data = documentSnapshot.getData();
                    }
                    if (data == null) {
                        data = new HashMap<>();
                    }
                    list = (List<String>) data.getOrDefault(collectionName, new ArrayList<>());
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                }
                if (list.contains(value)) {
                    list.remove(value);
                } else {
                    list.add(value);
                }
                data.put(collectionName, list);
                db.collection(collectionName).document(document).set(data, SetOptions.merge());
            }
        });
    }

    public void update() {
        getLikesByFollowers();
    }

    public void getLikesByFollowers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("LikesByUser").document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> data = new HashMap<>();
                List<String> list;
                if (!task.isSuccessful()) {
                    list = new ArrayList<>();
                } else {
                    if (task != null) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        data = documentSnapshot.getData();
                    }
                    if (data == null) {
                        data = new HashMap<>();
                    }
                    list = (List<String>) data.getOrDefault("LikesByUser", new ArrayList<>());
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                }
                likesByUser = list;
                notifyDataSetChanged();
            }
        });

    }


    @Override
    public int getItemCount() {
        Log.d("POST_COUNT", "Size: " + myPosts.size());
        return myPosts.size();
    }
}
