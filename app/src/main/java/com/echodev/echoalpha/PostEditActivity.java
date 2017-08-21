package com.echodev.echoalpha;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostEditActivity extends AppCompatActivity {

    // Debug log
    private static final String LOG_TAG = "Echo_Alpha_Post_Edit";

    // Bind views by ButterKnife
    @BindView(R.id.activity_post_edit)
    View rootView;

    /*
    @BindView(R.id.post_edit_btn_0)
    Button btn0;

    @BindView(R.id.post_edit_btn_1)
    Button btn1;

    @BindView(R.id.post_edit_btn_next)
    Button btnNext;
    */

    @BindView(R.id.post_edit_cancel_post)
    ImageView cancelPost;

    @BindView(R.id.post_edit_finish_post)
    ImageView finishPost;

    @BindView(R.id.post_edit_discard_audio)
    ImageView discardAudio;

    @BindView(R.id.post_edit_finish_audio)
    ImageView finishAudio;

    @BindView(R.id.post_edit_preview_audio)
    ImageView previewAudio;

    @BindView(R.id.post_edit_rotate_anticlockwise)
    ImageView rotateAnticlockwise;

    @BindView(R.id.post_edit_rotate_clockwise)
    ImageView rotateClockwise;

    @BindView(R.id.post_edit_record_audio)
    ImageView recordAudio;

    @BindView(R.id.post_edit_caption)
    TextView postCaption;

    @BindView(R.id.post_edit_preview_area)
    RelativeLayout previewArea;

    @BindView(R.id.post_edit_preview_image)
    ImageView previewImg;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDb;
    private DatabaseReference mDbRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    // Instance variables
    private FirebaseUserClass currentUser;
    private FirebasePost currentPost;
    private FirebaseBubbleWrapper bubbleWrapper;
    private ArrayList<FirebaseBubbleWrapper> bubbleWrapperList;

    private Context localContext;
    private Resources localResources;
    private boolean appDirExist, audioReady, bubbleReady, postChanged;
    private int originalBubbleCount, storageBubbleCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_edit);
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

        currentUser = new FirebaseUserClass(mUser);

        // Fetch data from the previous Activity
        currentPost = getIntent().getParcelableExtra("currentPost");

        if (currentPost.getBubbleList() == null) {
            currentPost.setBubbleList(new ArrayList<FirebaseBubble>());
        }

        if (currentPost.getCollaboratorIDList() == null) {
            currentPost.setCollaboratorIDList(new ArrayList<String>());
        }

        bubbleWrapperList = new ArrayList<>();

        // Check if app folder already exists
        appDirExist = MainActivity.createAppDir();
        postChanged = false;

        originalBubbleCount = currentPost.getBubbleCount();
        storageBubbleCounter = 0;

        preparePost();
    }

    // Post handling
    private void preparePost() {
        postCaption.setText(currentPost.getCaption());

        // Load the photo into preview area
        Glide.with(this)
                .load(currentPost.getPhotoUrl())
                .into(previewImg);

        // Load button icons
        Glide.with(this)
                .load(R.drawable.icons8_close_window_48)
                .into(cancelPost);

        Glide.with(this)
                .load(R.drawable.icons8_checked_checkbox_48)
                .into(finishPost);

        Glide.with(this)
                .load(R.drawable.icons8_cancel_64)
                .into(discardAudio);

        Glide.with(this)
                .load(R.drawable.icons8_checked_48)
                .into(finishAudio);

        Glide.with(this)
                .load(R.drawable.icons8_circled_play_48)
                .into(previewAudio);

        Glide.with(this)
                .load(R.drawable.icons8_rotate_anticlockwise_48)
                .into(rotateAnticlockwise);

        Glide.with(this)
                .load(R.drawable.icons8_rotate_clockwise_48)
                .into(rotateClockwise);

        Glide.with(this)
                .load(R.drawable.ic_album_blue_48px)
                .into(recordAudio);

        // Add the original speech bubbles
        if (currentPost.getBubbleList()!=null && !currentPost.getBubbleList().isEmpty()) {
            for (FirebaseBubble bubble : currentPost.getBubbleList()) {
                FirebaseBubbleWrapper bubbleWrapper = new FirebaseBubbleWrapper(bubble);
                bubbleWrapper.setContext(localContext);
                bubbleWrapper.initAudioPlayer();

                // Convert dp back to px for display
                int positionX = ImageHelper.convertDpToPx((int) bubble.getX(), localContext);
                int positionY = ImageHelper.convertDpToPx((int) bubble.getY(), localContext);

                // Get the coordinate ratio of the bubble
                double xRatio = bubble.getXRatio();
                double yRatio = bubble.getYRatio();

                // Get the parent container width and height
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int screenWidth = displayMetrics.widthPixels;
                int parentHeight = (int) Math.round(screenWidth / currentPost.getPhotoAspectRatio());

//                positionX = (int) Math.round(xRatio * screenWidth);
//                positionY = (int) Math.round(yRatio * parentHeight);

                bubbleWrapper.addBubbleImage(positionX, positionY, previewArea, localResources, localContext);
//                bubbleWrapper.addBubbleImageByRatio(xRatio, yRatio, screenWidth, parentHeight, previewArea, localResources, localContext);

                bubbleWrapper.bindPlayListener();
            }
        }

        enterAudioPrepareStage();
    }
    // End of post handling

    // Audio handling
    View.OnLongClickListener recordAuioStartListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (!appDirExist) {
                return false;
            }

            if (bubbleWrapper == null) {
                bubbleWrapper = new FirebaseBubbleWrapper(currentPost.getPostID(), currentUser.getUserID());
                bubbleWrapper.setContext(localContext);
                bubbleWrapper.setAudioUrl(AudioHelper.createAudioFile(localResources, currentUser.getUserID()));
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

                    bubbleWrapper.setAudioName(Uri.parse(bubbleWrapper.getAudioUrl()).getLastPathSegment());

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
                case R.id.post_edit_btn_0:
                    bubbleWrapper.setType(FirebaseBubbleWrapper.TYPE_SW);
                    break;
                case R.id.post_edit_btn_1:
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

            bubbleWrapper.addBubbleImage(centerX, centerY, previewArea, localResources, localContext);
            bubbleWrapper.bindAdjustListener();

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
            currentPost.addBubble(bubbleWrapper.getBubble());
            postChanged = true;

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

    // Finish editing post
    View.OnClickListener finishPostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!postChanged) {
                return;
            }

            if (!currentUser.getUserID().equals(currentPost.getCreatorID()) && currentPost.indexOfCollaboratorID(currentUser.getUserID())==-1) {
                currentPost.addCollaboratorID(currentUser.getUserID());
            }

            // Upload everything to Firebase
            if (currentPost.getBubbleCount() > originalBubbleCount) {
                for (int i=originalBubbleCount; i<currentPost.getBubbleCount(); i++) {
                    uploadAudioToFirebaseStorage(i);
                }
            } else {
                updatePostToFirebaseDatabase();
            }
        }
    };

    private void uploadAudioToFirebaseStorage(final int i) {
        StorageReference audioRef = mStorageRef.child("audio/" + currentPost.getBubble(i).getAudioName());
        Uri audioUri = Uri.fromFile(new File(currentPost.getBubble(i).getAudioUrl()));
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

                FirebaseBubble bubble = currentPost.getBubble(i);
                bubble.setAudioUrl(downloadUrl);
                currentPost.setBubble(i, bubble);

                Snackbar.make(rootView, "Audio to storage success", Snackbar.LENGTH_SHORT).show();

                if (++storageBubbleCounter == currentPost.getBubbleCount() - originalBubbleCount) {
                    storageBubbleCounter = 0;
                    updatePostToFirebaseDatabase();
                }
            }
        });
    }

    private void updatePostToFirebaseDatabase() {
        // TODO: Use regex instead of chained replace()
        String postKey = "post_" + currentPost.getCreationDate().replace(" ", "_").replace("-", "").replace(":", "");

        Map<String, Object> postUpdates = new HashMap<>();
        postUpdates.put(postKey, currentPost);

        DatabaseReference mPostListRef = mDbRef.child("post");
        mPostListRef.updateChildren(postUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Snackbar.make(rootView, "Post to database failed", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(rootView, "Post to database success", Snackbar.LENGTH_SHORT).show();

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

        recordAudio.setOnLongClickListener(recordAuioStartListener);
        recordAudio.setOnTouchListener(recordAudioStopListener);

        finishPost.setOnClickListener(finishPostListener);

        /*
        btn0.setText(localResources.getString(R.string.record));
        btn0.setOnClickListener(null);
        btn0.setOnLongClickListener(recordAuioStartListener);
        btn0.setOnTouchListener(recordAudioStopListener);

        btn1.setText(localResources.getString(R.string.play));
        btn1.setOnClickListener(playAudioListener);

        btnNext.setText(localResources.getString(R.string.finish));
        btnNext.setOnClickListener(finishPostListener);
        */
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

        /*
        btn0.setText(localResources.getString(R.string.add_bubble_left));
        btn0.setOnLongClickListener(null);
        btn0.setOnTouchListener(null);
        btn0.setOnClickListener(addBubbleImageListener);

        btn1.setText(localResources.getString(R.string.add_bubble_right));
        btn1.setOnClickListener(addBubbleImageListener);

        btnNext.setText(localResources.getString(R.string.finish_bubble));
        btnNext.setOnClickListener(finishBubbleListener);
        */
    }
}
