package com.echodev.echoalpha;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echodev.echoalpha.util.ImageHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class TagActivity extends AppCompatActivity {

    @BindView(R.id.relative_0)
    RelativeLayout mRelativeLayout0;

    @BindView(R.id.relative_1)
    RelativeLayout mRelativeLayout1;

    @BindView(R.id.hello_world)
    TextView helloWorldText;

//    @BindView(R.id.app_logo_small_1)
//    ImageView appLogoSmall;

//    @BindView(R.id.app_logo_small_2)
//    ImageView appLogoSmall2;

    private ImageView appLogoSmall1;
    private ImageView appLogoSmall2;

    // The 'active pointer' is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    private int dX, dY, targetX, targetY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        ButterKnife.bind(this);

//        addTag();

//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(R.dimen.logo_width_0, R.dimen.logo_height_0);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200, 200);
//        appLogoSmall.setLayoutParams(layoutParams);
    }

    @OnTouch(R.id.hello_world)
    public boolean helloWorldControl(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                helloWorldText.setText("YOU PRESS HELLO WORLD");
                addTag1();
                addTag2();
                break;
            case MotionEvent.ACTION_UP:
//                helloWorldText.setText("YOU RELEASE HELLO WORLD");
                break;
            default:
                return false;
        }
        return true;
    }

    private View.OnTouchListener tagListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final int rawX = (int) event.getRawX();
            final int rawY = (int) event.getRawY();

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams layoutParamsDown = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    // leftMargin and topMargin hold the current coordinate of the view
                    dX = rawX - layoutParamsDown.leftMargin;
                    dY = rawY - layoutParamsDown.topMargin;
                    break;
                case MotionEvent.ACTION_MOVE:
                    // To ensure the view won't be dragged out of the layout's boundary
                    // Different approach for getting the width and height of the RelativeLayout
//                    targetX = (rawX - dX < 0 || rawX - dX + view.getWidth() > ((View) view.getParent()).getWidth()) ? view.getLeft() : rawX - dX;
                    targetX = (rawX - dX < 0 || rawX - dX + view.getWidth() > mRelativeLayout0.getWidth()) ? view.getLeft() : rawX - dX;
                    targetY = (rawY - dY < 0 || rawY - dY + view.getHeight() > mRelativeLayout0.getHeight()) ? view.getTop() : rawY - dY;
                    RelativeLayout.LayoutParams layoutParamsMove = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    layoutParamsMove.leftMargin = targetX;
                    layoutParamsMove.topMargin = targetY;
                    layoutParamsMove.rightMargin = -250;
                    layoutParamsMove.bottomMargin = -250;
                    view.setLayoutParams(layoutParamsMove);
                    break;
                case MotionEvent.ACTION_UP:
                    updateLogoPosition(targetX, targetY);
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                default:
                    return false;
            }
            mRelativeLayout0.invalidate();
            return true;
        }
    };

