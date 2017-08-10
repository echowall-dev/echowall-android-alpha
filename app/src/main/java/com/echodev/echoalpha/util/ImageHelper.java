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

    public static float imageMaxPixel = 1080;
    public static int imageQuality = 80;

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

    public static String imageCompress(String photoPath, Context context) {
        File originalImage = new File(photoPath);
        String photoName = Uri.parse(photoPath).getLastPathSegment();
        String storagePath = photoPath.replace(photoName, "");
        float maxLength = imageMaxPixel;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoWidth = bmOptions.outWidth;
        int photoHeight = bmOptions.outHeight;
        float photoScale = (float) photoWidth / photoHeight;
        float targetWidth, targetHeight;

        // Calculate the target dimensions
        if (photoWidth > maxLength || photoHeight > maxLength) {
            if (photoWidth > photoHeight) {
                targetWidth = maxLength;
                targetHeight = Math.round(targetWidth / photoScale);
            } else {
                photoScale = 1 / photoScale;
                targetHeight = maxLength;
                targetWidth = Math.round(targetHeight / photoScale);
            }
        } else {
            targetWidth = photoWidth;
            targetHeight = photoHeight;
        }

        File compressedImage = new Compressor.Builder(context)
                .setMaxWidth(targetWidth)
                .setMaxHeight(targetHeight)
                .setQuality(imageQuality)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(storagePath)
                .build()
                .compressToFile(originalImage);

        // Remove the original photo and rename the compressed photo to .jpg
        originalImage.delete();
        String compressedImagePath = compressedImage.getAbsolutePath().replace(".jpeg", ".jpg");
        compressedImage.renameTo(new File(compressedImagePath));

        return compressedImagePath;
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
}
