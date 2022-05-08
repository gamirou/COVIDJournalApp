package com.gamiro.covidjournal.models.user;

import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;

public class UserData implements Serializable {

    public String name;
    public String dob;
    public String country;
    public String city;
    public String gender;
    public boolean mightHaveCorona;
    public boolean hasCorona;
    public String image;
    public boolean isAdEnabled;

    // For friends
    // key: id, status: {"friends", "sent_request", "pending"}
    public HashMap<String, String> friends = new HashMap<>();
    public HashMap<String, String> friendsRequestSent = new HashMap<>();

    public UserData() {}

    public UserData(String name, String dob, String country, String city, String gender, boolean mightHaveCorona, boolean hasCorona, String image, boolean isAdEnabled) {
        this.name = name;
        this.dob = dob;
        this.country = country;
        this.city = city;
        this.gender = gender;
        this.mightHaveCorona = mightHaveCorona;
        this.hasCorona = hasCorona;
        this.image = image;
        this.isAdEnabled = isAdEnabled;
    }

    public UserData(String name, String dob, String country, String city, String gender, boolean mightHaveCorona, boolean hasCorona, String image, boolean isAdEnabled, HashMap<String, String> friends, HashMap<String, String> friendsRequestSent) {
        this.name = name;
        this.dob = dob;
        this.country = country;
        this.city = city;
        this.gender = gender;
        this.mightHaveCorona = mightHaveCorona;
        this.hasCorona = hasCorona;
        this.image = image;
        this.isAdEnabled = isAdEnabled;
        this.friends = friends;
        this.friendsRequestSent = friendsRequestSent;
    }

    public String print() {
        String result = "";
        result += "Name: " + name + "\n";
        result += "Date of birth: " + dob + "\n";
        result += "Country: " + country + "\n";
        result += "City: " + city + "\n";
        result += "Gender: " + gender + "\n";
        result += "Might have corona: " + mightHaveCorona + "\n";
        result += "Has corona: " + hasCorona + "\n";
        result += "Image URL: " + image + "\n";
        result += "Ads: " + isAdEnabled + "\n";

        result += "************** FRIENDS ************** \n";

        for (String key: friends.keySet()) {
            result += "@" + key + " -> Status: " + friends.get(key) + "\n";
        }

        for (String key: friendsRequestSent.keySet()) {
            result += "@" + key + " -> Request at: " + friendsRequestSent.get(key) + "\n";
        }


        return result;
    }

    public HashMap<String, String> getFriendsRequestSent() {
        return friendsRequestSent;
    }

    public void setFriendsRequestSent(HashMap<String, String> friendsRequestSent) {
        this.friendsRequestSent = friendsRequestSent;
    }

    public String getGender() {
        return gender;
    }

    public boolean isAdEnabled() {
        return isAdEnabled;
    }

    public void setAdEnabled(boolean adEnabled) {
        isAdEnabled = adEnabled;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public HashMap<String, String> getFriends() {
        return friends;
    }

    public void setFriends(HashMap<String, String> friends) {
        this.friends = friends;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isHasCorona() {
        return hasCorona;
    }

    public void setHasCorona(boolean hasCorona) {
        this.hasCorona = hasCorona;
    }

    public boolean isMightHaveCorona() {
        return mightHaveCorona;
    }

    public void setMightHaveCorona(boolean mightHaveCorona) {
        this.mightHaveCorona = mightHaveCorona;
    }
}
