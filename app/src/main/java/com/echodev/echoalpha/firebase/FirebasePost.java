package com.echodev.echoalpha.firebase;

import com.echodev.echoalpha.util.PostClass;
import com.echodev.echoalpha.util.SpeechBubble;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ho on 5/3/2017.
 */

public class FirebasePost {

    // Instance variables
    private String postID, creatorID, photoUrl, photoName, caption, creationDate;
    private List<String> collaboratorIDList, speechBubbleIDList;
    private long likeNumber, commentNumber, shareNumber;

    // Constructors
    public FirebasePost() {
        // Required by Firebase
    }

    public FirebasePost(String creatorID) {
        this.postID = UUID.randomUUID().toString();
        this.creatorID = creatorID;
        this.photoUrl = "";
        this.photoName = "";
        this.caption = "";
        this.creationDate = "";
        this.collaboratorIDList = new ArrayList<String>();
        this.speechBubbleIDList = new ArrayList<String>();
        this.likeNumber = 0;
        this.commentNumber = 0;
        this.shareNumber = 0;
    }

    public FirebasePost(PostClass post) {
        this.postID = post.getPostIDString();
        this.creatorID = post.getUserID();
        this.photoUrl = post.getPhotoUriString();
        this.photoName = post.getPhotoUri().getLastPathSegment().replace("picture/", "");
        this.caption = post.getCaption();
        this.creationDate = post.getCreationDateString();
        this.collaboratorIDList = new ArrayList<String>();
        this.speechBubbleIDList = new ArrayList<String>();
        this.likeNumber = post.getLikeNumber();
        this.commentNumber = post.getLikeNumber();
        this.shareNumber = post.getShareNumber();

        for (SpeechBubble speechBubble : post.getSpeechBubbleList()) {
            speechBubbleIDList.add(speechBubble.getBubbleIDString());
        }
    }

    // Getters
    public String getPostID() {
        return postID;
    }

    public String getCreatorID() {
        return creatorID;
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

    public List<String> getSpeechBubbleIDList() {
        return speechBubbleIDList;
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

    public void setSpeechBubbleIDList(List<String> speechBubbleIDList) {
        this.speechBubbleIDList = speechBubbleIDList;
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

    // Instance methods - speechBubbleIDList
    public void addSpeechBubbleID(String collaboratorID) {
        speechBubbleIDList.add(collaboratorID);
    }

    public void addSpeechBubbleID(int i, String collaboratorID) {
        speechBubbleIDList.add(i, collaboratorID);
    }

    public void removeSpeechBubbleID(String collaboratorID) {
        speechBubbleIDList.remove(collaboratorID);
    }

    public void removeSpeechBubbleID(int i) {
        speechBubbleIDList.remove(i);
    }

    public void setSpeechBubbleID(int i, String collaboratorID) {
        speechBubbleIDList.set(i, collaboratorID);
    }

    public String getSpeechBubbleID(int i) {
        return speechBubbleIDList.get(i);
    }
}
