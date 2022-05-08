package com.gamiro.covidjournal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.models.user.UserPost;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {

    private Context context;
    private ArrayList<UserPost> posts;
    private HashMap<String, String> images;

    public PostsAdapter(Context context, ArrayList<UserPost> posts, HashMap<String, String> images) {
        this.context = context;
        this.posts = posts;
        this.images = images;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_row, parent,false);
        return new PostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        UserPost post = posts.get(position);

        holder.textTitle.setText(post.getTitle());
        holder.textType.setText(post.getPostType());

        // ACTIVITY
        if (post.getPostType().equals("activity")) {
            holder.textDescription.setText(post.getDescription() + " at " + post.getLocation());
            holder.imageUser.setImageResource(R.drawable.covid);
        } else {
            // PERSON
            String description = "You have entered in contact with " + post.getPersonName() +
                    " on " + post.getDate() + " at " + post.getTime() + /* ". This happened at " +
                    post.getLocation() + */ ".";
            holder.textDescription.setText(description);

            if (images.containsKey(post.getId())) {
                Glide.with(context).load(images.get(post.getId())).into(holder.imageUser);
            } else {
                holder.imageUser.setImageResource(R.drawable.userphoto);
            }

        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    protected class PostsViewHolder extends RecyclerView.ViewHolder {

        public TextView textTitle;
        public TextView textType;
        public TextView textDescription;
        public CardView cardUser;
        public ImageView imageUser;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.tv_post_title);
            textType = itemView.findViewById(R.id.tv_post_type);
            textDescription = itemView.findViewById(R.id.tv_post_user_description);
            cardUser = itemView.findViewById(R.id.card_post_user_picture);
            imageUser = itemView.findViewById(R.id.img_post_user_picture);
        }
    }
}
