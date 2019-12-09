package com.javosoft.burgeron;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.javosoft.burgeron.ViewHolder.CategoryViewHolder;
import com.javosoft.burgeron.common.Common;
import com.javosoft.burgeron.model.Category;
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
                    .child("detail")
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

                Intent categoryList = new Intent(CategoryActivity.this, OrdersActivity.class);
                Common.categorySelected=adapter.getRef(position1).getKey();
                startActivity(categoryList);
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
            if(Common.isInternetAvailable(getBaseContext())) {
                loadCategory();
            }
            else {
                Toast.makeText(getBaseContext(), "Please check your connection!", Toast.LENGTH_SHORT).show();
            }
        });
        swipeRefreshLayout.post(() ->{
            if(Common.isInternetAvailable(getBaseContext())) {
                loadCategory();
            }
            else {
                Toast.makeText(getBaseContext(), "Please check your connection!", Toast.LENGTH_SHORT).show();
            }
        });


        recycler_category = findViewById(R.id.recycler_category);
        recycler_category.setHasFixedSize(true);
        recycler_category.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        cart = menu.findItem(R.id.nav_cart);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

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
    protected void onStop(){
        super.onStop();
        adapter.stopListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        return true;
    }
}
