package com.echodev.echoalpha.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;

//import com.echowall.resizer.Resizer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.echodev.resizer.Resizer;

public class ImageHelper {

    public static int imageMaxPixel = 1080;
    public static int imageQuality = 80;

    public static File createImageFile(String userUuid) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = userUuid+ "_" + timeStamp + ".jpg";
        String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Echowall";
        imagePath += File.separator + Environment.DIRECTORY_PICTURES + File.separator + imageName;

        return new File(imagePath);
    }

    public static File imageResize(File imageFile, Context context) throws IOException {
//        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Echowall";
//        storagePath += File.separator + Environment.DIRECTORY_PICTURES;

        String storagePath = context.getExternalCacheDir().getAbsolutePath() + File.separator + Environment.DIRECTORY_PICTURES;

        File resizedImage = new Resizer(context)
                .setDestinationDirPath(storagePath)
                .setSourceImage(imageFile)
                .getResizedFile();

        return resizedImage;
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
