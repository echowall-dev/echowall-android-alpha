package com.echodev.echoalpha.util;

import java.util.UUID;

/**
 * Created by Ho on 7/2/2017.
 */

public class PostHelper {

    // Finite-state machine
    public static final int STATE_PHOTO_PREPARE = 0;
    public static final int STATE_AUDIO_PREPARE = 1;
    public static final int STATE_BUBBLE_PREPARE = 2;
    public static final int STATE_POST_READY = 3;

    private static final String LOG_TAG = "PostHelper";

    private final UUID postID = UUID.randomUUID();
    private AudioHelper mAudio;
    private ImageHelper mImage;

    public UUID getPostID() {
        return this.postID;
    }

    public String getPostIDString() {
        return this.postID.toString();
    }

    public boolean createPost() {
        return false;
    }

    public boolean editPost() {
        return false;
    }

    public boolean deletePost() {
        return false;
    }
}
