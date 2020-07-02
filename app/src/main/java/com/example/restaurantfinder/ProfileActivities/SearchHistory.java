package com.example.restaurantfinder.ProfileActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.restaurantfinder.CuisineDialog;
import com.example.restaurantfinder.R;
import com.example.restaurantfinder.Search;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class SearchHistory extends AppCompatActivity {


    RecyclerView recyclerView;
    ImageView back,nosearchimg;
    int lastPosition=-1;
    FirebaseRecyclerAdapter<Search, SearchViewHolder> firebaseRecyclerAdapter;
    int[] veg = new int[]{R.drawable.chinesefood, R.drawable.northindian, R.drawable.southindianfood, R.drawable.italianfood, R.drawable.streetfood, R.drawable.drinks, R.drawable.sweets, R.drawable.deserts, R.drawable.healthyveg, R.drawable.vegthai, R.drawable.mexiconveg};
    int nonveg[] = new int[]{R.drawable.nonindianfood, R.drawable.seafood1, R.drawable.biryani, R.drawable.healthynonveg, R.drawable.thainonveg};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);


        back = findViewById(R.id.backbutonsearchhistory);
        recyclerView = findViewById(R.id.recyclerviewsearchhistory);

        nosearchimg=findViewById(R.id.nosearchhistoryimg);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FirebaseRecyclerOptions<Search> options = new FirebaseRecyclerOptions.Builder<Search>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Search History").orderByChild("timestamp"), new SnapshotParser<Search>() {
                    @NonNull
                    @Override
                    public Search parseSnapshot(@NonNull DataSnapshot snapshot) {

                        return new Search(snapshot.child("cuisine").getValue(String.class), snapshot.child("posi").getValue(String.class), snapshot.child("vegnonveg").getValue(String.class), snapshot.child("whichtype").getValue(String.class));
                    }
                }).build();


        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Search, SearchViewHolder>(options) {

            @Override
            public void onViewAttachedToWindow(@NonNull final SearchViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                if(recyclerView.getAdapter().getItemCount()!=0)
                {
                    nosearchimg.setVisibility(View.GONE);
                }
                else{
                    nosearchimg.setVisibility(View.VISIBLE);
                }

                holder.itemView.setVisibility(View.INVISIBLE);

                if (holder.getPosition() > lastPosition) {
                    holder.itemView.getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.itemView.setVisibility(View.VISIBLE);
                            ObjectAnimator alpha = ObjectAnimator.ofFloat(holder.itemView, "alpha", 0f, 1f);
                            ObjectAnimator scaleY = ObjectAnimator.ofFloat(holder.itemView, "scaleY", 0f, 1f);
                            ObjectAnimator scaleX = ObjectAnimator.ofFloat(holder.itemView, "scaleX", 0f, 1f);
                            AnimatorSet animSet = new AnimatorSet();
                            animSet.play(alpha).with(scaleY).with(scaleX);
                            animSet.setInterpolator(new OvershootInterpolator());
                            animSet.setDuration(400);
                            animSet.start();

                        }
                    }, 200);

                    lastPosition = holder.getPosition();
                } else {
                    holder.itemView.setVisibility(View.VISIBLE);
                }


            }

            @Override
            protected void onBindViewHolder(@NonNull SearchViewHolder searchViewHolder, int i, @NonNull final Search search) {
                searchViewHolder.cuisine.setText(search.getCuisine());
                searchViewHolder.searchres.setText("Searched For " + search.getWhichtype());

                final int p = Integer.parseInt(search.getPosi());

                if (search.getWhichtype().equals("restaurant")) {
                    searchViewHolder.searchres.setText("Searched For " + "Restaurant");

                    if (search.getVegnonveg().equals("veg")) {
                        searchViewHolder.searchimg.setImageResource(veg[p]);
                    } else  if (search.getVegnonveg().equals("nonveg")){
                        searchViewHolder.searchimg.setImageResource(nonveg[p]);
                    }
                } else if (search.getWhichtype().equals("dhaba")) {
                    searchViewHolder.searchres.setText("Searched For " + "Dhaba");
                    searchViewHolder.searchimg.setImageResource(R.drawable.dhaba);

                } else if (search.getWhichtype().equals("cafe")) {
                    searchViewHolder.searchres.setText("Searched For " + "Cafe");
                    searchViewHolder.searchimg.setImageResource(R.drawable.coffee);

                }

                searchViewHolder.repeat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (search.getVegnonveg().equals("veg")) {
                            CuisineDialog.display(getSupportFragmentManager(),search.getWhichtype(),p,veg,search.getCuisine());
                        } else  if (search.getVegnonveg().equals("nonveg")){
                            CuisineDialog.display(getSupportFragmentManager(),search.getWhichtype(),p,nonveg,search.getCuisine());
                        }
                    }
                });
            }

            @NonNull
            @Override
            public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.seachhistoryitem, null, true);
                view.setLayoutParams(new RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                ));

                return new SearchViewHolder(view);
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(SearchHistory.this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.alerter_slide_in_from_left,R.anim.alerter_slide_out_to_right);
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {

        ImageView searchimg;
        TextView searchres, cuisine;
        Button repeat;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);

            searchimg = itemView.findViewById(R.id.searchimage);
            searchres = itemView.findViewById(R.id.searchfor);
            cuisine = itemView.findViewById(R.id.searchcuisine);
            repeat = itemView.findViewById(R.id.searchrepeat);

        }
    }
}
