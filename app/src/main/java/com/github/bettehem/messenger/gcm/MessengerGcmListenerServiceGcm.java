package com.github.bettehem.messenger.gcm;

import android.content.Intent;
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
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MessengerGcmListenerServiceGcm extends FirebaseMessagingService implements GcmReceivedListener {
    private static final String TOPIC_START = "/topics/";

    @Override
    public void onMessageReceived(RemoteMessage message){
        String from = message.getFrom();
        final Map data = message.getData();

        String type = (String) data.get("type");
        Handler mainHandler = new Handler(getApplication().getMainLooper());
        Runnable myRunnable;

        if (type != null){

            switch (type.toLowerCase()){

                case "notification":
                    notification((String)  data.get("title"), (String) data.get("message"), false);
                    break;

                case "chatrequest":
                    final String requestSender = getSender((String) data.get("sender"));
                    myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            ChatsManager.handleChatRequest(getApplicationContext(), requestSender, (String) data.get("key"));
                        }
                    };
                    mainHandler.post(myRunnable);
                    break;

                case "message":
                    new ReceivedMessage(getApplicationContext(), true, (String) data.get("sender"), (String) data.get("message"));
                    break;

                case "requestresponse":
                    String responseSender = getSender((String) data.get("sender"));
                    boolean requestAccepted = Boolean.valueOf((String) data.get("requestAccepted"));
                    String password = (String) data.get("password");
                    RequestResponse requestResponse = new RequestResponse(getApplicationContext(), requestAccepted, responseSender, password);
                    requestResponse.handleResponse();
                    break;

                case "startchat":
                    final String chatStartSender = getSender((String) data.get("sender"));
                    boolean correctPassword = Boolean.valueOf((String) data.get("correctPassword"));
                    if (correctPassword){
                        myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                ChatsManager.startNormalChat(getApplication(), chatStartSender, null);
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
