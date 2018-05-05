package com.github.bettehem.messenger.tools.users;

public class Sender {
    public String userName;
    public boolean isSecretMessage;
    public String emoji;

    public Sender(String userName, boolean isSecretMessage, String emoji){
        this.userName = userName;
        this.isSecretMessage = isSecretMessage;
        this.emoji = emoji;
    }
}
