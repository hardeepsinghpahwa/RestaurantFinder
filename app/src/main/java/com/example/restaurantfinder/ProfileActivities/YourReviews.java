package com.example.restaurantfinder.ProfileActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.restaurantfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class YourReviews extends AppCompatActivity {


    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ArrayList<Map<String,String>> values;
    ArrayList<String> keys;
    ImageView back,noreviewsimg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_reviews);

        values=new ArrayList<>();
        keys=new ArrayList<>();
        noreviewsimg=findViewById(R.id.noreviewsimg);

        back=findViewById(R.id.backbutonyourreviews);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.yourreviewsrecyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(YourReviews.this));

        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Reviews").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {


                    Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) dataSnapshot1.getValue();


                    for (String key : map.keySet()) {
                        HashMap<String, String> m = (HashMap<String, String>) map.get(key);

                        if(m.get("userid").equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        {
                            map.get(dataSnapshot1.getKey());

                            values.add(m);
                            keys.add(dataSnapshot1.getKey());
                        }

                    }

                }

                recyclerView.setAdapter(new YourReviewsAdapter(values,keys));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    class YourReviewsAdapter extends RecyclerView.Adapter<YourReviewsHol>
    {
        ArrayList<Map<String,String>> vals;

        ArrayList<String> kss;

        int lastPosition=-1;

        public YourReviewsAdapter(ArrayList<Map<String, String>> vals, ArrayList<String> kss) {
            this.vals = vals;
            this.kss = kss;
        }


        @Override
        public void onViewAttachedToWindow(@NonNull final YourReviewsHol holder) {
            super.onViewAttachedToWindow(holder);

            if(recyclerView.getAdapter().getItemCount()==0)
            {
                noreviewsimg.setVisibility(View.VISIBLE);
            }
            else {
                noreviewsimg.setVisibility(View.GONE);
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

        @NonNull
        @Override
        public YourReviewsHol onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.user_review_item, null, true);
            view.setLayoutParams(new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            ));

            return new YourReviewsHol(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final YourReviewsHol holder, int position) {


            Log.i("size", String.valueOf(vals.size()));

            HashMap<String,String> mp= (HashMap<String, String>) vals.get(position);

            holder.ratingBar.setRating(Float.parseFloat(mp.get("rating")));
            holder.review.setText(mp.get("review"));


            FirebaseDatabase.getInstance().getReference().child("Saved").child(kss.get(position)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    holder.name.setText("Wrote On "+dataSnapshot.child("buisnessname").getValue(String.class));
                    Glide.with(getApplicationContext()).load(dataSnapshot.child("profilepic").getValue(String.class)).into(holder.imageView);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        @Override
        public int getItemCount() {
            return vals.size();
        }
    }


    private class YourReviewsHol extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name,review;
        RatingBar ratingBar;

        public YourReviewsHol(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.userimage);
            name=itemView.findViewById(R.id.userresname);
            review=itemView.findViewById(R.id.userreview);
            ratingBar=itemView.findViewById(R.id.userrating);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.alerter_slide_in_from_left,R.anim.alerter_slide_out_to_right);
    }
}
