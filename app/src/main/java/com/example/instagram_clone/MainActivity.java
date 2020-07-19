package com.example.instagram_clone;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.instagram_clone.fragment.FeedFragment;
import com.example.instagram_clone.fragment.PostFragment;
import com.example.instagram_clone.fragment.ProfileFragment;
import com.example.instagram_clone.fragment.SearchFragment;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseConfig.getAuth();
    private BottomNavigationViewEx bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Instagram Clone");
        setSupportActionBar(toolbar);

        initActivity();
        configBottomView();
    }
    private void configBottomView() {
        int FEED_INDEX = 0;
        try {
            bottomNavigation = findViewById(R.id.bottomNavigation);
        } catch (Exception e) {
            Log.e("TAG", "configBottomView: " + e.getMessage() );
        }
        // animations
        bottomNavigation.enableAnimation(true);
        bottomNavigation.enableItemShiftingMode(true);
        bottomNavigation.enableShiftingMode(false);
        bottomNavigation.setTextVisibility(false);

        // Enable navigation
        enableNavigation(bottomNavigation);

        // Selecte feed tab
        Menu menu = bottomNavigation.getMenu();
        MenuItem menuItem = menu.getItem(FEED_INDEX);
        menuItem.setChecked(true);
    }

    private void initActivity() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.viewPagerFrameLayout, new FeedFragment()).commit();
    }

    /**
     * Method that treats click events on BottomNavigationViewEx buttons
     * @param viewEx (bottom menu itens)
     * @return
     */
    private void enableNavigation(BottomNavigationViewEx viewEx) {
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.icHome:
                        fragmentTransaction.replace(R.id.viewPagerFrameLayout, new FeedFragment()).commit();
                        return true;
                    case R.id.icSearch:
                        fragmentTransaction.replace(R.id.viewPagerFrameLayout, new SearchFragment()).commit();
                        return true;
                    case R.id.icAddPicture:
                        fragmentTransaction.replace(R.id.viewPagerFrameLayout, new PostFragment()).commit();
                        return true;
                    case R.id.icProfile:
                        fragmentTransaction.replace(R.id.viewPagerFrameLayout, new ProfileFragment()).commit();
                        return true;
                    default:
                        return false;
                }
            };
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogOut:
                auth.signOut();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}