package com.echodev.echoalpha.firebase;

import com.echodev.echoalpha.util.SpeechBubble;

import java.util.UUID;

/**
 * Created by Ho on 5/3/2017.
 */

public class FirebaseSpeechBubble {

    // Instance variables
    private String bubbleID, postID, creatorID, audioUrl, audioName, type, creationDate;
    private long x, y, playNumber;

    // Constructors
    public FirebaseSpeechBubble() {
        // Required by Firebase
    }

    public FirebaseSpeechBubble(String postID, String creatorID) {
        this.bubbleID = UUID.randomUUID().toString();
        this.postID = postID;
        this.creatorID = creatorID;
        this.audioUrl = "";
        this.audioName = "";
        this.type = "";
        this.creationDate = "";
        this.x = 0;
        this.y = 0;
        this.playNumber = 0;
    }

    public FirebaseSpeechBubble(SpeechBubble speechBubble) {
        this.bubbleID = speechBubble.getBubbleIDString();
        this.postID = speechBubble.getPostID();
        this.creatorID = speechBubble.getUserID();
        this.audioUrl = speechBubble.getAudioUriString();
        this.audioName = speechBubble.getAudioUri().getLastPathSegment().replace("audio/", "");
        this.type = (speechBubble.getType() == SpeechBubble.SPEECH_BUBBLE_TYPE_LEFT) ? "L" : "R";
        this.creationDate = speechBubble.getCreationDateString();
        this.x = (long) speechBubble.getX();
        this.y = (long) speechBubble.getY();
        this.playNumber = speechBubble.getPlayNumber();
    }

    // Getters
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

    public String getAudioName() {
        return audioName;
    }

    public String getType() {
        return type;
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

    public long getPlayNumber() {
        return playNumber;
    }

    // Setters
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

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public void setType(String type) {
        this.type = type;
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

    public void setPlayNumber(long playNumber) {
        this.playNumber = playNumber;
    }
}
