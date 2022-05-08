package com.gamiro.covidjournal.models.user;

public class UserPost {

    // Shared by both
    public String postType;
    public String timeWhenPostAdded;
    public String title;
    public String date;
    public String time;
    public String location;
    public boolean isAccepted;

    // Person - could be none and add name instead
    public String id;
    public String whoMadePost;
    public String personName;
    public String whoMadePostPersonName;

    // Activity
    public int countPeople;
    public String description;

    public UserPost() { /* empty needed */ }

    // Person constructor
    public UserPost(String postType, String timeWhenPostAdded, String title,
                    String date, String time, String location,
                    boolean isAccepted, String personName,
                    String id, String whoMadePost, String whoMadePostPersonName) {
        this.postType = postType;
        this.timeWhenPostAdded = timeWhenPostAdded;
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.isAccepted = isAccepted;
        this.personName = personName;
        this.id = id;
        this.whoMadePost = whoMadePost;
        this.whoMadePostPersonName = whoMadePostPersonName;
    }

    // Activity constructor
    public UserPost(String postType, String timeWhenPostAdded,
                    String title, String date, String time, String location,
                    String description, int countPeople) {
        this.postType = postType;
        this.timeWhenPostAdded = timeWhenPostAdded;
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.isAccepted = true;
        this.description = description;
        this.countPeople = countPeople;
    }

    public static UserPost clone(UserPost post) {
        if (post.getPostType().equals("person")) {
            return new UserPost(
                    post.getPostType(),
                    post.getTimeWhenPostAdded(),
                    post.getTitle(),
                    post.getDate(),
                    post.getTime(),
                    post.getLocation(),
                    post.isAccepted(),
                    post.getPersonName(),
                    post.getId(),
                    post.getWhoMadePost(),
                    post.getWhoMadePostPersonName()
            );
        } else {
            return new UserPost(
                    post.getPostType(),
                    post.getTimeWhenPostAdded(),
                    post.getTitle(),
                    post.getDate(),
                    post.getTime(),
                    post.getLocation(),
                    post.getDescription(),
                    post.getCountPeople()
            );
        }
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getWhoMadePost() {
        return whoMadePost;
    }

    public void setWhoMadePost(String whoMadePost) {
        this.whoMadePost = whoMadePost;
    }

    public String getWhoMadePostPersonName() {
        return whoMadePostPersonName;
    }

    public void setWhoMadePostPersonName(String whoMadePostPersonName) {
        this.whoMadePostPersonName = whoMadePostPersonName;
    }

    // Getters and setters
    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getTimeWhenPostAdded() {
        return timeWhenPostAdded;
    }

    public void setTimeWhenPostAdded(String timeWhenPostAdded) {
        this.timeWhenPostAdded = timeWhenPostAdded;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCountPeople() {
        return countPeople;
    }

    public void setCountPeople(int countPeople) {
        this.countPeople = countPeople;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
