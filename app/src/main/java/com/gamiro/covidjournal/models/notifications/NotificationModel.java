package com.gamiro.covidjournal.models.notifications;

import com.gamiro.covidjournal.models.user.UserPost;
import com.gamiro.covidjournal.models.user.UserTest;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class NotificationModel {

    // title can also be id of user who created the post
    public String title;
    public String body;
    public String dateCreated;
    public String action;
    public UserPost post;
    public UserTest test;

    public NotificationModel() {}

    public NotificationModel(String title, String body, String dateCreated, String action) {
        this.title = title;
        this.body = body;
        this.dateCreated = dateCreated;
        this.action = action;
    }

    // Change userpost to notification model
    @NonNull
    public static NotificationModel userPostToNotificationModel(UserPost userPost) {
        NotificationModel notification = new NotificationModel();
        notification.setPost(userPost);
        return notification;
    }

    public UserPost getPost() {
        return post;
    }

    public void setPost(UserPost post) {
        this.post = post;
    }

    public UserTest getTest() {
        return test;
    }

    public void setTest(UserTest test) {
        this.test = test;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
