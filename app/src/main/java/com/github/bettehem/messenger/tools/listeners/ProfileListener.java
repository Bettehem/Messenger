package com.github.bettehem.messenger.tools.listeners;

import com.github.bettehem.messenger.tools.users.UserProfile;

public interface ProfileListener {
    void onProfileSaved(UserProfile userProfile);
    void onProfileDeleted(String profileName);
}
