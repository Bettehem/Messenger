package com.github.bettehem.messenger.tools.items;

import com.github.bettehem.androidtools.misc.Time;

public class MessageItem {
    public String mMessage;
    public String mMessageId;
    public Time mTime;
    public boolean mIsOwnMessage;
    public boolean mMessageDelivered = false;

    public MessageItem (String message, String messageId, Time time, boolean isOwnMessage){
        mMessage = message;
        mMessageId = messageId;
        mTime = time;
        mIsOwnMessage = isOwnMessage;
    }

    public MessageItem (String message, String messageId, Time time, boolean isOwnMessage, boolean messageDelivered){
        mMessage = message;
        mMessageId = messageId;
        mTime = time;
        mIsOwnMessage = isOwnMessage;
        mMessageDelivered = messageDelivered;
    }

    public void setMessageDelivered(boolean delivered){
        mMessageDelivered = delivered;
    }
}
