package com.gamiro.covidjournal.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.interfaces.listeners.NotificationListener;
import com.gamiro.covidjournal.models.notifications.NotificationModel;
import com.gamiro.covidjournal.models.user.UserPost;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> {

    private static final String TAG = "NotificationsAdapter";

    private Context context;
    private ArrayList<NotificationModel> notifications;
    private NotificationListener mListener;

    public NotificationsAdapter(Context context, ArrayList<NotificationModel> notifications, NotificationListener mListener) {
        this.context = context;
        this.notifications = notifications;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_row, parent,false);
        return new NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
        NotificationModel notification = notifications.get(position);

        // If post to be accepted
        if (notification.getPost() != null) {
            UserPost post = notification.getPost();

            Log.i(TAG, post.getTitle());

            holder.textTitle.setText(post.getTitle());
            holder.textType.setText(post.getPostType());
            holder.textQuestion.setVisibility(View.VISIBLE);

            if (post.getPostType().equals("activity")) {
                holder.textDescription.setText(post.getDescription() + " at " + post.getLocation());
            } else {
                String description = "You have entered in contact with " + post.getWhoMadePostPersonName() +
                        " on " + post.getDate() + " at " + post.getTime() + /* ". This happened at " +
                        post.getLocation() + */ ".";
                holder.textDescription.setText(description);
            }

            holder.btnAccept.setOnClickListener(view -> {
                mListener.acceptPost(post);
            });

            holder.btnCancel.setOnClickListener(view -> {
                mListener.cancelPost(post);
            });
        } else {
            holder.textTitle.setText(notification.getTitle());
            holder.textDescription.setText(notification.getBody());
            holder.textType.setText(AppUtil.URGENT_NOTIFICATION);
            holder.btnAccept.setText(AppUtil.splitCamelCase(notification.getAction()));
            holder.textQuestion.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);

            // Set action
            holder.btnAccept.setOnClickListener(view -> {
                    mListener.performAction(notification);
            });
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    protected class NotificationsViewHolder extends RecyclerView.ViewHolder {

        public TextView textTitle;
        public TextView textType;
        public TextView textDescription;
        public TextView textQuestion;
        public CardView cardUser;
        public ImageView imageUser;
        public Button btnAccept, btnCancel;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);

            textTitle = itemView.findViewById(R.id.tv_post_title);
            textType = itemView.findViewById(R.id.tv_post_type);
            textDescription = itemView.findViewById(R.id.tv_post_user_description);
            cardUser = itemView.findViewById(R.id.card_post_user_picture);
            imageUser = itemView.findViewById(R.id.img_post_user_picture);

            textQuestion = itemView.findViewById(R.id.tv_post_decision);
            btnAccept = itemView.findViewById(R.id.btn_accept_post);
            btnCancel = itemView.findViewById(R.id.btn_cancel_post);

            textQuestion.setVisibility(View.VISIBLE);
            btnAccept.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
        }
    }
}
