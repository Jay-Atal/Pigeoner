package com.example.atal_jbernardes_jfinalproject.NotificationService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationService extends Service {
    private boolean onLaunch2 = true;
    private List<String> followers = new ArrayList<>();
    Intent broadcastIntent;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        getFollowers();
        attachFirestoreListener();
        return START_STICKY;
    }

    private void attachFirestoreListener() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Followers").document(FirebaseAuth.getInstance().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                List<String> newFollowers = (List<String>) data.get("Followers");
                Log.d("NotifTime", newFollowers.toString());
                if(newFollowers.size() > followers.size()) {
                    List<String> added = new ArrayList<>(newFollowers);
                    added.removeAll(followers);
                    Log.d("NewLiksAdded", added.toString());
                    for(String follower: added){
                        broadcastIntent = new Intent();
                        broadcastIntent.setAction("com.example.Notification");
                        broadcastIntent.putExtra("FollowerId", follower);
                        Log.d("FollowerId", follower);
                        sendBroadcast(broadcastIntent);
                    }
                }
                followers = newFollowers;
            }
        });
    }

    public void getFollowers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Followers").document(FirebaseAuth.getInstance().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                followers = list;
            }
        });

    }
}
