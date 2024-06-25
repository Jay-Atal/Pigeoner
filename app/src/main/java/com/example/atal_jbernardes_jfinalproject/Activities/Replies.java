package com.example.atal_jbernardes_jfinalproject.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atal_jbernardes_jfinalproject.Adapters.PostAdapter;
import com.example.atal_jbernardes_jfinalproject.Elements.Pigeon;
import com.example.atal_jbernardes_jfinalproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Replies extends AppCompatActivity {

    private RecyclerView recyclerView;

    private List<Pigeon> pigeons;

    private boolean onLaunch;

    private PostAdapter postAdapter;
    private String parentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_replies);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences appPreferences = getSharedPreferences("com.example.atal_jbernardes_jfinalproject",
                Context.MODE_PRIVATE);
        boolean useDarkMode = appPreferences.getBoolean("DARK_MODE", false);
        Log.v("THEME_MODE_REPLIES", ""+useDarkMode);

        if (useDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        pigeons = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        parentId = bundle.getString("parentId");
        onLaunch = true;
        getPigeons();
        recyclerView = findViewById(R.id.repliesList);
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
                    if(Objects.equals(pigeon.getParentId(),parentId)) {
                        pigeons.add(pigeon);
                    }
                }

                postAdapter = new PostAdapter(pigeons);
                recyclerView.setAdapter(postAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(Replies.this));
                pigeons.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
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
                                        int pigeonIndex = getPigeonIndex(currentPigeon.getPigeonId());
                                        if(!Objects.equals(parentId, currentPigeon.getParentId())){
                                            return;
                                        }
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