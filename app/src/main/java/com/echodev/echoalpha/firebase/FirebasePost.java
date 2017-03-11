package com.echodev.echoalpha.firebase;

import com.echodev.echoalpha.util.PostClass;
import com.echodev.echoalpha.util.SpeechBubble;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ho on 5/3/2017.
 */

public class FirebasePost {

    // Instance variables
    private String postID, creatorID, creatorName, creatorEmail, photoUrl, photoName, caption, creationDate;
    private List<String> collaboratorIDList;
    private List<FirebaseBubble> bubbleList;
    private long likeNumber, commentNumber, shareNumber;

    // Constructors
    public FirebasePost() {
        // Required by Firebase
    }

    public FirebasePost(FirebaseUserClass user) {
        this.postID = UUID.randomUUID().toString();
        this.creatorID = user.getUserID();
        this.creatorName = user.getUserName();
        this.creatorEmail = user.getUserEmail();
        this.photoUrl = "";
        this.photoName = "";
        this.caption = "";
        this.creationDate = "";
        this.collaboratorIDList = new ArrayList<String>();
        this.bubbleList = new ArrayList<FirebaseBubble>();
        this.likeNumber = 0;
        this.commentNumber = 0;
        this.shareNumber = 0;
    }

    public FirebasePost(PostClass post) {
        this.postID = post.getPostIDString();
        this.creatorID = post.getUserID();
        this.creatorName = post.getUserName();
        this.creatorEmail = post.getUserEmail();
        this.photoUrl = post.getPhotoUriString();
        this.photoName = post.getPhotoUri().getLastPathSegment().replace("picture/", "");
        this.caption = post.getCaption();
        this.creationDate = post.getCreationDateString();
        this.collaboratorIDList = new ArrayList<String>();
        this.bubbleList = new ArrayList<FirebaseBubble>();
        this.likeNumber = post.getLikeNumber();
        this.commentNumber = post.getLikeNumber();
        this.shareNumber = post.getShareNumber();

        for (SpeechBubble speechBubble : post.getSpeechBubbleList()) {
            bubbleList.add(new FirebaseBubble(speechBubble));
        }
    }

    // Getters
    public String getPostID() {
        return postID;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getPhotoName() {
        return photoName;
    }

    public String getCaption() {
        return caption;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public List<String> getCollaboratorIDList() {
        return collaboratorIDList;
    }

    public List<FirebaseBubble> getBubbleList() {
        return bubbleList;
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

    // Setters
    public void setPostID(String postID) {
        this.postID = postID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setCollaboratorIDList(List<String> collaboratorIDList) {
        this.collaboratorIDList = collaboratorIDList;
    }

    public void setBubbleList(List<FirebaseBubble> bubbleList) {
        this.bubbleList = bubbleList;
    }

    public void setLikeNumber(long likeNumber) {
        this.likeNumber = likeNumber;
    }

    public void setCommentNumber(long commentNumber) {
        this.commentNumber = commentNumber;
    }

    public void setShareNumber(long shareNumber) {
        this.shareNumber = shareNumber;
    }

    // Instance methods - collaboratorIDList
    public void addCollaboratorID(String collaboratorID) {
        collaboratorIDList.add(collaboratorID);
    }

    public void addCollaboratorID(int i, String collaboratorID) {
        collaboratorIDList.add(i, collaboratorID);
    }

    public void removeCollaboratorID(String collaboratorID) {
        collaboratorIDList.remove(collaboratorID);
    }

    public void removeCollaboratorID(int i) {
        collaboratorIDList.remove(i);
    }

    public void setCollaboratorID(int i, String collaboratorID) {
        collaboratorIDList.set(i, collaboratorID);
    }

    public String getCollaboratorID(int i) {
        return collaboratorIDList.get(i);
    }

    // Instance methods - bubbleList
    public void addBubble(FirebaseBubble bubble) {
        bubbleList.add(bubble);
    }

    public void addBubble(int i, FirebaseBubble bubble) {
        bubbleList.add(i, bubble);
    }

    public void removeBubble(FirebaseBubble bubble) {
        bubbleList.remove(bubble);
    }

    public void removeBubble(int i) {
        bubbleList.remove(i);
    }

    public void setBubble(int i, FirebaseBubble bubble) {
        bubbleList.set(i, bubble);
    }

    public FirebaseBubble getBubble(int i) {
        return bubbleList.get(i);
    }
}
