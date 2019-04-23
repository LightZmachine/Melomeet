package com.td.yassine.zekri.melomeet.models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class ChatMessage {
    private String message;
    private String author;
    private String authorID;
    private String receiverID;
    private String discussionID;
    private @ServerTimestamp
    Date date_created;

    public ChatMessage() {
    }

    public ChatMessage(String message, String author, String authorID, String receiverID, String discussionID, Date date_created) {
        this.message = message;
        this.author = author;
        this.authorID = authorID;
        this.receiverID = receiverID;
        this.discussionID = discussionID;
        this.date_created = date_created;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDiscussionID() {
        return discussionID;
    }

    public void setDiscussionID(String discussionID) {
        this.discussionID = discussionID;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "message='" + message + '\'' +
                ", author='" + author + '\'' +
                ", authorID='" + authorID + '\'' +
                ", receiverID='" + receiverID + '\'' +
                ", discussionID='" + discussionID + '\'' +
                ", date_created=" + date_created +
                '}';
    }
}
