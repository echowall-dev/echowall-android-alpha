package com.echodev.echoalpha.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.echodev.echoalpha.PostEditActivity;
import com.echodev.echoalpha.PostcardActivity;
import com.echodev.echoalpha.R;
import com.echodev.echoalpha.util.ImageHelper;

import java.util.ArrayList;

import static com.echodev.echoalpha.WallActivity.REQUEST_CODE_POSTCARD_CREATE;
import static com.echodev.echoalpha.WallActivity.REQUEST_CODE_POST_EDIT;

/**
 * Created by Ho on 5/3/2017.
 */

public class FirebasePostAdapter extends RecyclerView.Adapter<FirebasePostAdapter.ViewHolder> {

    private Resources resources;
    private Context context;
    private ArrayList<FirebasePost> postList;

    // Constructors
    public FirebasePostAdapter() {
        this.postList = new ArrayList<FirebasePost>();
    }

    public FirebasePostAdapter(Resources resources, Context context) {
        this.resources = resources;
        this.context = context;
        this.postList = new ArrayList<FirebasePost>();
    }

    // Getters
    public ArrayList<FirebasePost> getPostList() {
        return postList;
    }

    // Setters
    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setPostList(ArrayList<FirebasePost> postList) {
        this.postList = postList;
    }

    // Instance methods for modifying the dataset
    public void addPost(FirebasePost post) {
        postList.add(post);
    }

    public void addPost(int i, FirebasePost post) {
        postList.add(i, post);
    }

    public void removePost(FirebasePost post) {
        postList.remove(post);
    }

    public void removePost(int i) {
        postList.remove(i);
    }

    public void setPost(int i, FirebasePost post) {
        postList.set(i, post);
    }

    public int indexOfPost(FirebasePost post) {
        return postList.indexOf(post);
    }

    public int indexOfPostbyID(String targetID) {
        // TODO: Use binary search on postCreationDate instead of linear search on postID
        for (int i=0; i<postList.size(); i++) {
            if (postList.get(i).getPostID().equals(targetID)) {
                return i;
            }
        }
        return -1;
    }

    public FirebasePost getPost(int i) {
        return postList.get(i);
    }

