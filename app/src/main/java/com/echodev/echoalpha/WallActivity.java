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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;

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
    private FirebaseUser currentUser;
    private IdpResponse mIdpResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(MainActivity.createIntent(this));
            finish();
            return;
        }

        mIdpResponse = IdpResponse.fromResultIntent(getIntent());

        setContentView(R.layout.activity_wall);
        ButterKnife.bind(this);

//        populateProfile();
//        populateIdpToken();

        IDText.setText("You have signed in as\n" + currentUser.getUid() + "\n" + currentUser.getEmail());

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
    private void populateProfile() {

    }

    private void populateIdpToken() {

    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG)
                .show();
    }

    private void createAppDir() {
        File appDir = new File(Environment.getExternalStorageDirectory() + "/EchoAlpha");
        if (!appDir.exists()) {
            createDirSuccess = appDir.mkdir();
        }

        if (createDirSuccess) {
            audioFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            audioFileName += "/EchoAlpha/" + currentUser.getUid() + ".3gp";
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
        filePath += "/EchoAlpha/" + currentUser.getUid() + ".3gp";

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
