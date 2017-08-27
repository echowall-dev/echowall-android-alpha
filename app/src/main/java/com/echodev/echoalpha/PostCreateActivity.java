package com.echodev.echoalpha;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.echodev.echoalpha.Resizer.Resizer;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostCreateActivity extends AppCompatActivity {

    // Debug log
    private static final String LOG_TAG = "Echo_Alpha_Post_Create";

    // Request code for taking photo
    public static final int REQUEST_TAKE_PHOTO = 120;

    // Bind views by ButterKnife
    @BindView(R.id.activity_post_create)
    View rootView;

    /*
    @BindView(R.id.post_create_btn_0)
    Button btn0;

    @BindView(R.id.post_create_btn_1)
    Button btn1;

    @BindView(R.id.post_create_btn_next)
    Button btnNext;
    */

    @BindView(R.id.post_create_btn_cancel)
    TextView cancelPost;

    @BindView(R.id.post_create_btn_finish)
    TextView finishPost;

    @BindView(R.id.post_create_discard_audio)
    ImageView discardAudio;

    @BindView(R.id.post_create_finish_audio)
    ImageView finishAudio;

    @BindView(R.id.post_create_preview_audio)
    ImageView previewAudio;

    @BindView(R.id.post_create_rotate_anticlockwise)
    ImageView rotateAnticlockwise;

    @BindView(R.id.post_create_rotate_clockwise)
    ImageView rotateClockwise;

    @BindView(R.id.post_create_record_audio)
    ImageView recordAudio;

    @BindView(R.id.post_create_record_audio_hint)
    TextView recordAudioHint;

    @BindView(R.id.post_create_caption)
    EditText postCaption;

    @BindView(R.id.post_create_preview_area)
    RelativeLayout previewArea;

    @BindView(R.id.post_create_preview_image)
    ImageView previewImage;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDb;
    private DatabaseReference mDbRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    // Instance variables
    private FirebaseUserClass currentUser;
    private FirebasePost newPost;
    private FirebaseBubbleWrapper bubbleWrapper;
    private ArrayList<FirebaseBubbleWrapper> bubbleWrapperList;

    private Context localContext;
    private Resources localResources;
    private boolean appDirExist, audioReady, bubbleReady;
    private int bubbleCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_create);
        ButterKnife.bind(this);

        localContext = this.getApplicationContext();
        localResources = this.getResources();

        // Firebase instances initialization
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        mDbRef = mDb.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        // Fetch data from the previous Activity
        currentUser = getIntent().getParcelableExtra("currentUser");

        // Create new Post instance
        newPost = new FirebasePost(currentUser);

        bubbleWrapperList = new ArrayList<>();

        // Check if app folder already exists
        appDirExist = MainActivity.createAppDir();
        bubbleCounter = 0;

        // Start taking photo
        dispatchTakePictureIntent();
    }

    // Photo handling
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = ImageHelper.createImageFile(newPost.getCreatorUuid());
//            Uri photoUri = Uri.fromFile(photoFile);
            Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", photoFile);

            newPost.setPhotoUrl(photoFile.getAbsolutePath());
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private void galleryAddPic(String photoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File photoFile = new File(photoPath);
        Uri contentUri = Uri.fromFile(photoFile);
        mediaScanIntent.setData(contentUri);

        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    String photoPath = newPost.getPhotoUrl();
                    File originalImage = new File(photoPath);

                    // Add the photo name to the new Post instance
                    String photoName = originalImage.getName();
                    newPost.setPhotoName(photoName);

                    // Compress the photo
                    File compressedImage;
                    try {
                        compressedImage = ImageHelper.imageResize(originalImage, localContext);
                        newPost.setPhotoUrl(compressedImage.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Set the photo aspect ratio to the new post
                    newPost.setPhotoAspectRatio(ImageHelper.getImageAspectRatio(photoPath));

                    // Add the photo to the phone's gallery
                    galleryAddPic(photoPath);

                    // Load the photo into preview area
                    Glide.with(this)
                            .load(photoPath)
                            .into(previewImage);

                    // Load button icons
                    Glide.with(this)
                            .load(R.drawable.ic_cancel_grey_48px)
                            .into(discardAudio);

                    Glide.with(this)
                            .load(R.drawable.ic_check_circle_grey_48px)
                            .into(finishAudio);

                    Glide.with(this)
                            .load(R.drawable.ic_play_circle_filled_grey_48px)
                            .into(previewAudio);

                    Glide.with(this)
                            .load(R.drawable.ic_refresh_reflect_grey_48px)
                            .into(rotateAnticlockwise);

                    Glide.with(this)
                            .load(R.drawable.ic_refresh_grey_48px)
                            .into(rotateClockwise);

                    Glide.with(this)
                            .load(R.drawable.ic_mic_blue_48px)
                            .into(recordAudio);

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
    View.OnLongClickListener recordAudioStartListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (!appDirExist) {
                return false;
            }

            if (bubbleWrapper == null) {
                File audioFile = AudioHelper.createAudioFile(currentUser.getUuid());
                bubbleWrapper = new FirebaseBubbleWrapper(newPost.getPostID(), currentUser.getUserID());
                bubbleWrapper.setContext(localContext);
                bubbleWrapper.setAudioUrl(audioFile.getAbsolutePath());
                bubbleWrapper.setAudioName(audioFile.getName());
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
                    audioReady = true;

                    /*
                    btnNext.setText(localResources.getString(R.string.add_bubble));
                    btnNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            enterBubblePrepareStage();
                        }
                    });
                    */

                    addBubbleImage();
                    enterBubblePrepareStage();
                }
            }

            // This OnTouch event has to return false for the LonClick event to work
            return false;
        }
    };

    View.OnClickListener playAudioListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (audioReady) {
                AudioHelper.playAudioLocal(bubbleWrapper.getAudioUrl());
            }
        }
    };
    // End of audio handling

    // Speech bubble handling
    View.OnClickListener addBubbleImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (bubbleReady) {
                return;
            }

            /*
            switch (v.getId()) {
                case R.id.post_create_btn_0:
                    bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_SW);
                    break;
                case R.id.post_create_btn_1:
                    bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_SE);
                    break;
                default:
                    break;
            }
            */

            int targetW = localResources.getDimensionPixelSize(R.dimen.bubble_width);
            int targetH = localResources.getDimensionPixelSize(R.dimen.bubble_height);
            int centerX = (int) ((previewArea.getWidth() - targetW) * 0.5);
            int centerY = (int) ((previewArea.getHeight() - targetH) * 0.5);
            double xRatio = centerX / previewArea.getWidth();
            double yRatio = centerY / previewArea.getHeight();

            bubbleWrapper.addBubbleImage(centerX, centerY, previewArea, localResources, localContext);
