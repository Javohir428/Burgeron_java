package com.javosoft.burgeron;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.javosoft.burgeron.Database.Database;
import com.javosoft.burgeron.Helper.RecyclerItemTouchHelper;
import com.javosoft.burgeron.Interface.RecyclerItemTouchHelperListener;
import com.javosoft.burgeron.ViewHolder.CartAdapter;
import com.javosoft.burgeron.ViewHolder.CartViewHolder;
import com.javosoft.burgeron.common.Common;
import com.javosoft.burgeron.model.Order;
import com.javosoft.burgeron.model.Request;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests;

    RelativeLayout rootLayout;
    public TextView txtTotalPrice;
    Button btnGenerate;


    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getWindow().setStatusBarColor(Color.parseColor("#D00113"));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Cart");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Orders");

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.multimedia_rollover_068);

        rootLayout = findViewById(R.id.rootLayout);

        //Init


        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        txtTotalPrice = (TextView)findViewById(R.id.total);
        btnGenerate = (Button)findViewById(R.id.btnGenerate);

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date(System.currentTimeMillis()));


                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        txtTotalPrice.getText().toString(),
                        currentDateandTime,
                        Common.currentRestaurantName,
                        cart
                );
                String key = requests.push().getKey();

                requests.child(key).setValue(request);

                mp.start();

                Intent intentQR = new Intent(CartActivity.this, QrActivity.class);
                intentQR.putExtra("QR_key", key);
                startActivity(intentQR);

                new Database(getBaseContext()).cleanCart();
                finish();
            }
        });

        loadListFood();
    }



    private void loadListFood() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart,this);
        recyclerView.setAdapter(adapter);

        //total price
        double total = 0;
        for(Order order:cart)
            total+=(Double.parseDouble(order.getPrice()))*(Integer.parseInt(order.getQuantity()));

        BigDecimal resultRounded = new BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP);
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setCurrencySymbol("");
        formatter.setDecimalFormatSymbols(symbols);

        txtTotalPrice.setText(formatter.format(resultRounded));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder){
            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            Order deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId());

            double total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts();
            for(Order item:orders)
                total+=(Double.parseDouble(item.getPrice()))*(Integer.parseInt(item.getQuantity()));

            BigDecimal resultRounded = new BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP);
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            symbols.setCurrencySymbol("");
            formatter.setDecimalFormatSymbols(symbols);

            txtTotalPrice.setText(formatter.format(resultRounded));

            Snackbar snackbar = Snackbar.make(rootLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem, deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);


                    float total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCarts();
                    for(Order item:orders)
                        total+=(Double.parseDouble(item.getPrice()))*(Integer.parseInt(item.getQuantity()));

                    BigDecimal resultRounded = new BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP);
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                    DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
                    symbols.setCurrencySymbol("");
                    formatter.setDecimalFormatSymbols(symbols);

                    txtTotalPrice.setText(formatter.format(resultRounded));
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}