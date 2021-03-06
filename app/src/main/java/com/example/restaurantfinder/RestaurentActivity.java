package com.example.restaurantfinder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.restaurantfinder.Directions.TaskLoadedCallback;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.sothree.slidinguppanel.ScrollableViewHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.tapadoo.alerter.Alerter;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RestaurentActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback, LocationListener {

    CheckBox sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    TextView onsitekms;
    TextView name;
    LinearLayout call, kmslinear;
    SliderView sliderView;
    private Polyline currentPolyline;
    String markid,whichtype;
    SpinKitView spin;
    ArrayList<String> images;
    RecyclerView reviewrecyclerview;
    String buiss;
    ArrayList<String> menuitems=new ArrayList<>();
    TextView choose, area, title;
    ArrayList<String> chipnames = new ArrayList<>();
    NestedScrollView nestedScrollView;
    private SlidingUpPanelLayout mLayout;
    NestedScrollView nested;
    LatLng lastlocation;
    GoogleMap mMap;
    CardView pro;
    DatabaseReference reviewref;
    TextView status, writereview, noreviewsyet;
    TextView totime, fromtime, nomenu;
    LinearLayout det;
    ImageView back;
    ChipGroup chipGroup;
    int open = 0;
    FusedLocationProviderClient fusedLocationClient;
    LocationManager locationManager;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    Location mLastKnownLocation;
    boolean mLocationPermissionGranted = false;
    String brand;
    int a[] = new int[]{};
    RatingBar ratingBar2;
    ImageView propic;
    int pos;
    ImageView share,directions;
    SparkButton bookmark;
    RecyclerView menu;
    double lon, lat;
    SearchingDialog searchingDialog;
    ArrayList<String> names = new ArrayList<>();
    DatabaseReference databaseReference;
    FirebaseRecyclerAdapter<Review, ReviewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerAdapter<menu, MenuViewHolder> firebaseRecyclerAdapter1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurent);

         images = new ArrayList<>();

        onsitekms = findViewById(R.id.kms);
        call = findViewById(R.id.call);
        name = findViewById(R.id.editname);
        kmslinear = findViewById(R.id.kmslinear);

        sliderView = findViewById(R.id.imageSlider);
        sliderView.setIndicatorAnimation(IndicatorAnimations.SWAP);
        sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);

        sunday = findViewById(R.id.sunday);
        monday = findViewById(R.id.monday);
        tuesday = findViewById(R.id.tuesday);
        wednesday = findViewById(R.id.wednesday);
        thursday = findViewById(R.id.thursday);
        friday = findViewById(R.id.friday);
        saturday = findViewById(R.id.saturday);

        back = findViewById(R.id.backbuttonrest);
        menu = findViewById(R.id.menurecyclerview);

        nested = findViewById(R.id.nested);
        totime = findViewById(R.id.to);
        fromtime = findViewById(R.id.from);
        writereview = findViewById(R.id.writeareview);
        noreviewsyet = findViewById(R.id.noreviewsyettext);
        nomenu = findViewById(R.id.nomenu);

        share = findViewById(R.id.share);
        bookmark = findViewById(R.id.bookmark);
        whichtype=getIntent().getStringExtra("whichtype");
        directions=findViewById(R.id.resdirections);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        nestedScrollView = findViewById(R.id.nested);
        nestedScrollView.setSmoothScrollingEnabled(true);
        nestedScrollView.smoothScrollTo(4, 4);
        area = findViewById(R.id.area);
        choose = findViewById(R.id.choose);
        spin = findViewById(R.id.spin);
        title = findViewById(R.id.title1);

        ratingBar2 = findViewById(R.id.ratingBar3);
        propic = findViewById(R.id.detailsprofilepic);
        pro = findViewById(R.id.pro);

        sunday.setChecked(true);
        monday.setChecked(true);
        tuesday.setChecked(true);
        wednesday.setChecked(true);
        thursday.setChecked(true);
        friday.setChecked(true);
        saturday.setChecked(true);

        det = findViewById(R.id.det);
        chipGroup = findViewById(R.id.detailschipgroup);
        status = findViewById(R.id.status);
        reviewrecyclerview = findViewById(R.id.reviewsrecyclerview);

        pos = getIntent().getIntExtra("posi", -1);
        a = getIntent().getIntArrayExtra("array");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mLayout = findViewById(R.id.sliding);

        chipGroup.setChipSpacing(25);

        chipGroup.setPadding(40, 10, 10, 10);

        menu.setLayoutManager(new LinearLayoutManager(RestaurentActivity.this, LinearLayoutManager.HORIZONTAL, false));

        mLayout.setScrollableViewHelper(new NestedScrollableViewHelper());
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });



        new Load().execute();


        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {


            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    open = 1;
                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    open = 0;
                }
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 0, (LocationListener) RestaurentActivity.this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

        brand = getIntent().getStringExtra("brand");




    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 19));
                                }
                            }
                        });
            }
        } else {
            mMap.setMyLocationEnabled(true);

        }


        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                fusedLocationClient.getLastLocation().addOnSuccessListener(RestaurentActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                        }
                    }
                });

                return true;
            }
        });


        if(Calendar.getInstance().getTime().getHours()>=17 || Calendar.getInstance().getTime().getHours()<=4)
        {
            try {



                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.darkmap));

                if (!success) {
                    Log.e("Activity", "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e("Activity", "Can't find style. Error: ", e);
            }


        }
        else {
            try {



                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.map));

                if (!success) {
                    Log.e("Activity", "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e("Activity", "Can't find style. Error: ", e);
            }


        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (det.getVisibility() == View.VISIBLE) {

                    bookmark.setVisibility(View.INVISIBLE);
                    share.setVisibility(View.INVISIBLE);
                    directions.setVisibility(View.INVISIBLE);

                    if (name.getVisibility() == View.VISIBLE) {

                        title.setText("Nearby Restaurants");


                        YoYo.with(Techniques.FadeOut)
                                .duration(500)
                                .playOn(det);

                        YoYo.with(Techniques.FadeOut)
                                .duration(500)
                                .playOn(name);

                        YoYo.with(Techniques.FadeOut)
                                .duration(500)
                                .playOn(kmslinear);

                        YoYo.with(Techniques.FadeOut)
                                .duration(500)
                                .playOn(status);

                        YoYo.with(Techniques.FadeOut)
                                .duration(500)
                                .playOn(call);

                        YoYo.with(Techniques.FadeOut)
                                .duration(500)
                                .playOn(pro);

                        YoYo.with(Techniques.FadeOut)
                                .duration(500)
                                .playOn(ratingBar2);


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                det.setVisibility(View.INVISIBLE);
                                name.setVisibility(View.INVISIBLE);
                                kmslinear.setVisibility(View.INVISIBLE);
                                status.setVisibility(View.INVISIBLE);
                                call.setVisibility(View.INVISIBLE);
                            }
                        }, 500);

                        choose.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeIn)
                                .duration(500)
                                .playOn(choose);
                    }

                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final com.google.android.gms.maps.model.Marker marker) {

                marker.showInfoWindow();
                String m=marker.getTitle();

                LoadMarkerDetails(m);


                return true;
            }
        });
    }


    void LoadMarkerDetails(final String s)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.smoothScrollTo(0,0);

