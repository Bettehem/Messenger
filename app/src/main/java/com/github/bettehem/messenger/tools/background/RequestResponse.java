package com.github.bettehem.messenger.tools.background;

import android.content.Context;
import android.os.AsyncTask;

import com.github.bettehem.messenger.objects.ChatRequestResponseInfo;
import com.github.bettehem.messenger.tools.listeners.ChatRequestListener;
import com.github.bettehem.messenger.tools.managers.ChatsManager;

public class RequestResponse {
    private Context context;
    private String username;
    private String password;
    private static ChatRequestListener chatRequestListener;
    private ChatRequestResponseInfo chatRequestResponseInfo;
    private boolean acceptedRequest;

    public RequestResponse(Context context, boolean acceptedRequest, String username, String password){
        this.context = context;
        this.acceptedRequest = acceptedRequest;
        this.username = username;
        this.password = password;
    }

    public void handleResponse(){
        new ResponseHandler().execute();
    }

    public static void setRequestListener(ChatRequestListener chatRequestListener){
        RequestResponse.chatRequestListener = chatRequestListener;
    }

    private class ResponseHandler extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params){
            chatRequestResponseInfo = ChatsManager.handleChatRequestResponse(context, acceptedRequest, username, password);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (chatRequestListener != null){
                chatRequestListener.onChatRequestResponse(chatRequestResponseInfo);
            }
        }
    }

}
