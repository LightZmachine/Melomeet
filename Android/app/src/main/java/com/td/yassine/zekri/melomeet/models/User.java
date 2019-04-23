package com.td.yassine.zekri.melomeet.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private static User instance;
    private String email;
    private String username;
    private String birth_date;
    private String id;
    private String status;
    private String thumb_profile_image;
    private String name;
    private String firstname;
    private String description;
    private String fav_artist;
    private String fav_single;
    private int number_following;
    private int number_followers;
    private int number_posts;

    public User(String email, String username, String birth_date, String id, String status, String thumb_profile_image, String name, String firstname, String description, String fav_artist, String fav_single, int number_following, int number_followers, int number_posts) {
        this.email = email;
        this.username = username;
        this.birth_date = birth_date;
        this.id = id;
        this.status = status;
        this.thumb_profile_image = thumb_profile_image;
        this.name = name;
        this.firstname = firstname;
        this.description = description;
        this.fav_artist = fav_artist;
        this.fav_single = fav_single;
        this.number_following = number_following;
        this.number_followers = number_followers;
        this.number_posts = number_posts;
    }

    public User() {
        this.setNumber_following(0);
        this.setNumber_followers(0);
        this.setNumber_posts(0);
        this.setStatus("I love Melomeet !");
        this.setDescription("Write a description of yourself ;)");
        this.setFav_artist("Your favourite artist ??");
        this.setFav_single("Your favourite single ??");
        this.setThumb_profile_image("default");
    }

    protected User(Parcel in) {
        email = in.readString();
        username = in.readString();
        birth_date = in.readString();
        id = in.readString();
        status = in.readString();
        thumb_profile_image = in.readString();
        name = in.readString();
        firstname = in.readString();
        description = in.readString();
        fav_artist = in.readString();
        fav_single = in.readString();
        number_following = in.readInt();
        number_followers = in.readInt();
        number_posts = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFav_artist() {
        return fav_artist;
    }

    public void setFav_artist(String fav_artist) {
        this.fav_artist = fav_artist;
    }

    public String getFav_single() {
        return fav_single;
    }

    public void setFav_single(String fav_single) {
        this.fav_single = fav_single;
    }

    public int getNumber_following() {
        return number_following;
    }

    public void setNumber_following(int number_following) {
        this.number_following = number_following;
    }

    public int getNumber_followers() {
        return number_followers;
    }

    public void setNumber_followers(int number_followers) {
        this.number_followers = number_followers;
    }

    public int getNumber_posts() {
        return number_posts;
    }

    public void setNumber_posts(int number_posts) {
        this.number_posts = number_posts;
    }

    public String getThumb_profile_image() {
        return thumb_profile_image;
    }

    public void setThumb_profile_image(String thumb_profile_image) {
        this.thumb_profile_image = thumb_profile_image;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", birth_date='" + birth_date + '\'' +
                ", id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", thumb_profile_image='" + thumb_profile_image + '\'' +
                ", name='" + name + '\'' +
                ", firstname='" + firstname + '\'' +
                ", description='" + description + '\'' +
                ", fav_artist='" + fav_artist + '\'' +
                ", fav_single='" + fav_single + '\'' +
                ", number_following=" + number_following +
                ", number_followers=" + number_followers +
                ", number_posts=" + number_posts +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(email);
        parcel.writeString(username);
        parcel.writeString(birth_date);
        parcel.writeString(id);
        parcel.writeString(status);
        parcel.writeString(thumb_profile_image);
        parcel.writeString(name);
        parcel.writeString(firstname);
        parcel.writeString(description);
        parcel.writeString(fav_artist);
        parcel.writeString(fav_single);
        parcel.writeInt(number_following);
        parcel.writeInt(number_followers);
        parcel.writeInt(number_posts);
    }
}

