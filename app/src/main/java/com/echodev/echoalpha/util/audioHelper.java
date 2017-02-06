package com.echodev.echoalpha.util;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

import com.echodev.echoalpha.R;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

/**
 * Created by Ho on 6/2/2017.
 */

public class audioHelper {
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer = new MediaPlayer();
    private String mFileName;
    private String mFilePath;
    private boolean createDirSuccess = true;

    private FirebaseUser mUser;
    private String mPostID;

    public audioHelper(FirebaseUser currentUser, String postID) {
        this.mUser = currentUser;
        this.mPostID = postID;
        this.mFileName = currentUser.getUid() + "_" + postID + R.string.audio_format;
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

    private audioHelper setUser(FirebaseUser currentUser) {
        this.mUser = currentUser;
        return this;
    }

    private audioHelper setPostID(String postID) {
        this.mPostID = postID;
        return this;
    }

    private FirebaseUser getUser() {
        return this.mUser;
    }

    private String getPostID() {
        return this.mPostID;
    }
}
