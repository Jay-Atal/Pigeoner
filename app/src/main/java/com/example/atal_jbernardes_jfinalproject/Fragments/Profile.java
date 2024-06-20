package com.example.atal_jbernardes_jfinalproject.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.atal_jbernardes_jfinalproject.Adapters.PostAdapter;
import com.example.atal_jbernardes_jfinalproject.Elements.Pigeon;
import com.example.atal_jbernardes_jfinalproject.Elements.User;
import com.example.atal_jbernardes_jfinalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Profile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PICK_IMAGE_REQUEST = 43;
    private static final int TAKE_IMAGE_REQUEST = 45;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Uri filePath;

    private StorageReference storageReference;
    private FirebaseStorage storage;
    private ImageView imageView;

    private Button followButton;

    TextView nameField, bioField;


    public Profile() {
        // Required empty public constructor
    }

    private String userId;

    public Profile(String userId) {
        this.userId = userId;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        imageView = view.findViewById(R.id.imageView);
        imageView.setOnClickListener(v -> {
            selectImage();
        });
        nameField = view.findViewById(R.id.profileUsernameTextView);
        bioField = view.findViewById(R.id.profileBioTextView);
        getUserData();
        RecyclerView recyclerView = view.findViewById(R.id.profilePostsList);
        followButton = view.findViewById(R.id.followButton);
        if (userId.equals(FirebaseAuth.getInstance().getUid())) {
            followButton.setVisibility(View.GONE);
        }

        List<Pigeon> pigeons = new ArrayList<>();
        pigeons.add(new Pigeon(FirebaseAuth.getInstance().getUid(), "asdf"));
        pigeons.add(new Pigeon(FirebaseAuth.getInstance().getUid(), "asnvalnse"));
        pigeons.add(new Pigeon(FirebaseAuth.getInstance().getUid(), "asdlkmadsvl;knf"));
        PostAdapter postAdapter = new PostAdapter(pigeons);
        recyclerView.setAdapter(postAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.getAdapter().notifyDataSetChanged();

        StorageReference httpsReference;
        try {
            Log.d("here", "first");
            try {
                httpsReference = storage.getReferenceFromUrl("gs://pigeoner-a0dab.appspot.com/" + userId);
                extractImage(httpsReference);
            } catch (Exception e) {

            }
            Log.d("here", "second");

        } catch (Exception e) {
            Log.e("ImageEror", e.getMessage(), e.getCause());
        }

        return view;
    }

    private void extractImage(StorageReference httpsReference) {
        try {
            httpsReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Log.d("here", "third");
                    task.addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            if (!task.isSuccessful() || task.getResult() == null) {
                                StorageReference httpsReference = storage.getReferenceFromUrl("gs://pigeoner-a0dab.appspot.com/default.jpeg");
                                extractImage(httpsReference);
                                return;
                            }
                            imageView.setImageURI(task.getResult());
                            Glide.with(getActivity().getApplicationContext()).load(task.getResult()).into(imageView);
//                                   Picasso.get().load(task.getResult()).into(imageView);
//                            Picasso.with()
//                                    .load(task.getResult())
//                                    .into(imageView);
                        }
                    });
                    Log.d("here", "fourth");
                }
            });
        } catch (Exception e) {

        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                uploadImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {

            StorageReference storageRef = storage.getReference();
            StorageReference pfp = storageRef.child(userId);
            UploadTask uploadTask = pfp.putFile(filePath);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("FirebaseException", exception.getMessage(), exception.getCause());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    imageView.setImageURI(filePath);
                }
            });

        } else {
            Toast.makeText(getActivity().getApplicationContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Users").document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                User user = documentSnapshot.toObject(User.class);
                nameField.setText(user.getName());
                bioField.setText(user.getBio());
            }
        });
    }
}
