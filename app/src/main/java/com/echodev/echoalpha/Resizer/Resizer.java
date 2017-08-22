package com.echodev.echoalpha.Resizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;

/**
 * Created by K.K. Ho on 22/8/2017.
 */

public class Resizer {
    private int targetLength, quality;
    private Bitmap.CompressFormat compressFormat;
    private String destinationDirectoryPath;

    public Resizer(Context context) {
        targetLength = 1080;
        quality = 80;
        compressFormat = Bitmap.CompressFormat.JPEG;
        destinationDirectoryPath = context.getCacheDir().getPath() + File.separator + "images";
    }

    public Resizer setTargetLength(int targetLength) {
        this.targetLength = targetLength;
        return this;
    }

    public Resizer setCompressFormat(Bitmap.CompressFormat compressFormat) {
        this.compressFormat = compressFormat;
        return this;
    }

    public Resizer setQuality(int quality) {
        this.quality = quality;
        return this;
    }

    public Resizer setDestinationDirectoryPath(String destinationDirectoryPath) {
        this.destinationDirectoryPath = destinationDirectoryPath;
        return this;
    }

    public File resizeToFile(File imageFile) throws IOException {
        File file = new File(destinationDirectoryPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        String destinationFilePath = destinationDirectoryPath + File.separator + imageFile.getName();

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(destinationFilePath);
            resizeToBitmap(imageFile).compress(compressFormat, quality, fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        return new File(destinationFilePath);
    }

    public Bitmap resizeToBitmap(File imageFile) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        // Get the dimensions of the original bitmap
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;
        float aspectRatio = (float) originalWidth / originalHeight;

        // Calculate the target dimensions
        int targetWidth = originalWidth;
        int targetHeight = originalHeight;

        if (originalWidth > targetLength || originalHeight > targetLength) {
            if (originalWidth > originalHeight) {
                targetWidth = targetLength;
                targetHeight = Math.round(targetWidth / aspectRatio);
            } else {
                aspectRatio = 1 / aspectRatio;
                targetHeight = targetLength;
                targetWidth = Math.round(targetHeight / aspectRatio);
            }
        }

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
    }

    public Flowable<File> resizeToFileAsFlowable(final File imageFile) {
        return Flowable.defer(new Callable<Flowable<File>>() {
            @Override
            public Flowable<File> call() {
                try {
                    return Flowable.just(resizeToFile(imageFile));
                } catch (IOException e) {
                    return Flowable.error(e);
                }
            }
        });
    }

    public Flowable<Bitmap> resizeToBitmapAsFlowable(final File imageFile) {
        return Flowable.defer(new Callable<Flowable<Bitmap>>() {
            @Override
            public Flowable<Bitmap> call() {
                try {
                    return Flowable.just(resizeToBitmap(imageFile));
                } catch (IOException e) {
                    return Flowable.error(e);
                }
            }
        });
    }
}
