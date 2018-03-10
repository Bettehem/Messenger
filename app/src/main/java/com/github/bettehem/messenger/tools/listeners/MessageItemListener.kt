package com.github.bettehem.messenger.tools.listeners

import com.github.bettehem.messenger.tools.items.MessageItem

interface MessageItemListener{
    fun onMessageListUpdated()
    fun onMessageItemUpdated(item: MessageItem)
}