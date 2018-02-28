package com.github.bettehem.messenger.tools.listeners;

import android.view.View;

import com.github.bettehem.messenger.tools.items.ChatItem;

import java.util.ArrayList;

public interface ChatItemListener {
    void onItemClicked(View v, int position);
    boolean onItemLongCLicked(View v, int position);
    void onRequestAccepted(String username, String key);
    void onChatItemListUpdated(ArrayList<ChatItem> newItems);
}
