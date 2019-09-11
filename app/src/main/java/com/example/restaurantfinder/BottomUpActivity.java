package com.example.restaurantfinder;

import android.net.Uri;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.content.res.AppCompatResources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.restaurantfinder.ViewPagerFrags.NonVeg;
import com.example.restaurantfinder.ViewPagerFrags.Veg;

public class BottomUpActivity extends AppCompatActivity implements Veg.OnFragmentInteractionListener, NonVeg.OnFragmentInteractionListener{


    TabLayout tabLayout;
    CustomViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_up);


        DisplayMetrics displayMetrics=new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width=displayMetrics.widthPixels;
        int height=displayMetrics.heightPixels;

        getWindow().setLayout(width*1,(int)(height*0.5));
        getWindow().setGravity(Gravity.BOTTOM);

        tabLayout=findViewById(R.id.tablayout);
        viewPager=findViewById(R.id.viewpager);


        tabLayout.addTab(tabLayout.newTab().setText("VEG"));
        tabLayout.addTab(tabLayout.newTab().setText("NON-VEG"));
        tabLayout.setTabGravity(Gravity.NO_GRAVITY);

        setTabBG(R.drawable.left_select,R.drawable.right_unselect);

        final FragmentPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tabLayout.getSelectedTabPosition()==0) {
                    setTabBG(R.drawable.left_select,R.drawable.right_unselect);
                }
                else {
                    setTabBG(R.drawable.left_unselect,R.drawable.right_select);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setPagingEnabled(false);

    }



    private void setTabBG(int tab1, int tab2){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ViewGroup tabStrip = (ViewGroup) tabLayout.getChildAt(0);
            View tabView1 = tabStrip.getChildAt(0);
            View tabView2 = tabStrip.getChildAt(1);
            if (tabView1 != null) {
                int paddingStart = tabView1.getPaddingStart();
                int paddingTop = tabView1.getPaddingTop();
                int paddingEnd = tabView1.getPaddingEnd();
                int paddingBottom = tabView1.getPaddingBottom();
                ViewCompat.setBackground(tabView1, AppCompatResources.getDrawable(tabView1.getContext(), tab1));
                ViewCompat.setPaddingRelative(tabView1, paddingStart, paddingTop, paddingEnd, paddingBottom);
            }

            if (tabView2 != null) {
                int paddingStart = tabView2.getPaddingStart();
                int paddingTop = tabView2.getPaddingTop();
                int paddingEnd = tabView2.getPaddingEnd();
                int paddingBottom = tabView2.getPaddingBottom();
                ViewCompat.setBackground(tabView2, AppCompatResources.getDrawable(tabView2.getContext(), tab2));
                ViewCompat.setPaddingRelative(tabView2, paddingStart, paddingTop, paddingEnd, paddingBottom);
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.top,R.anim.bottom);
    }
}
