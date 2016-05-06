package com.github.bettehem.messenger.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.github.bettehem.androidtools.notification.CustomNotification;
import com.github.bettehem.messenger.MainActivity;
import com.github.bettehem.messenger.R;
import com.github.bettehem.messenger.tools.background.ReceivedMessage;
import com.github.bettehem.messenger.tools.background.RequestResponse;
import com.github.bettehem.messenger.tools.listeners.GcmReceivedListener;
import com.github.bettehem.messenger.tools.managers.ChatsManager;
import com.github.bettehem.messenger.tools.users.Sender;
import com.google.android.gms.gcm.GcmListenerService;

public class MessengerGcmListenerServiceGcm extends GcmListenerService implements GcmReceivedListener {
    private static final String TOPIC_START = "/topics/";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String type = data.getString("type");

        if (type != null){

            switch (type.toLowerCase()){

                case "notification":
                    notification(data.getString("title"), data.getString("message"), false);
                    break;

                case "chatrequest":
                    String requestSender = getSender(data.getString("sender"));
                    ChatsManager.handleChatRequest(getApplicationContext(), data.getString("sender").replace(ChatsManager.SPLITTER, " "), data.getString("key"));
                    break;

                case "message":
                    new ReceivedMessage(getApplicationContext(), true, data.getString("sender"), data.getString("message"));
                    break;

                case "requestresponse":
                    String responseSender = getSender(data.getString("sender"));
                    boolean requestAccepted = Boolean.valueOf(data.getString("requestAccepted"));
                    String password = data.getString("password");
                    RequestResponse requestResponse = new RequestResponse(getApplicationContext(), requestAccepted, responseSender, password);
                    requestResponse.handleResponse();
                    break;

                case "startchat":
                    final String chatStartSender = getSender(data.getString("sender"));
                    boolean correctPassword = Boolean.valueOf(data.getString("correctPassword"));
                    if (correctPassword){
                        Handler mainHandler = new Handler(getApplication().getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                ChatsManager.startNormalChat(getApplicationContext(), chatStartSender);
                            }
                        };
                        mainHandler.post(myRunnable);

                    }else {
                        //TODO: Tell user that their password was incorrect
                    }
                    break;

            }
        }

    }


    private void notification(String title, String message, boolean isSecretMessage){
        //TODO: implement user icons
        //TODO: add settings check if has notifications disabled
        if (isSecretMessage){
            //TODO: Remove hard-coded string
            CustomNotification.make(getApplicationContext(), R.mipmap.ic_launcher, title, "New Message from " + title.split("enger - ")[1], new Intent(this, MainActivity.class), false, true).show();
        }else{
            CustomNotification.make(getApplicationContext(), R.mipmap.ic_launcher, title, message, new Intent(this, MainActivity.class), false, true).show();
        }
    }

    private String getSender(String sender){
        return sender.contains(ChatsManager.SPLITTER) ? sender.replace(ChatsManager.SPLITTER, " ") : sender;
    }



    @Override
    public void onMessageReceived(Sender senderData, String message) {
        Looper.prepare();
        notification("Messenger - " + senderData.userName, message, senderData.isSecretMessage);
    }
}
