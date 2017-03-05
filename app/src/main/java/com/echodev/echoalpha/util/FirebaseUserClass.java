package com.echodev.echoalpha.util;

/**
 * Created by Ho on 5/3/2017.
 */

public class FirebaseUserClass {

    // Instance variables
    private String userID, userEmail, userName, proPicUrl, description;
    private long friendNumber, followerNumber;

    // Constructors
    public FirebaseUserClass() {
        // Required by Firebase
    }

    public FirebaseUserClass(String userID) {
        this.userID = userID;
        this.userEmail = "";
        this.userName = "";
        this.proPicUrl = "";
        this.description = "";
        this.friendNumber = 0;
        this.followerNumber = 0;
    }

    public FirebaseUserClass(String userID, String userEmail) {
        this.userID = userID;
        this.userEmail = userEmail;
        this.userName = "";
        this.proPicUrl = "";
        this.description = "";
        this.friendNumber = 0;
        this.followerNumber = 0;
    }

    public FirebaseUserClass(String userID, String userEmail, String userName) {
        this.userID = userID;
        this.userEmail = userEmail;
        this.userName = userName;
        this.proPicUrl = "";
        this.description = "";
        this.friendNumber = 0;
        this.followerNumber = 0;
    }

    // Getters
    public String getUserID() {
        return userID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getProPicUrl() {
        return proPicUrl;
    }

    public String getDescription() {
        return description;
    }

    public long getFriendNumber() {
        return friendNumber;
    }

    public long getFollowerNumber() {
        return followerNumber;
    }

    // Setters
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setProPicUrl(String proPicUrl) {
        this.proPicUrl = proPicUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFriendNumber(long friendNumber) {
        this.friendNumber = friendNumber;
    }

    public void setFollowerNumber(long followerNumber) {
        this.followerNumber = followerNumber;
    }
}
