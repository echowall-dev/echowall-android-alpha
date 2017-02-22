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
import com.echodev.echoalpha.util.ImageHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class PostActivity extends AppCompatActivity {
    public static final char SPEECH_BUBBLE_LEFT = 'L';
    public static final char SPEECH_BUBBLE_RIGHT = 'R';
    static final int REQUEST_TAKE_PHOTO = 103;

    @BindView(R.id.camera_btn)
    Button cameraBtn;

    @BindView(R.id.record_btn)
    Button Record_btn;

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

    private ImageView speechBubble;
    private int dX, dY, targetX, targetY, finalX, finalY;

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

        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString("userID");
        userEmail = bundle.getString("userEmail");
        postID = bundle.getString("postID");

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
                }
                break;
            default:
                break;
        }
    }

    @OnTouch(R.id.record_btn)
    public boolean recordAudioLocal(View view, MotionEvent event) {
        if (appDirExist && event.getAction() == MotionEvent.ACTION_DOWN) {
            // Start recording
            AudioHelper.startRecording(audioFilePath);
        } else if (appDirExist && event.getAction() == MotionEvent.ACTION_UP) {
            // Stop recording
            AudioHelper.stopRecording(audioFilePath);
        }
        return true;
    }

    public void playAudioLocal(View view) {
        if (!appDirExist) {
            return;
        }

        AudioHelper.playAudioLocal(audioFilePath);
    }

    @OnClick(R.id.add_bubble_btn_l)
    public void addSpeechBubbleL() {
        addSpeechBubble(SPEECH_BUBBLE_LEFT);
    }

    @OnClick(R.id.add_bubble_btn_r)
    public void addSpeechBubbleR() {
        addSpeechBubble(SPEECH_BUBBLE_RIGHT);
    }

    private void addSpeechBubble(final char bubbleOrientation) {
        // Get the dimensions of the View
        int targetW = localRes.getDimensionPixelSize(R.dimen.bubble_width);
        int targetH = localRes.getDimensionPixelSize(R.dimen.bubble_height);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(targetW, targetH);
        layoutParams.leftMargin = (int) ((previewArea.getWidth() - targetW) * 0.5);
        layoutParams.topMargin = (int) ((previewArea.getHeight() - targetH) * 0.5);

        speechBubble = new ImageView(this);
        speechBubble.setLayoutParams(layoutParams);
        previewArea.addView(speechBubble);

        if (bubbleOrientation == SPEECH_BUBBLE_LEFT) {
            ImageHelper.setPicFromResources(speechBubble, targetW, targetH, localRes, R.drawable.speech_bubble_l);
        } else if (bubbleOrientation == SPEECH_BUBBLE_RIGHT) {
            ImageHelper.setPicFromResources(speechBubble, targetW, targetH, localRes, R.drawable.speech_bubble_r);
        }

        speechBubble.setOnTouchListener(adjustBubbleListener);
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
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putString("photoPath", photoFilePath);
        bundle.putString("audioPath", audioFilePath);
        bundle.putInt("bubbleX", finalX);
        bundle.putInt("bubbleY", finalY);
        intent.putExtras(bundle);

        setResult(RESULT_OK, intent);
        finish();
    }
}
