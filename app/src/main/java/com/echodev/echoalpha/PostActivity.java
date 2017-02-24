package com.echodev.echoalpha;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.echodev.echoalpha.util.AudioHelper;
import com.echodev.echoalpha.util.SpeechBubble;
import com.echodev.echoalpha.util.ImageHelper;
import com.echodev.echoalpha.util.PostClass;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class PostActivity extends AppCompatActivity {

    public static final int REQUEST_TAKE_PHOTO = 120;

    private int currentPostState;

    @BindView(R.id.camera_btn)
    Button cameraBtn;

    @BindView(R.id.record_btn)
    Button recordBtn;

    @BindView(R.id.play_btn)
    Button playBtn;

    @BindView(R.id.add_bubble_btn_l)
    Button addBubbleBtnL;

    @BindView(R.id.add_bubble_btn_r)
    Button addBubbleBtnR;

    @BindView(R.id.finish_btn)
    Button finishBtn;

    @BindView(R.id.preview_area)
    RelativeLayout previewArea;

    @BindView(R.id.preview_image)
    ImageView previewImage;

    private ImageView bubbleImageView;
    private int dX, dY, targetX, targetY, finalX, finalY, finalOrientation;
    private PostClass newPost;
    private SpeechBubble speechBubble;

    private Resources localRes;
    private static String appName, audioFormat;
    private boolean appDirExist;
    private String userID, userEmail, postID;
    private String photoFilePath, audioFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);

        currentPostState = PostClass.STATE_PHOTO_PREPARE;

        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString("userID");
        userEmail = bundle.getString("userEmail");
        postID = bundle.getString("postID");

//        SpeechBubble speechBubble = new SpeechBubble(postID, userEmail);

        localRes = this.getResources();
        appName = localRes.getString(R.string.app_name);
        audioFormat = localRes.getString(R.string.audio_format);

        appDirExist = MainActivity.createAppDir();
        if (appDirExist) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            audioFilePath += "/" + appName + "/audio/" + userID + "_" + timeStamp + audioFormat;
        }
    }

    @OnClick(R.id.camera_btn)
    public void dispatchTakePictureIntent(View view) {
        if (currentPostState != PostClass.STATE_PHOTO_PREPARE) {
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                photoFile = ImageHelper.createImageFile(storageDir);

                // Save a file: path for use with ACTION_VIEW intents
                photoFilePath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
                // Terminate the app
                this.finishAffinity();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.echodev.echoalpha.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = ImageHelper.galleryAddPicIntent(photoFilePath);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    galleryAddPic();
                    ImageHelper.setPicFromFile(previewImage, photoFilePath);

                    currentPostState = PostClass.STATE_AUDIO_PREPARE;
                }
                break;
            default:
                break;
        }
    }

    @OnTouch(R.id.record_btn)
    public boolean recordAudioLocal(View view, MotionEvent event) {
        if (!appDirExist || currentPostState != PostClass.STATE_AUDIO_PREPARE) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Start recording
            AudioHelper.startRecording(audioFilePath);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            // Stop recording
            AudioHelper.stopRecording(audioFilePath);

            currentPostState = PostClass.STATE_BUBBLE_PREPARE;
        }

        return true;
    }

    @OnClick(R.id.play_btn)
    public void playAudioLocal(View view) {
        if (appDirExist && currentPostState == PostClass.STATE_BUBBLE_PREPARE) {
            AudioHelper.playAudioLocal(audioFilePath);
        }
    }

    @OnClick(R.id.add_bubble_btn_l)
    public void addSpeechBubbleL() {
        addSpeechBubble(SpeechBubble.SPEECH_BUBBLE_TYPE_LEFT);
    }

    @OnClick(R.id.add_bubble_btn_r)
    public void addSpeechBubbleR() {
        addSpeechBubble(SpeechBubble.SPEECH_BUBBLE_TYPE_RIGHT);
    }

    private void addSpeechBubble(final int bubbleOrientation) {
        if (currentPostState != PostClass.STATE_BUBBLE_PREPARE) {
            return;
        }

        // Get the dimensions of the View
        int targetW = localRes.getDimensionPixelSize(R.dimen.bubble_width);
        int targetH = localRes.getDimensionPixelSize(R.dimen.bubble_height);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(targetW, targetH);
        layoutParams.leftMargin = (int) ((previewArea.getWidth() - targetW) * 0.5);
        layoutParams.topMargin = (int) ((previewArea.getHeight() - targetH) * 0.5);

        bubbleImageView = new ImageView(this);
        bubbleImageView.setLayoutParams(layoutParams);
        previewArea.addView(bubbleImageView);

        if (bubbleOrientation == SpeechBubble.SPEECH_BUBBLE_TYPE_LEFT) {
            finalOrientation = SpeechBubble.SPEECH_BUBBLE_TYPE_LEFT;
//            ImageHelper.setPicFromResources(bubbleImageView, localRes, R.drawable.speech_bubble_l);
            ImageHelper.setPicFromResources(bubbleImageView, targetW, targetH, localRes, R.drawable.speech_bubble_l);
        } else if (bubbleOrientation == SpeechBubble.SPEECH_BUBBLE_TYPE_RIGHT) {
            finalOrientation = SpeechBubble.SPEECH_BUBBLE_TYPE_RIGHT;
//            ImageHelper.setPicFromResources(bubbleImageView, localRes, R.drawable.speech_bubble_r);
            ImageHelper.setPicFromResources(bubbleImageView, targetW, targetH, localRes, R.drawable.speech_bubble_r);
        }

        bubbleImageView.setOnTouchListener(adjustBubbleListener);

        currentPostState = PostClass.STATE_POST_READY;
    }

    private View.OnTouchListener adjustBubbleListener = new View.OnTouchListener() {
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
                    // To ensure the view won't be dragged out of the layout's boundary
                    targetX = (rawX - dX < 0 || rawX - dX + view.getWidth() > previewImage.getWidth()) ? view.getLeft() : rawX - dX;
                    targetY = (rawY - dY < 0 || rawY - dY + view.getHeight() > previewImage.getHeight()) ? view.getTop() : rawY - dY;
                    RelativeLayout.LayoutParams layoutParamsMove = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    layoutParamsMove.leftMargin = targetX;
                    layoutParamsMove.topMargin = targetY;
                    layoutParamsMove.rightMargin = -250;
                    layoutParamsMove.bottomMargin = -250;
                    view.setLayoutParams(layoutParamsMove);
                    break;
                case MotionEvent.ACTION_UP:
                    finalX = targetX;
                    finalY = targetY;
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                default:
                    return false;
            }
            previewArea.invalidate();
            return true;
        }
    };

    @OnClick(R.id.finish_btn)
    public void finishPost() {
        if (currentPostState != PostClass.STATE_POST_READY) {
            return;
        }

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putString("photoPath", photoFilePath);
        bundle.putString("audioPath", audioFilePath);
        bundle.putInt("bubbleOrientation", finalOrientation);
        bundle.putInt("bubbleX", finalX);
        bundle.putInt("bubbleY", finalY);
        intent.putExtras(bundle);

        currentPostState = PostClass.STATE_PHOTO_PREPARE;

        setResult(RESULT_OK, intent);
        finish();
    }
}
