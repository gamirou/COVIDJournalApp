package com.gamiro.covidjournal.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.adapters.friends.FriendAdapter;
import com.gamiro.covidjournal.adapters.friends.FriendRequestAdapter;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.interfaces.listeners.UserListener;
import com.gamiro.covidjournal.models.user.FriendModel;
import com.gamiro.covidjournal.models.user.UserData;
import com.gamiro.covidjournal.viewmodels.HomeActivityViewModel;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsFragment extends Fragment implements UserListener {

    // Layout elements
    private RecyclerView recyclerFriendRequests;
    private RecyclerView recyclerFriends;
    private TextView textFriendRequests;
    private TextView textFriends;

    private HomeActivityViewModel viewModel;

    // Data that needs to be used
    private ArrayList<FriendModel> friendRequests = new ArrayList<>();
    private ArrayList<FriendModel> friends = new ArrayList<>();

    private FriendRequestAdapter friendRequestAdapter;
    private FriendAdapter friendAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerFriendRequests = getView().findViewById(R.id.recycler_friend_requests);
        recyclerFriends = getView().findViewById(R.id.recycler_friends);

        textFriendRequests = getView().findViewById(R.id.tv_friend_requests);
        textFriends = getView().findViewById(R.id.tv_friends);

        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerFriendRequests.setLayoutManager(linearLayoutManager);
        recyclerFriends.setLayoutManager(new LinearLayoutManager(getActivity()));

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);

        friendRequestAdapter = new FriendRequestAdapter(getActivity(), friendRequests, this);
        recyclerFriendRequests.setAdapter(friendRequestAdapter);

        friendAdapter = new FriendAdapter(getActivity(), friends, this);
        recyclerFriends.setAdapter(friendAdapter);

        // Empty only for friend recycler
        LinearLayout emptyFriendContainer = AppUtil.addEmptyViewRecyclerView(
                getView().findViewById(R.id.friends_container),
                R.id.tv_friends,
                "Oh, you don't have any friends added. Search them in the 'Add Friend' page."
        );

        viewModel.getAllFriends().observe(getViewLifecycleOwner(), new Observer<HashMap<String, UserData>>() {
            @Override
            public void onChanged(HashMap<String, UserData> userDataHashMap) {
                friendRequests.clear();
                friends.clear();

                String currentID = viewModel.getCurrentUserID();

                for (String key: userDataHashMap.keySet()) {
                    UserData friend = userDataHashMap.get(key);
                    if (friend.getFriends() != null) {
                        if (friend.getFriends().containsKey(currentID)) {
                            FriendModel model = new FriendModel(key, friend);

                            if (friend.getFriends().get(currentID).equals(AppUtil.FRIEND_SEND_REQUEST)) {
                                friendRequests.add(model);
                            } else if (friend.getFriends().get(currentID).equals(AppUtil.FRIEND_ACCEPTED)) {
                                friends.add(model);
                            }
                        }
                    }
                }

                emptyFriendContainer.setVisibility(friends.isEmpty() ? View.VISIBLE : View.GONE);

                textFriendRequests.setText("Friend Requests (" + friendRequests.size() + ")");
                textFriends.setText("Friends (" + friends.size() + ")");
                friendRequestAdapter.notifyDataSetChanged();
                friendAdapter.notifyDataSetChanged();
            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    // Also accept
    public void sendFriendRequest(String id, UserData userData) {
        viewModel.acceptFriendRequest(id, userData);
    }

    @Override
    public String checkFriendStatus(HashMap<String, String> friends) {
        return null;
    }

    @Override
    public void cancelFriendRequest(String id, UserData userData) {
        viewModel.cancelFriendRequest(id, userData);
    }
}