package com.hfad.sayhello.Model;

public class Users {

    private String id, imageURL, username, msgSnippet, status;

    public Users() {
    }

    public Users(String id, String username, String imageURL, String msgSnippet, String status) {
        this.id = id;
        this.username = username;
        this.imageURL = imageURL;
        this.msgSnippet = msgSnippet;
        this.status   = status;

    }

    public String getMsgSnippet() {
        return msgSnippet;
    }

    public void setMsgSnippet(String msgSnippet) {
        this.msgSnippet = msgSnippet;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
