package com.github.bettehem.messenger.tools.listeners

interface TopicListener{
    fun onTopicAdded(topicName: String)
    fun onTopicDeleted(topicName : String)
}