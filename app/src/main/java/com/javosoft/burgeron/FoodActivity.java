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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.javosoft.burgeron.Database.Database;
import com.javosoft.burgeron.Interface.ItemClickListener;
import com.javosoft.burgeron.ViewHolder.FoodViewHolder;
import com.javosoft.burgeron.common.Common;
import com.javosoft.burgeron.model.Food;
import com.javosoft.burgeron.model.Order;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

public class FoodActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference foods;
    FirebaseRecyclerAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recycler_food;
    String categoryId;
    private MenuItem cart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        getWindow().setStatusBarColor(Color.parseColor("#D00113"));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Foods");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        swipeRefreshLayout = findViewById(R.id.swipe_layout_food);
        swipeRefreshLayout.setColorSchemeResources(R.color.SplashBG,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );

        //init firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        foods = firebaseDatabase.getReference()
                .child("Restaurants")
                .child(Common.restaurantSelected)
                .child("Foods");


        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
        }
        if (!categoryId.isEmpty() && categoryId == null) {
            loadFoods(categoryId);
        }


        recycler_food = findViewById(R.id.recycler_food);
        recycler_food.setHasFixedSize(true);
        recycler_food.setLayoutManager(new GridLayoutManager(this, 2));

        loadFoods(categoryId);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isInternetAvailable(getBaseContext())) {
                    adapter.stopListening();
                    loadFoods(categoryId);
                    adapter.startListening();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(getBaseContext(), "Please check your connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.nav_cart) {
            Intent cart = new Intent(FoodActivity.this, CartActivity.class);
            startActivity(cart);
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);

        }
        return super.onOptionsItemSelected(item);
    }

    //Helper Method
    private void loadFoods(String categoryId) {
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>().
                setQuery(foods.orderByChild("menuID").equalTo(categoryId), Food.class).build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, final int position, @NonNull final Food model) {
                TextView textViewName = holder.itemView.findViewById(R.id.food_name);
                TextView textViewPrice = holder.itemView.findViewById(R.id.food_list_price);
                ImageView imageView = holder.itemView.findViewById(R.id.food_image);

                textViewName.setText(model.getName());
                textViewPrice.setText(model.getPrice());
                Picasso.get().load(model.getImage()).into(imageView);

                holder.quick_add_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean isFoodInCart = new Database(getBaseContext()).checkFood(adapter.getRef(position).getKey());
                        if (!isFoodInCart) {
                            new Database(getBaseContext()).addToCart(new Order(
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    "1",
                                    model.getPrice(),
                                    model.getImage()
                            ));
                        } else {
                            new Database(getBaseContext()).increaseCart(adapter.getRef(position).getKey());
                        }
                        StyleableToast.makeText(FoodActivity.this, "Added to Cart", Toast.LENGTH_SHORT, R.style.CartAddToast).show();
                    }
                });

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Sending food_id to FoodDetailActivity
                        Intent intent = new Intent(FoodActivity.this, FoodDetailActivity.class);
                        intent.putExtra("foodId", adapter.getRef(position).getKey());
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });


            }
        };

        recycler_food.setAdapter(adapter);
        recycler_food.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_item_from_right));
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