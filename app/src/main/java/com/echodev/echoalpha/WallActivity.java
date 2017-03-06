package com.echodev.echoalpha;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.echodev.echoalpha.firebase.FirebaseUserClass;
import com.echodev.echoalpha.util.PostAdapter;
import com.echodev.echoalpha.util.PostClass;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WallActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Echo_Alpha_Wall";

    // Request code for creating new post
    public static final int REQUEST_CODE_POST = 110;

    // Bind views by ButterKnife
    @BindView(android.R.id.content)
    View mRootView;

    @BindView(R.id.identity_text)
    TextView IDTextView;

    @BindView(R.id.sign_out_btn)
    Button signOutBtn;

    @BindView(R.id.create_post_btn)
    Button createPostBtn;

    @BindView(R.id.post_list_area)
    RecyclerView postListArea;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private IdpResponse mIdpResponse;
    FirebaseUserClass firebaseUser;

    private FirebaseDatabase mDb;
    private DatabaseReference mDbRef;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser == null) {
            startMain();
            return;
        } else {
            // TODO: Push the user info to Firebase database if it has not been stored
            firebaseUser = new FirebaseUserClass(
                    mUser.getUid(),
                    mUser.getEmail(),
                    mUser.getDisplayName()
            );
            if (mUser.getPhotoUrl() != null) {
                firebaseUser.setProPicUrl(mUser.getPhotoUrl().toString());
            }
        }

        mDb = FirebaseDatabase.getInstance();
        mDbRef = mDb.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        setContentView(R.layout.activity_wall);
        ButterKnife.bind(this);

        // Use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        postListArea.setLayoutManager(linearLayoutManager);

        // specify an adapter
        postAdapter = new PostAdapter(this.getResources(), this.getApplicationContext());
        postListArea.setAdapter(postAdapter);

        mIdpResponse = IdpResponse.fromResultIntent(getIntent());
        populateProfile();
        populateIdpToken();
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

//        startMain();

//        String audioUrl = "https://firebasestorage.googleapis.com/v0/b/echoalpha-a289a.appspot.com/o/audio%2Fvoice_170305.3gp?alt=media&token=c19de445-513c-4ab7-8a49-dc940d2684f9";
//        AudioHelper.playAudioOnline(audioUrl);
    }

    @MainThread
    private void populateProfile() {
//        String userID = "user001";
//        String userEmail = "user001@echowall.com";

        String contentText = "";
        contentText += "You have signed in as";
        contentText += "\n" + firebaseUser.getUserName();
        contentText += "\n" + firebaseUser.getUserEmail();
        contentText += "\n" + firebaseUser.getUserID();

        IDTextView.setText(contentText);

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
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    @OnClick(R.id.create_post_btn)
    public void startCreatePost() {
//        String userID = "user001";
//        String userEmail = "user001@echowall.com";

        Bundle bundle = new Bundle();
        bundle.putString("userID", firebaseUser.getUserID());
        bundle.putString("userEmail", firebaseUser.getUserEmail());
        bundle.putString("userName", firebaseUser.getUserName());

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
                    newPost.setWidth(postListArea.getWidth());

                    // Show files path info on the page
                    String appName = this.getResources().getString(R.string.app_name);
                    String appPathInfo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + appName + "/";
                    String photoPathInfo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + appName + "/picture/";
                    String audioPathInfo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + appName + "/audio/";
//                    String filePathInfo = "photo location:\n" + photoPathInfo + "\naudio location:\n" + audioPathInfo;
                    String filePathInfo = "file location:\n" + appPathInfo;
                    IDTextView.setText(filePathInfo);

                    // Add the new post into the dataset of the RecyclerView Adapter
                    postAdapter.addPost(newPost);
                    postAdapter.notifyItemInserted(postAdapter.getPostList().size() - 1);
                    postListArea.scrollToPosition(postAdapter.getPostList().size() - 1);
                }
                break;
            default:
                break;
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
