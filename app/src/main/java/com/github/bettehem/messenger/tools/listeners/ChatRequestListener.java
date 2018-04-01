package com.github.bettehem.messenger.tools.listeners;

import android.content.Context;

import com.github.bettehem.messenger.objects.ChatPreparerInfo;
import com.github.bettehem.messenger.objects.ChatRequestResponseInfo;

public interface ChatRequestListener {
    void onChatPrepared(ChatPreparerInfo chatPreparerInfo);
    void onChatRequestResponse(Context context, ChatRequestResponseInfo responseInfo);
}
