package com.echodev.echoalpha.firebase;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.echodev.echoalpha.R;
import com.echodev.echoalpha.util.ImageHelper;
import com.echodev.echoalpha.util.PostClass;
import com.echodev.echoalpha.util.SpeechBubble;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
    public void onBindViewHolder(FirebasePostAdapter.ViewHolder holder, int position) {
        FirebasePost post = postList.get(position);

        // Fetch the data of the post
        long postLikeNumber = post.getLikeNumber();

        // Set template info for the post
//        holder.postUserProfileView.setText(post.getCreatorEmail());
        holder.postUserProfileView.setText("Post " + position);
        holder.postLikeNumberView.setText(postLikeNumber + ((postLikeNumber == 0) ? " Like" : " Likes"));
        holder.postUserNameView.setText(post.getCreatorName() + ":");
        holder.postCaptionView.setText(post.getCaption());
        holder.postCreationDateView.setText(post.getCreationDate());

        // Add the photo to the post
        Glide.with(context)
                .load(post.getPhotoUrl())
                .asBitmap()
                .into(holder.postImageView);

        // Add the speech bubbles at target position
        if (post.getBubbleList() != null && !post.getBubbleList().isEmpty()) {
            for (FirebaseBubble bubble : post.getBubbleList()) {
                FirebaseBubbleWrapper bubbleWrapper = new FirebaseBubbleWrapper(bubble);

                // Convert dp back to px for display
                int positionX = ImageHelper.convertDpToPx((int) bubble.getX(), context);
                int positionY = ImageHelper.convertDpToPx((int) bubble.getY(), context);
                bubbleWrapper.addBubbleImage(positionX, positionY, holder.postImageArea, resources, context);
                bubbleWrapper.bindPlayListener();
            }
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView postUserProfileView, postLikeNumberView, postUserNameView, postCaptionView, postCreationDateView;
        public RelativeLayout postImageArea;
        public ImageView postImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            // Prepare the views of the post
            postUserProfileView = (TextView) itemView.findViewById(R.id.post_user_profile);
            postImageArea = (RelativeLayout) itemView.findViewById(R.id.post_image_area);
            postImageView = (ImageView) itemView.findViewById(R.id.post_image);
            postLikeNumberView = (TextView) itemView.findViewById(R.id.post_like_number);
            postUserNameView = (TextView) itemView.findViewById(R.id.post_user_name);
            postCaptionView = (TextView) itemView.findViewById(R.id.post_caption);
            postCreationDateView = (TextView) itemView.findViewById(R.id.post_creation_time);
        }
    }
}
