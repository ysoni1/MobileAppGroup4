package com.gsu.csc.petman;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;

    private TextView txtUsername;
    private TextView txtEmail;
    private GlobalVariables globalVariables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_pet:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PetFragment()).commit();
                        break;

                    case R.id.nav_doctor:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new UnderConstructionFragment()).commit();
                        break;

                    case R.id.nav_task:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new TaskFragment()).commit();
                        break;

                    case R.id.nav_advisor:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new UnderConstructionFragment()).commit();
                        break;

                    case R.id.nav_user:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new UnderConstructionFragment()).commit();
                        break;

                    case R.id.nav_logout:
                        performLogoutAction();
                        break;

                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        // first time - setting default activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FrontPageFragment()).commit();
            //navigationView.setCheckedItem(R.id.nav_pet);
        }

        // navigation header
        GlobalVariables globalVariables = (GlobalVariables) getApplicationContext();
        View header = navigationView.getHeaderView(0);

        txtUsername = (TextView) header.findViewById(R.id.nav_header_username);
        txtEmail = (TextView) header.findViewById(R.id.nav_header_email);

        txtUsername.setText( globalVariables.getUsername());
        txtEmail.setText(globalVariables.getEmail());


    }

    @Override
    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void performLogoutAction() {
        AlertDialog.Builder alertDialogBuilder;

        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_btn_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        globalVariables.reset();

                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                })
                .setNegativeButton(R.string.dialog_btn_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
