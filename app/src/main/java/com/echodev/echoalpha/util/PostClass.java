package com.echodev.echoalpha.util;

import android.net.Uri;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
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
    private String postIDString, userID, userEmail, userName, photoPath, caption, platform;
    private Uri photoUri;
    private ArrayList<SpeechBubble> speechBubbleList;
    private Date creationDate;
    private long likeNumber, commentNumber, shareNumber;
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
        this.shareNumber = 0;
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

    public String getUserName() {
        return userName;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public String getCaption() {
        return caption;
    }

    public String getPlatform() {
        return platform;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public String getPhotoUriString() {
        return photoUri.toString();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getCreationDateString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(creationDate);
    }

    public long getLikeNumber() {
        return likeNumber;
    }

    public long getCommentNumber() {
        return commentNumber;
    }

    public long getShareNumber() {
        return shareNumber;
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

    public PostClass setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public PostClass setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
        return this;
    }

    public PostClass setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public PostClass setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
        return this;
    }

    public PostClass setPhotoUri(String photoUrlString) {
        this.photoUri = Uri.parse(photoUrlString);
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

    public PostClass setShareNumber(long shareNumber) {
        this.shareNumber = shareNumber;
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

    public void addSpeechBubble(int i, SpeechBubble speechBubble) {
        speechBubbleList.add(i, speechBubble);
    }

    public void removeSpeechBubble(SpeechBubble speechBubble) {
        speechBubbleList.remove(speechBubble);
    }

    public void removeSpeechBubble(int i) {
        speechBubbleList.remove(i);
    }

    public void setSpeechBubble(int i, SpeechBubble speechBubble) {
        speechBubbleList.set(i, speechBubble);
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
        userName = in.readString();
        photoPath = in.readString();
        caption = in.readString();
        platform = in.readString();
        photoUri = in.readParcelable(Uri.class.getClassLoader());
        speechBubbleList = in.createTypedArrayList(SpeechBubble.CREATOR);
        likeNumber = in.readLong();
        commentNumber = in.readLong();
        shareNumber = in.readLong();
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
        dest.writeString(userName);
        dest.writeString(photoPath);
        dest.writeString(caption);
        dest.writeString(platform);
        dest.writeParcelable(photoUri, flags);
        dest.writeTypedList(speechBubbleList);
        dest.writeLong(likeNumber);
        dest.writeLong(commentNumber);
        dest.writeLong(shareNumber);
        dest.writeInt(currentPostState);
        dest.writeByte((byte) (postReady ? 1 : 0));
        dest.writeInt(width);
        dest.writeInt(height);

        // Non-primitive data types handling for Parcelable
        dest.writeLong(creationDate.getTime());
    }
}
