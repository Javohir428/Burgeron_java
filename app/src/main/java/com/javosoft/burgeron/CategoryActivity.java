package com.javosoft.burgeron;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.javosoft.burgeron.Database.Database;
import com.javosoft.burgeron.ViewHolder.CategoryViewHolder;
import com.javosoft.burgeron.common.Common;
import com.javosoft.burgeron.model.Category;
import com.javosoft.burgeron.model.Order;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView recycler_category;
    SwipeRefreshLayout swipeRefreshLayout;
    private MenuItem cart;


    FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
            .setQuery(FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Restaurants")
                    .child(Common.restaurantSelected)
                    .child("Category"), Category.class)
            .build();

    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder> (options){
        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_item, parent, false);
            return new CategoryViewHolder(itemView);
        }

        @Override
        protected void onBindViewHolder(@NonNull CategoryViewHolder viewHolder, int position, @NonNull Category model){
            viewHolder.txt_category_name.setText(model.getName());
            final Category clickItem = model;

            Picasso.get().load(model.getImage()).into(viewHolder.img_category);
            viewHolder.setItemClickListener((view, position1, isLongClick) -> {

                Intent intentFood = new Intent(CategoryActivity.this, FoodActivity.class);
                intentFood.putExtra("CategoryId", adapter.getRef(position).getKey());
                startActivity(intentFood);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });

        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#D00113"));
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        swipeRefreshLayout = findViewById(R.id.swipe_layout_category);
        swipeRefreshLayout.setColorSchemeResources(R.color.SplashBG,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) () -> {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if(Common.isInternetAvailable(getBaseContext())) {
                loadCategory();
            }
            else {
                StyleableToast.makeText(getBaseContext(), "Please check your connection!", Toast.LENGTH_SHORT, R.style.RegisterToast).show();
            }
        });
        swipeRefreshLayout.post(() ->{
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if(Common.isInternetAvailable(getBaseContext())) {
                loadCategory();
            }
            else {
                StyleableToast.makeText(getBaseContext(), "Please check your connection!", Toast.LENGTH_SHORT, R.style.RegisterToast).show();
            }
        });

        recycler_category = findViewById(R.id.recycler_category);
        recycler_category.setHasFixedSize(true);
        recycler_category.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.nav_cart) {
            Intent cart = new Intent(CategoryActivity.this, CartActivity.class);
            startActivity(cart);
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadCategory(){
        adapter.startListening();
        recycler_category.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

        if (recycler_category.getAdapter() != null) {
            recycler_category.getAdapter().notifyDataSetChanged();
            recycler_category.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_item_from_right));

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        Database db = new Database(getBaseContext());

        if (db.getCarts().isEmpty()){
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your cart will be cleaned")
                    .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("YES", (dialog, which) -> {
                        super.onBackPressed();
                        db.cleanCart();
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                    });
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Database db = new Database(getBaseContext());

        if (db.getCarts().isEmpty()){
            onBackPressed();
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your cart will be cleaned")
                    .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("YES", (dialog, which) -> {
                        onBackPressed();
                        db.cleanCart();
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                    });
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        return true;
    }
}
