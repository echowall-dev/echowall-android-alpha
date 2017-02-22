package com.echodev.echoalpha.util;

import java.util.Date;

/**
 * Created by Ho on 19/2/2017.
 */

public class BubbleHelper {
    private String postID, userID, userEmail;
    private Date creationDate;
    private int languageCode;
    private int viewX, viewY;

    public BubbleHelper(String postID, String userEmail) {
        this.postID = postID;
        this.userEmail = userEmail;
    }

    public BubbleHelper setPostID(String postID) {
        this.postID = postID;
        return this;
    }

    public BubbleHelper setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public BubbleHelper setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public BubbleHelper setDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public BubbleHelper setLanguage(int languageCode) {
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

    public Date getDate() {
        return this.creationDate;
    }

    public int getLanguageCode() {
        return this.languageCode;
    }
}
