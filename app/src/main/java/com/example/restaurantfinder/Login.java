package com.example.restaurantfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieOnCompositionLoadedListener;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.concurrent.ConcurrentSkipListMap;

public class Login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final int RC_SIGN_IN_F = 2;
    TextView getgoin,looking;
    ConstraintLayout constraintLayout;
    LottieAnimationView animationView;
    Button google,facebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(Login.this);
        constraintLayout=findViewById(R.id.cons);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            startActivity(new Intent(Login.this,HomeMaps.class));
        }

        looking=findViewById(R.id.looking);
        getgoin=findViewById(R.id.getgoing);
        animationView=findViewById(R.id.lottieAnimationView);


        google=findViewById(R.id.googlesignin);
        facebook=findViewById(R.id.facebooksignin);

        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();

        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);

        animationDrawable.start();

        animationView.playAnimation();


        google.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInUp)
                .duration(1000)
                .playOn(google);

        facebook.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInUp)
                .duration(1000)
                .playOn(facebook);


        animationView.addLottieOnCompositionLoadedListener(new LottieOnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(LottieComposition composition) {

                        animationView.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.BounceIn)
                                .duration(1000)
                                .playOn(animationView);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        looking.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.BounceInUp)
                                .duration(1000)
                                .playOn(looking);
                    }
                },1000);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getgoin.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.BounceInUp)
                                .duration(1000)
                                .playOn(getgoin);
                    }
                },2000);

            }
        });





        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_login)
                .setGoogleButtonId(R.id.googlesignin)
                .setEmailButtonId(R.id.facebooksignin)
                // ...
                .build();

        AuthUI.getInstance(FirebaseApp.initializeApp(Login.this)).createSignInIntentBuilder()
                // ...
                .setAuthMethodPickerLayout(customLayout)
                .build();


        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.IdpConfig googleIdp = new AuthUI.IdpConfig.GoogleBuilder()
                        .setScopes(Arrays.asList(Scopes.PROFILE))
                        .build();

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(Arrays.asList(googleIdp))
                                .build(),
                        RC_SIGN_IN);
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.IdpConfig facebookIdp = new AuthUI.IdpConfig.FacebookBuilder()
                        .setPermissions(Arrays.asList("user_friends"))
                        .build();

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(Arrays.asList(facebookIdp))
                                .build(),
                        RC_SIGN_IN);
            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Login.this,HomeMaps.class));
                overridePendingTransition(R.anim.top,R.anim.bottom);
                finish();

            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, " null error", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No network", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                Log.e("Ac", "Sign-in error: ", response.getError());
            }
        }
    }
}
