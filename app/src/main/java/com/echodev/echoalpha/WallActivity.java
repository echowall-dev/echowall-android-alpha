package com.echodev.echoalpha;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WallActivity extends AppCompatActivity {

    @BindView(android.R.id.content)
    View mRootView;

    @BindView(R.id.identity_text)
    TextView IDText;

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
    }

    @OnClick(R.id.sign_out_btn)
    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(MainActivity.createIntent(WallActivity.this));
                            finish();
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

    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent intent = IdpResponse.getIntent(idpResponse);
        intent.setClass(context, WallActivity.class);
        return intent;
    }
}
