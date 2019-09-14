package com.example.restaurantfinder;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieOnCompositionLoadedListener;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.restaurantfinder.ProfileActivities.Bookmarks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;


public class ProfileDialog extends DialogFragment implements RecentSearches.OnFragmentInteractionListener{

    public static final String TAG = "example_dialog";

    ImageView profilepic;
    TextView name;
    ImageView back,locationpin;
    TextView logout;
    LinearLayout bookmarks;
    TextView currentlocation;
    LottieAnimationView lottieAnimationView;
    DatabaseReference databaseReference;
    ConstraintLayout constraintLayout,constraintLayout1;

    public ProfileDialog() {

    }


    public static ProfileDialog display(FragmentManager fragmentManager) {

        ProfileDialog exampleDialog = new ProfileDialog();
        exampleDialog.show(fragmentManager, TAG);
        return exampleDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_ProfileDialog);

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Exit);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        LayoutInflater layoutInflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.profiledialog, null, true);

        back=view.findViewById(R.id.profilebackpress);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        constraintLayout1=view.findViewById(R.id.conslayout1);

        back.bringToFront();

        //constraintLayout1.getBackground().setAlpha(100);

        name=view.findViewById(R.id.name1);
        profilepic=view.findViewById(R.id.profilepic);
        currentlocation=view.findViewById(R.id.currentlocation);
        locationpin=view.findViewById(R.id.locationpin);


        constraintLayout=view.findViewById(R.id.conslayout);
        lottieAnimationView=view.findViewById(R.id.profilelottie);
        bookmarks=view.findViewById(R.id.profilebookmarks);

        bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Bookmarks.class));
                getActivity().overridePendingTransition(R.anim.alerter_slide_in_from_left,R.anim.alerter_slide_out_to_right);
            }
        });

        logout=view.findViewById(R.id.logout);

        lottieAnimationView.setSpeed(1.5f);

        name.bringToFront();
        locationpin.bringToFront();
        currentlocation.bringToFront();
        profilepic.bringToFront();

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                constraintLayout.setVisibility(View.VISIBLE);
                lottieAnimationView.setVisibility(View.GONE);
                YoYo.with(Techniques.SlideInRight)
                                .duration(500)
                               .playOn(constraintLayout);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(),Login.class));
            }
        });

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("About");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("name").getValue(String.class));
                Glide.with(getActivity()).load(dataSnapshot.child("image").getValue(String.class)).into(profilepic);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
