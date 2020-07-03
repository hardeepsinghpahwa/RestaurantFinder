package com.example.restaurantfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.tapadoo.alerter.Alerter;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RestaurantDetails extends AppCompatActivity implements OnMapReadyCallback {


    CheckBox sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    TextView delivery;
    TextView name;
    TextView call;
    ArrayList<String>menuitems;
    SliderView sliderView;
    ArrayList<String> images;
    RecyclerView reviewrecyclerview;
    TextView area,noreviewsyet,writereview;
    ArrayList<String> chipnames = new ArrayList<>();
    TextView status,nomenu;
    TextView totime, fromtime;
    ChipGroup chipGroup;
    String buisnessname;
    TextView reviewnums;
    FusedLocationProviderClient fusedLocationClient;
    int a[] = new int[]{};
    RatingBar ratingBar2;
    ImageView propic,share,detailsdirection;
    RecyclerView menu;
    ImageView back;
    SparkButton bookmark;
    FirebaseRecyclerAdapter<Review, ReviewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerAdapter<menu, MenuViewHolder> firebaseRecyclerAdapter1;

    String id;
    CollapsingToolbarLayout collapsingToolbarLayout;
    GoogleMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        images = new ArrayList<>();
        sliderView = findViewById(R.id.imageSlider);
        writereview=findViewById(R.id.detailswriteareview);
        sliderView.setIndicatorAnimation(IndicatorAnimations.SWAP);
        sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        name = findViewById(R.id.detailname);
        status = findViewById(R.id.detailstatus);
        area=findViewById(R.id.detailaddress);
        propic=findViewById(R.id.detailspropic);
        nomenu=findViewById(R.id.detailsnomeu);
        noreviewsyet=findViewById(R.id.detailsnoreviews);
        id = getIntent().getStringExtra("id");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mpview);
        mapFragment.getMapAsync(RestaurantDetails.this);

        bookmark=findViewById(R.id.detailbookmark);
        share=findViewById(R.id.detailshare);
        detailsdirection=findViewById(R.id.detailsdirection);
        delivery = findViewById(R.id.detailsdelivery);
        call = findViewById(R.id.detailscall);
        sunday = findViewById(R.id.detailssunday);
        monday = findViewById(R.id.detailsmonday);
        tuesday = findViewById(R.id.detailstuesday);
        wednesday = findViewById(R.id.detailswednesday);
        thursday = findViewById(R.id.detailsthursday);
        friday = findViewById(R.id.detailsfriday);
        saturday = findViewById(R.id.detailssaturday);
        menu = findViewById(R.id.detailsmenurecyclerview);
        totime = findViewById(R.id.detailsto);
        fromtime = findViewById(R.id.detailsfrom);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ratingBar2 = findViewById(R.id.detailsrating);
        back=findViewById(R.id.detailsback);

        sunday.setChecked(true);
        monday.setChecked(true);
        tuesday.setChecked(true);
        wednesday.setChecked(true);
        thursday.setChecked(true);
        friday.setChecked(true);
        saturday.setChecked(true);

        chipGroup = findViewById(R.id.detailschipgroup);
        reviewrecyclerview = findViewById(R.id.detailsreviewrecyclerview);
        chipGroup.setChipSpacing(25);
        reviewnums=findViewById(R.id.reviewnumber);

        chipGroup.setPadding(40, 10, 10, 10);


        writereview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewDialog.display(getSupportFragmentManager(), id);
            }
        });
        back.bringToFront();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });


        FirebaseDatabase.getInstance().getReference().child("Images").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                images.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    images.add(dataSnapshot1.child("image").getValue(String.class));
                }

                sliderView.setSliderAdapter(new SliderAdapter(getApplicationContext(), images));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        new LoadData().execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;


    }

    class LoadData extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {

            DataLoad();
            return null;
        }
    }


    public void DataLoad()
    {

        menuitems=new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saved").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final details1 de = dataSnapshot.getValue(details1.class);

                name.setText(de.getBuisnessname());

                area.setText(de.getAreaname());

                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Alerter.create(RestaurantDetails.this).setTitle("Are you sure to call " + de.getBuisnessname())
                                .setBackgroundColorInt(Color.DKGRAY)
                                .addButton("Call", R.style.AlertButton, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:" + de.getContact1()));
                                        startActivity(intent);
                                        Alerter.clearCurrent(RestaurantDetails.this);
                                    }
                                }).addButton("No", R.style.AlertButton, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Alerter.clearCurrent(RestaurantDetails.this);
                            }
                        }).show();
                    }
                });

                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, de.getBuisnessname());
                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Got this amazing place "+de.getBuisnessname().toUpperCase()+" to eat from the Restaurant Finder app. Please do check out the app and Get to know about new places around to eat");
                        startActivity(Intent.createChooser(shareIntent, "Share via"));
                    }
                    });

                buisnessname=de.getBuisnessname();
                totime.setText(de.getTotime());

                if (de.getOnsite().equals("no")) {
                    delivery.setText("None");
                } else {
                    delivery.setText(de.getKms() + " kms");
                }

                if (de.getSunday().matches("false")) {
                    sunday.setBackground(getResources().getDrawable(R.drawable.checkboxselect));
                    sunday.setTextColor(Color.WHITE);
                    sunday.setChecked(false);
                } else if (de.getSunday().matches("true")) {
                    sunday.setBackground(getResources().getDrawable(R.drawable.checkboxunselect));
                    sunday.setTextColor(Color.parseColor("#e0e0e0"));
                    sunday.setChecked(true);
                }


                if (de.getMonday().matches("false")) {
                    monday.setBackground(getResources().getDrawable(R.drawable.checkboxselect));
                    monday.setTextColor(Color.WHITE);
                    monday.setChecked(false);
                } else if (de.getMonday().matches("true")) {
                    monday.setBackground(getResources().getDrawable(R.drawable.checkboxunselect));
                    monday.setTextColor(Color.parseColor("#e0e0e0"));
                    monday.setChecked(true);
                }

                if (de.getTuesday().matches("false")) {
                    tuesday.setBackground(getResources().getDrawable(R.drawable.checkboxselect));
                    tuesday.setTextColor(Color.WHITE);
                    tuesday.setChecked(false);
                } else if (de.getTuesday().matches("true")) {
                    tuesday.setBackground(getResources().getDrawable(R.drawable.checkboxunselect));
                    tuesday.setTextColor(Color.parseColor("#e0e0e0"));
                    tuesday.setChecked(true);
                }

                if (de.getWednesday().matches("false")) {
                    wednesday.setBackground(getResources().getDrawable(R.drawable.checkboxselect));
                    wednesday.setTextColor(Color.WHITE);
                    wednesday.setChecked(false);
                } else if (de.getWednesday().matches("true")) {
                    wednesday.setBackground(getResources().getDrawable(R.drawable.checkboxunselect));
                    wednesday.setTextColor(Color.parseColor("#e0e0e0"));
                    wednesday.setChecked(true);
                }

                if (de.getThursday().matches("false")) {
                    thursday.setBackground(getResources().getDrawable(R.drawable.checkboxselect));
                    thursday.setTextColor(Color.WHITE);
                    thursday.setChecked(false);
                } else if (de.getThursday().matches("true")) {
                    thursday.setBackground(getResources().getDrawable(R.drawable.checkboxunselect));
                    thursday.setTextColor(Color.parseColor("#e0e0e0"));
                    thursday.setChecked(true);
                }

                if (de.getFriday().matches("false")) {
                    friday.setBackground(getResources().getDrawable(R.drawable.checkboxselect));
                    friday.setTextColor(Color.WHITE);
                    friday.setChecked(false);
                } else if (de.getFriday().matches("true")) {
                    friday.setBackground(getResources().getDrawable(R.drawable.checkboxunselect));
                    friday.setTextColor(Color.parseColor("#e0e0e0"));
                    friday.setChecked(true);
                }

                if (de.getSaturday().matches("false")) {
                    saturday.setBackground(getResources().getDrawable(R.drawable.checkboxselect));
                    saturday.setTextColor(Color.WHITE);
                    saturday.setChecked(false);
                } else if (de.getSaturday().matches("true")) {
                    saturday.setBackground(getResources().getDrawable(R.drawable.checkboxunselect));
                    saturday.setTextColor(Color.parseColor("#e0e0e0"));
                    saturday.setChecked(true);
                }



                String from = de.getFromtime();
                String to = de.getTotime();

                Date currentTime = Calendar.getInstance().getTime();


                int m = 0;
                while (from.charAt(m) != ':') {
                    m++;
                }
                int frmhour = Integer.parseInt(de.getFromtime().substring(0, m));
                int n = 0;

                while (to.charAt(n) != ':') {
                    n++;
                }
                int tohour = Integer.parseInt(de.getTotime().substring(0, n));

                totime.setText(de.getTotime());
                fromtime.setText(de.getFromtime());

                if (currentTime.getHours() >= frmhour && (currentTime.getHours()) <= (12 + tohour)) {
                    status.setText("OPEN");
                    status.setTextColor(Color.parseColor("#64DD17"));
                } else {
                    status.setTextColor(Color.RED);
                    status.setText("CLOSED");
                }


                fromtime.setText(de.getFromtime());

                call.setText("Call "+de.getContact1());

                Glide.with(getApplicationContext()).load(de.getProfilepic()).into(propic);

                detailsdirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr="+de.getLatitude()+","+de.getLongitude()));
                        startActivity(intent);
                    }
                });

                if(map!=null)
                {
                    LatLng latLng=new LatLng(Double.valueOf(de.getLatitude()),Double.valueOf(de.getLongitude()));
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));

                    map.clear();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                    map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(de.getBuisnessname()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        FirebaseRecyclerOptions<Review> options = new FirebaseRecyclerOptions.Builder<Review>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Reviews").child(id).orderByChild("timestamp"), new SnapshotParser<Review>() {
                    @NonNull
                    @Override
                    public Review parseSnapshot(@NonNull DataSnapshot snapshot) {

                        return new Review(snapshot.child("rating").getValue(String.class), snapshot.child("review").getValue(String.class), snapshot.child("userid").getValue(String.class), snapshot.child("date").getValue(String.class));
                    }
                }).build();


        reviewrecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        menu.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));


        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Review, ReviewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ReviewHolder reviewHolder, int i, @NonNull Review review) {



                reviewHolder.review.setText(review.getReview());
                reviewHolder.ratingBar.setRating(Float.parseFloat(review.getRating()));
                reviewHolder.date.setText(review.getDate());

                String id = review.getUserid();

                FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("About").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reviewHolder.name.setText(dataSnapshot.child("name").getValue(String.class));
                        Glide.with(getApplicationContext()).load(dataSnapshot.child("image").getValue(String.class)).into(reviewHolder.pic);


                        if(dataSnapshot.child("name").getValue(String.class)!=null)
                        {
                            noreviewsyet.setVisibility(View.GONE);
                            reviewrecyclerview.setVisibility(View.VISIBLE);
                        }
                        else {
                            noreviewsyet.setVisibility(View.VISIBLE);
                            reviewrecyclerview.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
            }

            @NonNull
            @Override
            public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.review, null, true);
                view.setLayoutParams(new RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                ));

                return new ReviewHolder(view);

            }
        };

        reviewrecyclerview.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();


        FirebaseDatabase.getInstance().getReference().child("Brands").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chipGroup.removeAllViews();
                chipnames.clear();
                for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {


                    if (!chipnames.contains(dataSnapshot1.getValue(String.class))) {
                        chipnames.add(dataSnapshot1.getValue(String.class));

                        Chip chip = new Chip(chipGroup.getContext());

                        chip.setText(dataSnapshot1.getValue(String.class));
                        chip.setTextColor(Color.WHITE);
                        chipGroup.addView(chip);
                        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#f44336")));


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Menus").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menuitems.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    menuitems.add(dataSnapshot1.child("image").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<menu> options1 = new FirebaseRecyclerOptions.Builder<menu>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Menus").child(id), new SnapshotParser<menu>() {
                    @NonNull
                    @Override
                    public menu parseSnapshot(@NonNull DataSnapshot snapshot) {

                        return new menu(snapshot.child("image").getValue(String.class));
                    }
                }).build();


        firebaseRecyclerAdapter1=new FirebaseRecyclerAdapter<menu, MenuViewHolder>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull final MenuViewHolder menuViewHolder, int i, @NonNull menu men) {


                Glide.with(getApplicationContext()).load(men.getImage()).override(150,200).into(menuViewHolder.imageView);

                if(men.getImage()!=null)
                {
                    nomenu.setVisibility(View.GONE);
                    menu.setVisibility(View.VISIBLE);
                }else {
                    nomenu.setVisibility(View.VISIBLE);
                    menu.setVisibility(View.GONE);

                }

                menuViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ViewImagesDialog viewImagesDialog=new ViewImagesDialog(menuitems,menuViewHolder.getAdapterPosition());
                        viewImagesDialog.show(getSupportFragmentManager(),"Activity");
                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.addmenu,null);

                return new MenuViewHolder(view);
            }
        };

        menu.setAdapter(firebaseRecyclerAdapter1);
        firebaseRecyclerAdapter1.startListening();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Bookmarks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    if (dataSnapshot1.getKey().equals(id)) {
                        bookmark.setChecked(true);
                        break;
                    } else {
                        bookmark.setChecked(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        bookmark.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                bookmark.setClickable(false);
                if (buttonState) {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Bookmarks").child(id).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            bookmark.setClickable(true);
                            Alerter.create(RestaurantDetails.this)
                                    .setBackgroundColorInt(Color.parseColor("#FFA000"))
                                    .setDuration(500)
                                    .setTitle( buisnessname+ " Added To Bookmarks")
                                    .show();
                        }
                    });
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("Bookmarks").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                if (dataSnapshot1.getKey().equals(id)) {
                                    dataSnapshot1.getRef().removeValue();
                                    bookmark.setClickable(true);
                                    YoYo.with(Techniques.Wobble)
                                            .duration(700)
                                            .playOn(bookmark);
                                    Alerter.create(RestaurantDetails.this)
                                            .setBackgroundColorInt(Color.parseColor("#FFA000"))
                                            .setDuration(500)
                                            .setTitle(buisnessname + " Removed From Bookmarks")
                                            .show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            bookmark.setClickable(true);
                        }
                    });
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });


    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

        private Context context;
        ArrayList<String> imgs;

        public SliderAdapter(Context context, ArrayList<String> imgs) {
            this.context = context;
            this.imgs = imgs;
        }

        @Override
        public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageslideritem, null);
            return new SliderAdapterVH(inflate);
        }

        @Override
        public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {

            if (imgs.size() == 0) {
                viewHolder.imageViewBackground.setImageResource(R.drawable.imgsliderback);
            } else {
                Glide.with(context).load(imgs.get(position)).placeholder(R.drawable.imgbck).into(viewHolder.imageViewBackground);
            }
        }

        @Override
        public int getCount() {
            //slider view count could be dynamic size
            if (imgs.size() == 0) {
                return 1;
            } else return imgs.size();
        }


        class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

            View itemView;
            PhotoView imageViewBackground;

            public SliderAdapterVH(View itemView) {
                super(itemView);
                imageViewBackground = itemView.findViewById(R.id.slideimg);
                this.itemView = itemView;
            }
        }
    }
    class ReviewHolder extends RecyclerView.ViewHolder {
        TextView name, date, review;
        ImageView pic;
        RatingBar ratingBar;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.reviewname2);
            date = itemView.findViewById(R.id.reviewdate2);
            review = itemView.findViewById(R.id.review2);
            ratingBar = itemView.findViewById(R.id.ratingBar2);
            pic = itemView.findViewById(R.id.reviewimage2);
        }
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.menuimg);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
        if (firebaseRecyclerAdapter1 != null) {
            firebaseRecyclerAdapter1.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
        if (firebaseRecyclerAdapter1 != null) {
            firebaseRecyclerAdapter1.stopListening();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.alerter_slide_in_from_left,R.anim.alerter_slide_out_to_right);
    }
}
