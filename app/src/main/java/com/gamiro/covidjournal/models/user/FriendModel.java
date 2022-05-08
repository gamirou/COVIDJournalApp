package com.gamiro.covidjournal.models.user;

public class FriendModel {

    public String id;
    public UserData data;

    public FriendModel(String id, UserData data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }
}
