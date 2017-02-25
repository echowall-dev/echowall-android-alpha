package com.echodev.echoalpha;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echodev.echoalpha.util.ImageHelper;
import com.echodev.echoalpha.util.PostClass;
import com.echodev.echoalpha.util.SpeechBubble;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WallActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Record_audio_message";
    private static final int REQUEST_CODE_POST = 110;

    @BindView(android.R.id.content)
    View mRootView;

    @BindView(R.id.identity_text)
    TextView IDText;

    @BindView(R.id.sign_out_btn)
    Button signOutBtn;

    @BindView(R.id.create_post_btn)
    Button createPostBtn;

    @BindView(R.id.post_append_area)
    LinearLayout postAppendArea;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private IdpResponse mIdpResponse;

    private Resources localRes;
    private String postID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            startMain();
            return;
        }

        mIdpResponse = IdpResponse.fromResultIntent(getIntent());

        setContentView(R.layout.activity_wall);
        ButterKnife.bind(this);

        localRes = this.getResources();
        postID = UUID.randomUUID().toString();

        populateProfile(postID);
        populateIdpToken();

        // Add files path info at the bottom of the page
        String photoPathInfo = "photo";
        String audioPathInfo = "audio";
        String filePathInfo = "photo location:\n" + photoPathInfo + "\n\naudio location:\n" + audioPathInfo;
        TextView filePathInfoTextView = (TextView) findViewById(R.id.file_path_info);
        filePathInfoTextView.setText(filePathInfo);
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String contentText = "";
        contentText += "You have signed in as";
        contentText += "\n" + currentEmail;
        contentText += "\n" + currentUid;

        IDText.setText(contentText);

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

    @OnClick(R.id.create_post_btn)
    public void startCreatePost() {
        Bundle bundle = new Bundle();
        bundle.putString("userID", mUser.getUid());
        bundle.putString("userEmail", mUser.getEmail());
        bundle.putString("postID", postID);

        Intent intent = new Intent();
        intent.setClass(this, PostActivity.class);
        intent.putExtras(bundle);

        startActivityForResult(intent, REQUEST_CODE_POST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_POST:
                if (resultCode == RESULT_OK) {
                    PostClass newPost = (PostClass) data.getParcelableExtra("newPost");
                    addPost(postAppendArea, newPost);
                }
                break;
            default:
                break;
        }
    }

    private void addPost(ViewGroup postAppendArea, PostClass newPost) {
        // Fetch the data of the new post
        SpeechBubble newSpeechBubble = newPost.getSpeechBubble(0);

        // Prepare an empty post
        View view = LayoutInflater.from(postAppendArea.getContext()).inflate(R.layout.post_layout, postAppendArea, true);

        // Prepare the views of the post
        TextView postUserProfile = (TextView) view.findViewById(R.id.post_user_profile);
        RelativeLayout postImageArea = (RelativeLayout) view.findViewById(R.id.post_image_area);
        ImageView postImage = (ImageView) view.findViewById(R.id.post_image);
        TextView postLikeNumber = (TextView) view.findViewById(R.id.post_like_number);
        TextView postCaption = (TextView) view.findViewById(R.id.post_caption);
        TextView postCreationTime = (TextView) view.findViewById(R.id.post_creation_time);

        // Set template info
        postUserProfile.setText(mUser.getEmail());
        postLikeNumber.setText("0 Like");
        postCaption.setText("Peter:\nWhat a beautiful day!");
        postCreationTime.setText("23/02/2017");

        // Add the photo
//        ImageHelper.setPicFromFile(postImage, photoPath);
        ImageHelper.setPicFromFile(postImage, postAppendArea.getWidth(), newPost.getPhotoPath());

        // Add the speech bubble at target position
        newSpeechBubble.addBubbleImage(newSpeechBubble.getX(), newSpeechBubble.getY(), postImageArea, localRes, this);
        newSpeechBubble.bindPlayListener();
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
