package com.example.restaurantfinder;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.tapadoo.alerter.Alerter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ReviewDialog extends DialogFragment {

    public static final String TAG = "example_dialog";
    static String id;
    ImageView pic;
    TextView area, name;
    RatingBar ratingBar;
    EditText review;
    DatabaseReference databaseReference;
    MaterialButton submit, cancel;
    String rating;
    TextView limit;

    public ReviewDialog() {

    }

    public static ReviewDialog display(FragmentManager fragmentManager, String i) {

        ReviewDialog exampleDialog = new ReviewDialog();
        exampleDialog.show(fragmentManager, TAG);
        id = i;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.reviewdialog, container, false);

        ratingBar = view.findViewById(R.id.ratingBar);
        area = view.findViewById(R.id.reviewarea);
        pic = view.findViewById(R.id.reviewpic);
        name = view.findViewById(R.id.reiviewname);
        review = view.findViewById(R.id.review);
        submit = view.findViewById(R.id.reviewsubmit);
        cancel = view.findViewById(R.id.reviewcancel);
        limit = view.findViewById(R.id.limit);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Reviews").child(id).child(UUID.randomUUID().toString());

        FirebaseDatabase.getInstance().getReference().child("Saved").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText(dataSnapshot.child("buisnessname").getValue(String.class));
                area.setText(dataSnapshot.child("areaname").getValue(String.class));
                Glide.with(getContext()).load(dataSnapshot.child("profilepic").getValue(String.class)).into(pic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        review.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int wordsLength = countWords(s.toString());// words.length;
                // count == 0 means a new word is going to start
                if (wordsLength >= 80) {
                    setCharLimit(review, review.getText().length());
                } else {
                    removeFilter(review);
                }

                limit.setText((wordsLength) + "/" + 80);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = String.valueOf(ratingBar.getRating());

                if (ratingBar.getRating() == 0) {
                    Toast.makeText(getActivity(), "Select A Rating First", Toast.LENGTH_SHORT).show();
                } else {

                    SimpleDateFormat spf=new SimpleDateFormat("MMM dd, yyyy hh:mm aaa");

                    Date c = Calendar.getInstance().getTime();

                    String formattedDate = spf.format(c);


                    Review re = new Review(rating, review.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid(), String.valueOf(formattedDate));



                    databaseReference.setValue(re).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Map map = new HashMap();
                            map.put("timestamp", (ServerValue.TIMESTAMP));
                            databaseReference.updateChildren(map);
                            Alerter.create(getActivity())
                                    .setDuration(500)
                                    .setBackgroundColorInt(Color.parseColor("#FFA000"))
                                    .setTitle("Review Added")
                                    .setText("Thank You For The Review")
                                    .show();

                            getDialog().dismiss();
                        }
                    });
                }
            }
        });


        return view;
    }

    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }

    private InputFilter filter;

    private void setCharLimit(EditText et, int max) {
        filter = new InputFilter.LengthFilter(max);
        et.setFilters(new InputFilter[]{filter});
    }

    private void removeFilter(EditText et) {
        if (filter != null) {
            et.setFilters(new InputFilter[0]);
            filter = null;
        }

    }
}
