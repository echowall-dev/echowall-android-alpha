package com.echodev.echoalpha;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Environment;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Echo_Alpha_Main";

    // Request code
    public static final int RC_SIGN_IN = 100;
    public static final int REQUEST_CODE_All_PERMISSIONS = 101;

    // Bind views by ButterKnife
    @BindView(R.id.activity_main)
    View mRootView;

    @BindView(R.id.cover_image)
    ImageView coverImage;

    @BindView(R.id.sign_in_btn)
    Button signInBtn;

    @BindView(R.id.quit_btn)
    Button quitBtn;

    // Instance variables
    private Resources localResources;
    private static String appName;
    private IdpResponse mIdpResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        localResources = this.getResources();
        appName = localResources.getString(R.string.app_name);

        // Load the cover image
        Glide.with(this)
                .load(R.drawable.cover_lowres)
                .asBitmap()
                .into(coverImage);

        requestPermission();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // User is signed in
            startWall();
        } else {
            // User is signed out
        }
    }

    // Prepare app directory
    public static boolean createAppDir() {
        boolean createSuccess = true;

        File appDirPicture = new File(Environment.getExternalStorageDirectory() + "/" + appName + "/picture");
        File appDirAudio = new File(Environment.getExternalStorageDirectory() + "/" + appName + "/audio");

        // Use mkdirs() here instead of mkdir() for creating parent directories.
        if (!appDirPicture.exists()) {
            createSuccess = appDirPicture.mkdirs();
        }

        if (!appDirAudio.exists()) {
            createSuccess = appDirAudio.mkdirs();
        }

        return createSuccess;
    }

    private void requestPermission() {
        String[] permissionReqList = {
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int[] permissionCheckArray = new int[3];
        Arrays.fill(permissionCheckArray, -1);
        permissionCheckArray[0] = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        permissionCheckArray[1] = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        permissionCheckArray[2] = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        boolean allPermissionGranted = true;
        for (int permissionCheck : permissionCheckArray) {
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                allPermissionGranted = false;
                break;
            }
        }

        if (!allPermissionGranted) {
            ActivityCompat.requestPermissions(this, permissionReqList, REQUEST_CODE_All_PERMISSIONS);
        } else {
            createAppDir();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_All_PERMISSIONS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    createAppDir();
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                break;
            default:
                break;
            // other 'case' lines to check for other permissions this app might request
        }
    }

    @OnClick(R.id.sign_in_btn)
    public void signIn(View view) {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(R.style.AlphaTheme)
                        .setLogo(R.drawable.app_logo_cut)
                        .setProviders(Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);

//        startWall();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }

        showSnackbar(R.string.unknown_response);
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        mIdpResponse = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == ResultCodes.OK) {
            startActivity(WallActivity.createIntent(this, mIdpResponse));
            finish();
            return;
        } else {
            // Sign in failed
            if (mIdpResponse == null) {
                // User pressed back button
                showSnackbar(R.string.sign_in_cancelled);
                return;
            }

            if (mIdpResponse.getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_internet_connection);
                return;
            }

            if (mIdpResponse.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showSnackbar(R.string.unknown_error);
                return;
            }
        }

        showSnackbar(R.string.unknown_sign_in_response);
    }

    private void startWall() {
//        Intent intent = new Intent(this, WallActivity.class);
//        startActivity(intent);
        startActivity(WallActivity.createIntent(this, null));
        finish();
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        return intent;
    }

    @OnClick(R.id.quit_btn)
    public void quitApp(View view) {
        this.finishAffinity();
    }
}
