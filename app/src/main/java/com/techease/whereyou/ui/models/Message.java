package com.techease.whereyou.ui.models;

/**
 * Created by Adam Noor on 09-Feb-18.
 */

public class Message {
    private String content;
    private String username;

    public Message()
    {

    }

    public Message(String content, String username)
    {
        this.content=content;
        this.username=username;

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
