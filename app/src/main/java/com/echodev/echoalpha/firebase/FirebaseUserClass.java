package com.echodev.echoalpha.firebase;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

public class FirebaseUserClass implements Parcelable {

    // Instance variables
    private String userID, userEmail, userName, uuid, proPicUrl, description;
    private long friendNumber, followerNumber;

    // Constructors
    public FirebaseUserClass() {
        // Default constructor required by Firebase
    }

    public FirebaseUserClass(String userID) {
        this.userID = userID;
        this.uuid = UUID.randomUUID().toString();
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
        this.uuid = UUID.randomUUID().toString();
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
        this.uuid = UUID.randomUUID().toString();
        this.proPicUrl = "";
        this.description = "";
        this.friendNumber = 0;
        this.followerNumber = 0;
    }

    public FirebaseUserClass(FirebaseUser firebaseUser) {
        this.userID = firebaseUser.getUid();
        this.userEmail = firebaseUser.getEmail();
        this.userName = firebaseUser.getDisplayName();
        if (this.userName == null) {
            this.userName = "Anonymous";
        }

        if (firebaseUser.getPhotoUrl() != null) {
            this.proPicUrl = firebaseUser.getPhotoUrl().toString();
        } else {
            this.proPicUrl = "";
        }

        this.uuid = UUID.randomUUID().toString();
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

    public String getUuid() {
        return uuid;
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

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    // Parcelable implementation
    protected FirebaseUserClass(Parcel in) {
        userID = in.readString();
        uuid = in.readString();
        userEmail = in.readString();
        userName = in.readString();
        proPicUrl = in.readString();
        description = in.readString();
        friendNumber = in.readLong();
        followerNumber = in.readLong();
    }

    public static final Creator<FirebaseUserClass> CREATOR = new Creator<FirebaseUserClass>() {
        @Override
        public FirebaseUserClass createFromParcel(Parcel in) {
            return new FirebaseUserClass(in);
        }

        @Override
        public FirebaseUserClass[] newArray(int size) {
            return new FirebaseUserClass[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(uuid);
        dest.writeString(userEmail);
        dest.writeString(userName);
        dest.writeString(proPicUrl);
        dest.writeString(description);
        dest.writeLong(friendNumber);
        dest.writeLong(followerNumber);
    }
}
