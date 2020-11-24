package com.example.simtradergpw.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.simtradergpw.ProfileFragment;
import com.example.simtradergpw.R;
import com.example.simtradergpw.WalletFragment;
import com.example.simtradergpw.Wig20Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hook on bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.act_main_bottomNavigationBar);
        // Set on-click listener
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Display default fragment (in this case Wig20Fragment)
        getSupportFragmentManager().beginTransaction().replace(R.id.act_main_fragment_container,
                new Wig20Fragment()).commit();

        

    }


    // Listener for bottomNavigationView
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.nav_wigList:
                    selectedFragment = new Wig20Fragment();
                    break;
                case R.id.nav_wallet:
                    selectedFragment = new WalletFragment();
                    break;
                case R.id.nav_profile:
                    selectedFragment = new ProfileFragment();
                    break;
            }
            // Change displayed fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.act_main_fragment_container,
                    selectedFragment).commit();
            return true;
        }
    };


}