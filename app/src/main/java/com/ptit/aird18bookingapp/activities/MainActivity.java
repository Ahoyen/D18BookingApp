package com.ptit.aird18bookingapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.ptit.aird18bookingapp.R;
import com.ptit.aird18bookingapp.adapters.PostAdapter;
import com.ptit.aird18bookingapp.fragments.FavoriteFragment;
import com.ptit.aird18bookingapp.fragments.HomeFragment;
import com.ptit.aird18bookingapp.fragments.NotificationFragment;
import com.ptit.aird18bookingapp.fragments.Settings2Fragment;
import com.ptit.aird18bookingapp.fragments.SettingsFragment;
import com.ptit.aird18bookingapp.models.Notification;
import com.ptit.aird18bookingapp.utils.Constants;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ptit.aird18bookingapp.utils.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    Settings2Fragment settingsFragment = new Settings2Fragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    FavoriteFragment favoriteFragment = new FavoriteFragment();
    Locale mLocale;
    BadgeDrawable badgeDrawable;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
//        preferenceManager.clear();
        if(LocaleList.getDefault().get(0).getLanguage().equals("en")){
            mLocale=new Locale("en");
        }else {
            mLocale=new Locale("vi");
        }

        if(this.getResources().getConfiguration().locale.equals("en")){

        }else {

        }

        Locale.setDefault(mLocale);
        Configuration config = new Configuration();
        config.locale = mLocale;
        this.getResources().updateConfiguration(config,
                this.getResources().getDisplayMetrics());
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.notification);



        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                        return true;
                    case R.id.notification:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, notificationFragment).commit();
                        return true;
                    case R.id.favorite:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, favoriteFragment).commit();
                        return true;
                    case R.id.settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, settingsFragment).commit();
                        return true;
                }

                return false;
            }
        });

        //Hide status bar and navigation bar at the bottom
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        );


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "POST_NOTIFICATIONS permission granted");
            } else {
                Log.w("Permission", "POST_NOTIFICATIONS permission denied");
                // Nếu cần, bạn có thể hiện một thông báo cho người dùng ở đây
            }
        }
    }



}