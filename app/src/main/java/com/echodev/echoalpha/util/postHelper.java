package com.echodev.echoalpha.util;

import java.util.UUID;

/**
 * Created by Ho on 7/2/2017.
 */

public class postHelper {
    private final UUID postID = UUID.randomUUID();
    private audioHelper mAudio;
    private imageHelper mImage;

    public UUID getPostID() {
        return this.postID;
    }

    public String getPostIDString() {
        return this.postID.toString();
    }
}