//                new FetchURL(RestaurentActivity.this).execute(getUrl(getLastKnownLocation(), marker.getPosition(), "driving"), "driving");

                final String mark = s;

                choose.setVisibility(View.INVISIBLE);
                spin.setVisibility(View.VISIBLE);


                FirebaseDatabase.getInstance().getReference().child("Saved").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1.child("buisnessname").getValue(String.class).equals(mark)) {
                                markid = dataSnapshot1.getKey();
                                buiss = dataSnapshot1.child("buisnessname").getValue(String.class);
                                title.setText(dataSnapshot1.child("buisnessname").getValue(String.class));
                            }
                        }

                        writereview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ReviewDialog.display(getSupportFragmentManager(), markid);
                            }
                        });

                        reviewref = FirebaseDatabase.getInstance().getReference().child("Reviews").child(markid);


                        FirebaseRecyclerOptions<Review> options = new FirebaseRecyclerOptions.Builder<Review>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("Reviews").child(markid).orderByChild("timestamp"), new SnapshotParser<Review>() {
                                    @NonNull
                                    @Override
                                    public Review parseSnapshot(@NonNull DataSnapshot snapshot) {

                                        return new Review(snapshot.child("rating").getValue(String.class), snapshot.child("review").getValue(String.class), snapshot.child("userid").getValue(String.class), snapshot.child("date").getValue(String.class));
                                    }
                                }).build();


                        reviewrecyclerview.setLayoutManager(new LinearLayoutManager(RestaurentActivity.this, LinearLayoutManager.HORIZONTAL, false));


                        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Review, ReviewHolder>
                                (options) {

                            @Override
                            protected void onBindViewHolder(@NonNull final ReviewHolder reviewHolder, int i, @NonNull final Review review) {

                                reviewHolder.review.setText(review.getReview());
                                reviewHolder.ratingBar.setRating(Float.parseFloat(review.getRating()));
                                reviewHolder.date.setText(review.getDate());

                                String id = review.getUserid();

                                FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("About").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        reviewHolder.name.setText(dataSnapshot.child("name").getValue(String.class));
                                        Glide.with(RestaurentActivity.this).load(dataSnapshot.child("image").getValue(String.class)).into(reviewHolder.pic);

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



                        /*if(firebaseRecyclerAdapter.getItemCount()==0)
                        {
                            noreviewsyet.setVisibility(View.VISIBLE);
                            reviewrecyclerview.setVisibility(View.GONE);
                        }
                        else {
                            noreviewsyet.setVisibility(View.GONE);
                            reviewrecyclerview.setVisibility(View.VISIBLE);

                        }*/

                        FirebaseDatabase.getInstance().getReference().child("Images").child(markid).addValueEventListener(new ValueEventListener() {
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


                        databaseReference.child("Brands").child(markid).addValueEventListener(new ValueEventListener() {
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

                        FirebaseDatabase.getInstance().getReference().child("Menus").child(markid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("Menus").child(markid), new SnapshotParser<menu>() {
                                    @NonNull
                                    @Override
                                    public menu parseSnapshot(@NonNull DataSnapshot snapshot) {

                                        return new menu(snapshot.child("image").getValue(String.class));
                                    }
                                }).build();


                        firebaseRecyclerAdapter1 = new FirebaseRecyclerAdapter<menu, MenuViewHolder>
                                (options1) {
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

                                    if (dataSnapshot1.getKey().equals(markid)) {
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
                                            .child("Bookmarks").child(markid).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            bookmark.setClickable(true);
                                            Alerter.create(RestaurentActivity.this)
                                                    .setBackgroundColorInt(Color.parseColor("#FFA000"))
                                                    .setDuration(500)
                                                    .setTitle(buiss + " Added To Bookmarks")
                                                    .show();
                                        }
                                    });
                                } else {
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("Bookmarks").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                if (dataSnapshot1.getKey().equals(markid)) {
                                                    dataSnapshot1.getRef().removeValue();
                                                    bookmark.setClickable(true);
                                                    YoYo.with(Techniques.Wobble)
                                                            .duration(700)
                                                            .playOn(bookmark);
                                                    Alerter.create(RestaurentActivity.this)
                                                            .setBackgroundColorInt(Color.parseColor("#FFA000"))
                                                            .setDuration(500)
                                                            .setTitle(buiss + " Removed From Bookmarks")
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

                        FirebaseDatabase.getInstance().getReference().child("Saved").child(markid).addValueEventListener(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                final details1 de = dataSnapshot.getValue(details1.class);

                                String from = de.getFromtime();
                                String to = de.getTotime();

                                Date currentTime = Calendar.getInstance().getTime();


                                directions.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                Uri.parse("http://maps.google.com/maps?daddr="+de.getLatitude()+","+de.getLongitude()));
                                        startActivity(intent);
                                    }
                                });
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


                                call.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Alerter.create(RestaurentActivity.this).setTitle("Are you sure to call " + de.getBuisnessname())
                                                .setBackgroundColorInt(Color.DKGRAY)
                                                .addButton("Call", R.style.AlertButton, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                                        intent.setData(Uri.parse("tel:" + de.getContact1()));
                                                        startActivity(intent);
                                                        Alerter.clearCurrent(RestaurentActivity.this);
                                                    }
                                                }).addButton("No", R.style.AlertButton, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Alerter.clearCurrent(RestaurentActivity.this);
                                            }
                                        }).show();
                                    }
                                });

                                Glide.with(getApplicationContext()).load(de.getProfilepic()).into(propic);

                                ratingBar2.setRating(4);


                                area.setText(de.getAreaname());
                                name.setText(de.getBuisnessname());


                                if (de.getOnsite().equals("no")) {
                                    onsitekms.setText("None");
                                } else {
                                    onsitekms.setText(de.getKms() + " kms");
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

                                bookmark.setVisibility(View.VISIBLE);
                                directions.setVisibility(View.VISIBLE);
                                share.setVisibility(View.VISIBLE);


                                det.setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.FadeIn)
                                        .duration(1000)
                                        .playOn(det);

                                name.setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.FadeIn)
                                        .duration(1000)
                                        .playOn(name);

                                kmslinear.setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.FadeIn)
                                        .duration(1000)
                                        .playOn(kmslinear);

                                status.setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.FadeIn)
                                        .duration(1000)
                                        .playOn(status);

                                call.setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.FadeIn)
                                        .duration(1000)
                                        .playOn(call);

                                pro.setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.FadeIn)
                                        .duration(1000)
                                        .playOn(pro);

                                ratingBar2.setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.FadeIn)
                                        .duration(1000)
                                        .playOn(ratingBar2);

                                spin.setVisibility(View.INVISIBLE);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


    }


    class Load extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchingDialog= new SearchingDialog();
            searchingDialog.show(getSupportFragmentManager(), "Activity");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            databaseReference = FirebaseDatabase.getInstance().getReference();

            databaseReference.child("Brands").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {


                        Map<String, Object> map = (Map<String, Object>) dataSnapshot1.getValue();


                        if (map.containsValue(brand)) {

                            names.add(dataSnapshot1.getKey());


                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            databaseReference.child("Saved").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                    for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        lat = Double.parseDouble(dataSnapshot1.child("latitude").getValue(String.class));
                        lon = Double.parseDouble(dataSnapshot1.child("longitude").getValue(String.class));

                        LatLng lastloc = getLastKnownLocation();

                        final double dist = ((distance(lastloc.latitude, lastloc.longitude, lat, lon)));


                        Task<Void> task = databaseReference.child("Saved").child(dataSnapshot1.getKey()).child("distance").setValue(dist);

                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                if (dist < 10 && (dataSnapshot1.child("whichtype").getValue(String.class)).matches(whichtype) && (dataSnapshot1.child("online").getValue(String.class).equals("1"))) {
                                    if (names.contains(dataSnapshot1.getKey())) {
                                        MarkerOptions markerOptions = new MarkerOptions();

                                        LatLng latLng = new LatLng(Double.parseDouble(dataSnapshot1.child("latitude").getValue(String.class)), Double.parseDouble(dataSnapshot1.child("longitude").getValue(String.class)));
                                        markerOptions.position(latLng);

                                        markerOptions.title(dataSnapshot1.child("buisnessname").getValue(String.class));


                                        if(whichtype.equals("dhaba"))
                                        {
                                            int height = 170;
                                            int width = 130;
                                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.orangemarker);
                                            Bitmap b = bitmapdraw.getBitmap();
                                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(b));
                                        }
                                        else if(whichtype.equals("restaurant"))
                                        {
                                            int height = 170;
                                            int width = 130;
                                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.redmarker);
                                            Bitmap b = bitmapdraw.getBitmap();
                                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(b));
                                        }
                                        else if(whichtype.equals("cafe"))
                                        {
                                            int height = 170;
                                            int width = 130;
                                            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.greenmarker);
                                            Bitmap b = bitmapdraw.getBitmap();
                                            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(b));
                                        }

                                        mMap.addMarker(markerOptions);


                                    }
                                }

                            }
                        });


                    }

                    fusedLocationClient.getLastLocation().addOnSuccessListener(RestaurentActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                            }
                        }
                    });



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (searchingDialog != null ) {
                        searchingDialog.dismiss();
                    }

                }
            }, 4000);

        }
    }


    private LatLng getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        LatLng loc = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            Location l = locationManager.getLastKnownLocation(provider);


            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation != null) {
            loc = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());


            float zoomLevel = 16.0f; //This goes up to 21
            //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 19));


        }
        if (bestLocation == null) {
            return null;
        }
        return loc;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                }
            }
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 19));
                        }
                    }
                });
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    @Override
    public boolean onSupportNavigateUp() {
        if (open == 0) {
            onBackPressed();
        } else {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            super.onBackPressed();
            overridePendingTransition(R.anim.alerter_slide_in_from_left, R.anim.alerter_slide_out_to_right);
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        LatLng latLng = mMap.getCameraPosition().target;

        savedInstanceState.putDouble("lat", latLng.latitude);
        savedInstanceState.putDouble("long", latLng.longitude);
        // etc.
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        double lat = savedInstanceState.getDouble("lat");
        double longg = savedInstanceState.getDouble("long");

        LatLng coordinate = new LatLng(lat, longg);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                coordinate, 15);
        mMap.animateCamera(location);


    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        lastlocation = latLng;
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public class NestedScrollableViewHelper extends ScrollableViewHelper {
        public int getScrollableViewScrollPosition(View scrollableView, boolean isSlidingUp) {
            if (nested instanceof NestedScrollView) {
                if (isSlidingUp) {
                    return nested.getScrollY();
                } else {
                    NestedScrollView nsv = ((NestedScrollView) nested);
                    View child = nsv.getChildAt(0);
                    return (child.getBottom() - (nsv.getHeight() + nsv.getScrollY()));
                }
            } else {
                return 0;
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
                Glide.with(context).load(imgs.get(position)).override(500, 300).placeholder(R.drawable.imgbck).into(viewHolder.imageViewBackground);
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
            ImageView imageViewBackground;

            public SliderAdapterVH(View itemView) {
                super(itemView);
                imageViewBackground = itemView.findViewById(R.id.slideimg);
                this.itemView = itemView;
            }
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

    class MenuViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.menuimg);
        }
    }
}

