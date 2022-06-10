package com.h5200006_burak_oztas_yonlendirilmiscalisma;

public class User {

    private String uid;
    private String username;
    private String status;
    private String userImage;

    public User() {

    }

    public User(String uid, String username, String status, String userImage) {
        this.uid = uid;
        this.username = username;
        this.status = status;
        this.userImage = userImage;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

}