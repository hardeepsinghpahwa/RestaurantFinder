package com.example.restaurantfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;

public class Intro extends AppCompatActivity {


    ViewPager intro;
    ArrayList<Integer> items = new ArrayList<>();
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        intro = findViewById(R.id.introviewpager);

        items.add(R.layout.introitem);
        items.add(R.layout.introitem2);
        items.add(R.layout.introitem3);
        next = findViewById(R.id.nextintro);
        intro.setPageTransformer(true, new ZoomOutPageTransformer());

        intro.setAdapter(new ViewPagerAdapter1());

        intro.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
                if (next.getText().equals("Next")) {
                    next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            intro.setCurrentItem(position+1,true);
                        }
                    });
                }
            }

            @Override
            public void onPageSelected(final int position) {
                if (position == 2) {

                    next.setText("Go To Login Page");
                    next.setBackgroundColor(Color.parseColor("#76ba1b"));
                    YoYo.with(Techniques.ZoomIn)
                            .duration(500)
                            .playOn(next);
                }
                else {
                    YoYo.with(Techniques.ZoomIn)
                            .duration(500)
                            .playOn(next);
                    next.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    next.setText("Next");
                }
                 if (next.getText().equals("Go To Login Page")) {
                    next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intro.this, Login.class));
                            overridePendingTransition(R.anim.alerter_slide_in_from_left, R.anim.alerter_slide_out_to_right);
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    class ViewPagerAdapter1 extends PagerAdapter {
        public ViewPagerAdapter1() {
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            int modelObject = items.get(position);
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View layout = (ViewGroup) inflater.inflate(modelObject, container, false);
            container.addView(layout);


            return layout;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

    }
}
