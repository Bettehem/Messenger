package com.github.bettehem.messenger.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.bettehem.androidtools.Preferences;
import com.github.bettehem.messenger.MainActivity;
import com.github.bettehem.messenger.R;
import com.github.bettehem.messenger.tools.listeners.SettingsListener;
import com.github.bettehem.messenger.tools.managers.ProfileManager;
import com.github.bettehem.messenger.tools.users.UserProfile;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private View view;
    private AppCompatButton resetProfileButton;
    private AppCompatTextView appVersionText;
    private SettingsListener settingsListener;

    public void setListener(SettingsListener listener){
        settingsListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        //save the current fragment
        MainActivity.currentFragment = this;
        Preferences.saveString(getActivity(), "currentFragment", "ChatScreen");

        setup();

        return view;
    }

    private void setup() {
        buttons();
        textViews();
    }

    private void buttons() {
        resetProfileButton = (AppCompatButton) view.findViewById(R.id.resetProfileButton);

        resetProfileButton.setOnClickListener(this);
    }

    private void textViews(){
        appVersionText = view.findViewById(R.id.settingsAppVersionText);
        appVersionText.setText(getString(R.string.appVersionText) + " " + getString(R.string.app_version));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.resetProfileButton:
                ProfileManager.deleteProfile(getActivity());
                break;


        }
    }
}
