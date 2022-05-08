package com.gamiro.covidjournal.interfaces.listeners;

import com.gamiro.covidjournal.models.user.UserTest;

public interface UserTestListener {
    void onUserTestClick(int position);
    void editTest(UserTest userTest, String id);
    void deleteTest(UserTest userTest, String id);
}
