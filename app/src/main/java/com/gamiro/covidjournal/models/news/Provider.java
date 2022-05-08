
package com.gamiro.covidjournal.models.news;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Provider {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("images")
    @Expose
    private Object images;
    @SerializedName("publishers")
    @Expose
    private Object publishers;
    @SerializedName("authors")
    @Expose
    private Object authors;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Object getImages() {
        return images;
    }

    public void setImages(Object images) {
        this.images = images;
    }

    public Object getPublishers() {
        return publishers;
    }

    public void setPublishers(Object publishers) {
        this.publishers = publishers;
    }

    public Object getAuthors() {
        return authors;
    }

    public void setAuthors(Object authors) {
        this.authors = authors;
    }

}
