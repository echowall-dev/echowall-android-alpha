package com.echodev.echoalpha.util;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.echodev.echoalpha.R;

import java.util.Date;

/**
 * Created by Ho on 19/2/2017.
 */

public class SpeechBubble implements Parcelable, View.OnClickListener, View.OnTouchListener {

    // Class variables
    public static final int SPEECH_BUBBLE_TYPE_LEFT = 200;
    public static final int SPEECH_BUBBLE_TYPE_RIGHT = 201;

    // Instance variables
    private String postID, userID, userEmail, bubbleID;
    private int x, y, type;
    private String audioPath;
    private Uri audioUri;
    private Date creationDate;
    private boolean bubbleReady;
    private int languageCode;

    // Instance variables for adjusting the bubble
    private int dX, dY, targetX, targetY;
    private ImageView bubbleImageView;

    // Constructors
    public SpeechBubble(String postID, String userEmail) {
        this.postID = postID;
        this.userEmail = userEmail;
        bubbleReady = false;
    }

    public SpeechBubble(String postID, String userEmail, int x, int y) {
        this.postID = postID;
        this.userEmail = userEmail;
        this.x = x;
        this.y = y;
        this.bubbleReady = false;
    }

    public SpeechBubble(String postID, String userEmail, int x, int y, int type) {
        this.postID = postID;
        this.userEmail = userEmail;
        this.x = x;
        this.y = y;
        this.type = type;
        this.bubbleReady = false;
    }

    // Getters
    public String getPostID() {
        return this.postID;
    }

    public String getUserID() {
        return this.userID;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getType() {
        return this.type;
    }

    public String getAudioPath() {
        return this.audioPath;
    }

    public Uri getAudioUri() {
        return this.audioUri;
    }

    public Date getDate() {
        return this.creationDate;
    }

    // Setters
    public SpeechBubble setPostID(String postID) {
        this.postID = postID;
        return this;
    }

    public SpeechBubble setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public SpeechBubble setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public SpeechBubble setX(int x) {
        this.x = x;
        return this;
    }

    public SpeechBubble setY(int y) {
        this.y = y;
        return this;
    }

    public SpeechBubble setType(int type) {
        this.type = type;
        return this;
    }

    public SpeechBubble setAudioPath(String audioPath) {
        this.audioPath = audioPath;
        return this;
    }

    public SpeechBubble setAudioUri(Uri audioUri) {
        this.audioUri = audioUri;
        return this;
    }

    public SpeechBubble setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public SpeechBubble setBubbleReady(boolean bubbleReady) {
        this.bubbleReady = bubbleReady;
        return this;
    }

    // Instance methods
    public boolean isBubbleReady() {
        return this.bubbleReady;
    }

    public void addBubbleImage(int positionX, int positionY, ViewGroup viewGroup, Resources resources, Context context) {
        // Prepare the dimensions of the ImageView
        int targetW = resources.getDimensionPixelSize(R.dimen.bubble_width);
        int targetH = resources.getDimensionPixelSize(R.dimen.bubble_height);

        // Create a new ImageView to the the ViewGroup
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(targetW, targetH);
        layoutParams.leftMargin = positionX;
        layoutParams.topMargin = positionY;

        bubbleImageView = new ImageView(context);
        bubbleImageView.setLayoutParams(layoutParams);
        viewGroup.addView(bubbleImageView);

        // Fill the ImageView with the corresponding speech bubble image
        switch (this.type) {
            case SPEECH_BUBBLE_TYPE_LEFT:
                ImageHelper.setPicFromResources(bubbleImageView, targetW, targetH, resources, R.drawable.speech_bubble_l);
                break;
            case SPEECH_BUBBLE_TYPE_RIGHT:
                ImageHelper.setPicFromResources(bubbleImageView, targetW, targetH, resources, R.drawable.speech_bubble_r);
                break;
            default:
                break;
        }

        this.x = positionX;
        this.y = positionY;
    }

    // Listener binding methods
    public void bindPlayListener() {
        this.bubbleImageView.setOnClickListener(this);
    }

    public void setPlayListener(View.OnClickListener playListener) {
        this.bubbleImageView.setOnClickListener(playListener);
    }

    public void bindAdjustListener() {
        this.bubbleImageView.setOnTouchListener(this);
    }

    public void setAdjustListener(View.OnTouchListener adjustListener) {
        this.bubbleImageView.setOnTouchListener(adjustListener);
    }

    // Parcelable implementation
    protected SpeechBubble(Parcel in) {
        postID = in.readString();
        userID = in.readString();
        userEmail = in.readString();
        bubbleID = in.readString();
        x = in.readInt();
        y = in.readInt();
        type = in.readInt();
        audioPath = in.readString();
        audioUri = in.readParcelable(Uri.class.getClassLoader());
        bubbleReady = in.readByte() != 0;
        languageCode = in.readInt();
    }

    public static final Creator<SpeechBubble> CREATOR = new Creator<SpeechBubble>() {
        @Override
        public SpeechBubble createFromParcel(Parcel in) {
            return new SpeechBubble(in);
        }

        @Override
        public SpeechBubble[] newArray(int size) {
            return new SpeechBubble[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postID);
        dest.writeString(userID);
        dest.writeString(userEmail);
        dest.writeString(bubbleID);
        dest.writeInt(x);
        dest.writeInt(y);
        dest.writeInt(type);
        dest.writeString(audioPath);
        dest.writeParcelable(audioUri, flags);
        dest.writeByte((byte) (bubbleReady ? 1 : 0));
        dest.writeInt(languageCode);
    }

    // OnClickListener implementation
    @Override
    public void onClick(View view) {
        AudioHelper.playAudioLocal(this.audioPath);
    }

    // OnTouchListener implementation
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int rawX = (int) event.getRawX();
        final int rawY = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams layoutParamsDown = (RelativeLayout.LayoutParams) view.getLayoutParams();
                // leftMargin and topMargin hold the current coordinates of the view
                dX = rawX - layoutParamsDown.leftMargin;
                dY = rawY - layoutParamsDown.topMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                // To ensure the view is dragged within the layout's boundary
                targetX = (rawX - dX < 0 || rawX - dX + view.getWidth() > ((ViewGroup) view.getParent()).getWidth()) ? view.getLeft() : rawX - dX;
                targetY = (rawY - dY < 0 || rawY - dY + view.getHeight() > ((ViewGroup) view.getParent()).getHeight()) ? view.getTop() : rawY - dY;
                RelativeLayout.LayoutParams layoutParamsMove = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParamsMove.leftMargin = targetX;
                layoutParamsMove.topMargin = targetY;
                layoutParamsMove.rightMargin = -250;
                layoutParamsMove.bottomMargin = -250;
                view.setLayoutParams(layoutParamsMove);
                break;
            case MotionEvent.ACTION_UP:
                // Set the final value for x and y coordinate
                this.x = targetX;
                this.y = targetY;
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            default:
                return false;
        }
        ((ViewGroup) view.getParent()).invalidate();
        return true;
    }
}
