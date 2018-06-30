package com.example.sameh.pushnotification.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class DriverRateFragment extends Fragment {

    private double rate;
    Context context;
    WaveLoadingView waveLoadingView;
    public DriverRateFragment() {
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
        getDriverRate();
        View view = inflater.inflate(R.layout.fragment_driver_rate, container, false);
        //driverId = gIntent.getStringExtra("dirverId");
        waveLoadingView = view.findViewById(R.id.view);
        waveLoadingView.setProgressValue(4);
        int vale = (int) ((rate*100)/5);
        waveLoadingView.setProgressValue(vale);
        waveLoadingView.setCenterTitle(vale+"%");

        return view;
    }

    private void getDriverRate()
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        String driverId = sharedPreferences.getString("driverId","");
        Toast.makeText(context,"Driver Rate",Toast.LENGTH_SHORT).show();
        String url = "https://seelsapp.herokuapp.com/getDriver/"+driverId;
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
