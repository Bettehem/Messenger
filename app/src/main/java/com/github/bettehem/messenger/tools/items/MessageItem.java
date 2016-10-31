package com.github.bettehem.messenger.tools.items;

import com.github.bettehem.androidtools.misc.Time;

public class MessageItem {
    public String mMessage;
    public Time mTime;
    public boolean mIsOwnMessage;

    public MessageItem (String message, Time time, boolean isOwnMessage){
        mMessage = message;
        mTime = time;
        mIsOwnMessage = isOwnMessage;
    }


}
