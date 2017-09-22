package com.github.bettehem.messenger.objects;

import android.support.v4.app.FragmentManager;


public class ChatPreparerInfo {

    public String username;
    public String status;
    public String encryptedUsername;
    public int frameId;
    public FragmentManager fragmentManager;

    public ChatPreparerInfo(String username, String status, String encryptedUsername, int frameId, FragmentManager fragmentManager){
        this.username = username;
        this.status = status;
        this.encryptedUsername = encryptedUsername;
        this.frameId = frameId;
        this.fragmentManager = fragmentManager;
    }

}
