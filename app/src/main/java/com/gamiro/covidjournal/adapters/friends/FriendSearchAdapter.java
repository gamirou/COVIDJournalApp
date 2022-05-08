package com.gamiro.covidjournal.adapters.friends;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blongho.country_data.Country;
import com.blongho.country_data.World;
import com.bumptech.glide.Glide;
import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.interfaces.listeners.UserListener;
import com.gamiro.covidjournal.models.user.FriendModel;
import com.gamiro.covidjournal.models.user.UserData;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FriendSearchAdapter extends RecyclerView.Adapter<FriendSearchAdapter.FriendSearchViewHolder> {

    private Context context;
    private ArrayList<FriendModel> users;

    // Listener
    private UserListener mListener;

    public FriendSearchAdapter(Context context, ArrayList<FriendModel> users, UserListener userListener) {
        this.context = context;
        this.users = users;

        mListener = userListener;
    }

    @NonNull
    @Override
    public FriendSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.friend_row, parent,false);
        return new FriendSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendSearchViewHolder holder, int position) {
        FriendModel friendModel = users.get(position);

        UserData user = friendModel.getData();
        String id = friendModel.getId();

        holder.textName.setText(user.getName());
        if (!user.getCountry().isEmpty() || user.getCountry().equals("Not specified")) {
            holder.imageFlag.setImageResource(AppUtil.getFlagFromPickerCountry(user.getCountry()));
        }

        // Uri image
        if (user.getImage() != null) {
            if (!user.getImage().isEmpty()) {
                Glide.with(context).load(Uri.parse(user.getImage())).into(holder.imageUser);
            } else {
                holder.imageUser.setImageResource(R.drawable.userphoto);
            }
        } else {
            holder.imageUser.setImageResource(R.drawable.userphoto);
        }
        
        holder.btnSendRequest.setOnClickListener(view -> mListener.sendFriendRequest(id, user));

        if (user.getFriends() != null) {
            String friendStatus = mListener.checkFriendStatus(user.getFriends());

            if (friendStatus != null) {
                if (friendStatus.equals(AppUtil.FRIEND_PENDING)) {
                    // Current user sent the friend request || received
                    holder.btnSendRequest.setBackgroundResource(R.drawable.ic_friend_sent_request_24);
                    holder.textFriendStatus.setText("Request Sent");
                } else if (friendStatus.equals(AppUtil.FRIEND_SEND_REQUEST)) {
                    holder.btnSendRequest.setBackgroundResource(R.drawable.ic_baseline_check_circle_24);
                    holder.textFriendStatus.setText("Click to accept request");
                } else if (friendStatus.equals(AppUtil.FRIEND_ACCEPTED)) {
                    holder.btnSendRequest.setBackgroundResource(R.drawable.ic_friend_true_24);
                    holder.textFriendStatus.setText("Friends");
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void filter(ArrayList<FriendModel> filteredData) {
        users = filteredData;
        notifyDataSetChanged();
    }

    public class FriendSearchViewHolder extends RecyclerView.ViewHolder {

        public TextView textName;
        public TextView textFriendStatus;
        public Button btnSendRequest;
        public ImageView imageFlag;
        public ImageView imageUser;

        public FriendSearchViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.tv_user_name);
            btnSendRequest = itemView.findViewById(R.id.btn_add_page_add_friend);
            imageFlag = itemView.findViewById(R.id.img_user_flag);
            imageUser = itemView.findViewById(R.id.img_user);
            textFriendStatus = itemView.findViewById(R.id.tv_friend_status);
        }
    }
}