//            bubbleWrapper.bindPlayLocalListener();
            bubbleWrapper.bindAdjustListener();

            // TODO: not sure what this is for
            bubbleReady = true;
        }
    };

    // TODO: for new UI
    private void addBubbleImage() {
        if (bubbleReady) {
            return;
        }

        bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_SW);
        int targetW = localResources.getDimensionPixelSize(R.dimen.bubble_width);
        int targetH = localResources.getDimensionPixelSize(R.dimen.bubble_height);
        int centerX = (int) ((previewArea.getWidth() - targetW) * 0.5);
        int centerY = (int) ((previewArea.getHeight() - targetH) * 0.5);

        bubbleWrapper.addBubbleImage(centerX, centerY, previewArea, localResources, localContext);
        bubbleWrapper.bindAdjustListener();

        // TODO: not sure what this is for
        bubbleReady = true;
    }

    View.OnClickListener finishBubbleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*
            if (!bubbleReady) {
                return;
            }
            */

            bubbleWrapper.setCreationDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            bubbleWrapper.setAdjustListener(null);
            bubbleWrapper.bindPlayLocalListener();
            bubbleWrapperList.add(bubbleWrapper);
            newPost.addBubble(bubbleWrapper.getBubble());

            // Clear current speech bubble
            bubbleWrapper = null;

            enterAudioPrepareStage();
        }
    };

    View.OnClickListener rotateBubbleAnticlockwiseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_N)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_NW);
            } else if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_NW)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_W);
            } else if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_W)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_SW);
            } else if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_SW)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_S);
            } else if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_S)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_SE);
            } else if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_SE)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_E);
            } else if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_E)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_NE);
            } else if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_NE)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_N);
            }
            bubbleWrapper.loadBubbleImage();
        }
    };

    View.OnClickListener rotateBubbleClockwiseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_N)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_NE);
            } else if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_NE)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_E);
            } else if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_E)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_SE);
            } else if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_SE)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_S);
            } else if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_S)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_SW);
            } else if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_SW)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_W);
            } else if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_W)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_NW);
            } else if (bubbleWrapper.getType().equals(FirebaseBubbleWrapper.TYPE_NW)) {
                bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_N);
            }
            bubbleWrapper.loadBubbleImage();
        }
    };
    // End of speech bubble handling

    // Finish creating post
    View.OnClickListener finishPostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            newPost.setCaption(postCaption.getText().toString());
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
                Snackbar.make(rootView, "Photo to storage failed", Snackbar.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads
                String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                newPost.setPhotoUrl(downloadUrl);

                Snackbar.make(rootView, "Photo to storage success", Snackbar.LENGTH_SHORT).show();

                if (newPost.getBubbleCount() > 0) {
                    for (int i=0; i<newPost.getBubbleCount(); i++) {
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
                Snackbar.make(rootView, "Audio to storage failed", Snackbar.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads
                String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                FirebaseBubble bubble = newPost.getBubble(i);
                bubble.setAudioUrl(downloadUrl);
                newPost.setBubble(i, bubble);

                Snackbar.make(rootView, "Audio to storage success", Snackbar.LENGTH_SHORT).show();

                bubbleCounter++;
                if (bubbleCounter == newPost.getBubbleCount()) {
                    bubbleCounter = 0;
                    uploadPostToFirebaseDatabase();
                }
            }
        });
    }

    private void uploadPostToFirebaseDatabase() {
//        DatabaseReference mPostRef = mDbRef.child("post").child(newPost.getPostID());

        // TODO: Use regex instead of chained replace()
        String postKey = "post_" + newPost.getCreationDate().replace(" ", "_").replace("-", "").replace(":", "");

        DatabaseReference mPostRef = mDbRef.child("post").child(postKey);
        mPostRef.setValue(newPost, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Snackbar.make(rootView, "Post to database failed", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(rootView, "Post to database success", Snackbar.LENGTH_SHORT).show();

                    // When upload in done
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    // State transition
    public void enterAudioPrepareStage() {
        audioReady = false;

        discardAudio.setVisibility(View.INVISIBLE);
        finishAudio.setVisibility(View.INVISIBLE);
        previewAudio.setVisibility(View.INVISIBLE);
        rotateAnticlockwise.setVisibility(View.INVISIBLE);
        rotateClockwise.setVisibility(View.INVISIBLE);

        recordAudio.setOnLongClickListener(recordAudioStartListener);
        recordAudio.setOnTouchListener(recordAudioStopListener);

        finishPost.setOnClickListener(finishPostListener);
    }

    public void enterBubblePrepareStage() {
        bubbleReady = false;

        discardAudio.setVisibility(View.VISIBLE);
        finishAudio.setVisibility(View.VISIBLE);
        previewAudio.setVisibility(View.VISIBLE);
        rotateAnticlockwise.setVisibility(View.VISIBLE);
        rotateClockwise.setVisibility(View.VISIBLE);

        finishAudio.setOnClickListener(finishBubbleListener);
        previewAudio.setOnClickListener(playAudioListener);
        rotateAnticlockwise.setOnClickListener(rotateBubbleAnticlockwiseListener);
        rotateClockwise.setOnClickListener(rotateBubbleClockwiseListener);

        finishPost.setOnClickListener(null);
    }
}
