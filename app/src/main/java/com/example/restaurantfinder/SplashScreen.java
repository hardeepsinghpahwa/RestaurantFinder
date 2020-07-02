package com.example.restaurantfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SplashScreen extends AppCompatActivity {
    private Boolean firstTime = null;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


         sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        Thread myThread = new Thread()
        {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    if (isFirstTime()) {
                        sharedPreferences.edit().putString("first","no");
                        Intent intent = new Intent(getApplicationContext(),Intro.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.top,R.anim.bottom);
                        finish();
                    }
                    else{
                        Intent intent = new Intent(getApplicationContext(),Login.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.top,R.anim.bottom);
                        finish();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }

    private boolean isFirstTime() {
        if (firstTime == null) {
            SharedPreferences mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
            firstTime = mPreferences.getBoolean("firstTime", true);
            if (firstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
            }
        }
        return firstTime;
    }

}
