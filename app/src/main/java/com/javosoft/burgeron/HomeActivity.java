package com.javosoft.burgeron;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.javosoft.burgeron.Database.Database;
import com.javosoft.burgeron.ViewHolder.RestaurantViewHolder;
import com.javosoft.burgeron.common.Common;
import com.javosoft.burgeron.model.Restaurant;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private boolean isSinglePressed;


    FirebaseRecyclerOptions<Restaurant> options = new FirebaseRecyclerOptions.Builder<Restaurant>()
            .setQuery(FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Restaurants"), Restaurant.class)
            .build();

    FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder> adapter = new FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder> (options){
        @Override
        public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.restaurant_item, parent, false);
            return new RestaurantViewHolder(itemView);
        }

        @Override
        protected void onBindViewHolder(@NonNull RestaurantViewHolder viewHolder, int position, @NonNull Restaurant model){
            viewHolder.txt_restaurant_name.setText(model.getName());
            final Restaurant clickItem = model;

            Picasso.get().load(model.getImage()).into(viewHolder.img_restaurant);
            viewHolder.setItemClickListener((view, position1, isLongClick) -> {

                Intent categoryList = new Intent(HomeActivity.this, CategoryActivity.class);
                Common.restaurantSelected=adapter.getRef(position1).getKey();
                startActivity(categoryList);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Restaurants");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        TextView nav_user = hView.findViewById(R.id.nameUser);
        nav_user.setText(Common.currentUser.getName());


        swipeRefreshLayout = findViewById(R.id.swipe_layout);
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
                loadRestaurant();
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
                loadRestaurant();
            }
            else {
                StyleableToast.makeText(getBaseContext(), "Please check your connection!", Toast.LENGTH_SHORT, R.style.RegisterToast).show();
            }
        });

        recyclerView = findViewById(R.id.recycler_restaurant);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


    }

    private void loadRestaurant(){
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);


        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_item_from_right));

        }

    }

    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
        Database db = new Database(getBaseContext());
        db.cleanCart();

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Database db = new Database(getBaseContext());
        db.cleanCart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            if (isSinglePressed) {
                super.onBackPressed();
            } else {
                isSinglePressed = true;
                Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> isSinglePressed = false, 2000);
            }
        }


    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            Intent ordersIntent = new Intent(HomeActivity.this, OrdersActivity.class);
            startActivity(ordersIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_contact)  {
            Intent contactIntent = new Intent(HomeActivity.this, ContactActivity.class);
            startActivity(contactIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (id == R.id.nav_sign_out){
            signOut();
        }
        return true;
    }

    private void signOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sign Out")
                .setMessage("Are you sure?")
                .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("YES", (dialog, which) -> {
                    Common.currentUser = null;
                    FirebaseAuth.getInstance().signOut();
                    Intent signIn = new Intent(HomeActivity.this, MainActivity.class);
                    signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(signIn);
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}