package com.example.sameh.pushnotification;

import android.*;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sameh.pushnotification.Services.LocationService;
import com.example.sameh.pushnotification.other.SingleTon;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_REQUEST=500;
    private ArrayList<LatLng> locations;
    private double rad;
    private Circle circle;
    private Map<Integer,Marker> nearestTrucks;
    private Map<Integer,ArrayList<LatLng>> nearestTrucksPolylines;
    private ArrayList<Integer> drivers;
    SharedPreferences sharedPreferences;
    String roadId;
    private  boolean start = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent i =new Intent(getApplicationContext(),LocationService.class);
        startService(i);
        nearestTrucks = new HashMap<>();
        drivers = new ArrayList<>();
        nearestTrucksPolylines = new HashMap<>();
        getTrip();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        // get Location permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST);
            return;
        }
        // get my location
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        CircleOptions options = new CircleOptions();
        options.center(new LatLng(30.5,31.5));
        options.radius(0.01);
        circle = mMap.addCircle(options);
        /*MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(markerOptions);*/

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // mMap.clear();
                if (mMap.getMyLocation()!=null) {
                    start = true;
                    Location l = mMap.getMyLocation();
                    LatLng temp = new LatLng(l.getLatitude(), l.getLongitude());
                    circle.setCenter(temp);
                    rad = getDistance(latLng, temp);
                    circle.setRadius(rad);
                    circle.setStrokeColor(Color.WHITE);
                    circle.setFillColor(Color.CYAN);
                    TruckTask task = new TruckTask();
                    task.execute(new String[]{""});

                }
            }
        });


        /*// Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case LOCATION_REQUEST:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    mMap.setMyLocationEnabled(true);
                }
                break;
        }
    }

    public void getTrip() {
        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        roadId=sharedPreferences.getString("roadId","");

        String url = "http://seelsapp.herokuapp.com/getRoad/"+roadId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject Objectresponse) {
                try {
                    if(Objectresponse.has("Error"))
                    {
                        Toast.makeText(getApplicationContext(),Objectresponse.getString("Error"),Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        JSONArray response = Objectresponse.getJSONArray("Success");
                        LatLng temp;
                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.BLUE);
                        LatLng[] points = new LatLng[response.length()];
                        for (int i=0;i<response.length();i++)
                        {

                            JSONObject jsonObject = response.getJSONObject(i);
                            double lat = jsonObject.getDouble("lat");
                            double lon = jsonObject.getDouble("lon");
                            temp = new LatLng(lat,lon);
                            points[i] = temp;
                        }
                        polylineOptions.add(points);
                        mMap.addPolyline(polylineOptions);

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
        SingleTon.getInstance(MapsActivity.this).addToRequestQueue(request);

    }

    public void getNearestTrucks() {
        String driverId = getIntent().getStringExtra("driverId");
        String url = "http://seelsapp.herokuapp.com/getNearLocation/"+driverId+"/"+rad+"";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject ObjectResponse) {
                try {
                    if(ObjectResponse.has("Success"))
                    {
                        JSONArray response = ObjectResponse.getJSONArray("Success");
                        Log.i("tag", response.length() + " res");
                        LatLng temp;
                        //mMap.clear();

                        drivers.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            JSONObject driver = jsonObject.getJSONObject("driver");
                            int driver_id = driver.getInt("driver_id");
                            double lat = jsonObject.getDouble("lat");
                            double lon = jsonObject.getDouble("lon");
                            temp = new LatLng(lat, lon);
                            ArrayList<LatLng> tr;
                            if (!nearestTrucksPolylines.containsKey(driver_id))
                                nearestTrucksPolylines.put(driver_id, new ArrayList<LatLng>());
                            tr = nearestTrucksPolylines.get(driver_id);
                            tr.add(temp);
                            drivers.add(driver_id);
                            nearestTrucksPolylines.put(driver_id, tr);
                            if(!nearestTrucks.containsKey(driver_id)){
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(temp);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                markerOptions.title(driver.getString("name"));
                                nearestTrucks.put(driver_id, mMap.addMarker(markerOptions));
                            }else
                            {
                                Marker marker = nearestTrucks.get(driver_id);
                                marker.setPosition(temp);
                            }

                        }
                        LatLng lng;
                        Location l;
                        if (mMap.getMyLocation() != null) {
                            l = mMap.getMyLocation();
                            lng = new LatLng(l.getLatitude(), l.getLongitude());
                            circle.setCenter(lng);
                            Log.i("MapsActivity", "center Circle Change");
                        }
                        for(Map.Entry<Integer,Marker> entry :nearestTrucks.entrySet())
                        {
                            if (!drivers.contains(entry.getKey()))
                            {
                                entry.getValue().remove();
                            }
                        }

                        for (int i=0;i<drivers.size();i++)
                        {
                            PolylineOptions polylineOptions = new PolylineOptions();
                            polylineOptions.color(Color.RED);
                            ArrayList<LatLng> locations = nearestTrucksPolylines.get(drivers.get(i));
                            polylineOptions.addAll(locations);
                            mMap.addPolyline(polylineOptions);
                        }
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
        SingleTon.getInstance(MapsActivity.this).addToRequestQueue(request);

    }


    private double rad (double x) {
        return x * Math.PI / 180;
    };

    private double getDistance (LatLng p1, LatLng p2) {
        double R = 6378137; // Earth’s mean radius in meter
        double dLat = rad(p2.latitude - p1.latitude);
        double dLong = rad(p2.longitude - p1.longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(rad(p1.latitude)) * Math.cos(rad(p2.latitude)) *
                        Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d; // returns the distance in meter
    }

    private class TruckTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... strings) {
            while (start)
            {
                try {
                    Thread.sleep(2000);
                    getNearestTrucks();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return  null;
        }
    }


    @Override
    protected void onPause() {
        start = false;
        super.onPause();
    }



    @Override
    public void onBackPressed() {
        start = false;

        super.onBackPressed();

    }


}
