package com.example.thebeast.afyahelp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {
    FirebaseAuth mAuth;
    NavigationView navigationView;
    String user_id;
    FirebaseFirestore mfirestore;
    TextView user_names,user_location,gallery,alert;
    CircleImageView user_profile_pic;
    Connection_Detector cd;
    int currentPost=0;
    Snackbar snackbar;
    String check_name,check_imageuri=null;

    CardView card_cpr,card_respiratory,card_stings,card_bleeding,card_circulatory,
            card_poison,card_heat,card_head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         snackbar = Snackbar.make(findViewById(R.id.drawer_layout), "no internet connection", Snackbar.LENGTH_INDEFINITE)
                 .setAction("Action", null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mailMe();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        mAuth = FirebaseAuth.getInstance();
        mfirestore = FirebaseFirestore.getInstance();


        cd=new Connection_Detector(this);


        gallery=(TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_forum));


        //referencing cardviews
        card_respiratory=findViewById(R.id.Respiratory_card);
        card_circulatory=findViewById(R.id.Circulatory_card);
        card_bleeding=findViewById(R.id.Bleeding_card);
        card_stings=findViewById(R.id.Stings_card);
        card_head=findViewById(R.id.Head_card);
        card_heat=findViewById(R.id.Heat_card);
        card_poison=findViewById(R.id.Poison_card);
        card_cpr=findViewById(R.id.CPR_card);


        card_circulatory.setOnClickListener(this);
        card_respiratory.setOnClickListener(this);
        card_poison.setOnClickListener(this);
        card_stings.setOnClickListener(this);
        card_bleeding.setOnClickListener(this);
        card_heat.setOnClickListener(this);
        card_head.setOnClickListener(this);
        card_cpr.setOnClickListener(this);

    }


    private void mailMe() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Send Mail");
        builder1.setMessage("Are you sure you want to send Mail");
        builder1.setCancelable(true);
        builder1.setIcon(R.mipmap.email_icon);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    @SuppressLint("MissingPermission")
                    public void onClick(DialogInterface dialog, int id) {
                        String[]recepients= {"fidelomolo7@gmail.com"};

                        Intent intent=new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_EMAIL,recepients);
                        intent.putExtra(Intent.EXTRA_SUBJECT,"App reviews, comments, complaints and ratings");
                        //EXTRA_SUBJECT,EXTRA_TEXT

                        intent.setType("message/rfc822");//opening email clients
                        startActivity(Intent.createChooser(intent,"Choose an email app to send mail"));
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();


    }



    //loads user profile details on the navigation drawer
    private void initializeCountDrawer(){
        //Gravity property aligns the text
        gallery.setGravity(Gravity.CENTER_VERTICAL);
        gallery.setTypeface(null, Typeface.BOLD);
        gallery.setTextColor(getResources().getColor(R.color.colorAccent));

        mfirestore.collection("Forum_Posts").addSnapshotListener(MainActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    // Log.w("Beast", "Listen failed.", e);

                    return;
                }
                if (!queryDocumentSnapshots.isEmpty()){

                    currentPost=queryDocumentSnapshots.size();
                    gallery.setText(Integer.toString(currentPost));
                }

            }
        });

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()==null){
            Intent intent=new Intent(MainActivity.this,Login.class);
            startActivity(intent);
        }


        try {
            if(!cd.isInternetConnected()){
                snackbar.setAction("DISMISS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }else


            {snackbar.dismiss();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        user_profile_details();
        //test();
        initializeCountDrawer();

    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

         if (id == R.id.nav_blood) {
            Intent intent=new Intent(MainActivity.this,BloodActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_forum) {


           // currentPost=0;
            Intent intent=new Intent(MainActivity.this,Forum_page.class);
            startActivity(intent);



        } else if (id == R.id.nav_nearby_hospital) {

            Intent intent=new Intent(MainActivity.this,MapsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_profile) {
            Intent intent=new Intent(MainActivity.this,MyAccount.class);
            startActivity(intent);


        } else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent=new Intent(MainActivity.this,Login.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_alerts) {
            Intent intent=new Intent(MainActivity.this,ViewAlerts.class);
            startActivity(intent);


        }else if (id == R.id.nav_ambulance) {
            Intent intent=new Intent(MainActivity.this,EmergencyLines.class);
            startActivity(intent);

        }else if (id == R.id.nav_website) {
            Intent intent=new Intent(MainActivity.this,FirstAidWeb.class);
            startActivity(intent);

        }else if (id == R.id.nav_first_aid_kit) {
            Intent intent=new Intent(MainActivity.this,FirstAid_Kit.class);
            startActivity(intent);
             /*Intent intent2=new Intent(MainActivity.this,DirectionsApi.class);
             startActivity(intent2);*/
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    public void user_profile_details(){
        if(mAuth.getCurrentUser()!=null){
            user_id=mAuth.getCurrentUser().getUid();
            mfirestore.collection("user_table").document(user_id).get().addOnCompleteListener(this,new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()){

                        if (task.getResult().exists()){

                            String fname=task.getResult().getString("fname");
                            String lname=task.getResult().getString("lname");
                            String location=task.getResult().getString("location");
                            check_imageuri=task.getResult().getString("imageuri");

                            //you need to inflate the header view as it is not inflated automatically .
                            View header = navigationView.getHeaderView(0);
                            user_names = (TextView) header.findViewById(R.id.main_name);
                            user_location=header.findViewById(R.id.main_location);
                            user_profile_pic=header.findViewById(R.id.main_profile_pic);

                            user_names.setText(fname+" "+lname);
                            user_location.setText(location);

                            RequestOptions placeHolder=new RequestOptions();
                            placeHolder.placeholder(R.drawable.profile_placeholder);

                            Glide.with(MainActivity.this).setDefaultRequestOptions(placeHolder).load(check_imageuri).into(user_profile_pic);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "No data has been saved", Toast.LENGTH_LONG).show();
                            Intent in=new Intent(getApplicationContext(),Profile.class);
                            startActivity(in);
                        }
                    }

                    else{
                        String exception=task.getException().getMessage();

                        Toast.makeText(MainActivity.this, "Data Retreival Error is: "+exception, Toast.LENGTH_LONG).show();

                    }


                }
            });


        }




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.with(getApplicationContext()).pauseRequests();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.Circulatory_card:
                Intent i=new Intent(getApplicationContext(),Circulatory_Problem.class);
                startActivity(i);

                break;
            case R.id.Respiratory_card:
                Intent in=new Intent(getApplicationContext(),Respiratory_problem.class);
                startActivity(in);

                break;
            case R.id.Poison_card:
                Intent in1=new Intent(getApplicationContext(),Poison_main.class);
                startActivity(in1);

                break;

            case R.id.Stings_card:
                Intent in2=new Intent(getApplicationContext(),Bites_problem.class);
                startActivity(in2);

                break;

            case R.id.Bleeding_card:
                Intent in3=new Intent(getApplicationContext(),Bleeding_main.class);
                startActivity(in3);

                break;
            case R.id.Heat_card:
                Intent in4=new Intent(getApplicationContext(),Heat_main.class);
                startActivity(in4);

                break;

            case R.id.Head_card:
                Intent in5=new Intent(getApplicationContext(),Head_main.class);
                startActivity(in5);

                break;

            case R.id.CPR_card:
                Intent in6=new Intent(getApplicationContext(),Cpr_main.class);
                startActivity(in6);

                break;


            default:
                    break;

        }

    }
}
