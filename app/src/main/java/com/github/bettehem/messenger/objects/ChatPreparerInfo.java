package com.github.bettehem.messenger.objects;

import android.support.v4.app.FragmentManager;


public class ChatPreparerInfo {

    public String username;
    public String status;
    public int frameId;
    public FragmentManager fragmentManager;

    public ChatPreparerInfo(String username, String status, int frameId, FragmentManager fragmentManager){
        this.username = username;
        this.status = status;
        this.frameId = frameId;
        this.fragmentManager = fragmentManager;
    }

}
