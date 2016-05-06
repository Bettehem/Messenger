package com.github.bettehem.messenger.tools.background;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;

import com.github.bettehem.androidtools.Preferences;
import com.github.bettehem.messenger.objects.ChatPreparerInfo;
import com.github.bettehem.messenger.tools.listeners.ChatRequestListener;
import com.github.bettehem.messenger.tools.managers.ChatsManager;
import com.github.bettehem.messenger.tools.ui.CustomProgressDialog;

public class ChatPreparer {
    private Context context;
    private ChatRequestListener chatRequestListener;
    private FragmentManager fragmentManager;
    private String username;
    private String password;
    private static CustomProgressDialog progressDialog;
    private ChatPreparerInfo chatPreparerInfo;

    public ChatPreparer (Context context, boolean showDialog, FragmentManager fragmentManager, String username, String password){
        this.context = context;
        if (showDialog){
            dialog(context);
        }
        this.fragmentManager = fragmentManager;
        this.username = username;
        this.password = password;
    }

    private void dialog(Context context){
        progressDialog = new CustomProgressDialog(context, "New Message", "Loading", false);
        if (Preferences.loadBoolean(context, "appVisible")){
            progressDialog.show();
        }


    }

    public void prepare(){
        new GetMessage().execute();
    }


    public void setRequestListener(ChatRequestListener chatRequestListener){
        this.chatRequestListener = chatRequestListener;
    }


    private class GetMessage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params){
            chatPreparerInfo = ChatsManager.prepareChat(context, fragmentManager, username, password);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (chatRequestListener != null){
                chatRequestListener.onChatPrepared(chatPreparerInfo);
            }
            progressDialog.dismiss();
        }
    }
}
