package com.echodev.echoalpha.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import com.echodev.echoalpha.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.zelory.compressor.Compressor;

public class ImageHelper {

    public static int imageMaxPixel = 1080;
    public static int imageQuality = 75;

    public static File createImageFile(File storageDir) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp;

        // File.createTempFile() automatically adds a random number at the end of the file name for making it unique
        // Use new File() instead if don't want that random number or have custom unique naming scheme
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

    public static String imageCompress(String photoPath, Context context) {
        File originalImage = new File(photoPath);
        String photoName = Uri.parse(photoPath).getLastPathSegment();
        String storagePath = photoPath.replace(photoName, "");

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoWidth = bmOptions.outWidth;
        int photoHeight = bmOptions.outHeight;
        float photoScale = (float) photoWidth / photoHeight;
        int targetWidth, targetHeight;

        // Calculate the target dimensions
        if (photoWidth > imageMaxPixel || photoHeight > imageMaxPixel) {
            if (photoWidth > photoHeight) {
                targetWidth = imageMaxPixel;
                targetHeight = Math.round(targetWidth / photoScale);
            } else {
                photoScale = 1 / photoScale;
                targetHeight = imageMaxPixel;
                targetWidth = Math.round(targetHeight / photoScale);
            }
        } else {
            targetWidth = photoWidth;
            targetHeight = photoHeight;
        }

        // The default format and quality of Compressor are JPEG and 80 respectively
        File compressedImage = null;
        try {
            compressedImage = new Compressor(context)
                    .setMaxWidth(targetWidth)
                    .setMaxHeight(targetHeight)
                    .setDestinationDirectoryPath(storagePath)
                    .compressToFile(originalImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return compressedImage.getAbsolutePath();
    }

    public static double getImageAspectRatio(String photoPath) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoWidth = bmOptions.outWidth;
        int photoHeight = bmOptions.outHeight;
        double photoAspectRatio = (double) photoWidth / photoHeight;

        // Round the value up to 2 decimal places
        photoAspectRatio = (double) Math.round(photoAspectRatio * 100) / 100;
        return photoAspectRatio;
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

    public static double convertPxToRatio(int parentPx, int childPx) {
        double rawRatio = (double) childPx / parentPx;
        return (double) Math.round(rawRatio * 100) / 100;
    }

    public static int convertRatioToPx(int parentPx, double childRatio) {
        return (int) Math.round(parentPx * childRatio);
    }
}
