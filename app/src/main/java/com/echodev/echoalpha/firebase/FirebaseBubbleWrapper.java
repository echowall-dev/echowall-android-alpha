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

import java.io.IOException;

/**
 * Created by Ho on 10/3/2017.
 */

public class FirebaseBubbleWrapper implements View.OnTouchListener, View.OnClickListener {

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

    public int getX() {
        return bubble.getX();
    }

    public int getY() {
        return bubble.getY();
    }

    public double getXRatio() {
        return bubble.getXRatio();
    }

    public double getYRatio() {
        return bubble.getYRatio();
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

    public void setX(int x) {
        bubble.setX(x);
    }

    public void setY(int y) {
        bubble.setY(y);
    }

    public void setXRatio(double xRatio) {
        bubble.setXRatio(xRatio);
    }

    public void setYRatio(double yRatio) {
        bubble.setYRatio(yRatio);
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

        // Fill the ImageView with the corresponding speech bubble image
        loadBubbleImage();

        // Set the final value for x and y coordinate
        // Convert px to dp for data storage
        setX(ImageHelper.convertPxToDp(positionX, context));
        setY(ImageHelper.convertPxToDp(positionY, context));
    }

    public void addBubbleImageByRatio(double xRatio, double yRatio, int parentWidth, int parentHeight, ViewGroup viewGroup, Resources resources, Context context) {
        // Prepare the dimensions of the ImageView
        int targetW = resources.getDimensionPixelSize(R.dimen.bubble_width);
        int targetH = resources.getDimensionPixelSize(R.dimen.bubble_height);

        // Create a new ImageView to the the ViewGroup
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(targetW, targetH);

        int positionX = (int) Math.round(xRatio * parentWidth);
        int positionY = (int) Math.round(yRatio * parentHeight);

        layoutParams.leftMargin = positionX;
        layoutParams.topMargin = positionY;

        bubbleImageView = new ImageView(context);
        bubbleImageView.setLayoutParams(layoutParams);
        viewGroup.addView(bubbleImageView);

        // Fill the ImageView with the corresponding speech bubble image
        loadBubbleImage();

        // Set the final value for x and y coordinate
        // Store the ration of the coordinates to the dimensions of the parent RelativeLayout
        setCoordinateRatio(positionX, positionY, parentWidth, parentHeight);
    }

    public void loadBubbleImage() {
        if (bubbleImageView == null) {
            return;
        }

        boolean audioIsPlaying = false;
        if (audioPlayer != null) {
            audioIsPlaying = audioPlayer.isPlaying();
        }

        // Default image for speech bubble
        int bubbleImagePath = R.drawable.bubble_play_sw;

        // Load corresponding image for speech bubble
        if (!audioIsPlaying && getType().equals(TYPE_N)) {
            bubbleImagePath = R.drawable.bubble_play_n;
        } else if (!audioIsPlaying && getType().equals(TYPE_S)) {
            bubbleImagePath = R.drawable.bubble_play_s;
        } else if (!audioIsPlaying && getType().equals(TYPE_E)) {
            bubbleImagePath = R.drawable.bubble_play_e;
        } else if (!audioIsPlaying && getType().equals(TYPE_W)) {
            bubbleImagePath = R.drawable.bubble_play_w;
        } else if (!audioIsPlaying && getType().equals(TYPE_NE)) {
            bubbleImagePath = R.drawable.bubble_play_ne;
        } else if (!audioIsPlaying && getType().equals(TYPE_NW)) {
            bubbleImagePath = R.drawable.bubble_play_nw;
        } else if (!audioIsPlaying && getType().equals(TYPE_SE)) {
            bubbleImagePath = R.drawable.bubble_play_se;
        } else if (!audioIsPlaying && getType().equals(TYPE_SW)) {
            bubbleImagePath = R.drawable.bubble_play_sw;
        } else if (audioIsPlaying && getType().equals(TYPE_N)) {
            bubbleImagePath = R.drawable.bubble_pause_n;
        } else if (audioIsPlaying && getType().equals(TYPE_S)) {
            bubbleImagePath = R.drawable.bubble_pause_s;
        } else if (audioIsPlaying && getType().equals(TYPE_E)) {
            bubbleImagePath = R.drawable.bubble_pause_e;
        } else if (audioIsPlaying && getType().equals(TYPE_W)) {
            bubbleImagePath = R.drawable.bubble_pause_w;
        } else if (audioIsPlaying && getType().equals(TYPE_NE)) {
            bubbleImagePath = R.drawable.bubble_pause_ne;
        } else if (audioIsPlaying && getType().equals(TYPE_NW)) {
            bubbleImagePath = R.drawable.bubble_pause_nw;
        } else if (audioIsPlaying && getType().equals(TYPE_SE)) {
            bubbleImagePath = R.drawable.bubble_pause_se;
        } else if (audioIsPlaying && getType().equals(TYPE_SW)) {
            bubbleImagePath = R.drawable.bubble_pause_sw;
        }

        Glide.with(context)
                .load(bubbleImagePath)
                .fitCenter()
                .into(bubbleImageView);
    }

    public void initAudioPlayer() {
        audioPlayer = new MediaPlayer();
        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            audioPlayer.setDataSource(bubble.getAudioUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // For streams, use prepareAsync() instead of prepare() as it's non-blocking
        audioPlayer.prepareAsync();

        audioPlayer.setOnCompletionListener(audioCompletionListener);
    }

    public void setCoordinateRatio(int childX, int childY, int parentWidth, int parentHeight) {
        // Calculate the ration of the coordinates to the dimensions of the parent RelativeLayout
        double widthRatio = (double) childX / parentWidth;
        double heightRatio = (double) childY / parentHeight;

        // Round the values up to 2 decimal places and store them
        setXRatio((double) Math.round(widthRatio * 100) / 100);
        setYRatio((double) Math.round(heightRatio * 100) / 100);
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
                setX(ImageHelper.convertPxToDp(targetX, context));
                setY(ImageHelper.convertPxToDp(targetY, context));

                // Store the ration of the coordinates to the dimensions of the parent RelativeLayout
                setCoordinateRatio(targetX, targetY, ((ViewGroup) view.getParent()).getWidth(), ((ViewGroup) view.getParent()).getHeight());

                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            default:
                return false;
        }

        // Re-render the parent RelativeLayout
        ((ViewGroup) view.getParent()).invalidate();

        return true;
    }

    @Override
    public void onClick(View v) {
//        if (audioPlayer == null) {
//            initAudioPlayer();
//        }

        if (audioPlayer.isPlaying()) {
            loadBubbleImage();
            audioPlayer.pause();
        } else {
            loadBubbleImage();
            audioPlayer.start();
        }
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
            AudioHelper.playAudioLocal(bubble.getAudioUrl());
        }
    };

    MediaPlayer.OnCompletionListener audioCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
//            mp.release();
//            audioPlayer = null;
            loadBubbleImage();
        }
    };
}
