package com.damsdev.tbc;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

public class ChatItem {
    String chatName;
    Long creationDate;


    public ChatItem(String chatName){
        this.chatName = chatName;
    }
    public ChatItem() {
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public java.util.Map<String, String> getCreationDate() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public Long getCreationDateLong() {
        return creationDate;
    }
    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

}
