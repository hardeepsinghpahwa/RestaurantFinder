package com.example.restaurantfinder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeMaps extends FragmentActivity implements RecentSearches.OnFragmentInteractionListener, LocationListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    FloatingActionButton restaurant, dhaba, coffeeshop;
    CircleImageView pic;
    LocationManager locationManager;
    boolean mLocationPermissionGranted = false;
    int item;
    Location lastlocation;
    int a[];
    ShimmerFrameLayout nearbyresturantsshimmer, profileshimmer, nearbydhabasshimmer, nearbycafesshimmer;
    Double lat, lon;
    LatLng lastloc;
    String brand;
    double dist;
    RecyclerView nearbyrestaurants, nearbydhabas, nearbycafes;
    private FusedLocationProviderClient fusedLocationClient;
    TextView name;
    ArrayList<String> restaurants,dhabas,cafes;
    LinearLayout fl1, fl2, fl3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            getLocationPermission();

        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 0, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        restaurants = new ArrayList<>();
        dhabas=new ArrayList<>();
        cafes=new ArrayList<>();
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lastlocation = location;
                        }
                    }
                });
        restaurant = findViewById(R.id.restaurentfab);
        nearbyrestaurants = findViewById(R.id.nearbyrestaurants);
        nearbycafes = findViewById(R.id.nearbycafes);
        nearbydhabas = findViewById(R.id.nearbydhabas);
        name = findViewById(R.id.nme);
        pic = findViewById(R.id.pic);
        nearbyresturantsshimmer = findViewById(R.id.nearbyrestaurantsshimmer);
        profileshimmer = findViewById(R.id.profileshimmer);
        nearbycafesshimmer = findViewById(R.id.nearbycafessshimmer);
        nearbydhabasshimmer = findViewById(R.id.nearbydhabassshimmer);


        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileDialog.display(getSupportFragmentManager());
            }
        });
        dhaba = findViewById(R.id.dhaba);
        coffeeshop = findViewById(R.id.coffeeshop);

        brand = getIntent().getStringExtra("brand");

        fl1 = findViewById(R.id.fl1);
        fl2 = findViewById(R.id.fl2);
        fl3 = findViewById(R.id.fl3);


        nearbyrestaurants.setLayoutManager(new LinearLayoutManager(HomeMaps.this, LinearLayoutManager.HORIZONTAL, false));
        nearbydhabas.setLayoutManager(new LinearLayoutManager(HomeMaps.this, LinearLayoutManager.HORIZONTAL, false));
        nearbycafes.setLayoutManager(new LinearLayoutManager(HomeMaps.this, LinearLayoutManager.HORIZONTAL, false));

        a = new int[]{};

        item = getIntent().getIntExtra("posi", -1);
        Log.i("", String.valueOf(item));
        a = getIntent().getIntArrayExtra("array");

        Log.i("img", String.valueOf(item));


        FirebaseDatabase.getInstance().getReference().child("Saved").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                restaurants.clear();
                dhabas.clear();
                cafes.clear();
                for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    lat = Double.parseDouble(dataSnapshot1.child("latitude").getValue(String.class));
                    lon = Double.parseDouble(dataSnapshot1.child("longitude").getValue(String.class));


                    if (lastlocation != null) {
                        lastloc = new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude());
                        dist = ((distance(lastloc.latitude, lastloc.longitude, lat, lon))) / 100;
                    }




                    Log.i("dis", String.valueOf(dist));
                    if (dist < 1 && (dataSnapshot1.child("whichtype").getValue(String.class)).matches("restaurant") && (dataSnapshot1.child("online").getValue(String.class).equals("1"))) {

                        restaurants.add(dataSnapshot1.getKey());

                    } else if (dist < 1 && (dataSnapshot1.child("whichtype").getValue(String.class)).matches("dhaba") && (dataSnapshot1.child("online").getValue(String.class).equals("1"))) {

                            dhabas.add(dataSnapshot1.getKey());

                    }
                    else if (dist < 1 && (dataSnapshot1.child("whichtype").getValue(String.class)).matches("cafe") && (dataSnapshot1.child("online").getValue(String.class).equals("1"))) {

                        cafes.add(dataSnapshot1.getKey());

                    }
                }

                nearbyrestaurants.setAdapter(new ItemAdapter(restaurants));
                nearbycafes.setAdapter(new ItemAdapter(cafes));
                nearbydhabas.setAdapter(new ItemAdapter(dhabas));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("About").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name.setText(dataSnapshot.child("name").getValue(String.class));
                        Glide.with(getApplicationContext()).load(dataSnapshot.child("image").getValue(String.class)).into(pic);
                        profileshimmer.stopShimmer();
                        profileshimmer.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fl1.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.Landing)
                            .duration(1000)
                            .playOn(fl1);

                    fl3.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.Landing)
                            .duration(1000)
                            .playOn(fl2);

                    fl2.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.Landing)
                            .duration(1000)
                            .playOn(fl3);

                }
            }, 1000);


        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeMaps.this, BottomUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slideup, R.anim.slidedown);
            }
        });


        dhaba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CuisineDialog.display(getSupportFragmentManager(),"dhaba",0,new int[]{},"Dhaba");
            }
        });

        coffeeshop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CuisineDialog.display(getSupportFragmentManager(),"cafe",0,new int[]{},"Cafe");
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            if (lastlocation != null) {
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
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
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                    }
                                }
                            });

                } else {
                    getLocationPermission();
                    Toast.makeText(this, "Cant proceed without location permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLocationChanged(Location location) {
        /*LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 19);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);*/
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


    class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {

        ArrayList<String> ids;

        public ItemAdapter(ArrayList<String> ids) {
            this.ids = ids;
        }


        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.resitem, parent, false);

            return new ItemHolder(v);
        }

        @Override
        public void onViewAttachedToWindow(@NonNull ItemHolder holder) {
            super.onViewAttachedToWindow(holder);

            FirebaseDatabase.getInstance().getReference().child("Saved").child(ids.get(0)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("whichtype").getValue(String.class).equals("dhaba"))
                    {
                        nearbydhabasshimmer.stopShimmer();
                        nearbydhabasshimmer.setVisibility(View.GONE);
                    }
                    else if(dataSnapshot.child("whichtype").getValue(String.class).equals("restaurant")){
                        nearbyresturantsshimmer.stopShimmer();
                        nearbyresturantsshimmer.setVisibility(View.GONE);
                    }
                    else if(dataSnapshot.child("whichtype").getValue(String.class).equals("cafe"))
                    {
                        nearbycafesshimmer.stopShimmer();
                        nearbycafesshimmer.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        @Override
        public void onBindViewHolder(@NonNull final ItemHolder holder, int position) {

            FirebaseDatabase.getInstance().getReference().child("Saved").child(ids.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Log.i("value", "" + dataSnapshot.child("profilepic").getValue(String.class));
                    holder.name.setText(dataSnapshot.child("buisnessname").getValue(String.class));
                    //holder.rating.setText(dataSnapshot.child("rating").getValue(String.class));
                    holder.area.setText(dataSnapshot.child("areaname").getValue(String.class));
                    Glide.with(getApplicationContext()).load(dataSnapshot.child("profilepic").getValue(String.class)).into(holder.propic);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return ids.size();
        }

        public class ItemHolder extends RecyclerView.ViewHolder {

            TextView area, name, rating;
            ImageView propic;

            public ItemHolder(@NonNull View itemView) {
                super(itemView);

                name = itemView.findViewById(R.id.displayname);
                area = itemView.findViewById(R.id.displayarea);
                rating = itemView.findViewById(R.id.displayrating);
                propic = itemView.findViewById(R.id.displaypropic);
            }
        }

    }

    public double distance(Double lat_a, Double lng_a, double lat_b, double lng_b) {
        Location mylocation = new Location("");
        Location dest_location = new Location("");
        dest_location.setLatitude(lat_b);
        dest_location.setLongitude(lng_b);
        mylocation.setLatitude(lat_a);
        mylocation.setLongitude(lng_a);
        Double distance = Double.valueOf(mylocation.distanceTo(dest_location));

        return distance;
    }

}
