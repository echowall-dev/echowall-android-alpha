package com.echodev.echoalpha.util;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.echodev.echoalpha.R;

import java.util.ArrayList;

/**
 * Created by Ho on 26/2/2017.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Resources resources;
    private Context context;
    private ArrayList<PostClass> postList;

    // Constructors
    public PostAdapter() {
        this.postList = new ArrayList<PostClass>();
    }

    public PostAdapter(Resources resources, Context context) {
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
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new post
        View postView = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
        return new PostAdapter.ViewHolder(postView);
    }

    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder holder, int position) {
        PostClass post = postList.get(position);

        // Fetch the data of the post
        long postLikeNumber = post.getLikeNumber();

        // Set template info for the post
        holder.postUserProfileView.setText(post.getUserEmail());
        holder.postLikeNumberView.setText(postLikeNumber + ((postLikeNumber == 0) ? " Like" : " Likes"));
        holder.postCaptionView.setText(post.getCaption());
        holder.postCreationDateView.setText(post.getCreationDateString());

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
        public TextView postUserProfileView, postEditView, postLikeNumberView, postCaptionView, postCreationDateView;
        public RelativeLayout postImageArea;
        public ImageView postImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            // Prepare the views of the post
            postUserProfileView = (TextView) itemView.findViewById(R.id.post_layout_user_profile);
            postEditView = (TextView) itemView.findViewById(R.id.post_layout_edit);
            postImageArea = (RelativeLayout) itemView.findViewById(R.id.post_layout_image_area);
            postImageView = (ImageView) itemView.findViewById(R.id.post_layout_image);
            postLikeNumberView = (TextView) itemView.findViewById(R.id.post_layout_like_number);
            postCaptionView = (TextView) itemView.findViewById(R.id.post_layout_caption);
            postCreationDateView = (TextView) itemView.findViewById(R.id.post_layout_creation_time);
        }
    }
}
