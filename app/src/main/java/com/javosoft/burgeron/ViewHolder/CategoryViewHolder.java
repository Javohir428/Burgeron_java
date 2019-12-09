package com.javosoft.burgeron.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.javosoft.burgeron.Interface.ItemClickListener;
import com.javosoft.burgeron.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txt_category_name;
    public ImageView img_category;

    private ItemClickListener itemClickListener;
    public CategoryViewHolder(View itemView) {
        super(itemView);

        txt_category_name = itemView.findViewById(R.id.category_name);
        img_category = itemView.findViewById(R.id.category_image);

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
