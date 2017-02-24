package com.echodev.echoalpha.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Ho on 7/2/2017.
 */

public class PostClass implements Parcelable {

    // Post states for finite-state machine
    public static final int STATE_PHOTO_PREPARE = 0;
    public static final int STATE_AUDIO_PREPARE = 1;
    public static final int STATE_BUBBLE_PREPARE = 2;
    public static final int STATE_POST_READY = 3;

    // Class variables
    public static final String LOG_TAG = "PostClass";

    private UUID postID = UUID.randomUUID();
    private String postIDString = postID.toString();

    // Instance variables
    private int currentPostState;
    private String userID, userEmail, photoPath;
    private ArrayList<SpeechBubble> speechBubbleList;
    private Date creationDate;

    // Constructors
    public PostClass() {
        this.currentPostState = STATE_PHOTO_PREPARE;
        this.speechBubbleList = new ArrayList<SpeechBubble>();
    }

    // Getters
    public UUID getPostID() {
        return this.postID;
    }

    public String getPostIDString() {
        return this.postID.toString();
    }

    public String getUserID() {
        return userID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public ArrayList<SpeechBubble> getSpeechBubbleList() {
        return speechBubbleList;
    }

    public int getCurrentPostState() {
        return currentPostState;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    // Setters
    public PostClass setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public PostClass setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public PostClass setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
        return this;
    }

    public PostClass setSpeechBubbleList(ArrayList<SpeechBubble> speechBubbleList) {
        this.speechBubbleList = speechBubbleList;
        return this;
    }

    public PostClass setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public void setCurrentPostState(int currentPostState) {
        this.currentPostState = currentPostState;
    }

    // Class methods
    public static PostClass createPost() {
        return new PostClass();
    }

    // Instance methods
    public boolean editPost() {
        return false;
    }

    public boolean deletePost() {
        return false;
    }

    public void addSpeechBubble(SpeechBubble speechBubble) {
        this.speechBubbleList.add(speechBubble);
    }

    public void removeSpeechBubble(SpeechBubble speechBubble) {
        this.speechBubbleList.remove(speechBubble);
    }

    public SpeechBubble getSpeechBubble(int i) {
        return this.speechBubbleList.get(i);
    }

    public boolean matchCurrentPostState(int currentPostState) {
        return this.currentPostState == currentPostState;
    }

    // Parcelable implementation
    protected PostClass(Parcel in) {
        postIDString = in.readString();
        currentPostState = in.readInt();
        userID = in.readString();
        userEmail = in.readString();
        photoPath = in.readString();
        speechBubbleList = in.createTypedArrayList(SpeechBubble.CREATOR);
    }

    public static final Creator<PostClass> CREATOR = new Creator<PostClass>() {
        @Override
        public PostClass createFromParcel(Parcel in) {
            return new PostClass(in);
        }

        @Override
        public PostClass[] newArray(int size) {
            return new PostClass[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postIDString);
        dest.writeInt(currentPostState);
        dest.writeString(userID);
        dest.writeString(userEmail);
        dest.writeString(photoPath);
        dest.writeTypedList(speechBubbleList);
    }
}
