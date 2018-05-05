package com.github.bettehem.messenger.tools.items;

import com.github.bettehem.androidtools.misc.Time;

public class ChatItem {

    public String name;
    public String message;
    public Time time;
    public String emoji;

    public ChatItem(String name, String message, Time time, String emoji) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.emoji = emoji;
    }
}
