package com.gamiro.covidjournal.fragments.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.adapters.friends.FriendSearchAdapter;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.interfaces.listeners.UserListener;
import com.gamiro.covidjournal.models.user.FriendModel;
import com.gamiro.covidjournal.models.user.UserData;
import com.gamiro.covidjournal.viewmodels.HomeActivityViewModel;

import java.util.ArrayList;
import java.util.HashMap;

public class AddFriendFragment extends Fragment implements UserListener {

    private static final String TAG = "AddFriendFragment";
    // Layout
    private RecyclerView recyclerSearchUsers;
    private SearchView editSearchUsers;

    private FriendSearchAdapter usersAdapter;
    private HomeActivityViewModel viewModel;

    // HashMap and stuff
    private ArrayList<FriendModel> usersData;

    // TODO: I know it is kinda breaking MVVM but I don't have any other solution

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        editSearchUsers = getView().findViewById(R.id.search_users);
        recyclerSearchUsers = getView().findViewById(R.id.recycler_search_users);
        recyclerSearchUsers.setLayoutManager(new LinearLayoutManager(getActivity()));

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);

        usersData = new ArrayList<>();

        usersAdapter = new FriendSearchAdapter(getActivity(), usersData, this);
        recyclerSearchUsers.setAdapter(usersAdapter);

        // Empty container
        LinearLayout emptyContainer = AppUtil.addEmptyViewRecyclerView(
                getView().findViewById(R.id.add_friend_container),
                R.id.search_users,
                "Type in the search bar the name of your user!"
        );

        viewModel.getAllUsers().observe(getViewLifecycleOwner(), stringUserDataHashMap -> {
            String currentUserId = viewModel.getCurrentUserID();

            usersData.clear();

            if (stringUserDataHashMap != null) {
                for (String key : stringUserDataHashMap.keySet()) {
                    if (!key.equals(currentUserId)) {
                        if (stringUserDataHashMap.get(key).getImage() != null) {
                            System.out.println(stringUserDataHashMap.get(key).getImage());
                        }
                        FriendModel friend = new FriendModel(key, stringUserDataHashMap.get(key));
                        usersData.add(friend);
                    }
                }

                usersAdapter.filter(usersData);
            }

            // Handle empty
            if (usersData.size() == 0) {
                emptyContainer.setVisibility(View.VISIBLE);
            } else {
                emptyContainer.setVisibility(View.GONE);
            }
        });

        editSearchUsers.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() < 3) {
                    return false;
                }

                viewModel.setUsersQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                usersData.clear();
                usersAdapter.notifyDataSetChanged();
                emptyContainer.setVisibility(View.VISIBLE);
                return false;
            }
        });

        editSearchUsers.setOnCloseListener(() -> {
            viewModel.setUsersQuery("");
            emptyContainer.setVisibility(View.VISIBLE);
            return true;
        });
    }

    @Override
    public void sendFriendRequest(String id, UserData userData) {
        viewModel.sendFriendRequestUserData(id, userData);
    }

    @Override
    public String checkFriendStatus(HashMap<String, String> friends) {
        for (String key: friends.keySet()) {
            if (key.equals(viewModel.getCurrentUserID())) {
                return friends.get(key);
            }
        }

        return null;
    }

    @Override
    public void cancelFriendRequest(String id, UserData userData) {
        // Empty
    }
}