package com.javosoft.burgeron.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.javosoft.burgeron.Interface.ItemClickListener;
import com.javosoft.burgeron.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txt_food_name;
    public ImageView img_food;
    public Button quick_add_button;

    private ItemClickListener itemClickListener;
    public FoodViewHolder(View itemView) {
        super(itemView);

        txt_food_name = itemView.findViewById(R.id.food_name);
        img_food = itemView.findViewById(R.id.food_image);
        quick_add_button = itemView.findViewById(R.id.addBtnCart);

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

