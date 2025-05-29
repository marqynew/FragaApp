package com.example.fraga.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fraga.R;
import com.example.fraga.models.Post;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> posts;

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updatePosts(List<Post> newPosts) {
        this.posts = newPosts;
        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        private TextView contentText;
        private ImageView postImage;
        private TextView timestampText;
        private TextView likesCount;
        private TextView commentsCount;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            contentText = itemView.findViewById(R.id.contentText);
            postImage = itemView.findViewById(R.id.postImage);
            timestampText = itemView.findViewById(R.id.timestampText);
            likesCount = itemView.findViewById(R.id.likesCount);
            commentsCount = itemView.findViewById(R.id.commentsCount);
        }

        public void bind(Post post) {
            contentText.setText(post.getContent());
            
            // Handle image if exists
            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                postImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(post.getImageUrl())
                        .into(postImage);
            } else {
                postImage.setVisibility(View.GONE);
            }

            // Format timestamp
            timestampText.setText(dateFormat.format(post.getTimestamp()));

            // Set likes and comments count
            likesCount.setText(String.valueOf(post.getLikes().size()));
            commentsCount.setText(String.valueOf(post.getComments().size()));
        }
    }
} 