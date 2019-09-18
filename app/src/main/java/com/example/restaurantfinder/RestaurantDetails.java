package com.example.restaurantfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class RestaurantDetails extends AppCompatActivity {


    String id;
    SliderView sliderView;
    CollapsingToolbarLayout collapsingToolbarLayout;
    CheckBox sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    GoogleMap map;
    Button edit,editlocation;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    SwitchCompat status;
    Toolbar toolbar;
    CardView mapcard;
    boolean mLocationPermissionGranted = false;
    NestedScrollView scrollView;
    LinearLayout checkboxes, from, to;
    SpinKitView spinKitView;
    ArrayList<String> images;
    ImageView propic;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<menu,MenuViewHolder> firebaseRecyclerAdapter;
    TextView name, area, phone, opensat, closesat,menutext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        images=new ArrayList<>();
        sliderView = findViewById(R.id.imageSlider);
        sliderView.setIndicatorAnimation(IndicatorAnimations.SWAP);
        sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        id=getIntent().getStringExtra("id");

        FirebaseDatabase.getInstance().getReference().child("Images").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                images.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    images.add(dataSnapshot1.child("image").getValue(String.class));
                }

                sliderView.setSliderAdapter(new SliderAdapter(RestaurantDetails.this,images));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

        private Context context;
        ArrayList<String> imgs;

        public SliderAdapter(Context context,ArrayList<String> imgs) {
            this.context = context;
            this.imgs=imgs;
        }

        @Override
        public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageslideritem, null);
            return new SliderAdapterVH(inflate);
        }

        @Override
        public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {

            if(imgs.size()==0)
            {
                viewHolder.imageViewBackground.setImageResource(R.drawable.imgsliderback);
            }
            else {
                Glide.with(context).load(imgs.get(position)).placeholder(R.drawable.imgbck).into(viewHolder.imageViewBackground);
            }
        }

        @Override
        public int getCount() {
            //slider view count could be dynamic size
            if(imgs.size()==0)
            {
                return 1;
            }
            else return imgs.size();
        }

        class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

            View itemView;
            ImageView imageViewBackground;

            public SliderAdapterVH(View itemView) {
                super(itemView);
                imageViewBackground = itemView.findViewById(R.id.slideimg);
                this.itemView = itemView;
            }
        }
    }
    class MenuViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.menuimg);
        }
}
}
