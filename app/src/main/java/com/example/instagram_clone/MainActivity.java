package com.example.instagram_clone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.instagram_clone.activity.EditProfileActivity;
import com.example.instagram_clone.activity.ProfileActivity;
import com.example.instagram_clone.fragment.FeedFragment;
import com.example.instagram_clone.fragment.PostFragment;
import com.example.instagram_clone.fragment.SearchFragment;
import com.example.instagram_clone.model.user.User;
import com.example.instagram_clone.model.user.UserHelper;
import com.example.instagram_clone.utils.Constants;
import com.example.instagram_clone.utils.FirebaseConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity;
    private FirebaseAuth auth = FirebaseConfig.getAuth();
    private BottomNavigationViewEx bottomNavigation;

    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // instance of Main Activity to finish it if uses want to log out from ProfileActivity
        mainActivity = this;
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

        bottomNavigation = findViewById(R.id.bottomNavigation);
        // animations
        bottomNavigation.enableAnimation(false);
        bottomNavigation.enableItemShiftingMode(false);
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
        loggedUser = UserHelper.getLogged();
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
                        fragmentTransaction.commit(); // finish fragment transaction with no action
                        Intent intent  = new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra(Constants.IntentKey.SELECTED_USER, loggedUser);
                        startActivityForResult(intent, Constants.FeatureRequest.NAV_BOTTOM_CODE);
                        overridePendingTransition(0, 0); // Cancel transition animation
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.FeatureRequest.NAV_BOTTOM_CODE) {
                int tabId = data.getIntExtra(Constants.IntentKey.NAV_BOTTOM, R.id.icHome);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (tabId) {
                    case R.id.icHome:
                        fragmentTransaction.replace(R.id.viewPagerFrameLayout, new FeedFragment()).commit();
                        break;
                    case R.id.icSearch:
                        fragmentTransaction.replace(R.id.viewPagerFrameLayout, new SearchFragment()).commit();
                        break;
                    case R.id.icAddPicture:
                        fragmentTransaction.replace(R.id.viewPagerFrameLayout, new PostFragment()).commit();
                        break;
                }
            }
        }
    }
}