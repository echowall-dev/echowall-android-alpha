package com.echodev.echoalpha;

import android.content.Context;
import android.content.Intent;
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

import com.echodev.echoalpha.firebase.FirebasePost;
import com.echodev.echoalpha.firebase.FirebasePostAdapter;
import com.echodev.echoalpha.firebase.FirebaseUserClass;
import com.echodev.echoalpha.util.PostAdapter;
import com.echodev.echoalpha.util.PostClass;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WallActivity extends AppCompatActivity {

    // Debug log
    private static final String LOG_TAG = "Echo_Alpha_Wall";

    // Request code for creating new post
    public static final int REQUEST_CODE_POST = 110;
    public static final int REQUEST_CODE_POST_CREATE = 111;
    private int activityRequestCode;

    // Instance variables
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private IdpResponse mIdpResponse;
    private FirebaseUserClass firebaseUser;

    private FirebaseDatabase mDb;
    private DatabaseReference mDbRef;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    private PostAdapter postAdapter;
    private FirebasePostAdapter firebasePostAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityRequestCode = REQUEST_CODE_POST_CREATE;

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        mDbRef = mDb.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference();

        if (mUser == null) {
            startMain();
            return;
        } else {
            firebaseUser = new FirebaseUserClass(
                    mUser.getUid(),
                    mUser.getEmail(),
                    mUser.getDisplayName()
            );

            if (mUser.getPhotoUrl() != null) {
                firebaseUser.setProPicUrl(mUser.getPhotoUrl().toString());
            }

            // Push the user data to Firebase database if it has not been stored
            DatabaseReference mUserRef = mDbRef.child("user").child(firebaseUser.getUserID());
            mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()) {
                        mDbRef.child("user").child(firebaseUser.getUserID()).setValue(firebaseUser);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        setContentView(R.layout.activity_wall);
        ButterKnife.bind(this);

        // Use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        postListArea.setLayoutManager(linearLayoutManager);

        // specify an adapter
        if (activityRequestCode == REQUEST_CODE_POST) {
            postAdapter = new PostAdapter(this.getResources(), this.getApplicationContext());
            postListArea.setAdapter(postAdapter);
        } else {
            firebasePostAdapter = new FirebasePostAdapter(this.getResources(), this.getApplicationContext());
            postListArea.setAdapter(firebasePostAdapter);
        }

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
    }

    @MainThread
    private void populateProfile() {
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
        /*
        Bundle bundle = new Bundle();
        bundle.putString("userID", firebaseUser.getUserID());
        bundle.putString("userEmail", firebaseUser.getUserEmail());
        bundle.putString("userName", firebaseUser.getUserName());
        */

        Intent intent = new Intent();
//        intent.putExtras(bundle);
        intent.putExtra("currentUser", firebaseUser);

        if (activityRequestCode == REQUEST_CODE_POST) {
            intent.setClass(this, PostActivity.class);
        } else if (activityRequestCode == REQUEST_CODE_POST_CREATE) {
            intent.setClass(this, PostCreateActivity.class);
        }

        startActivityForResult(intent, activityRequestCode);
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
                    String filePathInfo = "file location:\n" + appPathInfo;
                    IDTextView.setText(filePathInfo);

                    // Add the new post into the dataset of the RecyclerView Adapter
                    postAdapter.addPost(newPost);
                    postAdapter.notifyItemInserted(postAdapter.getPostList().size() - 1);
                    postListArea.scrollToPosition(postAdapter.getPostList().size() - 1);
                }
                break;
            case REQUEST_CODE_POST_CREATE:
                if (resultCode == RESULT_OK) {
                    // Show files path info on the page
                    String appName = this.getResources().getString(R.string.app_name);
                    String appPathInfo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + appName + "/";
                    String filePathInfo = "file location:\n" + appPathInfo;
                    IDTextView.setText(filePathInfo);
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
