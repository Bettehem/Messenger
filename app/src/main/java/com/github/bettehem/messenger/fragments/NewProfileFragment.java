package com.github.bettehem.messenger.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.github.bettehem.androidtools.Preferences;
import com.github.bettehem.messenger.MainActivity;
import com.github.bettehem.messenger.R;
import com.github.bettehem.messenger.tools.managers.ProfileManager;
import com.github.bettehem.messenger.tools.users.UserProfile;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

public class NewProfileFragment extends Fragment implements View.OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener{

    private static final int USERNAME_VIEW = 0;
    private static final int EMOJI_VIEW = 1;

    private View view;


    private EmojiconTextView emojiTextView;
    private AppCompatButton selectEmojiButton;
    private AppCompatEditText usernameEditText;
    private AppCompatEditText statusEditText;
    private AppCompatButton saveProfileButton;
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private ViewFlipper viewFlipper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_profile, container, false);

        //Save current fragment
        MainActivity.currentFragment = this;
        Preferences.saveString(getActivity(), "currentFragment", "NewProfileFragment");

        setup();

        return view;
    }

    private void setup(){
        toolbars();
        textViews();
        editTexts();
        buttons();
        viewFlippers();
    }

    private void toolbars(){
        //TODO: Remove hard-coded strings
        MainActivity.toolbar.setTitle("Messenger");
        MainActivity.toolbar.setSubtitle("New Profile");
    }

    private void textViews(){
        emojiTextView = (EmojiconTextView) view.findViewById(R.id.fragmentNewProfileSelectProfileEmojiTextView);
    }

    private void editTexts(){
        usernameEditText = (AppCompatEditText) view.findViewById(R.id.fragmentNewProfileUserNameEditText);
        statusEditText = (AppCompatEditText) view.findViewById(R.id.fragmentNewProfileStatusEditText);
    }

    private void buttons(){
        selectEmojiButton = (AppCompatButton) view.findViewById(R.id.fragmentNewProfileSelectProfileEmojiButton);
        saveProfileButton = (AppCompatButton) view.findViewById(R.id.newProfileSaveButton);

        selectEmojiButton.setOnClickListener(this);
        saveProfileButton.setOnClickListener(this);
    }

    private void viewFlippers(){
        viewFlipper = (ViewFlipper) view.findViewById(R.id.newProfileViewFlipper);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fragmentNewProfileSelectProfileEmojiButton:
                //TODO: Add check if to use system default emojis.
                //TODO: Remove hard-coded strings
                if (selectEmojiButton.getText().toString().startsWith("Select")){
                    setEmojiconFragment(false);
                    selectEmojiButton.setText("Hide Emoji Selector");
                    viewFlipper.setDisplayedChild(EMOJI_VIEW);
                }else{
                    hideEmojiconFragment();
                    selectEmojiButton.setText("Select Profile Emoji");
                    viewFlipper.setDisplayedChild(USERNAME_VIEW);
                }
                break;

            case R.id.newProfileSaveButton:
                //TODO: Add checks to see if user typed anything before saving
                String emoji = emojiTextView.getText().toString();
                String name = usernameEditText.getText().toString();
                String status = statusEditText.getText().toString();
                ProfileManager.saveProfile(getActivity(), new UserProfile(emoji, name, status));
                break;
        }
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        emojiTextView.setText("");
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        String text = emojiTextView.getText().toString();

        if (text.length() < 4){
            text = text + emojicon.getEmoji();
        }

        emojiTextView.setText(text);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        fragment = EmojiconsFragment.newInstance(useSystemDefault);
        fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentNewProfileEmojiFrameLayout, fragment).commit();
    }

    private void hideEmojiconFragment(){
        fragmentManager.beginTransaction().remove(fragment).commit();
    }
}
