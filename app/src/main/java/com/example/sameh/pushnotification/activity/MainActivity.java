package com.example.sameh.pushnotification.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sameh.pushnotification.MapsActivity;
import com.example.sameh.pushnotification.R;
import com.example.sameh.pushnotification.Services.LocationService;
import com.example.sameh.pushnotification.other.SingleTon;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navigationView;
    SharedPreferences sharedPreferences;
    String driverId;
    String tripId;
    String token;
    Button start;
    Button stop;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        start = findViewById(R.id.startService);
        stop = findViewById(R.id.stopService);

        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF),Context.MODE_PRIVATE);
        driverId=sharedPreferences.getString("driverId","");

        if(!runtime_permissions())
            enable_buttons();
        getMyTrip();
        FirebaseMessaging.getInstance().subscribeToTopic("topicA");
        token = sharedPreferences.getString(getString(R.string.FCM_TOKEN),"");
        updateToken();

    }

    private void enable_buttons() {

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("start","start");
                Log.i("start","trip : "+sharedPreferences.getString("trip",""));
                Log.i("start","tripID : "+sharedPreferences.getString("tripId",""));
                if (!sharedPreferences.contains("roadId")) {
                    if (!sharedPreferences.contains("tripId")) {
                        getMyTrip();
                    }
                    else {
                        StartTrip();
                    }
                }
                else {
                    Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                    intent.putExtra("driverId",driverId);
                    intent.putExtra("tripId",tripId);
                    startActivity(intent);
                }

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences.contains("roadId"))
                    StopTrip();
                else
                    Toast.makeText(getApplicationContext(),"You not start Trip yet",Toast.LENGTH_LONG).show();
                Log.i("stop","stop");
                Intent i = new Intent(getApplicationContext(),LocationService.class);
                stopService(i);

            }
        });

    }

    private void StopTrip() {
        String tripId = sharedPreferences.getString("tripId","");
        String url = "https://seelsapp.herokuapp.com/endTrip/"+driverId+'/'+tripId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.has("Error"))
                    {
                        Toast.makeText(getApplicationContext(),response.getString("Error"),Toast.LENGTH_LONG).show();
                    }
                    else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("tripId");
                        editor.remove("roadId");
                        editor.commit();
                        String tripId = response.getString("Success");
                        Toast.makeText(getApplicationContext(),tripId,Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        SingleTon.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void getMyTrip() {
        String url = "https://seelsapp.herokuapp.com/driverTrip/"+driverId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.has("Error"))
                    {
                        Toast.makeText(getApplicationContext(),response.getString("Error"),Toast.LENGTH_SHORT).show();
                    }
                    else {
                        tripId = response.getString("Success");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("tripId", tripId);
                        editor.commit();
                        Toast.makeText(getApplicationContext(),"You can start Your Trip Now",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        SingleTon.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
    private void StartTrip() {
        String url = "https://seelsapp.herokuapp.com/startTrip/"+driverId+"/"+tripId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.has("Error"))
                    {
                        Toast.makeText(getApplicationContext(),response.getString("Error"),Toast.LENGTH_LONG).show();
                    }
                    else {
                        String roadId = response.getString("Success");
                        Toast.makeText(getApplicationContext(),roadId,Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("roadId", roadId);
                        editor.commit();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        SingleTon.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }else {
                runtime_permissions();
            }
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("driverId");
            editor.remove("password");
            editor.remove("roadId");
            editor.remove("tripId");
            editor.commit();
            Intent intent = new Intent(this,Login.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        if(id == R.id.nav_home) {
            intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_trip) {
            intent = new Intent(this,TripActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_rate) {
            intent = new Intent(this,RateActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_penalty) {
            intent = new Intent(this,PenaltyActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_profile) {
            intent = new Intent(this,ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_about_us) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void updateToken() {
        String app_server_url = "http://seelsapp.herokuapp.com/updateToken/"+driverId+"/"+token;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, app_server_url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error network Connection",Toast.LENGTH_LONG).show();
            }
        });
        SingleTon.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
