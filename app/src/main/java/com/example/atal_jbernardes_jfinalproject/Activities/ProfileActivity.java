package com.example.atal_jbernardes_jfinalproject.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.atal_jbernardes_jfinalproject.Fragments.Profile;
import com.example.atal_jbernardes_jfinalproject.R;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle bundle = getIntent().getExtras();
        String profileId  = bundle.getString("userId");
        Fragment exampleFragment = new Profile(profileId);
        exampleFragment.setArguments(getIntent().getExtras());


        // Use FragmentManager and FragmentTransaction to add the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);


        for(Fragment fragment: fragmentManager.getFragments()) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.replace(R.id.fragmentContainerView, exampleFragment);
//        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }
}