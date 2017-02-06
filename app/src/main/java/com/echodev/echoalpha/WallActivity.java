package com.echodev.echoalpha;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.echodev.echoalpha.util.audioHelper;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class WallActivity extends AppCompatActivity {
    private static final String LOG_TAG = "Record_audio_message";

    @BindView(android.R.id.content)
    View mRootView;

    @BindView(R.id.identity_text)
    TextView IDText;

    @BindView(R.id.sign_out_btn)
    Button signOutBtn;

    @BindView(R.id.camera_btn)
    Button cameraBtn;

    @BindView(R.id.view_btn)
    Button viewBtn;

    @BindView(R.id.record_btn)
    Button recordBtn;

    @BindView(R.id.play_btn)
    Button playBtn;

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer = new MediaPlayer();
    private String audioFileName;
    private boolean createDirSuccess = true;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private IdpResponse mIdpResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            startActivity(MainActivity.createIntent(this));
            finish();
            return;
        }

        mIdpResponse = IdpResponse.fromResultIntent(getIntent());

        setContentView(R.layout.activity_wall);
        ButterKnife.bind(this);

        String postID = UUID.randomUUID().toString();

        audioHelper mAudio = new audioHelper(mUser.getUid(), postID);

        populateProfile(postID);
        populateIdpToken();

        createAppDir();
    }

    @OnClick(R.id.sign_out_btn)
    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startMain();
                        } else {
                            showSnackbar(R.string.sign_out_failed);
                        }
                    }
                });
    }

    @MainThread
    private void populateProfile(String postID) {
        String currentUid = mUser.getUid();
        String currentEmail = mUser.getEmail();

        IDText.setText("You have signed in as\n" + currentUid + "\n" + currentEmail + "\n" + postID);

        /*
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .fitCenter()
                    .into(mUserProfilePicture);
        }

        mUserEmail.setText(
                TextUtils.isEmpty(user.getEmail()) ? "No email" : user.getEmail());
        mUserDisplayName.setText(
                TextUtils.isEmpty(user.getDisplayName()) ? "No display name" : user.getDisplayName());

        StringBuilder providerList = new StringBuilder(100);

        providerList.append("Providers used: ");

        if (user.getProviders() == null || user.getProviders().isEmpty()) {
            providerList.append("none");
        } else {
            Iterator<String> providerIter = user.getProviders().iterator();
            while (providerIter.hasNext()) {
                String provider = providerIter.next();
                if (GoogleAuthProvider.PROVIDER_ID.equals(provider)) {
                    providerList.append("Google");
                } else if (FacebookAuthProvider.PROVIDER_ID.equals(provider)) {
                    providerList.append("Facebook");
                } else if (EmailAuthProvider.PROVIDER_ID.equals(provider)) {
                    providerList.append("Password");
                } else {
                    providerList.append(provider);
                }

                if (providerIter.hasNext()) {
                    providerList.append(", ");
                }
            }
        }

        mEnabledProviders.setText(providerList);
        */
    }

    private void populateIdpToken() {
        /*
        String token = null;
        String secret = null;
        if (mIdpResponse != null) {
            token = mIdpResponse.getIdpToken();
            secret = mIdpResponse.getIdpSecret();
        }
        if (token == null) {
            findViewById(R.id.idp_token_layout).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.idp_token)).setText(token);
        }
        if (secret == null) {
            findViewById(R.id.idp_secret_layout).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.idp_secret)).setText(secret);
        }
        */
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG)
                .show();
    }

    private void createAppDir() {
        File appDir = new File(Environment.getExternalStorageDirectory() + "/" + R.string.app_name);
        if (!appDir.exists()) {
            createDirSuccess = appDir.mkdir();
        }

        if (createDirSuccess) {
            audioFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            audioFileName += "/" + R.string.app_name + "/" + mUser.getUid() + R.string.audio_format;
        }
    }

    @OnTouch(R.id.record_btn)
    public boolean controlRecording(View view, MotionEvent motionEvent) {
        if (createDirSuccess && motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            // Start recording

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(audioFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
            mRecorder.start();

            return true;
        } else if (createDirSuccess && motionEvent.getAction() == MotionEvent.ACTION_UP) {
            // Stop recording

            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;

            return true;
        }

        return false;
    }

    @OnClick(R.id.play_btn)
    public void playAudioLocal(View view) {
        if (!createDirSuccess) {
            return;
        }

        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        filePath += "/" + R.string.app_name + "/" + mUser.getUid() + R.string.audio_format;

        File appFile = new File(filePath);
        if (appFile.exists()) {
            mPlayer.reset();
            mPlayer = new MediaPlayer();
//            mp.release();
            try {
                mPlayer.setDataSource(filePath);
                mPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.start();
        }
    }

    private void startMain() {
        startActivity(MainActivity.createIntent(WallActivity.this));
        finish();
    }

    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent intent = IdpResponse.getIntent(idpResponse);
        intent.setClass(context, WallActivity.class);
        return intent;
    }
}
