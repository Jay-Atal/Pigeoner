package com.example.atal_jbernardes_jfinalproject.Adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.atal_jbernardes_jfinalproject.Fragments.AddPost;
import com.example.atal_jbernardes_jfinalproject.Fragments.Explore;
import com.example.atal_jbernardes_jfinalproject.Fragments.Home;
import com.example.atal_jbernardes_jfinalproject.Fragments.LikedPosts;
import com.example.atal_jbernardes_jfinalproject.Fragments.Profile;
import com.example.atal_jbernardes_jfinalproject.Fragments.Settings;
import com.google.firebase.auth.FirebaseAuth;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0: return new Home();
            case 1: return new Explore();
            case 2: return new LikedPosts();
            case 3: return new AddPost();
            case 4: return new Profile(FirebaseAuth.getInstance().getUid());
            case 5: return new Settings();
            default: return new Home();
        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
