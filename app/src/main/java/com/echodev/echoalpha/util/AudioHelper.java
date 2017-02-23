package com.echodev.echoalpha.util;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import com.echodev.echoalpha.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ho on 6/2/2017.
 */

public class AudioHelper {

    private static final String LOG_TAG = "AudioHelper";

    private static MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private String mFileName;

    private String mPostID, mUserID, mUserEmail;

    public AudioHelper(String userID, String postID) {
        this.mUserID = userID;
        this.mPostID = postID;
        this.mFileName = userID + "_" + postID + R.string.audio_format;
    }

    public static void startRecording(String audioName) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(audioName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
    }

    public static void stopRecording(String audioName) {
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
            mPlayer.release();
        }
    }

    public AudioHelper setUserID(String userID) {
        this.mUserID = userID;
        return this;
    }

    public AudioHelper setPostID(String postID) {
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
