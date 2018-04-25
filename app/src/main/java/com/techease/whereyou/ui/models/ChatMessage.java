package com.techease.whereyou.ui.models;

import java.util.Date;

/**
 * Created by k.zahid on 4/18/18.
 */

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private long messageTime;
    private String id;
    private boolean link;

    public ChatMessage(String messageText, String messageUser, String id, boolean link) {
        this.messageText = messageText;
        this.messageUser = messageUser;

        // Initialize to current time
        messageTime = new Date().getTime();
        this.id = id;
        this.link = link;
    }


    public ChatMessage() {

    }

    public boolean isLink() {
        return link;
    }

    public void setLink(boolean link) {
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