//    @OnTouch(R.id.app_logo_small_1)
    public boolean tagDrag(View view, MotionEvent event) {
        final int rawX = (int) event.getRawX();
        final int rawY = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams layoutParamsDown = (RelativeLayout.LayoutParams) view.getLayoutParams();
                // leftMargin and topMargin hold the current coordinate of the view
                dX = rawX - layoutParamsDown.leftMargin;
                dY = rawY - layoutParamsDown.topMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                // To ensure the view won't be dragged out of the layout's boundary
                // Different approach for getting the width and height of the RelativeLayout
//                targetX = (rawX - dX < 0 || rawX - dX + view.getWidth() > ((View) view.getParent()).getWidth()) ? view.getLeft() : rawX - dX;
                targetX = (rawX - dX < 0 || rawX - dX + view.getWidth() > mRelativeLayout0.getWidth()) ? view.getLeft() : rawX - dX;
                targetY = (rawY - dY < 0 || rawY - dY + view.getHeight() > mRelativeLayout0.getHeight()) ? view.getTop() : rawY - dY;
                RelativeLayout.LayoutParams layoutParamsMove = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParamsMove.leftMargin = targetX;
                layoutParamsMove.topMargin = targetY;
                layoutParamsMove.rightMargin = -250;
                layoutParamsMove.bottomMargin = -250;
                view.setLayoutParams(layoutParamsMove);
                break;
            case MotionEvent.ACTION_UP:
                updateLogoPosition(targetX, targetY);
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            default:
                return false;
        }
        mRelativeLayout0.invalidate();
        return true;
    }

    private void updateLogoPosition(int targetX, int targetY) {
        RelativeLayout.LayoutParams layoutParamsTarget = (RelativeLayout.LayoutParams) appLogoSmall2.getLayoutParams();
        layoutParamsTarget.leftMargin = targetX;
        layoutParamsTarget.topMargin = targetY;
        layoutParamsTarget.rightMargin = -250;
        layoutParamsTarget.bottomMargin = -250;
        appLogoSmall2.setLayoutParams(layoutParamsTarget);
        mRelativeLayout1.invalidate();
    }

    private void addTag1() {
        appLogoSmall1 = new ImageView(this);
        appLogoSmall1.setId(01);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(200, 200);
//        layoutParams.leftMargin = (int) ((mRelativeLayout0.getWidth() - appLogoSmall1.getWidth()) * 0.5);
//        layoutParams.topMargin = (int) ((mRelativeLayout0.getHeight() - appLogoSmall1.getHeight()) * 0.5);
        layoutParams.leftMargin = (int) ((mRelativeLayout0.getWidth() - 200) * 0.5);
        layoutParams.topMargin = (int) ((mRelativeLayout0.getHeight() - 200) * 0.5);
        appLogoSmall1.setLayoutParams(layoutParams);
        mRelativeLayout0.addView(appLogoSmall1);

//        appLogoSmall1.setImageResource(R.drawable.echo_logo_200px);

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(this.getResources(), R.drawable.echo_logo_200px, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Get the dimensions of the View
//        int targetW = appLogoSmall1.getWidth();
//        int targetH = appLogoSmall1.getHeight();
        int targetW = 200;
        int targetH = 200;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.echo_logo_200px, bmOptions);
        appLogoSmall1.setImageBitmap(bitmap);

        float bigX = mRelativeLayout0.getWidth();
        float bigY = mRelativeLayout0.getHeight();
        float smallX = appLogoSmall1.getWidth();
        float smallY = appLogoSmall1.getHeight();
        int targetX = layoutParams.leftMargin;
        int targetY = layoutParams.topMargin;

        String debugMsg = "bigX: " + bigX + " bigY: " + bigY;
        debugMsg += "\nsmallX: " + smallX + " smallY: " + smallY;
        debugMsg += "\ntargetX: " + targetX + " targetY: " + targetY;
        helloWorldText.setText(debugMsg);

        appLogoSmall1.setOnTouchListener(tagListener);
    }

    private void addTag2() {
        appLogoSmall2 = new ImageView(this);
        appLogoSmall2.setId(02);
        appLogoSmall2.setImageResource(R.drawable.echo_logo_200px);
        int targetWidth = (int) ImageHelper.convertDpToPixel(R.dimen.logo_width_1, this);
        int targetHeight = (int) ImageHelper.convertDpToPixel(R.dimen.logo_height_1, this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(targetWidth, targetHeight);
//        layoutParams.leftMargin = (int) ((mRelativeLayout1.getWidth() - appLogoSmall2.getWidth()) * 0.5);
//        layoutParams.topMargin = (int) ((mRelativeLayout1.getHeight() - appLogoSmall2.getHeight()) * 0.5);
        appLogoSmall2.setLayoutParams(layoutParams);
        mRelativeLayout1.addView(appLogoSmall2);
    }
}
