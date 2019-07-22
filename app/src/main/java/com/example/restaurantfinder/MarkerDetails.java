package com.example.restaurantfinder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MarkerDetails extends AppCompatActivity {

    String bname;
    CheckBox sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    TextView onsitekms;
    TextView timefrom, timeto,name;
    ImageView call;
    int a[]=new int[]{};
    ImageView imageView;
    int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_details);

        DisplayMetrics displayMetrics=new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width=displayMetrics.widthPixels;
        int height=displayMetrics.heightPixels;
        imageView=findViewById(R.id.img);

        pos=getIntent().getIntExtra("posi",-1);
        a=getIntent().getIntArrayExtra("array");

        imageView.setImageResource(a[pos]);

        getWindow().setLayout(width*1,(int)(height*0.4));
        getWindow().setGravity(Gravity.BOTTOM);

        bname=getIntent().getStringExtra("bname");
        onsitekms = findViewById(R.id.kms);
        call=findViewById(R.id.call);
        timefrom = findViewById(R.id.timefrom);
        timeto = findViewById(R.id.timeto);
        name = findViewById(R.id.name);


        sunday = findViewById(R.id.sunday);
        monday = findViewById(R.id.monday);
        tuesday = findViewById(R.id.tuesday);
        wednesday = findViewById(R.id.wednesday);
        thursday = findViewById(R.id.thursday);
        friday = findViewById(R.id.friday);
        saturday = findViewById(R.id.saturday);

        sunday.setChecked(true);
        monday.setChecked(true);
        tuesday.setChecked(true);
        wednesday.setChecked(true);
        thursday.setChecked(true);
        friday.setChecked(true);
        saturday.setChecked(true);



        FirebaseDatabase.getInstance().getReference().child("Saved").child(bname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final details1 de = dataSnapshot.getValue(details1.class);


                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder=new AlertDialog.Builder(MarkerDetails.this);
                        builder.setMessage("Are you sure to call "+de.getBuisnessname());
                        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:"+de.getContact1()));
                                startActivity(intent);
                                builder.create().dismiss();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.create().dismiss();
                            }
                        });

                        builder.create().show();
                    }
                });

                name.setText(de.getBuisnessname());
                String phone=de.getContact1();
                timefrom.setText(de.getFromtime());
                timeto.setText(de.getTotime());
                if(de.getOnsite().equals("no"))
                {
                    onsitekms.setText("No Onsite Service");
                }
                else {
                    onsitekms.setText(de.getKms()+" kms");
                }

                if (de.getSunday().matches("false")) {
                    sunday.setBackground(getResources().getDrawable(R.drawable.checkboxselect));
                    sunday.setTextColor(Color.WHITE);
                    sunday.setChecked(false);
                } else if (de.getSunday().matches("true")) {
                    sunday.setBackground(getResources().getDrawable(R.drawable.checkboxunselect));
                    sunday.setTextColor(Color.parseColor("#e0e0e0"));
                    sunday.setChecked(true);
                }


                if (de.getMonday().matches("false")) {
                    monday.setBackground(getResources().getDrawable(R.drawable.checkboxselect));
                    monday.setTextColor(Color.WHITE);
                    monday.setChecked(false);
                } else if (de.getMonday().matches("true")) {
                    monday.setBackground(getResources().getDrawable(R.drawable.checkboxunselect));
                    monday.setTextColor(Color.parseColor("#e0e0e0"));
                    monday.setChecked(true);
                }

                if (de.getTuesday().matches("false")) {
                    tuesday.setBackground(getResources().getDrawable(R.drawable.checkboxselect));
                    tuesday.setTextColor(Color.WHITE);
                    tuesday.setChecked(false);
                } else if (de.getTuesday().matches("true")) {
                    tuesday.setBackground(getResources().getDrawable(R.drawable.checkboxunselect));
                    tuesday.setTextColor(Color.parseColor("#e0e0e0"));
                    tuesday.setChecked(true);
                }

                if (de.getWednesday().matches("false")) {
                    wednesday.setBackground(getResources().getDrawable(R.drawable.checkboxselect));
                    wednesday.setTextColor(Color.WHITE);
                    wednesday.setChecked(false);
                } else if (de.getWednesday().matches("true")) {
                    wednesday.setBackground(getResources().getDrawable(R.drawable.checkboxunselect));
                    wednesday.setTextColor(Color.parseColor("#e0e0e0"));
                    wednesday.setChecked(true);
                }

                if (de.getThursday().matches("false")) {
                    thursday.setBackground(getResources().getDrawable(R.drawable.checkboxselect));
                    thursday.setTextColor(Color.WHITE);
                    thursday.setChecked(false);
                } else if (de.getThursday().matches("true")) {
                    thursday.setBackground(getResources().getDrawable(R.drawable.checkboxunselect));
                    thursday.setTextColor(Color.parseColor("#e0e0e0"));
                    thursday.setChecked(true);
                }

                if (de.getFriday().matches("false")) {
                    friday.setBackground(getResources().getDrawable(R.drawable.checkboxselect));
                    friday.setTextColor(Color.WHITE);
                    friday.setChecked(false);
                } else if (de.getFriday().matches("true")) {
                    friday.setBackground(getResources().getDrawable(R.drawable.checkboxunselect));
                    friday.setTextColor(Color.parseColor("#e0e0e0"));
                    friday.setChecked(true);
                }

                if (de.getSaturday().matches("false")) {
                    saturday.setBackground(getResources().getDrawable(R.drawable.checkboxselect));
                    saturday.setTextColor(Color.WHITE);
                    saturday.setChecked(false);
                } else if (de.getSaturday().matches("true")) {
                    saturday.setBackground(getResources().getDrawable(R.drawable.checkboxunselect));
                    saturday.setTextColor(Color.parseColor("#e0e0e0"));
                    saturday.setChecked(true);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
