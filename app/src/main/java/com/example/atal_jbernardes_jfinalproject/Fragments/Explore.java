package com.example.atal_jbernardes_jfinalproject.Fragments;

import static com.google.api.ChangeType.ADDED;
import static com.google.api.ChangeType.MODIFIED;
import static com.google.api.ChangeType.REMOVED;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Explore#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Explore extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;

    private List<Pigeon> pigeons;

    private boolean onLaunch;

    private PostAdapter postAdapter;


    public Explore() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Explore.
     */
    // TODO: Rename and change types and number of parameters
    public static Explore newInstance(String param1, String param2) {
        Explore fragment = new Explore();
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
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        SharedPreferences appPreferences = getActivity().getSharedPreferences(
                "com.example.atal_jbernardes_jfinalproject", Context.MODE_PRIVATE);
        boolean useDarkMode = appPreferences.getBoolean("DARK_MODE", false);
        Log.v("THEME_MODE_EXPLORE", ""+useDarkMode);

//        if (useDarkMode) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }

        recyclerView = view.findViewById(R.id.exploreRecyclerView);
        pigeons = new ArrayList<>();
        onLaunch = true;
        getPigeons();
        return view;
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
                    if(pigeon.getParentId() == null) {
                        pigeons.add(pigeon);
                    }
                }

                postAdapter = new PostAdapter(pigeons);
                recyclerView.setAdapter(postAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.getAdapter().notifyDataSetChanged();
                postAdapter.update();
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
                                        if(currentPigeon.getParentId() != null) {
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