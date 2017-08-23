package com.echodev.echoalpha.firebase;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

public class FirebaseBubble implements Parcelable {

    // Instance variables
    private String bubbleID, postID, creatorID, audioUrl, audioName, type, creationDate, platform;
    private int x, y;
    private double xRatio, yRatio;
    private long playNumber;

    // Constructors
    public FirebaseBubble() {
        // Default constructor required by Firebase
    }

    public FirebaseBubble(String postID, String creatorID) {
        this.bubbleID = UUID.randomUUID().toString();
        this.postID = postID;
        this.creatorID = creatorID;
        this.audioUrl = "";
        this.audioName = "";
        this.type = "";
        this.creationDate = "";
        this.platform = "Android";
        this.x = 0;
        this.y = 0;
        this.playNumber = 0;
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

    public String getPlatform() {
        return platform;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getXRatio() {
        return xRatio;
    }

    public double getYRatio() {
        return yRatio;
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

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setXRatio(double xRatio) {
        this.xRatio = xRatio;
    }

    public void setYRatio(double yRatio) {
        this.yRatio = yRatio;
    }

    public void setPlayNumber(long playNumber) {
        this.playNumber = playNumber;
    }

    // Parcelable implementation
    protected FirebaseBubble(Parcel in) {
        bubbleID = in.readString();
        postID = in.readString();
        creatorID = in.readString();
        audioUrl = in.readString();
        audioName = in.readString();
        type = in.readString();
        creationDate = in.readString();
        platform = in.readString();
        x = in.readInt();
        y = in.readInt();
        xRatio = in.readDouble();
        yRatio = in.readDouble();
        playNumber = in.readLong();
    }

    public static final Creator<FirebaseBubble> CREATOR = new Creator<FirebaseBubble>() {
        @Override
        public FirebaseBubble createFromParcel(Parcel in) {
            return new FirebaseBubble(in);
        }

        @Override
        public FirebaseBubble[] newArray(int size) {
            return new FirebaseBubble[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bubbleID);
        dest.writeString(postID);
        dest.writeString(creatorID);
        dest.writeString(audioUrl);
        dest.writeString(audioName);
        dest.writeString(type);
        dest.writeString(creationDate);
        dest.writeString(platform);
        dest.writeInt(x);
        dest.writeInt(y);
        dest.writeDouble(xRatio);
        dest.writeDouble(yRatio);
        dest.writeLong(playNumber);
    }
}
