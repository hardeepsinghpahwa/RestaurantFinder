package com.example.restaurantfinder.ProfileActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.restaurantfinder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tapadoo.alerter.Alerter;
import com.varunest.sparkbutton.SparkButton;

import java.util.ArrayList;

public class Bookmarks extends AppCompatActivity {


    LottieAnimationView lottieAnimationView;
    ImageView back;
    TextView nobookmarks;
    RecyclerView recyclerView;
    ArrayList<String> bookmarks;
    String type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        bookmarks = new ArrayList<>();

        lottieAnimationView = findViewById(R.id.bookmarkanimation);

        nobookmarks = findViewById(R.id.nobookmarkstext);

        recyclerView = findViewById(R.id.bookmarksrecyclerview);


        recyclerView.setLayoutManager(new LinearLayoutManager(Bookmarks.this));
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Bookmarks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookmarks.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    bookmarks.add(dataSnapshot1.getKey());
                }

                recyclerView.setAdapter(new BookmarksAdapter(bookmarks));

                if (bookmarks.size() == 0) {
                    nobookmarks.setVisibility(View.VISIBLE);
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    lottieAnimationView.playAnimation();
                    lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        back = findViewById(R.id.bookmarksback);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.ViewHolderBookmarks> {

        ArrayList<String> marks;
        int lastPosition = -1;


        public BookmarksAdapter(ArrayList<String> marks) {
            this.marks = marks;
        }

        @Override
        public void onViewAttachedToWindow(@NonNull final ViewHolderBookmarks holder) {
            super.onViewAttachedToWindow(holder);
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
        public ViewHolderBookmarks onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmarklayout, parent, false);

            return new ViewHolderBookmarks(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderBookmarks holder, int position) {

            holder.bookmark.setChecked(true);

            holder.bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.ZoomOut)
                            .duration(700)
                            .playOn(holder.itemView);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Bookmarks").child(marks.get(holder.getAdapterPosition())).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataSnapshot.getRef().removeValue();

                                    Alerter.create(Bookmarks.this)
                                            .setBackgroundColorInt(Color.parseColor("#FFA000"))
                                            .setDuration(700)
                                            .setTitle("Bookmark Removed")
                                            .show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }, 700);


                }
            });


            FirebaseDatabase.getInstance().getReference().child("Saved").child(marks.get(position)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    holder.name.setText(dataSnapshot.child("buisnessname").getValue(String.class));
                    holder.area.setText(dataSnapshot.child("areaname").getValue(String.class));
                    type = dataSnapshot.child("whichtype").getValue(String.class);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            FirebaseDatabase.getInstance().getReference().child("Images").child(marks.get(position)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.child("image").getValue(String.class) != null) {
                        Glide.with(Bookmarks.this).load(dataSnapshot.child("image")).into(holder.imageView);
                    } else if (type.equals("restaurant")) {
                        Glide.with(Bookmarks.this).load("https://firebasestorage.googleapis.com/v0/b/urbanrider-a7875.appspot.com/o/restaurant.png?alt=media&token=146f9520-f2cd-46fe-99f3-6dc33f0f8e59").into(holder.imageView);
                        holder.imageView.setBackgroundColor(Color.WHITE);

                    }
                    else if (type.equals("dhaba")) {
                        Glide.with(Bookmarks.this).load("https://firebasestorage.googleapis.com/v0/b/urbanrider-a7875.appspot.com/o/dhaba.png?alt=media&token=28266211-e1a0-4ed8-99e5-14965b39be7c").into(holder.imageView);
                        holder.imageView.setBackgroundColor(Color.WHITE);

                    }
                    else if (type.equals("cafe")) {
                        Glide.with(Bookmarks.this).load("https://firebasestorage.googleapis.com/v0/b/urbanrider-a7875.appspot.com/o/coffee.png?alt=media&token=c4510f20-75c0-44af-986e-c1b63981b11b").into(holder.imageView);
                        holder.imageView.setBackgroundColor(Color.WHITE);

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return marks.size();
        }

        public class ViewHolderBookmarks extends RecyclerView.ViewHolder {

            TextView name, area, rating;
            ImageView imageView;
            SparkButton bookmark;

            public ViewHolderBookmarks(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.bookmarkname);
                area = itemView.findViewById(R.id.bookmarkarea);
                rating = itemView.findViewById(R.id.displayrating);
                imageView = itemView.findViewById(R.id.bookmarkimg);
                bookmark = itemView.findViewById(R.id.bookmarkicon);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.alerter_slide_in_from_left, R.anim.alerter_slide_out_to_right);
    }
}
