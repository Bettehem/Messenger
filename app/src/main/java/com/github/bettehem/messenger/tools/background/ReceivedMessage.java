package com.github.bettehem.messenger.tools.background;

import android.content.Context;
import android.os.AsyncTask;

import com.github.bettehem.androidtools.Preferences;
import com.github.bettehem.messenger.tools.listeners.GcmReceivedListener;
import com.github.bettehem.messenger.tools.managers.ChatsManager;
import com.github.bettehem.messenger.tools.ui.CustomProgressDialog;
import com.github.bettehem.messenger.tools.users.Sender;

public class ReceivedMessage {

    private Context context;
    private GcmReceivedListener messageListener;
    private String senderDataString;
    private String rawMessage;
    private Sender senderData;
    private String message;
    private String messageId;

    public ReceivedMessage (Context context, boolean showDialog, String senderDataString, String rawMessage, String messageId){
        this.context = context;
        //if (showDialog){
        //    dialog(context);
        //}
        this.senderDataString = senderDataString;
        this.rawMessage = rawMessage;
        this.messageId = messageId;
    }

    private void dialog(Context context){
        CustomProgressDialog progressDialog = new CustomProgressDialog(context, "New Message", "Loading", false);
        if (Preferences.loadBoolean(context, "appVisible")){
            progressDialog.show();
        }


    }

    public void getMessage(){
        new GetMessage().execute();
    }


    public void setMessageListener(GcmReceivedListener messageListener){
        this.messageListener = messageListener;
    }




    private void message(String senderDataString, String rawMessage){
        senderData = ChatsManager.getSenderData(context, senderDataString);
        message = ChatsManager.getMessage(context, senderData, rawMessage);
    }


    private class GetMessage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params){
            message(senderDataString, rawMessage);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (messageListener != null){
                messageListener.onMessageReceived(senderData, message, messageId);
            }
        }
    }
}
