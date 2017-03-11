package com.echodev.echoalpha.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

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

    public static Intent galleryAddPicIntent(String imgPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File photoFile = new File(imgPath);
        Uri contentUri = Uri.fromFile(photoFile);
        mediaScanIntent.setData(contentUri);

        return mediaScanIntent;
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
