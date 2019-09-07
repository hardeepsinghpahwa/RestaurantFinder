package com.example.restaurantfinder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeMaps extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    FloatingActionButton floatingActionButton,dhaba,coffeeshop;
    CircleImageView pic;
    LocationManager locationManager;
    Location mLastKnownLocation;
    boolean mLocationPermissionGranted = false;
    int item;
    ImageView imageView;
    int a[];
    String brand;
    Button search;
    TextView name;
    LinearLayout fl1,fl2,fl3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        floatingActionButton = findViewById(R.id.restaurentfab);
        imageView = findViewById(R.id.item);
        search=findViewById(R.id.search);

        name=findViewById(R.id.nme);
        pic=findViewById(R.id.pic);


        dhaba=findViewById(R.id.dhaba);
        coffeeshop=findViewById(R.id.coffeeshop);

        brand = getIntent().getStringExtra("brand");

        fl1=findViewById(R.id.fl1);
        fl2=findViewById(R.id.fl2);
        fl3=findViewById(R.id.fl3);


        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            Glide.with(HomeMaps.this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(pic);

        }

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
            },1000);

        }else {
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
            },1000);

        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(HomeMaps.this, BottomUpActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.top, R.anim.bottom);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeMaps.this,RestaurentActivity.class);
                intent.putExtra("brand",brand);
                intent.putExtra("array",a);
                intent.putExtra("posi",item);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.top,R.anim.bottom);
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
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }

        updateLocationUI();

        getLastKnownLocation();


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
            Log.d("last location:", provider + l);


            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                Log.d("found last location: %s", String.valueOf(l));
                bestLocation = l;
            }
        }
        if (bestLocation != null) {
            loc = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());


            float zoomLevel = 16.0f; //This goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 19));


        }
        if (bestLocation == null) {
            return null;
        }
        return loc;
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);

                Log.i("kjn", "ok");

            } else {
                Log.i("kjn", "no");
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }

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
                    getLastKnownLocation();
                    updateLocationUI();

                }
                else{
                    getLocationPermission();
                    Toast.makeText(this, "Cant proceed without location permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
        updateLocationUI();
    }
}
