package com.example.restaurantfinder;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.concurrent.ConcurrentSkipListMap;

public class Login extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    private static final int RC_SIGN_IN = 123;
//    TextView getgoin,looking;
//    ConstraintLayout constraintLayout;
//    LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
//        FacebookSdk.sdkInitialize(Login.this);
//        constraintLayout=findViewById(R.id.cons);


        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            startActivity(new Intent(Login.this, HomeMaps.class));
            overridePendingTransition(R.anim.top, R.anim.bottom);
            finish();
        }
        else Authenticate();


//        looking=findViewById(R.id.looking);
//        getgoin=findViewById(R.id.getgoing);
//        animationView=findViewById(R.id.lottieAnimationView);


//        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
//
//        animationDrawable.setEnterFadeDuration(2500);
//        animationDrawable.setExitFadeDuration(5000);
//
//        animationDrawable.start();
//
//        animationView.playAnimation();


//        animationView.addLottieOnCompositionLoadedListener(new LottieOnCompositionLoadedListener() {
//            @Override
//            public void onCompositionLoaded(LottieComposition composition) {
//
//                        animationView.setVisibility(View.VISIBLE);
//                        YoYo.with(Techniques.BounceIn)
//                                .duration(1000)
//                                .playOn(animationView);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        looking.setVisibility(View.VISIBLE);
//                        YoYo.with(Techniques.BounceInUp)
//                                .duration(1000)
//                                .playOn(looking);
//                    }
//                },1000);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        getgoin.setVisibility(View.VISIBLE);
//                        YoYo.with(Techniques.BounceInUp)
//                                .duration(1000)
//                                .playOn(getgoin);
//                    }
//                },2000);
//
//            }
//        });


    }

    private void Authenticate() {
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_login)
                .setGoogleButtonId(R.id.googlesignin)
                .setFacebookButtonId(R.id.facebooksignin)
                .build();

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder()
                                        .setScopes(Arrays.asList(Scopes.PROFILE))
                                        .build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false)
                        .setAuthMethodPickerLayout(customLayout)
                        .setTheme(R.style.SigninTheme)
                        .build(), RC_SIGN_IN
        );
    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {

                if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                {
                    FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.child("Users").exists())
                            {
                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("About");
                                databaseReference.child("image").setValue(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
                                databaseReference.child("name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                databaseReference.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(Login.this, HomeMaps.class));
                                        overridePendingTransition(R.anim.top, R.anim.bottom);
                                        finish();
                                    }
                                });

                            }else
                            {
                                if(!dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists())
                                {
                                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("About");
                                    databaseReference.child("image").setValue(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
                                    databaseReference.child("name").setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                    databaseReference.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startActivity(new Intent(Login.this, HomeMaps.class));
                                            overridePendingTransition(R.anim.top, R.anim.bottom);
                                            finish();
                                        }
                                    });
                                }
                                else {
                                    startActivity(new Intent(Login.this, HomeMaps.class));
                                    overridePendingTransition(R.anim.top, R.anim.bottom);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }


            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "Sign in Cancelled", Toast.LENGTH_SHORT).show();
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "No network", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(this, "Sign In Error", Toast.LENGTH_SHORT).show();
                Log.e("Ac", "Sign-in error: ", response.getError());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
