package com.echodev.echoalpha.firebase;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.echodev.echoalpha.R;
import com.echodev.echoalpha.util.AudioHelper;
import com.echodev.echoalpha.util.ImageHelper;
import com.echodev.echoalpha.util.SpeechBubble;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ho on 10/3/2017.
 */

public class FirebaseBubbleWrapper implements View.OnTouchListener, View.OnClickListener {

    // Playback states of the MediaPlayer
    public static final int STATE_PLAY = 100;
    public static final int STATE_PAUSE = 101;

    // Types of the speech bubble
    public static final String TYPE_N = "N";
    public static final String TYPE_S = "S";
    public static final String TYPE_E = "E";
    public static final String TYPE_W = "W";
    public static final String TYPE_NE = "NE";
    public static final String TYPE_NW = "NW";
    public static final String TYPE_SE = "SE";
    public static final String TYPE_SW = "SW";

    private FirebaseBubble bubble;
    private int dX, dY, targetX, targetY;
    private ImageView bubbleImageView;
    private Context context;
    private MediaPlayer audioPlayer;

    // Constructors
    public FirebaseBubbleWrapper() {
        this.bubble = new FirebaseBubble();
    }

    public FirebaseBubbleWrapper(String postID, String creatorID) {
        this.bubble = new FirebaseBubble(postID, creatorID);
    }

    public FirebaseBubbleWrapper(SpeechBubble speechBubble) {
        this.bubble = new FirebaseBubble(speechBubble);
    }

    public FirebaseBubbleWrapper(FirebaseBubble bubble) {
        this.bubble = bubble;
    }

    // Getters
    public FirebaseBubble getBubble() {
        return bubble;
    }

    public String getBubbleID() {
        return bubble.getBubbleID();
    }

    public String getPostID() {
        return bubble.getPostID();
    }

    public String getCreatorID() {
        return bubble.getCreatorID();
    }

    public String getAudioUrl() {
        return bubble.getAudioUrl();
    }

    public String getAudioName() {
        return bubble.getAudioName();
    }

    public String getType() {
        return bubble.getType();
    }

    public String getCreationDate() {
        return bubble.getCreationDate();
    }

    public String getPlatform() {
        return bubble.getPlatform();
    }

    public long getX() {
        return bubble.getX();
    }

    public long getY() {
        return bubble.getY();
    }

    public long getPlayNumber() {
        return bubble.getPlayNumber();
    }

    // Setters
    public void setBubble(FirebaseBubble bubble) {
        this.bubble = bubble;
    }

    public void setBubbleID(String bubbleID) {
        bubble.setBubbleID(bubbleID);
    }

    public void setPostID(String postID) {
        bubble.setPostID(postID);
    }

    public void setCreatorID(String creatorID) {
        bubble.setCreatorID(creatorID);
    }

    public void setAudioUrl(String audioUrl) {
        bubble.setAudioUrl(audioUrl);
    }

    public void setAudioName(String audioName) {
        bubble.setAudioName(audioName);
    }

    public void setType(String type) {
        bubble.setType(type);
    }

    public void setCreationDate(String creationDate) {
        bubble.setCreationDate(creationDate);
    }

    public void setPlatform(String platform) {
        bubble.setPlatform(platform);
    }

    public void setX(long x) {
        bubble.setX(x);
    }

    public void setY(long y) {
        bubble.setY(y);
    }

    public void setPlayNumber(long playNumber) {
        bubble.setPlayNumber(playNumber);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    // Instance methods
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

        // Initialize the MediaPlayer for playing the audio
        initAudioPlayer();

        // Fill the ImageView with the corresponding speech bubble image
        loadBubbleImage();

        // Set the final value for x and y coordinate
        // Convert px to dp for data storage
        this.setX((long) ImageHelper.convertPxToDp(positionX, context));
        this.setY((long) ImageHelper.convertPxToDp(positionY, context));
    }

    public void loadBubbleImage() {
        if (bubbleImageView == null) {
            return;
        }

        int state = audioPlayer.isPlaying()? STATE_PAUSE : STATE_PLAY;

        if (state==STATE_PLAY && getType().equals(TYPE_N)) {
            Glide.with(context)
                    .load(R.drawable.bubble_n_play)
                    .fitCenter()
                    .into(bubbleImageView);
        } else if (state==STATE_PLAY && getType().equals(TYPE_S)) {
            Glide.with(context)
                    .load(R.drawable.bubble_s_play)
                    .fitCenter()
                    .into(bubbleImageView);
        } else if (state==STATE_PLAY && getType().equals(TYPE_E)) {
            Glide.with(context)
                    .load(R.drawable.bubble_e_play)
                    .fitCenter()
                    .into(bubbleImageView);
        } else if (state==STATE_PLAY && getType().equals(TYPE_W)) {
            Glide.with(context)
                    .load(R.drawable.bubble_w_play)
                    .fitCenter()
                    .into(bubbleImageView);
        } else if (state==STATE_PAUSE && getType().equals(TYPE_N)) {
            Glide.with(context)
                    .load(R.drawable.bubble_n_pause)
                    .fitCenter()
                    .into(bubbleImageView);
        } else if (state==STATE_PAUSE && getType().equals(TYPE_S)) {
            Glide.with(context)
                    .load(R.drawable.bubble_s_pause)
                    .fitCenter()
                    .into(bubbleImageView);
        } else if (state==STATE_PAUSE && getType().equals(TYPE_E)) {
            Glide.with(context)
                    .load(R.drawable.bubble_e_pause)
                    .fitCenter()
                    .into(bubbleImageView);
        } else if (state==STATE_PAUSE && getType().equals(TYPE_W)) {
            Glide.with(context)
                    .load(R.drawable.bubble_w_pause)
                    .fitCenter()
                    .into(bubbleImageView);
        }
    }

    public void initAudioPlayer() {
        audioPlayer = new MediaPlayer();
        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            audioPlayer.setDataSource(bubble.getAudioUrl());
            audioPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        audioPlayer.setOnCompletionListener(audioCompletionListener);
    }

    // Event listener binding methods
    public void bindAdjustListener() {
        bubbleImageView.setOnTouchListener(this);
    }

    public void setAdjustListener(View.OnTouchListener adjustListener) {
        bubbleImageView.setOnTouchListener(adjustListener);
    }

    public void bindPlayListener() {
        bubbleImageView.setOnClickListener(this);
    }

    public void bindPlayLocalListener() {
        bubbleImageView.setOnClickListener(playAudioLocalListener);
    }

    public void setPlayListener(View.OnClickListener playListener) {
        bubbleImageView.setOnClickListener(playListener);
    }

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
                // Convert px to dp for data storage
                this.setX((long) ImageHelper.convertPxToDp(targetX, context));
                this.setY((long) ImageHelper.convertPxToDp(targetY, context));
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

    @Override
    public void onClick(View v) {
        AudioHelper.playAudioOnline(bubble.getAudioUrl());
    }

    View.OnClickListener playAudioOnlineListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AudioHelper.playAudioOnline(bubble.getAudioUrl());
        }
    };

    View.OnClickListener playAudioLocalListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            if (audioPlayer == null) {
//                initAudioPlayer();
//            }

            if (audioPlayer.isPlaying()) {
                audioPlayer.pause();
                loadBubbleImage();
            } else {
//                AudioHelper.playAudioLocal(bubble.getAudioUrl());
                audioPlayer.start();
                loadBubbleImage();
            }
        }
    };

    MediaPlayer.OnCompletionListener audioCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            loadBubbleImage();
        }
    };
}
