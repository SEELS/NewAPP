package com.example.sameh.pushnotification.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.sameh.pushnotification.R;
import com.example.sameh.pushnotification.other.ItemData;
import com.example.sameh.pushnotification.other.RAdabter;
import com.example.sameh.pushnotification.other.SingleTon;

import org.json.JSONObject;

import java.util.ArrayList;


public class TripsShow extends Fragment {

    private Context mContext;
    private ArrayList<ItemData> itemData;

    public TripsShow() {
        // Required empty public constructor
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trips_show,null);
        mContext = this.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        itemData = new ArrayList<>();
        getItemsData();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        RAdabter adapt =new RAdabter(itemData);
        recyclerView.setAdapter(adapt);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void getItemsData() {
        // fill item
        ItemData item = new ItemData("Amina",R.drawable.rec);
        itemData.add(item);
        return;
//        String Url = "";
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//        SingleTon.getInstance(mContext).addToRequestQueue(request);
    }
}
