package com.example.restaurantfinder;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class RestaurentActivity extends AppCompatActivity implements OnMapReadyCallback {



    String bname;
    CheckBox sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    Button onsitekms;
    TextView timefrom, timeto,name;
    Button call;
    ImageView imageView;
    SpinKitView spin;
    TextView choose,area;


    private SlidingUpPanelLayout mLayout;
    RelativeLayout overlay;
    Button ok;
    GoogleMap mMap;
    LatLng origin;
    RelativeLayout details;
    LocationManager locationManager;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    Location mLastKnownLocation;
    boolean mLocationPermissionGranted = false;
    String brand;
    int a[]=new int[]{};
    int pos;
    double lon, lat;
    ArrayList<String> names = new ArrayList<>();
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurent);


        getSupportActionBar().setTitle("Nearby Restaurants");


        onsitekms = findViewById(R.id.kms);
        call=findViewById(R.id.call);
        name = findViewById(R.id.name);


        sunday = findViewById(R.id.sunday);
        monday = findViewById(R.id.monday);
        tuesday = findViewById(R.id.tuesday);
        wednesday = findViewById(R.id.wednesday);
        thursday = findViewById(R.id.thursday);
        friday = findViewById(R.id.friday);
        saturday = findViewById(R.id.saturday);

        area=findViewById(R.id.area);
        choose=findViewById(R.id.choose);
        spin=findViewById(R.id.spin);

        sunday.setChecked(true);
        monday.setChecked(true);
        tuesday.setChecked(true);
        wednesday.setChecked(true);
        thursday.setChecked(true);
        friday.setChecked(true);
        saturday.setChecked(true);







        overlay = findViewById(R.id.overlay);
        ok = findViewById(R.id.ok);

        pos = getIntent().getIntExtra("posi", -1);
        a = getIntent().getIntArrayExtra("array");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mLayout=findViewById(R.id.sliding);

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i("Main", "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i("Main", "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        brand = getIntent().getStringExtra("brand");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay.setVisibility(View.GONE);
            }
        });

           final AlertDialog alertDialog = new SpotsDialog.Builder()
                            .setContext(RestaurentActivity.this)
                            .setMessage("Searching nearby")
                            .setCancelable(false)
                            .setTheme(R.style.Custom)
                            .build();;
                            alertDialog.show();

        databaseReference.child("Brands").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {


                    Map<String, Object> map = (Map<String, Object>) dataSnapshot1.getValue();


                    if (map.containsValue(brand)) {

                        //Log.i("", String.valueOf(map.size()));
                        //Log.i("",dataSnapshot1.getKey());
                        names.add(dataSnapshot1.getKey());
                        Log.i("size", String.valueOf(names.size()));


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("Saved").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    lat = Double.parseDouble(dataSnapshot1.child("latitude").getValue(String.class));
                    lon = Double.parseDouble(dataSnapshot1.child("longitude").getValue(String.class));


                    LatLng lastloc = getLastKnownLocation();

                    final double dist = ((distance(lastloc.latitude, lastloc.longitude, lat, lon))) / 1000;
                    Log.i("d", String.valueOf(dist));

                    Log.i("", dataSnapshot1.getKey());


                    Task<Void> task = databaseReference.child("Saved").child(dataSnapshot1.getKey()).child("distance").setValue(dist);

                    task.addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            alertDialog.dismiss();
                            if (dist < 10 && (dataSnapshot1.child("restaurenttype").getValue(String.class)).matches("indian")) {
                                if (names.contains(dataSnapshot1.getKey())) {
                                    MarkerOptions markerOptions = new MarkerOptions();

                                    LatLng latLng = new LatLng(Double.parseDouble(dataSnapshot1.child("latitude").getValue(String.class)), Double.parseDouble(dataSnapshot1.child("longitude").getValue(String.class)));
                                    markerOptions.position(latLng);

                                    markerOptions.title(dataSnapshot1.getKey());

                                    int height = 100;
                                    int width = 100;
                                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.bluemarker);
                                    Bitmap b=bitmapdraw.getBitmap();
                                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                                    mMap.animateCamera(CameraUpdateFactory.newLatLng(getLastKnownLocation()));

                                    // Placing a marker on the touched position
                                    mMap.addMarker(markerOptions);


                                }
                            }
                            if (dist < 10 && (dataSnapshot1.child("restaurenttype").getValue(String.class)).matches("nonindian")) {
                                MarkerOptions markerOptions = new MarkerOptions();

                                LatLng latLng = new LatLng(Double.parseDouble(dataSnapshot1.child("latitude").getValue(String.class)), Double.parseDouble(dataSnapshot1.child("longitude").getValue(String.class)));
                                markerOptions.position(latLng);

                                int height = 100;
                                int width = 100;
                                BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.redmarker);
                                Bitmap b=bitmapdraw.getBitmap();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);


                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                markerOptions.title(dataSnapshot1.getKey());

                                mMap.animateCamera(CameraUpdateFactory.newLatLng(getLastKnownLocation()));

                                // Placing a marker on the touched position
                                mMap.addMarker(markerOptions);


                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private BitmapDescriptor getBitmapDescriptor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) getDrawable(id);

            int h = vectorDrawable.getIntrinsicHeight();
            int w = vectorDrawable.getIntrinsicWidth();

            vectorDrawable.setBounds(0, 0, w, h);

            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bm);

        } else {
            return BitmapDescriptorFactory.fromResource(id);
        }
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
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
        updateLocationUI();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getLastKnownLocation(), 19));


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(details.getVisibility()==View.VISIBLE)
                {
                    details.setVisibility(View.GONE);
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {


                String mark = marker.getTitle();
                Log.i("ljn", mark);
                /*
                FirebaseDatabase.getInstance().getReference().child("Saved").child(mark).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        Log.i("", dataSnapshot.child("latitude").getValue(String.class) + (dataSnapshot.child("longitude").getValue(String.class)));


                        origin=new LatLng(Double.parseDouble(dataSnapshot.child("latitude").getValue(String.class)),Double.parseDouble(dataSnapshot.child("longitude").getValue(String.class)));

                        LatLng destPosition = getLastKnownLocation();


                        String url = getDirectionsUrl(origin, destPosition);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                Intent i = new Intent(RestaurentActivity.this, MarkerDetails.class);
                i.putExtra("array",a);
                i.putExtra("posi",pos);
                i.putExtra("bname", marker.getTitle());
                startActivity(i);
                overridePendingTransition(R.anim.slidein, R.anim.slideout);
*/



                choose.setVisibility(View.INVISIBLE);
                spin.setVisibility(View.VISIBLE);

                FirebaseDatabase.getInstance().getReference().child("Saved").child(mark).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final details1 de = dataSnapshot.getValue(details1.class);

                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(RestaurentActivity.this);
                                builder.setMessage("Are you sure to call "+de.getBuisnessname());
                                builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel:"+de.getContact1()));
                                        startActivity(intent);
                                        builder.create().dismiss();
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        builder.create().dismiss();
                                    }
                                });

                                builder.create().show();
                            }
                        });

                        area.setText(de.getAreaname());
                        name.setText(de.getBuisnessname());
                        String phone=de.getContact1();
                        if(de.getOnsite().equals("no"))
                        {
                            onsitekms.setText("None");
                        }
                        else {
                            onsitekms.setText(de.getKms()+" kms");
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

                        spin.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                return true;
            }
        });


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

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);


            } else {
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
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
                }
            }
        }
        updateLocationUI();

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

    public static <T extends Comparable<T>> int findMinIndex(final List<T> xs) {
        int minIndex;
        if (xs.isEmpty()) {
            minIndex = -1;
        } else {
            final ListIterator<T> itr = xs.listIterator();
            T min = itr.next(); // first element as the current minimum
            minIndex = itr.previousIndex();
            while (itr.hasNext()) {
                final T curr = itr.next();
                if (curr.compareTo(min) < 0) {
                    min = curr;
                    minIndex = itr.previousIndex();
                }
            }
        }
        return minIndex;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            Log.i("result",result);
            parserTask.execute(result);

        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

                mMap.addPolyline(lineOptions);


            }

// Drawing polyline in the Google Map for the i-th route
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        LatLng latLng=mMap.getCameraPosition().target;

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

        LatLng coordinate=new LatLng(lat,longg);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                coordinate, 15);
        mMap.animateCamera(location);


    }
    @Override
    protected void onPause() {
        super.onPause();

    }
}

