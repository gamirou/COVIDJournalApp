package com.gamiro.covidjournal.fragments.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gamiro.covidjournal.HomeActivity;
import com.gamiro.covidjournal.R;
import com.gamiro.covidjournal.adapters.NotificationsAdapter;
import com.gamiro.covidjournal.fragments.ProfileFragmentDirections;
import com.gamiro.covidjournal.helpers.AppUtil;
import com.gamiro.covidjournal.interfaces.listeners.NotificationListener;
import com.gamiro.covidjournal.models.notifications.NotificationModel;
import com.gamiro.covidjournal.models.user.UserPost;
import com.gamiro.covidjournal.models.user.UserTest;
import com.gamiro.covidjournal.viewmodels.HomeActivityViewModel;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment implements NotificationListener {

    private static final String TAG = "NotificationsFragment";

    // View model
    private HomeActivityViewModel viewModel;

    // Layout elements
    private RecyclerView recyclerNotifications;
    private NotificationsAdapter notificationsAdapter;
    private ArrayList<NotificationModel> notifications = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerNotifications = getView().findViewById(R.id.recycler_notifications);
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((HomeActivity) getActivity()).setProgressState(true);

        viewModel = new ViewModelProvider(requireActivity()).get(HomeActivityViewModel.class);
        notificationsAdapter = new NotificationsAdapter(getContext(), notifications, this);
        recyclerNotifications.setAdapter(notificationsAdapter);

        // Empty container if recycler view is empty
        LinearLayout emptyContainer = AppUtil.addEmptyViewRecyclerView(
                getView().findViewById(R.id.notifications_content),
                R.id.notifications_content, ""
        );

        // Actual notifications
        viewModel.getNotificationModels().observe(getViewLifecycleOwner(), notificationModels -> {
            notifications.clear();

            notifications.addAll(notificationModels);
            notificationsAdapter.notifyDataSetChanged();

            emptyContainer.setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);
            ((HomeActivity) getActivity()).setProgressState(false);
        });
    }

    @Override
    public void acceptPost(UserPost post) {
        post.setAccepted(true);
        viewModel.acceptPost(post);
    }

    @Override
    public void cancelPost(UserPost post) {
        viewModel.cancelPost(post);
    }

    @Override
    public void performAction(NotificationModel notification) {
        switch (notification.getAction()) {
            case AppUtil.OPEN_USER_TEST:
                Navigation.findNavController(requireView()).navigate(R.id.homeFragment);
                Navigation.findNavController(requireView()).navigate(R.id.profileFragment);

                // Last one
                ProfileFragmentDirections.ActionProfileFragmentToUserTestFragment action =
                        ProfileFragmentDirections.actionProfileFragmentToUserTestFragment(new UserTest(), "", "", "");
                Navigation.findNavController(requireView()).navigate(action);
                break;

            case AppUtil.OPEN_FRIENDS:
                Navigation.findNavController(requireView()).navigate(R.id.homeFragment);
                Navigation.findNavController(requireView()).navigate(R.id.friendsFragment);
                break;

            case AppUtil.REMINDER_TEST_PENDING:
                Navigation.findNavController(requireView()).navigate(R.id.homeFragment);
                Navigation.findNavController(requireView()).navigate(R.id.profileFragment);

                // Last one
                ProfileFragmentDirections.ActionProfileFragmentToUserTestFragment action2 =
                        ProfileFragmentDirections.actionProfileFragmentToUserTestFragment(notification.getTest(), notification.getDateCreated(), "", "");
                Navigation.findNavController(requireView()).navigate(action2);
                break;

            case AppUtil.REMINDER_VERIFY_EMAIL:
                Navigation.findNavController(requireView()).navigate(R.id.emailVerificationFragment);
                break;
            default:
                break;
        }
    }
}