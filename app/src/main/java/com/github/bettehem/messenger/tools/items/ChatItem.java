package com.github.bettehem.messenger.tools.items;

import com.github.bettehem.androidtools.misc.Time;
import java.util.Calendar;

public class ChatItem implements Comparable<ChatItem>{

    public String name;
    public String message;
    public Time time;

    public ChatItem(String name, String message, Time time) {
        this.name = name;
        this.message = message;
        this.time = time;
    }

    @Override
    public int compareTo(ChatItem chatItem) {
        //check if null
        if (chatItem == null || chatItem.time == null) {
            throw new NullPointerException("anotherCalendar == null");
        }

        //create Calendar object
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(chatItem.time.year), Integer.parseInt(chatItem.time.month), Integer.parseInt(chatItem.time.date), Integer.parseInt(chatItem.time.hour), Integer.parseInt(chatItem.time.minute), Integer.parseInt(chatItem.time.second));

        //get the time in milliseconds
        long timeInMillis = time.getTimeInMillis();

        //get the comparable time in milliseconds
        long anotherTimeInMillis = chatItem.time.getTimeInMillis();
        if (timeInMillis > anotherTimeInMillis) {
            return 1;
        }
        if (timeInMillis == anotherTimeInMillis) {
            return 0;
        }
        return -1;
    }
}