    @Override
    public FirebasePostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new post
        View postView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
        return new FirebasePostAdapter.ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(final FirebasePostAdapter.ViewHolder holder, int position) {
        final FirebasePost post = postList.get(position);

        // Remove previous speech bubbles in case the view is recycled
        // The ImageView at [0] is the post photo so start removing from [1]
        if (holder.postImgArea.getChildCount() > 1) {
            for (int i=1; i<holder.postImgArea.getChildCount(); i++) {
                View childImgView = holder.postImgArea.getChildAt(i);
                if (childImgView.getWidth() <= resources.getDimensionPixelSize(R.dimen.bubble_width)) {
                    holder.postImgArea.removeView(childImgView);
                }
            }
        }

        /*
        if (post.isPostPrepared()) {
            return;
        }
        */

        // Set template info for the post
        holder.postUserName.setText(post.getCreatorName());
        holder.postLikeCount.setText(Long.toString(post.getLikeNumber()));
        holder.postCaption.setText(post.getCaption());

        int secondIndex = post.getCreationDate().lastIndexOf(":");
        String secondStr = post.getCreationDate().substring(secondIndex);
        String displayDate = post.getCreationDate().replace(secondStr, "");
        holder.postCreationDate.setText(displayDate);

        Glide.with(context)
                .load(R.drawable.ic_account_circle_grey_48px)
                .asBitmap()
                .into(holder.postUserPicture);

        Glide.with(context)
                .load(R.drawable.ic_mail_outline_grey_48px)
                .asBitmap()
                .into(holder.postPostcard);

        Glide.with(context)
                .load(R.drawable.ic_favorite_border_red_48px)
                .asBitmap()
                .into(holder.postLikeHeart);

        Glide.with(context)
                .load(R.drawable.ic_album_blue_48px)
                .asBitmap()
                .into(holder.postEdit);

        // Add the photo to the post
        Glide.with(context)
                .load(post.getPhotoUrl())
                .asBitmap()
                .into(holder.postImg);

        // Add the speech bubbles at target position
        if (post.getBubbleList()!=null && !post.getBubbleList().isEmpty()) {
            for (FirebaseBubble bubble : post.getBubbleList()) {
                FirebaseBubbleWrapper bubbleWrapper = new FirebaseBubbleWrapper(bubble);
                bubbleWrapper.setContext(context);
                bubbleWrapper.initAudioPlayer();

                // Convert dp back to px for display
                int positionX = ImageHelper.convertDpToPx((int) bubble.getX(), context);
                int positionY = ImageHelper.convertDpToPx((int) bubble.getY(), context);

                double xRation = bubble.getXRatio();
                double yRation = bubble.getYRatio();

                int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
                int parentHeight = (int) Math.round(screenWidth / post.getPhotoAspectRatio());

                bubbleWrapper.addBubbleImage(positionX, positionY, holder.postImgArea, resources, context);
//                bubbleWrapper.addBubbleImageByRatio(xRation, yRation, screenWidth, parentHeight, holder.postImgArea, resources, context);

                bubbleWrapper.bindPlayListener();
            }
        }

        holder.postLikeHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(context)
                        .load(R.drawable.ic_favorite_red_48px)
                        .asBitmap()
                        .into(holder.postLikeHeart);

                holder.postLikeCount.setText(Long.toString(post.getLikeNumber() + 1));
            }
        });

        holder.postEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostEditActivity.class);
                intent.putExtra("currentPost", post);

                ((Activity) v.getContext()).startActivityForResult(intent, REQUEST_CODE_POST_EDIT);
            }
        });

        holder.postPostcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostcardActivity.class);
                intent.putExtra("currentPost", post);

                ((Activity) v.getContext()).startActivityForResult(intent, REQUEST_CODE_POSTCARD_CREATE);
            }
        });

        /*
        post.setPostPrepared(true);

        // Set the corresponding post in the list to prepared
        postList.set(position, post);
        */
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        // Remove the speech bubbles when the view is recycled
        // The ImageView at [0] is the post photo so start removing from [1]
        if (holder.postImgArea.getChildCount() > 1) {
            for (int i=1; i<holder.postImgArea.getChildCount(); i++) {
                View childImgView = holder.postImgArea.getChildAt(i);
                if (childImgView.getWidth() <= resources.getDimensionPixelSize(R.dimen.bubble_width)) {
                    holder.postImgArea.removeView(childImgView);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView postUserName, postLikeCount, postCaption, postCreationDate;
        public RelativeLayout postImgArea;
        public ImageView postUserPicture, postImg, postLikeHeart, postEdit, postPostcard;

        public ViewHolder(View itemView) {
            super(itemView);
            // Prepare the views of the post
            postUserPicture = (ImageView) itemView.findViewById(R.id.post_layout_user_picture);
            postUserName = (TextView) itemView.findViewById(R.id.post_layout_user_name);
            postPostcard = (ImageView) itemView.findViewById(R.id.post_layout_postcard);
            postImgArea = (RelativeLayout) itemView.findViewById(R.id.post_layout_image_area);
            postImg = (ImageView) itemView.findViewById(R.id.post_layout_image);
            postLikeHeart = (ImageView) itemView.findViewById(R.id.post_layout_like_heart);
            postLikeCount = (TextView) itemView.findViewById(R.id.post_layout_like_count);
            postEdit = (ImageView) itemView.findViewById(R.id.post_layout_edit);
            postCaption = (TextView) itemView.findViewById(R.id.post_layout_caption);
            postCreationDate = (TextView) itemView.findViewById(R.id.post_layout_creation_time);
        }
    }
}
