package com.github.bettehem.messenger.tools.managers

import android.content.Context
import com.github.bettehem.androidtools.Preferences
import com.github.bettehem.messenger.tools.listeners.TopicListener

class TopicManager(context: Context, topicListener: TopicListener){

    var mContext = context
    var listener = topicListener

    fun saveTopics(topics : ArrayList<String>){
        var topicList : Array<String> = emptyArray()
        for (topic in topics){
            topicList += topic
        }
        Preferences.saveStringArray(mContext, "topics", topicList)
    }

    //adds a topic to the list of topics
    fun addTopic(topicName : String){
        var topics = Preferences.loadStringArray(mContext, "topics", "FCMTopics")
        topics += topicName
        Preferences.saveStringArray(mContext, "topics", topics, "FCMTopics")
        listener.onTopicAdded(topicName)
    }

    //gets the list of topics
    fun getTopics() : ArrayList<String>{
        var topics : ArrayList<String> = ArrayList()
        for (topic in Preferences.loadStringArray(mContext, "topics", "FCMTopics")){
            if (topic != "")
            topics.add(topic)
        }
        return topics
    }

    //deletes a topic
    fun deleteTopic(topicName: String){
        var topics = getTopics()
        topics.remove(topicName)
        saveTopics(topics)
        listener.onTopicDeleted(topicName)
    }
}