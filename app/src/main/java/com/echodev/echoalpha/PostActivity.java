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

import com.bumptech.glide.Glide;
import com.echodev.echoalpha.util.AudioHelper;
import com.echodev.echoalpha.firebase.FirebasePost;
import com.echodev.echoalpha.firebase.FirebaseSpeechBubble;
import com.echodev.echoalpha.util.SpeechBubble;
import com.echodev.echoalpha.util.ImageHelper;
import com.echodev.echoalpha.util.PostClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTouch;

public class PostActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Echo_Alpha_Post";

    // Request code for taking photo
    public static final int REQUEST_TAKE_PHOTO = 120;

    // Request code for Firebase Storage
    public static final int STORAGE_PHOTO = 220;
    public static final int STORAGE_AUDIO = 221;

    // Bind views by ButterKnife
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

    @BindView(R.id.finish_bubble_btn)
    Button finishBubbleBtn;

    @BindView(R.id.finish_post_btn)
    Button finishPostBtn;

    @BindView(R.id.preview_area)
    RelativeLayout previewArea;

    @BindView(R.id.preview_image)
    ImageView previewImage;

    // Instance variables
    private PostClass newPost;
    private SpeechBubble speechBubble;

    private Resources localResources;
    private boolean appDirExist, audioBubbleEditing;
    private String photoFilePath, audioFilePath;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FirebaseDatabase mDb;
    private DatabaseReference mDbRef;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        mDbRef = mDb.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        // Fetch data from the previous Activity
        Bundle bundle = getIntent().getExtras();

        // Create new Post instance
        newPost = new PostClass();
        newPost.setUserID(bundle.getString("userID"))
                .setUserEmail(bundle.getString("userEmail"))
                .setUserName(bundle.getString("userName"));

        // Check if app folder already exists
        appDirExist = MainActivity.createAppDir();
        audioBubbleEditing = false;

        // Prepare app resources for use
        localResources = this.getResources();
    }

    // Photo handling
    @OnClick(R.id.camera_btn)
    public void dispatchTakePictureIntent(View view) {
        if (!newPost.matchCurrentPostState(PostClass.STATE_PHOTO_PREPARE)) {
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
            } catch (IOException e) {
                // Error occurred while creating the File
                e.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.echodev.echoalpha.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

            /*
            photoFilePath = ImageHelper.createImageFile(localResources, newPost.getUserID());
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFilePath);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            */
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
                    // Add the photo to the phone's gallery
                    galleryAddPic();

                    // Load the photo into preview area
                    Glide.with(this)
                            .load(photoFilePath)
                            .asBitmap()
                            .into(previewImage);

                    // Add the photo to the new Post object
                    newPost.setPhotoPath(photoFilePath);

                    // Set post state to not ready
                    newPost.setCurrentPostState(PostClass.STATE_AUDIO_PREPARE);
                    newPost.setPostReady(true);
                }
                break;
            default:
                break;
        }
    }
    // End of photo handling

    // Audio handling
    @OnLongClick(R.id.record_btn)
    public boolean recordAudioLocalStart() {
        if (!newPost.matchCurrentPostState(PostClass.STATE_AUDIO_PREPARE) || audioBubbleEditing || !appDirExist) {
            return false;
        }

        // Prepare new audio file path and name
        if (audioFilePath == null || audioFilePath.isEmpty()) {
            audioFilePath = AudioHelper.createAudioFile(localResources, newPost.getUserID());
        }

        if (speechBubble == null) {
            speechBubble = new SpeechBubble();
            speechBubble.setPostID(newPost.getPostIDString())
                    .setUserID(newPost.getUserID())
                    .setUserEmail(newPost.getUserEmail());
        }

        // Start recording when button is clicked and held but not for a short click
        boolean startSuccess = AudioHelper.startRecording(audioFilePath);

        return startSuccess;
    }

    @OnTouch(R.id.record_btn)
    public boolean recordAudioLocalControl(View view, MotionEvent event) {
        if (!newPost.matchCurrentPostState(PostClass.STATE_AUDIO_PREPARE) || audioBubbleEditing || !appDirExist) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            // Stop recording when button is released
            boolean stopSuccess = AudioHelper.stopRecording();

            // Set post state to not ready if recording stops successfully
            if (stopSuccess) {
                newPost.setCurrentPostState(PostClass.STATE_BUBBLE_PREPARE);
                newPost.setPostReady(false);
                audioBubbleEditing = true;
            }
        }

        // This OnTouch event needs to return false for the LonClick event to work
        return false;
    }

    @OnClick(R.id.play_btn)
    public void playAudioLocal(View view) {
        if (newPost.matchCurrentPostState(PostClass.STATE_BUBBLE_PREPARE) && audioBubbleEditing && appDirExist) {
            AudioHelper.playAudioLocal(audioFilePath);
        }
    }
    // End of audio handling

    // Speech bubble handling
    @OnClick(R.id.add_bubble_btn_l)
    public void addSpeechBubbleL() {
        addSpeechBubble(SpeechBubble.SPEECH_BUBBLE_TYPE_LEFT);
    }

    @OnClick(R.id.add_bubble_btn_r)
    public void addSpeechBubbleR() {
        addSpeechBubble(SpeechBubble.SPEECH_BUBBLE_TYPE_RIGHT);
    }

    private void addSpeechBubble(final int bubbleType) {
        if (!newPost.matchCurrentPostState(PostClass.STATE_BUBBLE_PREPARE) || !audioBubbleEditing || speechBubble.isBubbleReady() || !appDirExist) {
            return;
        }

        speechBubble.setAudioPath(audioFilePath);

        if (bubbleType == SpeechBubble.SPEECH_BUBBLE_TYPE_LEFT) {
            speechBubble.setType(SpeechBubble.SPEECH_BUBBLE_TYPE_LEFT);
        } else if (bubbleType == SpeechBubble.SPEECH_BUBBLE_TYPE_RIGHT) {
            speechBubble.setType(SpeechBubble.SPEECH_BUBBLE_TYPE_RIGHT);
        }

        // Add a new ImageView at the center of the ViewGroup
        int targetW = localResources.getDimensionPixelSize(R.dimen.bubble_width);
        int targetH = localResources.getDimensionPixelSize(R.dimen.bubble_height);
        int centerX = (int) ((previewArea.getWidth() - targetW) * 0.5);
        int centerY = (int) ((previewArea.getHeight() - targetH) * 0.5);
        speechBubble.addBubbleImage(centerX, centerY, previewArea, localResources, this.getApplicationContext());
        speechBubble.bindAdjustListener();
        speechBubble.setBubbleReady(true);
    }

    @OnClick(R.id.finish_bubble_btn)
    public void finishBubble() {
        if (!audioBubbleEditing || !speechBubble.isBubbleReady()) {
            return;
        }

        speechBubble.setCreationDate(new Date());
        newPost.addSpeechBubble(speechBubble);
        audioFilePath = null;
        speechBubble = null;

        audioBubbleEditing = false;
        newPost.setCurrentPostState(PostClass.STATE_AUDIO_PREPARE);
        newPost.setPostReady(true);
    }
    // End of speech bubble handling

    // Finish creating post
    @OnClick(R.id.finish_post_btn)
    public void finishPost() {
        if (!newPost.isPostReady()) {
            return;
        }

        newPost.setCaption("What a beautiful day!")
                .setCreationDate(new Date());

        /*
        // Upload files and data to Firebase
        String photoUrl = uploadToFirebaseStorage(newPost.getPhotoPath(), STORAGE_PHOTO);
        newPost.setPhotoUri(photoUrl);

        for (int i=0; i<newPost.getSpeechBubbleList().size(); i++) {
            SpeechBubble speechBubble = newPost.getSpeechBubble(i);
            String audioUrl = uploadToFirebaseStorage(speechBubble.getAudioPath(), STORAGE_AUDIO);
            speechBubble.setAudioUri(audioUrl);
            newPost.setSpeechBubble(i, speechBubble);
        }

        uploadToFirebaseDatabse(newPost);
        // End of uploading
        */

        Intent intent = new Intent();
        intent.putExtra("newPost", newPost);

        setResult(RESULT_OK, intent);
        finish();
    }

    public String uploadToFirebaseStorage(String filePath, int storageType) {
        String firebaseFileUrl = "";

        if (storageType == STORAGE_PHOTO) {
            // TODO: Upload the photo to Firebase storage
        } else if (storageType == STORAGE_AUDIO) {
            // TODO: Upload the audio to Firebase storage
        }

        return firebaseFileUrl;
    }

    public boolean uploadToFirebaseDatabse(PostClass newPost) {
        boolean uploadSuccess = true;

        FirebasePost newFirebasePost = new FirebasePost(newPost);
        // TODO: Push the post to Firebase database
        newFirebasePost.setPhotoUrl(newPost.getPhotoPath());

        for (SpeechBubble speechBubble : newPost.getSpeechBubbleList()) {
        	// TODO: Push the speech bubble to Firebase database
            FirebaseSpeechBubble newFirebaseBubble = new FirebaseSpeechBubble(speechBubble);
        }

        return uploadSuccess;
    }
}
