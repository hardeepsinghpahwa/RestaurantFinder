package com.example.restaurantfinder;

import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.smarteist.autoimageslider.CircularSliderHandle;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class ViewImagesDialog extends DialogFragment {

    public static final String TAG = "example_dialog";
    private ArrayList<String> ims;
    private int po;
    private SliderView sliderView;
    private ImageView back;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private TextView imgnumber;

    public ViewImagesDialog(ArrayList<String> ims, int po) {
        this.ims = ims;
        this.po = po;
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
            dialog.setCancelable(false);
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_SlideFromRight);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.viewimagesdialog, container, false);

        sliderView=view.findViewById(R.id.viewimagesslider);
        back=view.findViewById(R.id.viewimageback);
        sliderView.setSliderAdapter(new SliderAdapter(getContext(),ims));
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.setCurrentPagePosition(po);
        imgnumber=view.findViewById(R.id.imgno);
        imgnumber.setText(po+1+"/"+ims.size());

        sliderView.setCurrentPageListener(new CircularSliderHandle.CurrentPageListener() {
            @Override
            public void onCurrentPageChanged(int currentPosition) {
                imgnumber.setText(currentPosition+1+"/"+ims.size());
            }
        });

        Log.i("size", String.valueOf(ims.size()));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }

    class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

        private Context context;
        ArrayList<String> imgs;

        public SliderAdapter(Context context, ArrayList<String> imgs) {
            this.context = context;
            this.imgs = imgs;
        }

        @Override
        public SliderAdapter.SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageslideritem, null);
            return new SliderAdapter.SliderAdapterVH(inflate);
        }


        @Override
        public void onBindViewHolder(SliderAdapter.SliderAdapterVH viewHolder, int position) {



            if (imgs.size() == 0) {
                viewHolder.imageViewBackground.setImageResource(R.drawable.imgsliderback);
            } else {
                Glide.with(context).load(imgs.get(position)).placeholder(R.drawable.imgbck).into(viewHolder.imageViewBackground);
            }
        }

        @Override
        public int getCount() {
            //slider view count could be dynamic size
            if (imgs.size() == 0) {
                return 1;
            } else return imgs.size();
        }


        class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

            View itemView;
            PhotoView imageViewBackground;

            public SliderAdapterVH(View itemView) {
                super(itemView);
                imageViewBackground = itemView.findViewById(R.id.slideimg);
                this.itemView = itemView;
            }
        }


    }

}