package com.example.sameh.pushnotification.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sameh.pushnotification.R;



public class TripShow extends Fragment {
    Context context;
    public TripShow() {
        // Required empty public constructor
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {

        context = this.getContext();
        View view = inflater.inflate(R.layout.fragment_trip_show, container, false);

        final ListView listView = (ListView) view.findViewById(R.id.list);
        String[] values = {

                "Sameh","Osama","Ebrahem"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,android.R.id.text1
        ,values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = listView.getItemAtPosition(position).toString();
                Toast.makeText(context,value,Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }
}
