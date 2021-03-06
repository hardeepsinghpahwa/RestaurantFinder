package com.example.restaurantfinder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CuisineDialog extends DialogFragment {

    public static final String TAG = "example_dialog";


    Button goforit,change;
    ImageView cuisineimg;
    TextView cuisinename;
    static String whichtype,cuisine;
    static int position;
    static int imgs[];

    public CuisineDialog() {

    }


    public static CuisineDialog display(FragmentManager fragmentManager,String type,int posi,int img[],String cui) {

        CuisineDialog exampleDialog = new CuisineDialog();
        exampleDialog.show(fragmentManager, TAG);
        whichtype=type;
        position=posi;
        imgs=img;
        cuisine=cui;
        return exampleDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Searching);

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        LayoutInflater layoutInflater = (LayoutInflater) getContext()
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.cuisinedialog, null, true);


        cuisineimg=view.findViewById(R.id.cuisineimage);
        cuisinename=view.findViewById(R.id.cuisinetosearch);
        goforit=view.findViewById(R.id.goforit);
        change=view.findViewById(R.id.cancelsearch);


        if(whichtype.equals("restaurant"))
        {
            cuisinename.setText(cuisine);
            cuisineimg.setImageResource(imgs[position]);
        }

        else if(whichtype.equals("dhaba"))
        {
            cuisinename.setText(cuisine);
            cuisineimg.setImageResource(R.drawable.dhaba);
        }

        else if(whichtype.equals("cafe"))
        {
            cuisinename.setText(cuisine);
            cuisineimg.setImageResource(R.drawable.coffee);
        }


        goforit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String v="";
                String posi= String.valueOf(position);

                Map<String,Object> map=new HashMap<>();
                map.put("cuisine", cuisine);
                map.put("posi",posi);
                map.put("timestamp", (ServerValue.TIMESTAMP));
                map.put("whichtype",whichtype);
                if(imgs.length>5)
                {
                    v="veg";
                }
                else {
                    v="nonveg";
                }
                map.put("vegnonveg",v);

                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Search History").child(UUID.randomUUID().toString()).updateChildren(map);

                getDialog().dismiss();
                Log.i("ac",getActivity().getClass().getSimpleName());
                if(getActivity().getClass().getSimpleName().equals("BottomUpActivity"))
                {
                    getActivity().finish();
                }
                Intent intent = new Intent(getActivity(), RestaurentActivity.class);
                intent.putExtra("brand", cuisine);
                intent.putExtra("array", imgs);
                intent.putExtra("posi", position);
                intent.putExtra("whichtype",whichtype);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.alerter_slide_in_from_left, R.anim.alerter_slide_out_to_right);
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });


        return view;
    }
}
