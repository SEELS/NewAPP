package com.example.sameh.pushnotification.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sameh.pushnotification.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by sameh on 6/22/2018.
 */

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private ArrayList<TripItem> tripItems;
    private Context mContext;

    public TripAdapter(ArrayList<TripItem> tripItems, Context mContext) {
        this.tripItems = tripItems;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_card,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.TruckID.setText(tripItems.get(position).getTruckId());
        holder.Date.setText(tripItems.get(position).getDate());
        holder.Destinaion.setText(tripItems.get(position).getDestination());
        holder.ratingBar.setRating(tripItems.get(position).getRate());
        String url = "http://i.imgur.com/bIRGzVO.jpg";
        Picasso.with(mContext).load(url).into(holder.mapImage);
        // holder.mapImage.setImageBitmap(tripItems.get(position).getImage());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,position+"",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView mapImage;
        TextView  Date;
        TextView  Destinaion;
        TextView  TruckID;
        RatingBar ratingBar;
        RelativeLayout layout;
        public ViewHolder(View itemView) {
            super(itemView);
            mapImage = itemView.findViewById(R.id.trip_image);
            Date = itemView.findViewById(R.id.trip_date);
            Destinaion = itemView.findViewById(R.id.trip_Destination);
            TruckID = itemView.findViewById(R.id.trip_truck);
            layout = itemView.findViewById(R.id.trip_layout);
            ratingBar = itemView.findViewById(R.id.trip_rate);
        }
    }
}
