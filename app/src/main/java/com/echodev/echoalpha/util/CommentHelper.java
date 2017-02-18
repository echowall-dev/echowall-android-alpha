package com.echodev.echoalpha.util;

import java.util.Date;

/**
 * Created by Ho on 19/2/2017.
 */

public class CommentHelper {
    private String postID, userID, userEmail;
    private Date creationDate;
    private int languageCode;

    public CommentHelper(String postID, String userEmail) {
        this.postID = postID;
        this.userEmail = userEmail;
    }

    public CommentHelper setPostID(String postID) {
        this.postID = postID;
        return this;
    }

    public CommentHelper setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public CommentHelper setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public CommentHelper setDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public CommentHelper setLanguage(int languageCode) {
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
