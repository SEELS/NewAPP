package com.example.sameh.pushnotification.other;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sameh.pushnotification.R;

import java.util.ArrayList;

/**
 * Created by amina on 6/19/2018.
 */

public class RAdabter extends RecyclerView.Adapter<RAdabter.ViewHolder> {
    private ArrayList<ItemData> itemsData;
    public RAdabter(ArrayList<ItemData>  itemsData)
    {
        this.itemsData=itemsData;
    }

    @NonNull
    @Override
    public RAdabter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_txt_view,null);
        ViewHolder viewHolder= new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RAdabter.ViewHolder holder, int position) {

        holder.txtViewTitle.setText(itemsData.get(position).title);
        holder.imageViewIcon.setImageResource(itemsData.get(position).imageUrl);

    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txtViewTitle;
        public ImageView imageViewIcon;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = (TextView) itemLayoutView.findViewById(R.id.textView);
            imageViewIcon = (ImageView) itemLayoutView.findViewById(R.id.imageView);
        }
    }
}
