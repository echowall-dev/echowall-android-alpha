package com.echodev.echoalpha;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.echodev.echoalpha.firebase.FirebasePost;
import com.echodev.echoalpha.firebase.FirebaseSpeechBubble;
import com.echodev.echoalpha.firebase.FirebaseUserClass;
import com.echodev.echoalpha.util.ImageHelper;
import com.echodev.echoalpha.util.PostClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostCreateActivity extends AppCompatActivity {

    // Debug log
    private static final String LOG_TAG = "Echo_Alpha_Post_Create";

    // Request code for taking photo
    public static final int REQUEST_TAKE_PHOTO = 120;

    // Request code for Firebase Storage
    public static final int STORAGE_PHOTO = 220;
    public static final int STORAGE_AUDIO = 221;

    // Instance variables
    private FirebaseUserClass firebaseUser;
    private FirebasePost firebasePost;
    private FirebaseSpeechBubble firebaseBubble;
    private ArrayList<FirebaseSpeechBubble> firebaseBubbleList;

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

    // Bind views by ButterKnife
    @BindView(R.id.activity_post_create)
    View rootView;

    @BindView(R.id.post_create_btn_0)
    Button btn0;

    @BindView(R.id.post_create_btn_1)
    Button btn1;

    @BindView(R.id.post_create_btn_next)
    Button btnNext;

    @BindView(R.id.post_create_preview_area)
    RelativeLayout previewArea;

    @BindView(R.id.post_create_preview_image)
    ImageView previewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_create);
        ButterKnife.bind(this);

        // Firebase instances initialization
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        mDbRef = mDb.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        // Fetch data from the previous Activity
        Bundle bundle = getIntent().getExtras();

        // Create new User instance
        firebaseUser = new FirebaseUserClass(
                bundle.getString("userID"),
                bundle.getString("userEmail"),
                bundle.getString("userName")
        );

        // Create new ArrayList for storing speech bubbles before uploading to Firebase
        firebaseBubbleList = new ArrayList<FirebaseSpeechBubble>();

        // Create new Post instance
        firebasePost = new FirebasePost(firebaseUser.getUserID());

        // Check if app folder already exists
        appDirExist = MainActivity.createAppDir();
        audioBubbleEditing = false;

        // Prepare app resources for use
        localResources = this.getResources();

        // Start taking photo
        dispatchTakePictureIntent();
    }

    // Photo handling
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            firebasePost.setPhotoUrl(ImageHelper.createImageFile(localResources, firebasePost.getCreatorID()));
            Uri photoUri = Uri.fromFile(new File(firebasePost.getPhotoUrl()));
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = ImageHelper.galleryAddPicIntent(firebasePost.getPhotoUrl());
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
                            .load(firebasePost.getPhotoUrl())
                            .asBitmap()
                            .into(previewImage);

                    // Add the photo name to the new Post instance
                    firebasePost.setPhotoName(Uri.parse(firebasePost.getPhotoUrl()).getLastPathSegment());

                    enterAudioPrepareStage();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                break;
            default:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
    // End of photo handling

    // Audio handling
    View.OnLongClickListener startRecordingListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            return true;
        }
    };

    View.OnTouchListener stopRecordingListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                btnNext.setText(localResources.getString(R.string.add_bubble));
                btnNext.setOnClickListener(null);
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        enterBubblePrepareStage();
                    }
                });
            }

            // This OnTouch event has to return false for the LonClick event to work
            return false;
        }
    };

    View.OnClickListener playAudioListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
    // End of audio handling

    // Speech bubble handling
    View.OnClickListener setBubbleTypeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.post_create_btn_0:
                    firebaseBubble.setType("L");
                    break;
                case R.id.post_create_btn_1:
                    firebaseBubble.setType("R");
                    break;
                default:
                    break;
            }
        }
    };

    View.OnClickListener finishBubbleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            firebaseBubbleList.add(firebaseBubble);
            enterAudioPrepareStage();
        }
    };
    // End of speech bubble handling

    // Finish creating post
    View.OnClickListener finishPostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO: upload everything to Firebase

            // TODO: when uploading is done
            setResult(RESULT_OK);
            finish();
        }
    };

    // State transition
    public void enterAudioPrepareStage() {
        btn0.setText(localResources.getString(R.string.record));
        btn0.setOnLongClickListener(startRecordingListener);
        btn0.setOnTouchListener(stopRecordingListener);

        btn1.setText(localResources.getString(R.string.play));
        btn1.setOnClickListener(playAudioListener);

        btnNext.setText(localResources.getString(R.string.finish));
        btnNext.setOnClickListener(finishPostListener);
    }

    public void enterBubblePrepareStage() {
        btn0.setText(localResources.getString(R.string.add_bubble_left));
        btn0.setOnLongClickListener(null);
        btn0.setOnTouchListener(null);
        // TODO
        btn0.setOnClickListener(null);

        btn1.setText(localResources.getString(R.string.add_bubble_right));
        // TODO
        btn1.setOnClickListener(null);

        btnNext.setText(localResources.getString(R.string.finish_bubble));
        btnNext.setOnClickListener(finishBubbleListener);
    }
}
