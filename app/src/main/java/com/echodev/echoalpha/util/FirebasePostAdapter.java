package com.echodev.echoalpha.util;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Ho on 5/3/2017.
 */

public class FirebasePostAdapter extends RecyclerView.Adapter<FirebasePostAdapter.ViewHolder> {

    private Resources resources;
    private Context context;
    private ArrayList<PostClass> postList;

    // Constructors
    public FirebasePostAdapter() {
        this.postList = new ArrayList<PostClass>();
    }

    public FirebasePostAdapter(Resources resources, Context context) {
        this.resources = resources;
        this.context = context;
        this.postList = new ArrayList<PostClass>();
    }

    // Getters
    public ArrayList<PostClass> getPostList() {
        return postList;
    }

    // Setters
    public void setPostList(ArrayList<PostClass> postList) {
        this.postList = postList;
    }

    // Instance methods for modifying the dataset
    public void addPost(PostClass post) {
        postList.add(post);
    }

    public void addPost(int i, PostClass post) {
        postList.add(i, post);
    }

    public void removePost(PostClass post) {
        postList.remove(post);
    }

    public void removePost(int i) {
        postList.remove(i);
    }

    @Override
    public FirebasePostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new post
        View postView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
        return new FirebasePostAdapter.ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(FirebasePostAdapter.ViewHolder holder, int position) {
        PostClass post = postList.get(position);

        // Fetch the data of the post
        long postLikeNumber = post.getLikeNumber();
        String postCreationDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(post.getCreationDate());

        // Set template info for the post
        holder.postUserProfileView.setText(post.getUserEmail());
        holder.postLikeNumberView.setText(postLikeNumber + ((postLikeNumber == 0) ? " Like" : " Likes"));
        holder.postCaptionView.setText("Peter:\nWhat a beautiful day!");
        holder.postCreationDateView.setText(postCreationDate);

        // Add the photo to the post
        Glide.with(context)
                .load(post.getPhotoPath())
                .asBitmap()
                .into(holder.postImageView);


        // Add the speech bubbles at target position
        for (SpeechBubble speechBubble : post.getSpeechBubbleList()) {
            speechBubble.addBubbleImage(speechBubble.getX(), speechBubble.getY(), holder.postImageArea, resources, context);
            speechBubble.bindPlayListener();
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView postUserProfileView, postLikeNumberView, postCaptionView, postCreationDateView;
        public RelativeLayout postImageArea;
        public ImageView postImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            // Prepare the views of the post
            postUserProfileView = (TextView) itemView.findViewById(R.id.post_user_profile);
            postImageArea = (RelativeLayout) itemView.findViewById(R.id.post_image_area);
            postImageView = (ImageView) itemView.findViewById(R.id.post_image);
            postLikeNumberView = (TextView) itemView.findViewById(R.id.post_like_number);
            postCaptionView = (TextView) itemView.findViewById(R.id.post_caption);
            postCreationDateView = (TextView) itemView.findViewById(R.id.post_creation_time);
        }
    }
}
