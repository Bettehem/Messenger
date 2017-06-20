package com.github.bettehem.messenger.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.bettehem.androidtools.Preferences;
import com.github.bettehem.androidtools.dialog.CustomAlertDialog;
import com.github.bettehem.androidtools.interfaces.DialogButtonsListener;
import com.github.bettehem.messenger.MainActivity;
import com.github.bettehem.messenger.R;
import com.github.bettehem.messenger.tools.background.ChatPreparer;

public class NewChatAuthFragment extends Fragment implements View.OnLongClickListener, DialogButtonsListener, View.OnClickListener{

    private View view;

    private AppCompatEditText usernameEditText;
    private AppCompatEditText passwordEditText;
    private AppCompatEditText confirmPasswordEditText;
    private AppCompatButton startChatButton;
    private AppCompatTextView passwordInfoTextView;
    private boolean isVisible = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_chat_auth, container, false);

        //Save current fragment
        MainActivity.currentFragment = this;
        Preferences.saveString(getActivity(), "currentFragment", "NewChatAuthFragment");

        setup();

        return view;
    }

    private void setup(){
        toolbars();
        editTexts();
        buttons();
        textViews();
    }

    private void toolbars(){
        //TODO: Remove hard-coded strings
        MainActivity.toolbar.setTitle("Messenger");
        MainActivity.toolbar.setSubtitle("New Chat");
    }

    private void editTexts(){
        usernameEditText = (AppCompatEditText) view.findViewById(R.id.newChatAuthUsername);
        passwordEditText = (AppCompatEditText) view.findViewById(R.id.newChatAuthPassword);
        confirmPasswordEditText = (AppCompatEditText) view.findViewById(R.id.newChatAuthConfirmPassword);
    }

    private void buttons(){
        startChatButton = (AppCompatButton) view.findViewById(R.id.newChatAuthStartChatButton);

        startChatButton.setOnClickListener(this);
    }

    private void textViews(){
        passwordInfoTextView = (AppCompatTextView) view.findViewById(R.id.newChatAuthPasswordInfoTextView);
        passwordInfoTextView.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        boolean returnValue;

        switch (v.getId()){
            case R.id.newChatAuthPasswordInfoTextView:
                togglePasswordVisibility();
                returnValue =  true;
                break;

            default:
                returnValue = false;
                break;
        }
        return returnValue;
    }

    private void togglePasswordVisibility(){
        CustomAlertDialog.make(getActivity(), "Password", "Do you want to toggle the password visibility?", false, "Toggle", "Cancel", this, "toggleAuthPasswordVisibilityDialog").show();
    }

    @Override
    public void onPositiveButtonClicked(String id) {

        //FIXME: Fix visibility toggle
        switch (id){
            case "toggleAuthPasswordVisibilityDialog":
                if(isVisible){
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    confirmPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    isVisible = true;
                }else {
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
                    confirmPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    isVisible = false;
                }
                break;
        }
    }

    @Override
    public void onNeutralButtonClicked(String id) {

    }

    @Override
    public void onNegativeButtonClicked(String id) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.newChatAuthStartChatButton:
                if (passwordEditText.getText().toString().contentEquals(confirmPasswordEditText.getText().toString())){
                    ChatPreparer chatPreparer = new ChatPreparer(getActivity(), true, MainActivity.fragmentManager,  usernameEditText.getText().toString(), confirmPasswordEditText.getText().toString());
                    chatPreparer.setRequestListener(MainActivity.chatRequestListener);
                    chatPreparer.prepare();
                }
                break;
        }
    }
}
