package com.github.bettehem.messenger.tools.listeners;

import com.github.bettehem.messenger.tools.users.Sender;

public interface GcmReceivedListener {
    void onMessageReceived(Sender senderData, String message, String messageId);
}
