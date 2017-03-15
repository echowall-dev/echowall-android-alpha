package com.echodev.echoalpha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.echodev.echoalpha.firebase.FirebasePost;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PostcardActivity extends AppCompatActivity {

    // Debug log
    private static final String LOG_TAG = "Echo_Alpha_Postcard";

    // Bind views by ButterKnife
    @BindView(R.id.postcard_image)
    ImageView postcardImg;

    @BindView(R.id.postcard_layout)
    View postcardLayout;

    @BindView(R.id.postcard_message_edit)
    EditText postcardMsgEdit;

    @BindView(R.id.postcard_finish)
    Button postcardFinish;

    // Instance variables
    private FirebasePost currentPost;
    private RelativeLayout postcardMsgArea;
    private ImageView postcardMsgTemplate, postcardQRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postcard);
        ButterKnife.bind(this);

        currentPost = getIntent().getParcelableExtra("currentPost");

        // Initialize the postcard layout
        postcardMsgArea = (RelativeLayout) postcardLayout.findViewById(R.id.postcard_message_area);
        postcardMsgTemplate = (ImageView) postcardLayout.findViewById(R.id.postcard_message_template);
        postcardQRCode = (ImageView) postcardLayout.findViewById(R.id.postcard_qr_code);

        // Load the images
        Glide.with(this)
                .load(currentPost.getPhotoUrl())
                .asBitmap()
                .into(postcardImg);

        Glide.with(this)
                .load(R.drawable.postcard_template)
                .asBitmap()
                .into(postcardMsgTemplate);

        Glide.with(this)
                .load(R.drawable.qr_code_sample)
                .asBitmap()
                .into(postcardQRCode);
    }

    @OnClick(R.id.postcard_finish)
    public void postcardFinish() {
        setResult(RESULT_OK);
        finish();
    }
}
