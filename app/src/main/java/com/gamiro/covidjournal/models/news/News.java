
package com.gamiro.covidjournal.models.news;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class News {

    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("excerpt")
    @Expose
    private String excerpt;
    @SerializedName("heat")
    @Expose
    private Integer heat;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("webUrl")
    @Expose
    private String webUrl;
    @SerializedName("ampWebUrl")
    @Expose
    private Object ampWebUrl;
    @SerializedName("cdnAmpWebUrl")
    @Expose
    private Object cdnAmpWebUrl;
    @SerializedName("publishedDateTime")
    @Expose
    private String publishedDateTime;
    @SerializedName("updatedDateTime")
    @Expose
    private Object updatedDateTime;
    @SerializedName("provider")
    @Expose
    private Provider provider;
    @SerializedName("images")
    @Expose
    private List<NewsImage> images;
    @SerializedName("locale")
    @Expose
    private String locale;
    @SerializedName("categories")
    @Expose
    private List<String> categories = null;
    @SerializedName("topics")
    @Expose
    private List<String> topics = null;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public Integer getHeat() {
        return heat;
    }

    public void setHeat(Integer heat) {
        this.heat = heat;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public Object getAmpWebUrl() {
        return ampWebUrl;
    }

    public void setAmpWebUrl(Object ampWebUrl) {
        this.ampWebUrl = ampWebUrl;
    }

    public Object getCdnAmpWebUrl() {
        return cdnAmpWebUrl;
    }

    public void setCdnAmpWebUrl(Object cdnAmpWebUrl) {
        this.cdnAmpWebUrl = cdnAmpWebUrl;
    }

    public String getPublishedDateTime() {
        return publishedDateTime;
    }

    public void setPublishedDateTime(String publishedDateTime) {
        this.publishedDateTime = publishedDateTime;
    }

    public Object getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(Object updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public List<NewsImage> getImages() {
        return images;
    }

    public void setImages(List<NewsImage> images) {
        this.images = images;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

}
