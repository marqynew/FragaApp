package com.example.fraga.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fraga.R;
import com.example.fraga.UserProfileActivity;
import com.example.fraga.models.UserProfile;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<UserProfile> users;
    private Context context;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onFollowClick(UserProfile user, boolean isFollowing);
        void onUserClick(UserProfile user);
    }

    public UserAdapter(Context context, List<UserProfile> users) {
        this.context = context;
        this.users = users;
        if (context instanceof OnUserClickListener) {
            this.listener = (OnUserClickListener) context;
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserProfile user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateUsers(List<UserProfile> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    public List<UserProfile> getUsers() {
        return users;
    }

    public void updateUser(UserProfile user) {
        int position = users.indexOf(user);
        if (position != -1) {
            users.set(position, user);
            notifyItemChanged(position);
        }
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private ImageView profileImage;
        private TextView usernameText;
        private Button viewProfileButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            usernameText = itemView.findViewById(R.id.usernameText);
            viewProfileButton = itemView.findViewById(R.id.viewProfileButton);
        }

        public void bind(UserProfile user) {
            usernameText.setText(user.getFullName());

            if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(user.getProfileImageUrl())
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.default_profile_pic);
            }

            viewProfileButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(user);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(user);
                }
            });
        }
    }
} 