package com.example.sameh.pushnotification.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sameh.pushnotification.AboutUsActivity;
import com.example.sameh.pushnotification.R;
import com.example.sameh.pushnotification.adapter.PenaltyAdapter;
import com.example.sameh.pushnotification.adapter.PenaltyItem;
import com.example.sameh.pushnotification.adapter.TripAdapter;
import com.example.sameh.pushnotification.adapter.TripItem;
import com.example.sameh.pushnotification.other.SingleTon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PenaltyActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<PenaltyItem> items;
    String tripId;


    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penalty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_penalty);

//        ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
//        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//        pagerAdapter.addFragment(new DriverRateFragment(),"Trip");
//        pagerAdapter.addFragment(new TripRateFragment(),"All");
//        viewPager.setAdapter(pagerAdapter);
//
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // specify an adapter (see also next example)
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF),Context.MODE_PRIVATE);
        tripId=sharedPreferences.getString("lastId","");

        items = new ArrayList<>();
        // fill items
        getItemsData(this);
//        for (int i=0;i<100;i++) {
//            PenaltyItem item = new PenaltyItem();
//            item.setDescription("Description sajgjagfgsagfyuasyfyusafuyasfyufasyfuaf");
//            item.setType("Speed Limit");
//            item.setImageId(R.drawable.speed);
//            items.add(item);
//        }


    }

    private void getItemsData(final Context mContext) {
        // fill items

        String Url = "http://seelsapp.herokuapp.com/getPenaltiesByTrip/"+tripId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject responseObject) {
                try {
                    if (responseObject.has("Error"))
                    {
                        Toast.makeText(getApplicationContext(),responseObject.getString("Error"),Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        JSONArray response = responseObject.getJSONArray("Success");
                        for (int i=0;i<response.length();i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String type = jsonObject.getString("type");
                            String value = jsonObject.getString("value");
                            PenaltyItem item = new PenaltyItem();
                            item.setType(type);
                            item.setImageId(R.drawable.speed);
                            // change based on type
                            String Desc = "You have breack speed Limit with Speed"+value;
                            item.setDescription(Desc);
                            items.add(item);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                mAdapter = new PenaltyAdapter(items,mContext);
                mRecyclerView.setAdapter(mAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        SingleTon.getInstance(getApplicationContext()).addToRequestQueue(request);
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
            editor.remove("trip");
            editor.remove("tripId");
            editor.remove("lastId");
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
            finish();
        } else if (id == R.id.nav_trip) {
            intent = new Intent(this,TripActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_rate) {
            intent = new Intent(this,RateActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_penalty) {
            intent = new Intent(this,PenaltyActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_about_us) {
            intent = new Intent(this,AboutUsActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
