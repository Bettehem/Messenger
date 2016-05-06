package com.github.bettehem.messenger.tools.listeners;

import com.github.bettehem.messenger.objects.ChatPreparerInfo;
import com.github.bettehem.messenger.objects.ChatRequestResponseInfo;

public interface ChatRequestListener {
    void onChatPrepared(ChatPreparerInfo chatPreparerInfo);
    void onChatRequestResponse(ChatRequestResponseInfo responseInfo);
}
