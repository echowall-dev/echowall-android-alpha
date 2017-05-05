package com.echodev.echoalpha.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.echodev.echoalpha.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ho on 6/2/2017.
 */

public class ImageHelper {

    public static File createImageFile(File storageDir) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp;
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    public static String createImageFile(Resources resources, String userID) {
        String appName = resources.getString(R.string.app_name);
        String imageFormat = resources.getString(R.string.image_format);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        imagePath += "/" + appName + "/picture/" + userID + "_" + timeStamp + imageFormat;

        return imagePath;
    }

    public static Bitmap imageCompress(String photoPath) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        float photoScale;

        int targetW;
        int targetH;

        // Calculate the target dimensions
        if (photoW > photoH) {
            photoScale = (float) photoW / photoH;
            targetW = 1080;
            targetH = Math.round(targetW / photoScale);
        } else {
            photoScale = (float) photoH / photoW;
            targetH = 1080;
            targetW = Math.round(targetH / photoScale);
        }

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);

        return bitmap;
    }

    /**
     * Covert dp to px
     * @param dp
     * @param context
     * @return pixel
     */
    public static int convertDpToPx(int dp, Context context) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }

    /**
     * Covert px to dp
     * @param px
     * @param context
     * @return dp
     */
    public static int convertPxToDp(int px, Context context) {
        return Math.round(px / context.getResources().getDisplayMetrics().density);
    }
}
