package com.gamiro.covidjournal.interfaces.listeners;

import com.gamiro.covidjournal.models.notifications.NotificationModel;
import com.gamiro.covidjournal.models.user.UserPost;

public interface NotificationListener {
    void acceptPost(UserPost post);
    void cancelPost(UserPost post);
    void performAction(NotificationModel notification);
}
