package com.echodev.echoalpha;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.echodev.echoalpha.firebase.FirebasePost;
import com.echodev.echoalpha.firebase.FirebaseUserClass;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostcardActivity extends AppCompatActivity {

    // Debug log
    private static final String LOG_TAG = "Echo_Alpha_Postcard";

    @BindView(R.id.postcard_image)
    ImageView postcardImg;

    @BindView(R.id.postcard_layout)
    View postcardLayout;

    private FirebaseUserClass currentUser;
    private FirebasePost currentPost;
    private RelativeLayout postcardMsgArea;
    private ImageView postcardMsgTemplate, postcardQRCode;

    private Context localContext;
    private Resources localResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postcard);
        ButterKnife.bind(this);

        currentPost = getIntent().getParcelableExtra("currentPost");

        postcardMsgArea = (RelativeLayout) postcardLayout.findViewById(R.id.postcard_message_area);
        postcardMsgTemplate = (ImageView) postcardLayout.findViewById(R.id.postcard_message_template);
        postcardQRCode = (ImageView) postcardLayout.findViewById(R.id.postcard_qr_code);

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
}
