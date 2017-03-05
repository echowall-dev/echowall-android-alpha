package com.echodev.echoalpha.util;

/**
 * Created by Ho on 5/3/2017.
 */

public class FirebaseUser {

    private String userID, userEmail, userName;

    public FirebaseUser() {
        // Required by Firebase
    }

    public FirebaseUser(String userID, String userEmail) {
        this.userID = userID;
        this.userEmail = userEmail;
    }

    public FirebaseUser(String userID, String userEmail, String userName) {
        this.userID = userID;
        this.userEmail = userEmail;
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
