package com.echodev.echoalpha.util;

import java.util.List;
import java.util.UUID;

/**
 * Created by Ho on 5/3/2017.
 */

public class FirebasePost {

    private String postID, creatorID, photoUrl, postCaption, creationDate;
    private List<String> collaboratorIDList, speechBubbleIDList;
    private long likeNumber, commentNumber, shareNumber;

    public FirebasePost() {
    }

    public FirebasePost(String creatorID) {
        this.creatorID = creatorID;
        this.postID = UUID.randomUUID().toString();
        this.likeNumber = 0;
        this.commentNumber = 0;
        this.shareNumber = 0;
    }

    public String getPostID() {
        return postID;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getPostCaption() {
        return postCaption;
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

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setPostCaption(String postCaption) {
        this.postCaption = postCaption;
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
}
