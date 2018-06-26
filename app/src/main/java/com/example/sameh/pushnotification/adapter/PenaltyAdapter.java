package com.example.sameh.pushnotification.adapter;

/**
 * Created by sameh on 6/23/2018.
 */


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

public class PenaltyAdapter extends RecyclerView.Adapter<PenaltyAdapter.ViewHolder> {

    private ArrayList<PenaltyItem> penaltyItems;
    private Context mContext;

    public PenaltyAdapter(ArrayList<PenaltyItem> penaltyItems, Context mContext) {
        this.penaltyItems = penaltyItems;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.penalty_card,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.description.setText(penaltyItems.get(position).getDescription());
        holder.type.setText(penaltyItems.get(position).getType());
        holder.typeImage.setImageResource(penaltyItems.get(position).getImageId());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,position+"",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return penaltyItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView typeImage;
        TextView  type;
        TextView  description;
        RelativeLayout layout;
        public ViewHolder(View itemView) {
            super(itemView);
            typeImage = itemView.findViewById(R.id.penalty_image);
            type = itemView.findViewById(R.id.penalty_type);
            description = itemView.findViewById(R.id.penalty_description);
            layout = itemView.findViewById(R.id.penalty_layout);
        }
    }
}
