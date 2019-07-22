package com.example.restaurantfinder.ViewPagerFrags;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.restaurantfinder.HomeMaps;
import com.example.restaurantfinder.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NonVeg.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NonVeg#newInstance} factory method to
 * create an instance of this fragment.
 */
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
        images = new int[]{R.drawable.indiannonveg, R.drawable.seafood};

        recyclerView=v.findViewById(R.id.nonvegrecyclerview);
        recyclerView.setAdapter(new GridAdapter(images,getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));

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
        String brands[]=new String[]{"nonveg1","nonveg2"};

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
        }

        @Override
        public int getItemCount() {
            return a.length;
        }


        public class GridViewHolder extends RecyclerView.ViewHolder {
            CircularImageView imageView;
            private SparseBooleanArray selectedItems = new SparseBooleanArray();


            public GridViewHolder(@NonNull final View itemView) {
                super(itemView);

                imageView = itemView.findViewById(R.id.gridimage);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(), HomeMaps.class);
                        intent.putExtra("posi",getAdapterPosition());
                        intent.putExtra("array",a);
                        intent.putExtra("brand",brands[getAdapterPosition()]);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
            }
        }
    }
}
