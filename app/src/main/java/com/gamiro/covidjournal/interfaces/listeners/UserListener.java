package com.gamiro.covidjournal.interfaces.listeners;

import com.gamiro.covidjournal.models.user.UserData;

import java.util.HashMap;

public interface UserListener {

    // For search
    void sendFriendRequest(String id, UserData userData);
    String checkFriendStatus(HashMap<String, String> friends);

    // For friends page
    void cancelFriendRequest(String id, UserData userData);
}
