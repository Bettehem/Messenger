package com.github.bettehem.messenger.tools.users;

public class UserProfile {
    public String emoji;
    public String name;
    public String status;
    private boolean hasData;

    public UserProfile(String emoji, String name, String status){
        this.emoji = emoji;
        this.name = name;
        this.status = status;
        hasData = true;
    }

    public boolean hasData(){
        return (hasData && !emoji.contentEquals("") && !name.contentEquals("") && !status.contentEquals(""));
    }
}
