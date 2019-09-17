package com.example.restaurantfinder.ViewPagerFrags;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.restaurantfinder.CuisineDialog;
import com.example.restaurantfinder.HomeMaps;
import com.example.restaurantfinder.R;

import java.util.ArrayList;


public class NonVeg extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public NonVeg() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    ArrayList<Integer> imgs = new ArrayList<>();
    int[] images;
    LinearLayoutManager linearLayoutManager;


    // TODO: Rename and change types and number of parameters
    public static NonVeg newInstance(String param1, String param2) {
        NonVeg fragment = new NonVeg();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_non_veg, container, false);
        images = new int[]{R.drawable.nonindianfood, R.drawable.seafood1,R.drawable.biryani,R.drawable.healthynonveg,R.drawable.thainonveg};

        recyclerView=v.findViewById(R.id.nonvegrecyclerview);
        recyclerView.setAdapter(new GridAdapter(images,getActivity()));
        linearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {

        int a[];
        String brands[]=new String[]{"Indian","Sea Food","Biryani","Healthy Non Veg","Thai"};
        String names[]=new String[]{"Indian","Sea Food","Biryani","Healthy Non Veg","Thai"};

        int lastPosition=-1;

        Context context;
        ArrayList<Integer> images = new ArrayList<>();

        public GridAdapter(int[] a, Context context) {
            this.a = a;
            this.context = context;
        }

        @NonNull
        @Override
        public GridAdapter.GridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.griditem, null);

            return new GridAdapter.GridViewHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull GridAdapter.GridViewHolder gridViewHolder, int i) {
            gridViewHolder.imageView.setImageResource(a[i]);
            gridViewHolder.name.setText(names[i]);
        }

        @Override
        public int getItemCount() {
            return a.length;
        }

        @Override
        public void onViewAttachedToWindow(@NonNull final GridViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            holder.itemView.setVisibility(View.INVISIBLE);

            if (holder.getPosition() > lastPosition) {
                holder.itemView.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.itemView.setVisibility(View.VISIBLE);
                        ObjectAnimator alpha = ObjectAnimator.ofFloat(holder.itemView, "alpha", 0f, 1f);
                        ObjectAnimator scaleY = ObjectAnimator.ofFloat(holder.itemView, "scaleY", 0f, 1f);
                        ObjectAnimator scaleX = ObjectAnimator.ofFloat(holder.itemView, "scaleX", 0f, 1f);
                        AnimatorSet animSet = new AnimatorSet();
                        animSet.play(alpha).with(scaleY).with(scaleX);
                        animSet.setInterpolator(new OvershootInterpolator());
                        animSet.setDuration(400);
                        animSet.start();

                    }
                }, 200);

                lastPosition = holder.getPosition();
            } else {
                holder.itemView.setVisibility(View.VISIBLE);
            }
        }

        public class GridViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView name;

            private SparseBooleanArray selectedItems = new SparseBooleanArray();


            public GridViewHolder(@NonNull final View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.gridimage);
                name=itemView.findViewById(R.id.cuisinename);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        CuisineDialog.display(getFragmentManager(),"restaurant",getAdapterPosition(),a,brands[getAdapterPosition()]);

                        Intent intent=new Intent(getActivity(), HomeMaps.class);
                        intent.putExtra("posi",getAdapterPosition());
                        intent.putExtra("whichtype","restaurant");
                        intent.putExtra("array",a);
                        intent.putExtra("brand",brands[getAdapterPosition()]);
                        startActivity(intent);
                        //getActivity().finish();
                        getActivity().overridePendingTransition(R.anim.fadein,R.anim.fadeout);


                    }
                });
            }
        }
    }
}
