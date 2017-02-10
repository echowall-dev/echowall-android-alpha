package com.echodev.echoalpha.util;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ho on 6/2/2017.
 */

public class ImageHelper {
    private static final String LOG_TAG = "ImageHelper";

    private String mUserID;
    private String mPostID;

    public ImageHelper(String userID, String postID) {
        this.mUserID = userID;
        this.mPostID = postID;
    }

    public static void setPicFromResources(ImageView imgView, Resources appRes, int imgID, TextView debugText) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(appRes, imgID, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        float photoScale = (float) photoW/photoH;

        // Get the dimensions of the View
        int targetW = imgView.getWidth();
        int targetH = Math.round(targetW/photoScale);

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeResource(appRes, imgID, bmOptions);
        imgView.setImageBitmap(bitmap);

        String debug_msg = "raw photo W: " + photoW + "  H: " + photoH;
        debug_msg += "\n" + "raw photo scale: " + photoScale;
        debug_msg += "\n" + "target photo W: " + targetW + "  H: " + targetH;
        debug_msg += "\n" + "scale factor W: " + photoW/targetW + "  H: " + photoH/targetH;

//        debugText.setText(debug_msg);
    }

    public static void setPicFromFile(ImageView imgView, String imgPath, TextView debugText) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        float photoScale = (float) photoW/photoH;

        // Get the dimensions of the View
        int targetW = imgView.getWidth();
        int targetH = Math.round(targetW/photoScale);

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, bmOptions);
        imgView.setImageBitmap(bitmap);

        String debug_msg = "raw photo W: " + photoW + "  H: " + photoH;
        debug_msg += "\n" + "raw photo scale: " + photoScale;
        debug_msg += "\n" + "target photo W: " + targetW + "  H: " + targetH;
        debug_msg += "\n" + "scale factor W: " + photoW/targetW + "  H: " + photoH/targetH;

        debugText.setText(debug_msg);
    }

    public static File createImageFile(File storageDir) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_pic";
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public static Intent galleryAddPicIntent(String imgPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imgPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);

        return mediaScanIntent;
    }

    public ImageHelper setUserID(String userID) {
        this.mUserID = userID;
        return this;
    }

    public ImageHelper setPostID(String postID) {
        this.mPostID = postID;
        return this;
    }

    public String getUserID() {
        return this.mUserID;
    }

    public String getPostID() {
        return this.mPostID;
    }
}
