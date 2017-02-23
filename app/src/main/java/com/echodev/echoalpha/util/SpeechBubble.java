package com.echodev.echoalpha.util;

import android.content.res.Resources;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Date;

/**
 * Created by Ho on 19/2/2017.
 */

public class SpeechBubble {

    public static final int SPEECH_BUBBLE_LEFT = 200;
    public static final int SPEECH_BUBBLE_RIGHT = 201;

    private String postID, userID, userEmail;
    private int x, y, orientation;
    private ImageView bubbleImageView;
    private Date creationDate;
    private int languageCode;

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

    public SpeechBubble setOrientation(int orientation) {
        this.orientation = orientation;
        return this;
    }

    public SpeechBubble setDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public SpeechBubble setLanguage(int languageCode) {
        this.languageCode = languageCode;
        return this;
    }

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

    public int getOrientation() {
        return this.orientation;
    }

    public Date getDate() {
        return this.creationDate;
    }

    public int getLanguageCode() {
        return this.languageCode;
    }

    public boolean addBubble(Resources resources, ViewGroup placeArea) {
        return true;
    }
}
