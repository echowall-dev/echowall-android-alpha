package com.echodev.echoalpha.util;

import android.net.Uri;

import java.util.Date;

/**
 * Created by Ho on 19/2/2017.
 */

public class SpeechBubble {

    public static final int SPEECH_BUBBLE_TYPE_LEFT = 200;
    public static final int SPEECH_BUBBLE_TYPE_RIGHT = 201;

    private String postID, userID, userEmail, bubbleID;
    private int x, y, type;
    private String audioPath;
    private Uri audioUri;
    private Date creationDate;
    private int languageCode;

    // Constructors
    public SpeechBubble(String postID, String userEmail) {
        this.postID = postID;
        this.userEmail = userEmail;
    }

    public SpeechBubble(String postID, String userEmail, int x, int y) {
        this.postID = postID;
        this.userEmail = userEmail;
        this.x = x;
        this.y = y;
    }

    public SpeechBubble(String postID, String userEmail, int x, int y, int type) {
        this.postID = postID;
        this.userEmail = userEmail;
        this.x = x;
        this.y = y;
        this.type = type;
    }

    // Setters
    public SpeechBubble setPostID(String postID) {
        this.postID = postID;
        return this;
    }

    public SpeechBubble setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public SpeechBubble setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public SpeechBubble setX(int x) {
        this.x = x;
        return this;
    }

    public SpeechBubble setY(int y) {
        this.y = y;
        return this;
    }

    public SpeechBubble setType(int type) {
        this.type = type;
        return this;
    }

    public SpeechBubble setAudioPath(String audioPath) {
        this.audioPath = audioPath;
        return this;
    }

    public SpeechBubble setAudioUri(Uri audioUri) {
        this.audioUri = audioUri;
        return this;
    }

    public SpeechBubble setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    // Getters
    public String getPostID() {
        return this.postID;
    }

    public String getUserID() {
        return this.userID;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getType() {
        return this.type;
    }

    public String getAudioPath() {
        return this.audioPath;
    }

    public Uri getAudioUri() {
        return this.audioUri;
    }

    public Date getDate() {
        return this.creationDate;
    }
}
