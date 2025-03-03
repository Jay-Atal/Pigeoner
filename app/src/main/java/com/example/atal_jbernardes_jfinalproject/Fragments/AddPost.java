package com.example.atal_jbernardes_jfinalproject.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.atal_jbernardes_jfinalproject.Elements.Pigeon;
import com.example.atal_jbernardes_jfinalproject.Elements.User;
import com.example.atal_jbernardes_jfinalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPost#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPost extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button sentButton;
    private EditText content;

    public AddPost() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddTweet.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPost newInstance(String param1, String param2) {
        AddPost fragment = new AddPost();
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
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        SharedPreferences appPreferences = getActivity().getSharedPreferences(
                "com.example.atal_jbernardes_jfinalproject", Context.MODE_PRIVATE);
        boolean useDarkMode = appPreferences.getBoolean("DARK_MODE", false);
        Log.v("THEME_MODE_ADD_POST", ""+useDarkMode);

//        if (useDarkMode) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }

        sentButton = view.findViewById(R.id.sendButton);
        content = view.findViewById(R.id.postDetailsEditText);
        sentButton.setOnClickListener(v -> {

            Pigeon pigeon = new Pigeon(FirebaseAuth.getInstance().getUid(), content.getText().toString());
            addPigeon(pigeon);
        });
        return view;
    }

    private void addPigeon(Pigeon pigeon) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Pigeons").add(pigeon).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Unsucessful", Toast.LENGTH_SHORT).show();
                }
                DocumentReference documentReference = task.getResult();
                String pigeonId = documentReference.getId();
                updatePigeonsList(pigeonId);
                db.collection("Pigeons").document(pigeonId).get().addOnCompleteListener(task1 -> {
                    if(!task1.isSuccessful()){

                    }
                    DocumentSnapshot documentSnapshot = task1.getResult();
                    Pigeon pigeon1 = documentSnapshot.toObject(Pigeon.class);
                    pigeon1.setPigeonId(pigeonId);
                    db.collection("Pigeons").document(pigeonId).set(pigeon1, SetOptions.merge());
                });
            }
        });
    }

    private void updatePigeonsList(String pigeonId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("PigeonsByUser").document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> data = new HashMap<>();
                List<String> pigeons = new ArrayList<>();
                if (!task.isSuccessful()) {
                    pigeons = new ArrayList<>();
                } else {

                    if (task != null) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        data = documentSnapshot.getData();
                    }
                    if(data == null){
                        data = new HashMap<>();
                    }
                    pigeons = (List<String>) data.getOrDefault("Pigeons", new ArrayList<>());
                    if (pigeons == null) {
                        pigeons = new ArrayList<>();
                    }
                    content.setText("");
                    Toast.makeText(getActivity().getApplicationContext(), "Sent Post", Toast.LENGTH_SHORT).show();
                }
                pigeons.add(pigeonId);

                data.put("Pigeons", pigeons);
                db.collection("PigeonsByUser").document(FirebaseAuth.getInstance().getUid()).set(data, SetOptions.merge());
            }
        });

    }
}