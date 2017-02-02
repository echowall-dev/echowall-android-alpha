package com.echodev.echoalpha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class WallActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    TextView IDText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        mAuth = FirebaseAuth.getInstance();

        IDText = (TextView) findViewById(R.id.identity_text);
        IDText.setText("You have signed in as " + mAuth.getCurrentUser().getEmail());
    }
}
