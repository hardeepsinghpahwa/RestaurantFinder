package com.example.restaurantfinder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeMaps extends FragmentActivity implements OnMapReadyCallback, RecentSearches.OnFragmentInteractionListener, LocationListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    FloatingActionButton floatingActionButton, dhaba, coffeeshop;
    CircleImageView pic;
    LocationManager locationManager;
    boolean mLocationPermissionGranted = false;
    int item;
    ImageView imageView;
    LatLng lastlocation;
    int a[];
    String brand;
    Button search;
    private FusedLocationProviderClient fusedLocationClient;
    TextView name;
    LinearLayout fl1, fl2, fl3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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
            getLocationPermission();

        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 0, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
        floatingActionButton = findViewById(R.id.restaurentfab);
        imageView = findViewById(R.id.item);
        search = findViewById(R.id.search);

        name = findViewById(R.id.nme);
        pic = findViewById(R.id.pic);


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


        a = new int[]{};

        item = getIntent().getIntExtra("posi", -1);
        Log.i("", String.valueOf(item));
        a = getIntent().getIntArrayExtra("array");

        Log.i("img", String.valueOf(item));

        if (item != -1) {
            imageView.setImageResource(a[item]);
            fl1.setVisibility(View.GONE);
            fl2.setVisibility(View.GONE);
            fl3.setVisibility(View.GONE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    search.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.Landing)
                            .duration(1000)
                            .playOn(search);
                }
            }, 1000);

        } else {

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("About").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        name.setText(dataSnapshot.child("name").getValue(String.class));
                        Glide.with(getApplicationContext()).load(dataSnapshot.child("image").getValue(String.class)).into(pic);

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

        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeMaps.this, BottomUpActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slideup, R.anim.slidedown);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeMaps.this, RestaurentActivity.class);
                intent.putExtra("brand", brand);
                intent.putExtra("array", a);
                intent.putExtra("posi", item);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.top, R.anim.bottom);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                if (lastlocation != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastlocation, 19));
                }
                //getLastKnownLocation();
                //updateLocationUI();
            }
        } else {
            getLocationPermission();
        }

    }



    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            mMap.setMyLocationEnabled(true);
            if (lastlocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastlocation, 19));
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
}
