package com.example.simtradergpw.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.simtradergpw.DatabaseConnection;
import com.example.simtradergpw.ProfileFragment;
import com.example.simtradergpw.R;
import com.example.simtradergpw.TransactionsFragment;
import com.example.simtradergpw.WalletFragment;
import com.example.simtradergpw.Wig20Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if connection is ok
        checkConnection();

        // Hook on bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.act_main_bottomNavigationBar);
        // Set on-click listener
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Display default fragment (in this case Wig20Fragment)
        getSupportFragmentManager().beginTransaction().replace(R.id.act_main_fragment_container,
                new Wig20Fragment()).commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if connection is ok
        checkConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if connection is ok
        checkConnection();
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
                 case R.id.nav_transactions:
                    selectedFragment = new TransactionsFragment();
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

    private void checkConnection() {
        try {
            if (DatabaseConnection.getConnection() == null || DatabaseConnection.getConnection().isClosed()) {
                DatabaseConnection.setConnection();
            }
        } catch (SQLException throwables) {
            Toast.makeText(this, throwables.getMessage(), Toast.LENGTH_LONG).show();
            throwables.printStackTrace();
        }
    }
}