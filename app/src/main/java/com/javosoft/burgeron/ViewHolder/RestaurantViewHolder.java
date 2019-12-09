package com.javosoft.burgeron.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.javosoft.burgeron.Interface.ItemClickListener;
import com.javosoft.burgeron.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txt_restaurant_name;
    public ImageView img_restaurant;

    private ItemClickListener itemClickListener;
    public RestaurantViewHolder(View itemView) {
        super(itemView);

        txt_restaurant_name = (TextView) itemView.findViewById(R.id.restaurant_name);
        img_restaurant = (ImageView) itemView.findViewById(R.id.restaurant_image);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition(),false);
    }
}
