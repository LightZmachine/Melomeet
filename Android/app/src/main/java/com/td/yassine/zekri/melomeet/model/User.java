package com.td.yassine.zekri.melomeet.model;

public class User {

    /*
        Singleton Support
     */
    private static User instance;
    private String email;
    private String username;
    private String birth_date;
    private String id;
    private String status;
    private String image;
    private String thumb_image;
    private String name;
    private String firstname;
    private String description;
    private String fav_artist;
    private String fav_single;
    private int number_following;
    private int number_followers;
    private int number_posts;

    private User(String email, String username, String birth_date, String id, String status, String image, String thumb_image, String name, String firstname, String description, String fav_artist, String fav_single, int number_following, int number_followers, int number_posts) {
        this.email = email;
        this.username = username;
        this.birth_date = birth_date;
        this.id = id;
        this.status = status;
        this.image = image;
        this.thumb_image = thumb_image;
        this.name = name;
        this.firstname = firstname;
        this.description = description;
        this.fav_artist = fav_artist;
        this.fav_single = fav_single;
        this.number_following = number_following;
        this.number_followers = number_followers;
        this.number_posts = number_posts;
    }

    private User() {
        this.setNumber_following(0);
        this.setNumber_followers(0);
        this.setNumber_posts(0);
        this.setStatus("I love Melomeet !");
        this.setDescription("Write a description of yourself ;)");
        this.setFav_artist("Your favourite artist ??");
        this.setFav_single("Your favourite single ??");
        this.setImage("default");
        this.setThumb_image("default");
    }

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    /*
        Normal Object code
     */
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", birth_date='" + birth_date + '\'' +
                ", id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", image='" + image + '\'' +
                ", thumb_image='" + thumb_image + '\'' +
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
}
