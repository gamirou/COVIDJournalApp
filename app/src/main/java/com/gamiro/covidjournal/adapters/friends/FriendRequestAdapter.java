package com.gamiro.covidjournal.adapters.friends;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.interfaces.listeners.UserListener;
import com.gamiro.covidjournal.models.user.FriendModel;
import com.gamiro.covidjournal.models.user.UserData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder> {

    private static final String TAG = "FriendRequestAdapter";

    private Context context;
    private ArrayList<FriendModel> friendRequests;

    private UserListener mListener;

    public FriendRequestAdapter(Context context, ArrayList<FriendModel> friendRequests, UserListener mListener) {
        this.context = context;
        this.friendRequests = friendRequests;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.friend_request_row, parent, false);
        return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position) {
        FriendModel friendModel = friendRequests.get(position);

        UserData user = friendModel.getData();
        String id = friendModel.getId();

        holder.friendRequestName.setText(user.getName());

        // Uri image
        Log.i(TAG, "User image = null? " + (user.getImage().isEmpty()));
        if (user.getImage() != null) {
            if (!user.getImage().isEmpty()) {
                Glide.with(context).load(Uri.parse(user.getImage())).into(holder.friendRequestPhoto);
            } else {
                holder.friendRequestPhoto.setImageResource(R.drawable.userphoto);
            }
        } else {
            Log.i(TAG, "You should do this");
            holder.friendRequestPhoto.setImageResource(R.drawable.userphoto);
        }

        holder.friendRequestAccept.setOnClickListener(view -> mListener.sendFriendRequest(id, user));
        holder.friendRequestCancel.setOnClickListener(view -> mListener.cancelFriendRequest(id, user));
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public class FriendRequestViewHolder extends RecyclerView.ViewHolder {

        public TextView friendRequestName;
        public ImageView friendRequestCancel;
        public ImageView friendRequestAccept;
        public ImageView friendRequestPhoto;

        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);

            friendRequestName = itemView.findViewById(R.id.tv_friend_request_name);
            friendRequestCancel = itemView.findViewById(R.id.img_friend_request_cancel);
            friendRequestAccept = itemView.findViewById(R.id.img_friend_request_accept);
            friendRequestPhoto = itemView.findViewById(R.id.img_friend_request_profile);
        }
    }
}
