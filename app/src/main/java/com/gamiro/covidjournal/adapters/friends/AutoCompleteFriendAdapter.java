package com.gamiro.covidjournal.adapters.friends;

import android.content.Context;
import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blongho.country_data.Country;
import com.blongho.country_data.World;
import com.bumptech.glide.Glide;
import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.models.user.FriendModel;
import com.gamiro.covidjournal.models.user.UserData;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class AutoCompleteFriendAdapter extends ArrayAdapter<FriendModel> {
    public List<FriendModel> friendsListFull;

    public AutoCompleteFriendAdapter(@NonNull Context context, @NonNull List<FriendModel> objects) {
        super(context, 0, objects);
        friendsListFull = new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.friend_row, parent, false
            );
        }

        CardView cardImage = convertView.findViewById(R.id.card_user_image);
        ImageView userImage = convertView.findViewById(R.id.img_user);
        ImageView userFlag = convertView.findViewById(R.id.img_user_flag);
        TextView textUserName = convertView.findViewById(R.id.tv_user_name);

        // Hide these elements
        TextView hideFriendStatus = convertView.findViewById(R.id.tv_friend_status);
        Button hideButton = convertView.findViewById(R.id.btn_add_page_add_friend);
        hideFriendStatus.setVisibility(View.GONE);
        hideButton.setVisibility(View.GONE);

        FriendModel friendModel = getItem(position);

        if (friendModel != null) {
            UserData data = friendModel.getData();

            // Name
            textUserName.setText(data.getName());

            // Uri image
            if (data.getImage() != null) {
                if (!data.getImage().isEmpty()) {
                    Glide.with(getContext()).load(Uri.parse(data.getImage())).into(userImage);
                } else {
                    userImage.setImageResource(R.drawable.userphoto);
                }
            } else {
                userImage.setImageResource(R.drawable.userphoto);
            }

            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getContext().getResources().getDisplayMetrics());
            cardImage.setLayoutParams(new ConstraintLayout.LayoutParams(size, size));

            cardImage.setRadius(size/2);

            // Flag
            if (!data.getCountry().isEmpty() || data.getCountry().equals("Not specified")) {
                userFlag.setImageResource(AppUtil.getFlagFromPickerCountry(data.getCountry()));
            }
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return friendFilter;
    }

    private Filter friendFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            List<FriendModel> suggestions = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                suggestions.addAll(friendsListFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (FriendModel friendModel: friendsListFull) {
                    UserData data = friendModel.getData();

                    if (data.getName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(friendModel);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((List) filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            FriendModel result = (FriendModel) resultValue;
            return result.getData().getName() + "+" + result.getId();
        }
    };

    public void update(List<FriendModel> friends) {
        friendsListFull.clear();
        friendsListFull.addAll(friends);
        notifyDataSetChanged();
    }
}
