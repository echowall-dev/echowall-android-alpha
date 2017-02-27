package com.echodev.echoalpha.util;

import android.os.Parcel;
import android.os.ParcelUuid;
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

    // Instance variables
    private UUID postID;
    private ParcelUuid postIDParcel;
    private String postIDString, userID, userEmail, photoPath, platform;
    private ArrayList<SpeechBubble> speechBubbleList;
    private Date creationDate;
    private long likeNumber, commentNumber;
    private int currentPostState;
    private boolean postReady;

    // Post width and height, set to match the layout
    private int width, height;

    // Constructors
    public PostClass() {
        this.postID = UUID.randomUUID();
        this.postIDParcel = new ParcelUuid(postID);
        this.postIDString = postID.toString();
        this.platform = "Android";
        this.likeNumber = 0;
        this.commentNumber = 0;
        this.speechBubbleList = new ArrayList<SpeechBubble>();
        this.currentPostState = STATE_PHOTO_PREPARE;
        this.postReady = false;
    }

    // Getters
    public UUID getPostID() {
        return postIDParcel.getUuid();
    }

    public ParcelUuid getPostIDParcel() {
        return postIDParcel;
    }

    public String getPostIDString() {
        return postIDString;
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

    public String getPlatform() {
        return platform;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public long getLikeNumber() {
        return likeNumber;
    }

    public long getCommentNumber() {
        return commentNumber;
    }

    public ArrayList<SpeechBubble> getSpeechBubbleList() {
        return speechBubbleList;
    }

    public int getCurrentPostState() {
        return currentPostState;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

    public PostClass setLikeNumber(long likeNumber) {
        this.likeNumber = likeNumber;
        return this;
    }

    public PostClass setCommentNumber(long commentNumber) {
        this.commentNumber = commentNumber;
        return this;
    }

    public PostClass setCurrentPostState(int currentPostState) {
        this.currentPostState = currentPostState;
        return this;
    }

    public PostClass setPostReady(boolean postReady) {
        this.postReady = postReady;
        return this;
    }

    public PostClass setWidth(int width) {
        this.width = width;
        return this;
    }

    public PostClass setHeight(int height) {
        this.height = height;
        return this;
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
        speechBubbleList.add(speechBubble);
    }

    public void removeSpeechBubble(SpeechBubble speechBubble) {
        speechBubbleList.remove(speechBubble);
    }

    public SpeechBubble getSpeechBubble(int i) {
        return speechBubbleList.get(i);
    }

    public boolean matchCurrentPostState(int currentPostState) {
        return this.currentPostState == currentPostState;
    }

    public boolean isPostReady() {
        return postReady;
    }

    // Parcelable implementation
    protected PostClass(Parcel in) {
        postIDParcel = in.readParcelable(ParcelUuid.class.getClassLoader());
        postIDString = in.readString();
        userID = in.readString();
        userEmail = in.readString();
        photoPath = in.readString();
        platform = in.readString();
        speechBubbleList = in.createTypedArrayList(SpeechBubble.CREATOR);
        likeNumber = in.readLong();
        commentNumber = in.readLong();
        currentPostState = in.readInt();
        postReady = in.readByte() != 0;
        width = in.readInt();
        height = in.readInt();

        // Non-primitive data types handling for Parcelable
        creationDate = new Date(in.readLong());
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
        dest.writeParcelable(postIDParcel, flags);
        dest.writeString(postIDString);
        dest.writeString(userID);
        dest.writeString(userEmail);
        dest.writeString(photoPath);
        dest.writeString(platform);
        dest.writeTypedList(speechBubbleList);
        dest.writeLong(likeNumber);
        dest.writeLong(commentNumber);
        dest.writeInt(currentPostState);
        dest.writeByte((byte) (postReady ? 1 : 0));
        dest.writeInt(width);
        dest.writeInt(height);

        // Non-primitive data types handling for Parcelable
        dest.writeLong(creationDate.getTime());
    }
}
