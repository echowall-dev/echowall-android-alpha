package com.echodev.echoalpha;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.echodev.echoalpha.firebase.FirebaseBubble;
import com.echodev.echoalpha.firebase.FirebaseBubbleWrapper;
import com.echodev.echoalpha.firebase.FirebasePost;
import com.echodev.echoalpha.firebase.FirebaseUserClass;
import com.echodev.echoalpha.util.AudioHelper;
import com.echodev.echoalpha.util.ImageHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostCreateActivity extends AppCompatActivity {

    // Debug log
    private static final String LOG_TAG = "Echo_Alpha_Post_Create";

    // Request code for taking photo
    public static final int REQUEST_TAKE_PHOTO = 120;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDb;
    private DatabaseReference mDbRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    // Instance variables
    private FirebaseUserClass localUser;
    private FirebasePost newPost;
    private FirebaseBubbleWrapper bubbleWrapper;

    private Context localContext;
    private Resources localResources;
    private boolean appDirExist, audioBubbleEditing;
    private int bubbleCounter;

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

        localContext = this.getApplicationContext();
        localResources = this.getResources();

        // Fetch data from the previous Activity
        localUser = (FirebaseUserClass) getIntent().getParcelableExtra("currentUser");

        // Create new Post instance
        newPost = new FirebasePost(localUser);

        // Firebase instances initialization
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        mDbRef = mDb.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        // Check if app folder already exists
        appDirExist = MainActivity.createAppDir();
        audioBubbleEditing = false;
        bubbleCounter = 0;

        // Start taking photo
        dispatchTakePictureIntent();
    }

    // Photo handling
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            newPost.setPhotoUrl(ImageHelper.createImageFile(localResources, newPost.getCreatorID()));
            Uri photoUri = Uri.fromFile(new File(newPost.getPhotoUrl()));
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = ImageHelper.galleryAddPicIntent(newPost.getPhotoUrl());
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
                            .load(newPost.getPhotoUrl())
                            .asBitmap()
                            .into(previewImage);

                    // Add the photo name to the new Post instance
                    newPost.setPhotoName(Uri.parse(newPost.getPhotoUrl()).getLastPathSegment());

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
    View.OnLongClickListener recordAuioStartListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (bubbleWrapper == null) {
                bubbleWrapper = new FirebaseBubbleWrapper(newPost.getPostID(), localUser.getUserID());
                bubbleWrapper.setContext(localContext);
                bubbleWrapper.setAudioUrl(AudioHelper.createAudioFile(localResources, localUser.getUserID()));
            }

            boolean startSuccess = AudioHelper.startRecording(bubbleWrapper.getAudioUrl());

            return startSuccess;
        }
    };

    View.OnTouchListener recordAudioStopListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                boolean stopSuccess = AudioHelper.stopRecording();

                if (stopSuccess) {
                    bubbleWrapper.setAudioName(Uri.parse(bubbleWrapper.getAudioUrl()).getLastPathSegment());

                    btnNext.setText(localResources.getString(R.string.add_bubble));
                    btnNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enterBubblePrepareStage();
                        }
                    });
                }
            }

            // This OnTouch event has to return false for the LonClick event to work
            return false;
        }
    };

    View.OnClickListener playAudioListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AudioHelper.playAudioLocal(bubbleWrapper.getAudioUrl());
        }
    };
    // End of audio handling

    // Speech bubble handling
    View.OnClickListener addBubbleImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.post_create_btn_0:
                    bubbleWrapper.setType("L");
                    break;
                case R.id.post_create_btn_1:
                    bubbleWrapper.setType("R");
                    break;
                default:
                    break;
            }

            int targetW = localResources.getDimensionPixelSize(R.dimen.bubble_width);
            int targetH = localResources.getDimensionPixelSize(R.dimen.bubble_height);
            int centerX = (int) ((previewArea.getWidth() - targetW) * 0.5);
            int centerY = (int) ((previewArea.getHeight() - targetH) * 0.5);

            bubbleWrapper.addBubbleImage(centerX, centerY, previewArea, localResources, localContext);
            bubbleWrapper.bindAdjustListener();
        }
    };

    View.OnClickListener finishBubbleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            bubbleWrapper.setCreationDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            newPost.addBubble(bubbleWrapper.getBubble());

            // Clear current speech bubble
            bubbleWrapper.setAdjustListener(null);
            bubbleWrapper = null;

            enterAudioPrepareStage();
        }
    };
    // End of speech bubble handling

    // Finish creating post
    View.OnClickListener finishPostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            newPost.setCaption("What a beautiful day!");
            newPost.setCreationDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            // Upload everything to Firebase
            uploadPhotoToFirebaseStorage();

            // TODO: when upload is done
            /*
            setResult(RESULT_OK);
            finish();
            */
        }
    };

    public void uploadPhotoToFirebaseStorage() {
        // Upload the photo to Firebase storage
        StorageReference photoRef = mStorageRef.child("picture/" + newPost.getPhotoName());
        Uri photoUri = Uri.fromFile(new File(newPost.getPhotoUrl()));
        UploadTask uploadTask = photoRef.putFile(photoUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle unsuccessful uploads
                Snackbar.make(rootView, "Photo storage failed", Snackbar.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads
                String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                newPost.setPhotoUrl(downloadUrl);

                Snackbar.make(rootView, "Photo storage success", Snackbar.LENGTH_SHORT).show();

                if (newPost.getBubbleList().size() > 0) {
                    for (int i = 0; i < newPost.getBubbleList().size(); i++) {
                        FirebaseBubble bubble = newPost.getBubble(i);
                        uploadAudioToFirebaseStorage(i);
                    }
                } else {
                    uploadPostToFirebaseDatabase();
                }
            }
        });
    }

    private void uploadAudioToFirebaseStorage(final int i) {
        StorageReference audioRef = mStorageRef.child("audio/" + newPost.getBubble(i).getAudioName());
        Uri audioUri = Uri.fromFile(new File(newPost.getBubble(i).getAudioUrl()));
        UploadTask uploadTask = audioRef.putFile(audioUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle unsuccessful uploads
                Snackbar.make(rootView, "Audio storage failed", Snackbar.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads
                String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                FirebaseBubble bubble = newPost.getBubble(i);
                bubble.setAudioUrl(downloadUrl);
                newPost.setBubble(i, bubble);

                Snackbar.make(rootView, "Audio storage success", Snackbar.LENGTH_SHORT).show();

                if (++bubbleCounter == newPost.getBubbleList().size()) {
                    bubbleCounter = 0;
                    uploadPostToFirebaseDatabase();
                }
            }
        });
    }

    private void uploadPostToFirebaseDatabase() {
//        DatabaseReference mPostRef = mDbRef.child("post").child(newPost.getPostID());
        String postKey = "post_" + newPost.getCreationDate().replace(" ", "_").replace("-", "").replace(":", "");
        DatabaseReference mPostRef = mDbRef.child("post").child(postKey);
        mPostRef.setValue(newPost, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Snackbar.make(rootView, "Post to databse failed", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(rootView, "Post to databse success", Snackbar.LENGTH_SHORT).show();

                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    // State transition
    public void enterAudioPrepareStage() {
        btn0.setText(localResources.getString(R.string.record));
        btn0.setOnClickListener(null);
        btn0.setOnLongClickListener(recordAuioStartListener);
        btn0.setOnTouchListener(recordAudioStopListener);

        btn1.setText(localResources.getString(R.string.play));
        btn1.setOnClickListener(playAudioListener);

        btnNext.setText(localResources.getString(R.string.finish));
        btnNext.setOnClickListener(finishPostListener);
    }

    public void enterBubblePrepareStage() {
        btn0.setText(localResources.getString(R.string.add_bubble_left));
        btn0.setOnLongClickListener(null);
        btn0.setOnTouchListener(null);
        btn0.setOnClickListener(addBubbleImageListener);

        btn1.setText(localResources.getString(R.string.add_bubble_right));
        btn1.setOnClickListener(addBubbleImageListener);

        btnNext.setText(localResources.getString(R.string.finish_bubble));
        btnNext.setOnClickListener(finishBubbleListener);
    }
}
