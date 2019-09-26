package com.example.restaurantfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static com.example.restaurantfinder.NetworkUtil.TYPE_MOBILE;
import static com.example.restaurantfinder.NetworkUtil.TYPE_NOT_CONNECTED;
import static com.example.restaurantfinder.NetworkUtil.TYPE_WIFI;

public class NoInternet extends AppCompatActivity {

    Button retry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        retry=findViewById(R.id.retrybutton);
        final String status = NetworkUtil.getConnectivityStatusString(getApplicationContext());


        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.equals("Wifi enabled") || status.equals("Mobile data enabled"))
                {
                    finish();
                }
                else if(status.equals("Not connected to Internet"))
                {
                    Toast.makeText(NoInternet.this, "Still No Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
