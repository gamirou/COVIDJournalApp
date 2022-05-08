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

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private Context context;
    private ArrayList<FriendModel> friends;

    // TODO: Add delete button
    private UserListener mListener;

    public FriendAdapter(Context context, ArrayList<FriendModel> friends, UserListener mListener) {
        this.context = context;
        this.friends = friends;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.friend_row, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        FriendModel friendModel = friends.get(position);

        UserData user = friendModel.getData();
        String id = friendModel.getId();

        holder.textFriendStatus.setVisibility(View.GONE);

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

        holder.btnSendRequest.setBackgroundResource(R.drawable.ic_friend_true_24);
        holder.btnSendRequest.setOnClickListener(view -> {
            // TODO: DELETE FRIEND
        });
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {

        public TextView textName;
        public TextView textFriendStatus;
        public Button btnSendRequest;
        public ImageView imageFlag;
        public ImageView imageUser;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.tv_user_name);
            btnSendRequest = itemView.findViewById(R.id.btn_add_page_add_friend);
            imageFlag = itemView.findViewById(R.id.img_user_flag);
            imageUser = itemView.findViewById(R.id.img_user);
            textFriendStatus = itemView.findViewById(R.id.tv_friend_status);
        }
    }
}
