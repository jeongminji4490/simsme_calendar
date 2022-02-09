package com.example.mycustomcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView main_navi;
    private NavigationView navi_view;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private CalendarPage fragmentCalendar = new CalendarPage();
    private UpcomingPage fragmentUpcoming = new UpcomingPage();
    private TimerPage fragmentTimer = new TimerPage();
    private LicensePage fragmentLicense = new LicensePage();
    private ImageButton menuBtn;
    private DrawerLayout drawerLayout;
    private long backPressedTime=0;
    //private TrashPage fragmentTrash = new TrashPage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction fTransaction = fragmentManager.beginTransaction();
        fTransaction.replace(R.id.frame_layout, fragmentCalendar).commitAllowingStateLoss();
        main_navi = (BottomNavigationView)findViewById(R.id.main_underbar);
        main_navi.setItemIconTintList(null);
        main_navi.setOnNavigationItemSelectedListener(new ItemSelectedListener());
        menuBtn=(ImageButton) findViewById(R.id.menuBtn);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navi_view=(NavigationView)findViewById(R.id.navi_view);
        navi_view.setItemIconTintList(null);

        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navi_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fTransaction = fragmentManager.beginTransaction();
                int id=item.getItemId();
                if (id==R.id.license_btn){
//                    Intent intent=new Intent(MainActivity.this,LicensePage.class);
//                    startActivity(intent);
                    fTransaction.replace(R.id.frame_layout, fragmentLicense).commitAllowingStateLoss();
                    drawerLayout.close();
                }
                return false;
            }
        });


    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()){
                case R.id.navi_calendar:
                    fTransaction.replace(R.id.frame_layout, fragmentCalendar).commitAllowingStateLoss();
                    break;
                case R.id.navi_upcoming:
                    fTransaction.replace(R.id.frame_layout, fragmentUpcoming).commitAllowingStateLoss();
                    break;
                case R.id.navi_timer:
                    fTransaction.replace(R.id.frame_layout, fragmentTimer).commitAllowingStateLoss();
            }
            return true;
        }
    }

    //뒤로가기버튼 2번 누르면 앱 종료
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis()>backPressedTime+2000){
            backPressedTime=System.currentTimeMillis();
            return;
        }
        if (System.currentTimeMillis()<=backPressedTime+2000){
            finish();
        }
    }
}