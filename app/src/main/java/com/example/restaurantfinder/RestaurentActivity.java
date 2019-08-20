package com.example.restaurantfinder;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.RelativeLayout;

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

    RelativeLayout overlay;
    Button ok;
    GoogleMap mMap;
    LatLng origin;
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
        overlay = findViewById(R.id.overlay);
        ok = findViewById(R.id.ok);

        pos = getIntent().getIntExtra("posi", -1);
        a = getIntent().getIntArrayExtra("array");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.indian));

                                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                                    // Placing a marker on the touched position
                                    mMap.addMarker(markerOptions);


                                }
                            }
                            if (dist < 10 && (dataSnapshot1.child("restaurenttype").getValue(String.class)).matches("nonindian")) {
                                MarkerOptions markerOptions = new MarkerOptions();

                                LatLng latLng = new LatLng(Double.parseDouble(dataSnapshot1.child("latitude").getValue(String.class)), Double.parseDouble(dataSnapshot1.child("longitude").getValue(String.class)));
                                markerOptions.position(latLng);


                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.non));

                                markerOptions.title(dataSnapshot1.getKey());

                                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

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



        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {

                String mark = marker.getTitle();
                Log.i("ljn", mark);

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

        startActivity(new Intent(RestaurentActivity.this,HomeMaps.class));
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

