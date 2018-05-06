package com.github.bettehem.messenger.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.github.bettehem.androidtools.Preferences;
import com.github.bettehem.androidtools.dialog.CustomAlertDialog;
import com.github.bettehem.androidtools.interfaces.DialogButtonsListener;
import com.github.bettehem.messenger.MainActivity;
import com.github.bettehem.messenger.R;
import com.github.bettehem.messenger.tools.fcm.MessengerGcmListenerServiceGcm;
import com.github.bettehem.messenger.tools.adapters.ChatsScreenMessageAdapter;
import com.github.bettehem.messenger.tools.items.MessageItem;
import com.github.bettehem.messenger.tools.listeners.ChatItemListener;
import com.github.bettehem.messenger.tools.listeners.MessageItemListener;
import com.github.bettehem.messenger.tools.managers.ChatsManager;

import java.util.ArrayList;

public class ChatScreen extends Fragment implements View.OnClickListener, MessageItemListener {

    private static final int CHAT_VIEW = 0;
    private static final int PENDING_VIEW = 1;
    private static final int REQUEST_VIEW = 2;



    private View view;
    private String username;
    private ViewFlipper chatViews;

    private AppCompatEditText passwordEditText;
    private AppCompatButton acceptRequestButton;
    private AppCompatButton rejectRequestButton;

    private AppCompatImageButton emojiSelectionButton;
    private AppCompatEditText messageEditText;
    private AppCompatButton sendMessageButton;
    private RecyclerView messageRecycler;
    private ChatsScreenMessageAdapter messageAdapter;

    private ChatItemListener chatItemListener;
    private MessageItemListener messageItemListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chat_screen, container, false);

        //save the current fragment
        MainActivity.currentFragment = this;
        Preferences.saveString(getActivity(), "currentFragment", "ChatScreen");

        //set the current username
        username = Preferences.loadString(getActivity(), "username", "CurrentChat");

        messageItemListener = this;
        MessengerGcmListenerServiceGcm.setMessageItemListener(messageItemListener);

        setup();

        checkStatus(getActivity());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkStatus(getActivity());
    }

    private void setup(){
        toolbars();
        viewFlippers();
        editTexts();
        buttons();
        recyclers();
    }

    private void toolbars(){
        Preferences.saveString(getActivity(), "defaultToolbarText", MainActivity.toolbar.getTitle().toString());
        MainActivity.toolbar.setTitle(username);
    }

    private void viewFlippers(){
        chatViews = (ViewFlipper) view.findViewById(R.id.chatScreenViewFlipper);
        chatViews.setDisplayedChild(CHAT_VIEW);
    }

    private void buttons(){
        acceptRequestButton = (AppCompatButton) view.findViewById(R.id.acceptChatRequestButton);
        rejectRequestButton = (AppCompatButton) view.findViewById(R.id.rejectChatRequestButton);
        emojiSelectionButton = (AppCompatImageButton) view.findViewById(R.id.chatScreenEmojiSelectButton);
        sendMessageButton = (AppCompatButton) view.findViewById(R.id.chatScreenSendMessageButton);

        acceptRequestButton.setOnClickListener(this);
        rejectRequestButton.setOnClickListener(this);
        emojiSelectionButton.setOnClickListener(this);
        sendMessageButton.setOnClickListener(this);
    }

    private void editTexts(){
        passwordEditText = (AppCompatEditText) view.findViewById(R.id.chatScreenPasswordEditText);
        messageEditText = (AppCompatEditText) view.findViewById(R.id.chatScreenMessageEditText);
    }

    private void recyclers(){
        messageRecycler = (RecyclerView) view.findViewById(R.id.chatScreenMessageRecycler);

        messageAdapter = new ChatsScreenMessageAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        messageRecycler.setLayoutManager(layoutManager);
        messageRecycler.setHasFixedSize(true);
        messageRecycler.setAdapter(messageAdapter);
    }





    public void checkStatus(Context context){
        if (username != null){
            String status = Preferences.loadString(context, "chatStatus", username);
            switch (status){
                case "normal":
                    //show normal chat
                    chatViews.setDisplayedChild(CHAT_VIEW);
                    ArrayList<MessageItem> items = ChatsManager.getMessageItems(context, username);
                    messageAdapter.setMessageItems(items);
                    messageRecycler.scrollToPosition(items.size() - 1);
                    break;

                case "pending":
                    //show pending text
                    chatViews.setDisplayedChild(PENDING_VIEW);
                    AppCompatTextView textView = (AppCompatTextView) view.findViewById(R.id.pendingTextView);
                    textView.setText("Waiting for " + username + " to answer your chat request...");
                    break;

                case "chatRequest":
                    //show chat request screen
                    chatViews.setDisplayedChild(REQUEST_VIEW);
                    break;
            }
        }
    }

    public void setChatItemListener(ChatItemListener chatItemListener){
        this.chatItemListener = chatItemListener;
    }

    public void setMessageItemListener(){
        messageItemListener = this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.acceptChatRequestButton:
                if (passwordEditText.getText().toString().contentEquals("")){
                    CustomAlertDialog.make(getActivity(), "Warning!", "You can't use an empty password", false, "Ok", new DialogButtonsListener() {
                        @Override
                        public void onPositiveButtonClicked(String id) {

                        }

                        @Override
                        public void onNeutralButtonClicked(String id) {

                        }

                        @Override
                        public void onNegativeButtonClicked(String id) {

                        }
                    }, "requestResponseEmptyPasswordDialog").show();
                }else {
                    ChatsManager.responseToRequest(getActivity(), true, username, passwordEditText.getText().toString(), chatItemListener);
                }
                break;

            case R.id.rejectChatRequestButton:
                ChatsManager.responseToRequest(getActivity(), false, username, "", chatItemListener);
                break;

            case R.id.chatScreenSendMessageButton:
                if (!messageEditText.getText().toString().contentEquals("")){
                    ChatsManager.sendMessage(getActivity(), username, messageEditText.getText().toString(), messageItemListener);
                    messageAdapter.setMessageItems(ChatsManager.getMessageItems(getActivity(), username));
                    messageEditText.setText("");
                }
                break;

            case R.id.chatScreenEmojiSelectButton:
                Toast.makeText(getActivity(), "Not implemented yet!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onMessageListUpdated(Context context) {
        try {
            ArrayList<MessageItem> items = ChatsManager.getMessageItems(context, username);
            if (Preferences.loadBoolean(context, "appVisible")){
                messageAdapter.setMessageItems(items);
                messageRecycler.scrollToPosition(items.size() - 1);
            }
            ChatsManager.editChatItem(context, username, "", items.get(items.size() - 1).mMessage, items.get(items.size() - 1).mTime);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageItemUpdated(MessageItem item) {
        messageAdapter.updateItem(item);
    }
}
