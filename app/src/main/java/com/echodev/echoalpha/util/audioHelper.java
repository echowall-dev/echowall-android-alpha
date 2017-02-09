package com.echodev.echoalpha.util;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.echodev.echoalpha.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ho on 6/2/2017.
 */

public class AudioHelper {
    private static final String LOG_TAG = "AudioHelper";

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer = new MediaPlayer();
    private String mFileName;
    private String mFilePath;
    private boolean createDirSuccess = true;

    private String mUserID;
    private String mPostID;

//    @BindView(R.id.record_btn)
    Button recordBtn;

//    @BindView(R.id.play_btn)
    Button playBtn;

    public AudioHelper(String userID, String postID) {
        this.mUserID = userID;
        this.mPostID = postID;
        this.mFileName = userID + "_" + postID + R.string.audio_format;
    }

    public static boolean createAppDir() {
        boolean createDirSuccess;
        File appDir = new File(Environment.getExternalStorageDirectory() + "/" + R.string.app_name);

        if (!appDir.exists()) {
            createDirSuccess = appDir.mkdir();
        } else {
            createDirSuccess = true;
        }

        return createDirSuccess;
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

//    @OnTouch(R.id.record_btn)
    public boolean controlRecording(View view, MotionEvent motionEvent) {
        return true;
    }

//    @OnClick(R.id.play_btn)
    public void playAudioLocal(View view) {
        if (!createDirSuccess) {
            return;
        }

        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        filePath += "/" + R.string.app_name + "/" + mUserID + R.string.audio_format;

        File appFile = new File(filePath);
        if (appFile.exists()) {
            mPlayer.reset();
            mPlayer = new MediaPlayer();
//            mp.release();
            try {
                mPlayer.setDataSource(filePath);
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.start();
        }
    }
}
