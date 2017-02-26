package com.echodev.echoalpha.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echodev.echoalpha.R;

import java.util.ArrayList;

/**
 * Created by Ho on 26/2/2017.
 */

/*
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PostClass> postList;

    public PostAdapter() {
        this.postList = new ArrayList<PostClass>();
    }

    public PostAdapter(ArrayList<PostClass> postList) {
        this.postList = postList;
    }

    public PostAdapter(Context context, ArrayList<PostClass> postList) {
        this.context = context;
        this.postList = postList;
    }

    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new post
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);

        // Prepare the views of the post
        TextView postUserProfile = (TextView) view.findViewById(R.id.post_user_profile);
        RelativeLayout postImageArea = (RelativeLayout) view.findViewById(R.id.post_image_area);
        ImageView postImageView = (ImageView) view.findViewById(R.id.post_image);
        TextView postLikeNumberView = (TextView) view.findViewById(R.id.post_like_number);
        TextView postCaptionView = (TextView) view.findViewById(R.id.post_caption);
        TextView postCreationDateView = (TextView) view.findViewById(R.id.post_creation_time);

        // Set template info
        postUserProfile.setText("Test Email");
        postLikeNumberView.setText("0 like");
        postCaptionView.setText("Peter:\nWhat a beautiful day!");
        postCreationDateView.setText("2046");

        // Add the photo
        ImageHelper.setPicFromFile(postImageView, postAppendArea.getWidth(), newPost.getPhotoPath());

        // Add the speech bubbles at target position
        newSpeechBubble.addBubbleImage(newSpeechBubble.getX(), newSpeechBubble.getY(), postImageArea, localRes, this);
        newSpeechBubble.bindPlayListener();

        PostAdapter.ViewHolder postViewHolder = new PostAdapter.ViewHolder(view);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(PostAdapter.ViewHolder holder, int position) {
        holder.itemView = postList.get(position);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
*/
