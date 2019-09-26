package com.example.restaurantfinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.Toast;

public class OnPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    private final static String TAG_PINCH_LISTENER = "PINCH_LISTENER";

    // Pinch zoon occurred on this image view object.
    private ImageView srcImageView = null;

    // Related appication context.
    private Context context = null;

    // The default constructor pass context and imageview object.
    public OnPinchListener(Context context, ImageView srcImageView) {
        this.context = context;
        this.srcImageView = srcImageView;
    }

    // When pinch zoom gesture occurred.
    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        if(detector!=null) {

            float scaleFactor = detector.getScaleFactor();

            if (srcImageView != null) {

                // Scale the image with pinch zoom value.
                scaleImage(scaleFactor, scaleFactor);

            } else {
                if (context != null) {
                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG_PINCH_LISTENER, "Both context and srcImageView is null.");
                }
            }
        }else
        {
            Log.e(TAG_PINCH_LISTENER, "Pinch listener onScale detector parameter is null.");
        }

        return true;
    }

    /* This method is used to scale the image when user pinch zoom it. */
    private void scaleImage(float xScale, float yScale)
    {
        // Get source image bitmap object.
        BitmapDrawable srcBitmapDrawable = (BitmapDrawable) srcImageView.getDrawable();
        Bitmap srcBitmap = srcBitmapDrawable.getBitmap();

        // Get source image width and height.
        int srcImageWith = srcBitmap.getWidth();
        int srcImageHeight = srcBitmap.getHeight();

        // Get source image config object.
        Bitmap.Config srcImageConfig = srcBitmap.getConfig();

        // Create a new bitmap which has scaled width and height value from source bitmap.
        Bitmap scaleBitmap = Bitmap.createBitmap((int)(srcImageWith * xScale), (int)(srcImageHeight * yScale), srcImageConfig);

        // Create the scaled canvas.
        Canvas scaleCanvas = new Canvas(scaleBitmap);

        // Create the Matrix object which will scale the source bitmap to target.
        Matrix scaleMatrix = new Matrix();

        // Set x y scale value.
        scaleMatrix.setScale(xScale, yScale);

        // Create a new paint object.
        Paint paint = new Paint();

        // Draw the new scaled bitmap in the canvas.
        scaleCanvas.drawBitmap(srcBitmap, scaleMatrix, paint);

        // Display the new scaled bitmap to source image view object.
        srcImageView.setImageBitmap(scaleBitmap);
    }
}