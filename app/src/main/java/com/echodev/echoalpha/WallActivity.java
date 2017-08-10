package com.echodev.echoalpha;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WallActivity extends AppCompatActivity {

    // Debug log
    private static final String LOG_TAG = "Echo_Alpha_Wall";

    // Request code for creating new post
    public static final int REQUEST_CODE_POST = 110;
    public static final int REQUEST_CODE_POST_CREATE = 111;
    public static final int REQUEST_CODE_POST_EDIT = 112;
    public static final int REQUEST_CODE_POSTCARD_CREATE = 113;
    private int activityRequestCode;

    // Bind views by ButterKnife
    @BindView(android.R.id.content)
    View rootView;

    /*
    @BindView(R.id.identity_text)
    TextView IDTextView;
    */

    @BindView(R.id.post_list_area)
    RecyclerView postListArea;

    // Instance variables
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private IdpResponse mIdpResponse;
    private FirebaseUserClass currentUser;

    private FirebaseDatabase mDb;
    private DatabaseReference mDbRef;
    private DatabaseReference mUserRef;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    private PostAdapter postAdapter;
    private FirebasePostAdapter firebasePostAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startMain();
                    return true;
                case R.id.navigation_create:
                    startCreatePost();
                    return true;
                case R.id.navigation_logout:
                    signOut();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);
        ButterKnife.bind(this);

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
            currentUser = new FirebaseUserClass(mUser);

            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            // Push the user data to Firebase database if it has not been stored
            mUserRef = mDbRef.child("user").child(currentUser.getUserID());
            mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()) {
                        mDbRef.child("user").child(currentUser.getUserID()).setValue(currentUser);
                    } else {
                        FirebaseUserClass firebaseUser = dataSnapshot.getValue(FirebaseUserClass.class);

                        if (firebaseUser.getUserName() != null) {
                            if (!firebaseUser.getUserName().equals(currentUser.getUserName()) || firebaseUser.getUserName().isEmpty()) {
                                Map<String, Object> userUpdates = new HashMap<>();
                                userUpdates.put("userName", currentUser.getUserName());

                                mUserRef.updateChildren(userUpdates);
                            }
                        } else {
                            mUserRef.child("userName").setValue(currentUser.getUserName());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

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

        ValueEventListener fetchPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0 && firebasePostAdapter.getItemCount()==0) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        FirebasePost post = postSnapshot.getValue(FirebasePost.class);
                        firebasePostAdapter.addPost(post);
//                        firebasePostAdapter.notifyItemInserted(firebasePostAdapter.getItemCount() - 1);
//                        postListArea.scrollToPosition(firebasePostAdapter.getItemCount() - 1);
                    }
                    firebasePostAdapter.notifyDataSetChanged();
                    postListArea.scrollToPosition(firebasePostAdapter.getItemCount() - 1);
                    // TODO: Debug not scrolling to the newest post
                }

//                if (firebasePostAdapter.getItemCount() > 0) {
//                    postListArea.scrollToPosition(firebasePostAdapter.getItemCount() - 1);
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        ChildEventListener changePostListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FirebasePost post = dataSnapshot.getValue(FirebasePost.class);

                // TODO: Debug return -1 even post exists
//                int position = firebasePostAdapter.indexOfPost(post);
//                Log.d(LOG_TAG, "post index: " + position);

                int position = firebasePostAdapter.indexOfPostbyID(post.getPostID());

                if (position < 0) {
                    firebasePostAdapter.addPost(post);
                    firebasePostAdapter.notifyItemInserted(firebasePostAdapter.getItemCount() - 1);
                    postListArea.scrollToPosition(firebasePostAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FirebasePost post = dataSnapshot.getValue(FirebasePost.class);

                int position = firebasePostAdapter.indexOfPostbyID(post.getPostID());
//                Log.d(LOG_TAG, "post ID: " + post.getPostID());
//                Log.d(LOG_TAG, "post index: " + position);

                if (position >= 0) {
                    firebasePostAdapter.setPost(position, post);
                    firebasePostAdapter.notifyItemChanged(position);
                    postListArea.scrollToPosition(position);
                } else {
                    firebasePostAdapter.addPost(post);
                    firebasePostAdapter.notifyItemInserted(firebasePostAdapter.getItemCount() - 1);
                    postListArea.scrollToPosition(firebasePostAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                FirebasePost post = dataSnapshot.getValue(FirebasePost.class);

                // TODO: Debug return -1 even post exists
//                int position = firebasePostAdapter.indexOfPost(post);
//                firebasePostAdapter.removePost(post);

                int position = firebasePostAdapter.indexOfPostbyID(post.getPostID());
//                Log.d(LOG_TAG, "post index: " + position);

                firebasePostAdapter.removePost(position);
                firebasePostAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        DatabaseReference mPostRef = mDbRef.child("post");
        mPostRef.addValueEventListener(fetchPostListener);
        mPostRef.addChildEventListener(changePostListener);

        mIdpResponse = IdpResponse.fromResultIntent(getIntent());
        populateProfile();
        populateIdpToken();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

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
        /*
        String contentText = "";
        contentText += "You have signed in as";
        contentText += "\n" + currentUser.getUserName();
        contentText += "\n" + currentUser.getUserEmail();

        IDTextView.setText(contentText);
        */

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
        Snackbar.make(rootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    public void startCreatePost() {
        Intent intent = new Intent();
//        intent.putExtras(bundle);
        intent.putExtra("currentUser", currentUser);

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

                    /*
                    // Show files path info on the page
                    String appName = this.getResources().getString(R.string.app_name);
                    String appPathInfo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + appName + "/";
                    String filePathInfo = "file location:\n" + appPathInfo;
                    IDTextView.setText(filePathInfo);
                    */

                    // Add the new post into the dataset of the RecyclerView Adapter
                    postAdapter.addPost(newPost);
                    postAdapter.notifyItemInserted(postAdapter.getPostList().size() - 1);
                    postListArea.scrollToPosition(postAdapter.getPostList().size() - 1);
                }
                break;
            case REQUEST_CODE_POST_CREATE:
                if (resultCode == RESULT_OK) {
                    /*
                    // Show files path info on the page
                    String appName = this.getResources().getString(R.string.app_name);
                    String appPathInfo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + appName + "/";
                    String filePathInfo = "file location:\n" + appPathInfo;
                    IDTextView.setText(filePathInfo);
                    */

                    Snackbar.make(rootView, "Post added", Snackbar.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_POST_EDIT:
                if (resultCode == RESULT_OK) {
                    /*
                    String appName = this.getResources().getString(R.string.app_name);
                    String appPathInfo = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + appName + "/";
                    String filePathInfo = "file location:\n" + appPathInfo;
                    IDTextView.setText(filePathInfo);
                    */

                    Snackbar.make(rootView, "Post changed", Snackbar.LENGTH_SHORT).show();
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
