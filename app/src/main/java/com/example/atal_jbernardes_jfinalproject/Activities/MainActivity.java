package com.example.atal_jbernardes_jfinalproject.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.atal_jbernardes_jfinalproject.NotificationService.NotificationReceiver;
import com.example.atal_jbernardes_jfinalproject.NotificationService.NotificationService;
import com.example.atal_jbernardes_jfinalproject.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MainActivity extends AppCompatActivity {
    ImageView appLogo;
    private static boolean isHandlerExecuted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent serviceIntent = new Intent(this, NotificationService.class);
        startService(serviceIntent);

        BroadcastReceiver receiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.Notification");
        registerReceiver(receiver, filter, RECEIVER_EXPORTED);

        SharedPreferences appPreferences = getSharedPreferences("com.example.atal_jbernardes_jfinalproject",
                Context.MODE_PRIVATE);
        boolean useDarkMode = appPreferences.getBoolean("DARK_MODE", false);
        Log.v("THEME_MODE_MAIN_ACTIVITY", "" + useDarkMode);

        if (useDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Handler handler = new Handler();
        appLogo = findViewById(R.id.splashScreenImage);
        appLogo.setImageResource(R.drawable.pigoneer_logo);
        if (!isHandlerExecuted) {
            isHandlerExecuted = true;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                            .setPersistenceEnabled(true)
                            .build();
                    db.setFirestoreSettings(settings);
                    setContentView(R.layout.activity_main);
                    Intent intent = new Intent(MainActivity.this, SignIn.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        } else {
            setContentView(R.layout.activity_main);
        }
    }
}