package com.example.restaurantfinder.ProfileActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.restaurantfinder.R;

public class SearchHistory extends AppCompatActivity {


    RecyclerView recyclerView;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);


        back=findViewById(R.id.backbutonsearchhistory);
        recyclerView=findViewById(R.id.recyclerviewsearchhistory);



    }
}
