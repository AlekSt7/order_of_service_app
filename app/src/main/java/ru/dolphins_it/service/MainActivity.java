package ru.dolphins_it.service;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import ru.dolphins_it.service.AboutCompany.AboutCompany;
import ru.dolphins_it.service.Auth.AuthActivity;
import ru.dolphins_it.service.Contacts.Contacts;
import ru.dolphins_it.service.DigitalSignage.Digital_signageCalculator;
import ru.dolphins_it.service.DigitalSignage.Digital_signance;
import ru.dolphins_it.service.Digital_Consultant.DigitalConsultant;
import ru.dolphins_it.service.E_Ocher.E_ocher;
import ru.dolphins_it.service.E_Ocher.E_ocherCalculator;
import ru.dolphins_it.service.News.News;
import ru.dolphins_it.service.TESOSQ.TESOSQ;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    SlidingUpPanelLayout mLayout;
    DrawerLayout drawer;
    Session session = new Session();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session.inicialize(this);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
                    E_ocherCalculator.header.setAnimation(null);
                }
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new News()).commit(); //Задаём фрагмент в Activity
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.news) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new News()).commit(); //Задаём фрагмент в Activity
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            getSupportActionBar().setTitle("Новости");
        } else if (id == R.id.about_company) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new AboutCompany()).commit(); //Задаём фрагмент в Activity
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            getSupportActionBar().setTitle("О компании");
        } else if (id == R.id.my_data) {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
        } else if (id == R.id.contacts) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Contacts()).commit(); //Задаём фрагмент в Activity
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        } else if (id == R.id.e_ocher) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new E_ocher()).commit(); //Задаём фрагмент в Activity
            if(session.session_exist()){
            if(!session.getAccessRight()) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().beginTransaction().replace(R.id.slidingUpFragment, new E_ocherCalculator()).commit(); //Задаём фрагмент в slidingUpPanel
                    }

                };

                thread.setPriority(1);
                thread.start();
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }}else{
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().beginTransaction().replace(R.id.slidingUpFragment, new E_ocherCalculator()).commit(); //Задаём фрагмент в slidingUpPanel
                    }

                };

                thread.setPriority(1);
                thread.start();
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }

            getSupportActionBar().setTitle("Электронная очередь");
        } else if (id == R.id.digital_signage) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new Digital_signance()).commit(); //Задаём фрагмент в Activity

            if(session.session_exist()){
            if(!session.getAccessRight()) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().beginTransaction().replace(R.id.slidingUpFragment, new Digital_signageCalculator()).commit(); //Задаём фрагмент в slidingUpPanel
                    }

                };

                thread.setPriority(1);
                thread.start();
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }}else{
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().beginTransaction().replace(R.id.slidingUpFragment, new Digital_signageCalculator()).commit(); //Задаём фрагмент в slidingUpPanel
                    }

                };

                thread.setPriority(1);
                thread.start();
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }

            getSupportActionBar().setTitle("Цифровые вывески");
        }else if (id == R.id.tesosq) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new TESOSQ()).commit(); //Задаём фрагмент в Activity
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            getSupportActionBar().setTitle("СОКОК");
        }else if (id == R.id.digital_consultant) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new DigitalConsultant()).commit(); //Задаём фрагмент в Activity
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            getSupportActionBar().setTitle("Электронный консультант");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
    }else{
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            Toast.makeText(getBaseContext(), "Нажмите ещё раз для выхода", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();}
    }

}
