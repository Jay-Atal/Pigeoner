package com.example.atal_jbernardes_jfinalproject.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.atal_jbernardes_jfinalproject.Adapters.PostAdapter;
import com.example.atal_jbernardes_jfinalproject.Elements.Pigeon;
import com.example.atal_jbernardes_jfinalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LikedPosts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikedPosts extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;

    private List<Pigeon> pigeons;
    private List<String> likes;

    private boolean onLaunch;
    private boolean onLaunch2;

    private PostAdapter postAdapter;

    public LikedPosts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LikedPosts.
     */
    // TODO: Rename and change types and number of parameters
    public static LikedPosts newInstance(String param1, String param2) {
        LikedPosts fragment = new LikedPosts();
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
        View view = inflater.inflate(R.layout.fragment_liked_posts, container, false);
        recyclerView = view.findViewById(R.id.likedPostList);
        pigeons = new ArrayList<>();
        onLaunch = true;
        onLaunch2 = true;
        getLikes(true);
        return view;
    }

    public void getLikes(boolean getPigeons) {
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
                likes = list;
                if(getPigeons) {
                    getPigeons();
                }
            }
        });

    }


    private void getPigeons() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Pigeons").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                QuerySnapshot querySnapshot = task.getResult();
                List<DocumentChange> documentChanges = querySnapshot.getDocumentChanges();

                for (DocumentChange documentChange : documentChanges) {
                    Pigeon pigeon = documentChange.getDocument().toObject(Pigeon.class);
                    if(likes.contains(pigeon.getPigeonId())) {
                        pigeons.add(pigeon);
                    }
                }

                postAdapter = new PostAdapter(pigeons);
                recyclerView.setAdapter(postAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));;
                recyclerView.getAdapter().notifyDataSetChanged();
                attachFirestoreListener();
            }
        });
    }

    private void attachFirestoreListener() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Pigeons")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle the error
                            return;
                        }
                        if (snapshots != null && !snapshots.isEmpty()) {
                            if(onLaunch) {
                                onLaunch = false;
                                return;
                            }
                            for (DocumentChange documentChange: snapshots.getDocumentChanges())
                                switch (documentChange.getType()) {
                                    case ADDED:

                                        break;
                                    case MODIFIED:
                                        Log.d("Explore", documentChange.getDocument().toString());
                                        Pigeon currentPigeon = documentChange.getDocument().toObject(Pigeon.class);
                                        if(!likes.contains(currentPigeon.getPigeonId())) {
                                            return;
                                        }
                                        int pigeonIndex = getPigeonIndex(currentPigeon.getPigeonId());
                                        if(pigeonIndex == -1) {
                                            pigeons.add(0, currentPigeon);
                                        } else {
                                            pigeons.remove(pigeonIndex);
                                            pigeons.add(pigeonIndex, currentPigeon);
                                        }
                                        postAdapter.update();
                                        break;
                                    case REMOVED:

                                        break;
                                }
                        }
                    }
                });
        db.collection("LikesByUser").document(FirebaseAuth.getInstance().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(onLaunch2) {
                    onLaunch2 = false;
                    return;
                }
                Map<String, Object> data = value.getData();
                if(data == null){
                    return;
                }
                List<String> newLikes = (List<String>) data.get("LikesByUser");
                Log.d("NewLiks", newLikes.toString());
                if(newLikes.size() > likes.size()) {
                    likes = newLikes;
                } else {
                    List<String> removed = new ArrayList<>(likes);
                    removed.removeAll(newLikes);
                    Log.d("removed", removed.toString());
                    Log.d("removed", pigeons.toString());
                    for(int i = pigeons.size() - 1; i >= 0; i--) {
                        Log.d("removed1", pigeons.get(i).getPigeonId());
                        if(removed.contains(pigeons.get(i).getPigeonId())){
                            Log.d("removed", pigeons.toString());
                            pigeons.remove(i);
                            postAdapter.notifyItemRemoved(i);
                        }
                    }
                    likes.removeAll(removed);
                }
                postAdapter.update();
            }
        });
    }
    private int getPigeonIndex(String id) {
        for(int i = 0; i < pigeons.size(); i++) {
            Pigeon currentPigeon = pigeons.get(i);
            if(currentPigeon.getPigeonId().equals(id)) {
                return i;
            }
        }
        return -1;
    }
}