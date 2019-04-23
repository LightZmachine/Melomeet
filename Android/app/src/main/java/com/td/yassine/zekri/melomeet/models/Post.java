package com.td.yassine.zekri.melomeet.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

@IgnoreExtraProperties
public class Post {

    @Exclude
    private User user;
    @Exclude
    private ArrayList<String> imgUrls;
    private String title;
    private String content;
    private String user_id;
    private String post_id;
    private @ServerTimestamp
    Date date_created;
    private boolean hasAttachments;

    public Post() {
    }

    public Post(User user, ArrayList<String> imgUrls, String title, String content, String user_id, String post_id, Date date_created, boolean hasAttachments) {
        this.user = user;
        this.imgUrls = imgUrls;
        this.title = title;
        this.content = content;
        this.user_id = user_id;
        this.post_id = post_id;
        this.date_created = date_created;
        this.hasAttachments = hasAttachments;
    }

    public boolean isHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    @Exclude
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Exclude
    public ArrayList<String> getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(ArrayList<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    @Override
    public String toString() {
        return "Post{" +
                "user=" + user +
                ", imgUrls=" + imgUrls +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", user_id='" + user_id + '\'' +
                ", post_id='" + post_id + '\'' +
                ", date_created=" + date_created +
                ", hasAttachments=" + hasAttachments +
                '}';
    }
}
