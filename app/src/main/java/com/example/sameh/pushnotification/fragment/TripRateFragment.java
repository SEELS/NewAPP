package com.example.sameh.pushnotification.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sameh.pushnotification.R;
import com.example.sameh.pushnotification.other.SingleTon;

import org.json.JSONException;
import org.json.JSONObject;

import me.itangqi.waveloadingview.WaveLoadingView;

public class TripRateFragment extends Fragment {


    private double rate;
    Context context;
    WaveLoadingView waveLoadingView;
    public TripRateFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = this.getActivity();
        getTripRate();
        View view = inflater.inflate(R.layout.fragment_driver_rate, container, false);
        //driverId = gIntent.getStringExtra("dirverId");
        waveLoadingView = view.findViewById(R.id.view);
        waveLoadingView.setProgressValue(4);
        Toast.makeText(context,"Trip",Toast.LENGTH_LONG).show();

        return view;
    }

    private void getTripRate(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        String tripId = sharedPreferences.getString("lastId","");
        if(tripId.equals(""))
        {
            Toast.makeText(context,"You are not in Trip now!!",Toast.LENGTH_LONG).show();
            return;
        }
        String url = "https://seelsapp.herokuapp.com/getTrip/"+tripId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(response.has("Error"))
                {
                    try {
                        String res = response.getString("Error");
                        Toast.makeText(context,res,Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try {
                        JSONObject object = response.getJSONObject("Success");
                        rate = object.getDouble("rate");
                        int vale = (int) ((rate*100)/5);
                        waveLoadingView.setProgressValue(vale);
                        waveLoadingView.setCenterTitle(vale+"%");
                        Toast.makeText(context,rate+"",Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"Error network Connection",Toast.LENGTH_LONG).show();
            }
        });
        SingleTon.getInstance(context).addToRequestQueue(request);
    }

}
