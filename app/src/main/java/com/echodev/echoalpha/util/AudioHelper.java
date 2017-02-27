package com.echodev.echoalpha.util;

import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

import com.echodev.echoalpha.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ho on 6/2/2017.
 */

public class AudioHelper {

    private static MediaRecorder mRecorder;

    public static void startRecording(String audioPath) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(audioPath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
    }

    public static void stopRecording(String audioPath) {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public static void playAudioLocal(String audioPath) {
        MediaPlayer mPlayer = new MediaPlayer();
        File appFile = new File(audioPath);
        if (appFile.exists()) {
            try {
                mPlayer.setDataSource(audioPath);
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.start();
//            mPlayer.release();
        }
    }

    public static String createAudioFile(Resources resources, String userID) {
        String appName = resources.getString(R.string.app_name);
        String audioFormat = resources.getString(R.string.audio_format);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        audioFilePath += "/" + appName + "/audio/" + userID + "_" + timeStamp + audioFormat;

        return audioFilePath;
    }
}
