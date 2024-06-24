package com.example.atal_jbernardes_jfinalproject.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atal_jbernardes_jfinalproject.Adapters.UserAdapter;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Followers extends AppCompatActivity {
    RecyclerView followersList;
    private String userId;
    private List<User> followers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_followers);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences appPreferences = getSharedPreferences("com.example.atal_jbernardes_jfinalproject",
                Context.MODE_PRIVATE);
        boolean useDarkMode = appPreferences.getBoolean("DARK_MODE", false);
        Log.v("THEME_MODE_FOLLOWERS", ""+useDarkMode);

        if (useDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Bundle bundle = getIntent().getExtras();
        userId = bundle.getString("userId");

        followersList = findViewById(R.id.followersList);
        getFollowers();
    }

    private void getFollowers() {
        followers = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Followers").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                    list = (List<String>) data.getOrDefault("Followers", new ArrayList<>());
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                }
                for(String id: list) {
                    Log.d("FollowersActivity", id);
                    getUserData(id);
                }

            }
        });
        UserAdapter userAdapter = new UserAdapter(followers);
        followersList.setAdapter(userAdapter);
        followersList.setLayoutManager(new LinearLayoutManager(Followers.this));
        followersList.getAdapter().notifyDataSetChanged();
    }

    private void getUserData(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Users").document(id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                User user = documentSnapshot.toObject(User.class);
                followers.add(user);
                followersList.getAdapter().notifyDataSetChanged();
            }
        });
    }
}