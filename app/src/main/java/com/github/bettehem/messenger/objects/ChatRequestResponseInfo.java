package com.github.bettehem.messenger.objects;

public class ChatRequestResponseInfo {
    public boolean requestAccepted;
    public boolean correctPassword;
    public String username;

    public ChatRequestResponseInfo(boolean requestAccepted, boolean correctPassword, String username){
        this.requestAccepted = requestAccepted;
        this.correctPassword = correctPassword;
        this.username = username;
    }
}
