package com.javosoft.burgeron.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.javosoft.burgeron.CartActivity;
import com.javosoft.burgeron.Database.Database;
import com.javosoft.burgeron.R;
import com.javosoft.burgeron.model.Order;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{
    private List<Order> listData = new ArrayList<>();
    private CartActivity cart;

    public CartAdapter(List<Order> listData, CartActivity cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_item,parent,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Picasso.get().load(listData.get(position)
                .getImage()).resize(70,70)
                .centerCrop()
                .into(holder.cart_image);

        holder.btn_quantity.setNumber(listData.get(position).getQuantity());
        holder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);

                //total price
                double total = 0;
                List<Order> orders = new Database(cart).getCarts();
                for(Order item:orders)
                    total+=(Double.parseDouble(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
                BigDecimal resultRounded = new BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP);

                DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
                symbols.setCurrencySymbol("");
                formatter.setDecimalFormatSymbols(symbols);

                cart.txtTotalPrice.setText(formatter.format(resultRounded));


                Locale locale = new Locale("en","US");
                NumberFormat ft = NumberFormat.getCurrencyInstance(locale);
                double price = (Double.parseDouble(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
                BigDecimal resultPriceRounded = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
                holder.txt_price.setText(ft.format(resultPriceRounded));
                holder.txt_cart_name.setText(listData.get(position).getProductName());

            }
        });

        Locale locale = new Locale("en","US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        double price = (Double.parseDouble(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
        BigDecimal resultPriceRounded = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        holder.txt_price.setText(fmt.format(resultPriceRounded));
        holder.txt_cart_name.setText(listData.get(position).getProductName());

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public Order getItem(int position){
        return listData.get(position);
    }

    public void removeItem(int position){
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Order item,int position){
        listData.add(position, item);
        notifyItemInserted(position);
    }
}