package com.td.yassine.zekri.melomeet.models;

public class Discussion {
    private String discussionID;

    public Discussion() {
    }

    public Discussion(String discussionID) {
        this.discussionID = discussionID;
    }

    public String getDiscussionID() {
        return discussionID;
    }

    public void setDiscussionID(String discussionID) {
        this.discussionID = discussionID;
    }

    @Override
    public String toString() {
        return "Discussion{" +
                "discussionID='" + discussionID + '\'' +
                '}';
    }
}
