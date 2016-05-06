package com.github.bettehem.messenger.tools.users;

public class Sender {
    public String userName;
    public boolean isSecretMessage;

    public Sender(String userName, boolean isSecretMessage){
        this.userName = userName;
        this.isSecretMessage = isSecretMessage;
    }
}
