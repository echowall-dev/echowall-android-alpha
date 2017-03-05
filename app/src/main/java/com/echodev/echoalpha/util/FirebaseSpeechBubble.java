package com.echodev.echoalpha.util;

import java.util.UUID;

/**
 * Created by Ho on 5/3/2017.
 */

public class FirebaseSpeechBubble {

    private String bubbleID, postID, creatorID, audioUrl, creationDate;
    private long x, y, type, playNumber;

    public FirebaseSpeechBubble() {
    }

    public FirebaseSpeechBubble(String postID, String creatorID) {
        this.postID = postID;
        this.creatorID = creatorID;
        this.bubbleID = UUID.randomUUID().toString();
        this.playNumber = 0;
    }

    public String getBubbleID() {
        return bubbleID;
    }

    public String getPostID() {
        return postID;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public long getType() {
        return type;
    }

    public long getPlayNumber() {
        return playNumber;
    }

    public void setBubbleID(String bubbleID) {
        this.bubbleID = bubbleID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setX(long x) {
        this.x = x;
    }

    public void setY(long y) {
        this.y = y;
    }

    public void setType(long type) {
        this.type = type;
    }

    public void setPlayNumber(long playNumber) {
        this.playNumber = playNumber;
    }
}
