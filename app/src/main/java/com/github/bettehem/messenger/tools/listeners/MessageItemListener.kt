package com.github.bettehem.messenger.tools.listeners

import android.content.Context
import com.github.bettehem.messenger.tools.items.MessageItem

interface MessageItemListener{
    fun onMessageListUpdated(context: Context)
    fun onMessageItemUpdated(item: MessageItem)
}