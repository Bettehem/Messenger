package com.github.bettehem.messenger.tools.managers;

import android.content.Context;
import com.github.bettehem.androidtools.Preferences;
import com.github.bettehem.messenger.tools.listeners.ProfileListener;
import com.github.bettehem.messenger.tools.users.UserProfile;

public abstract class ProfileManager {

    public static final String FILENAME = "UserProfile";
    private static ProfileListener profileListener;

    public static void saveProfile(final Context context, final UserProfile userProfile){
        Preferences.saveString(context, "emoji", userProfile.emoji, FILENAME);
        Preferences.saveString(context, "name", userProfile.name, FILENAME);
        Preferences.saveString(context, "status", userProfile.status, FILENAME);
        if (profileListener != null){
            profileListener.onProfileSaved(userProfile);
        }
    }

    public static UserProfile getProfile(Context context){
        String emoji = Preferences.loadString(context, "emoji", FILENAME);
        String name = Preferences.loadString(context, "name", FILENAME);
        String status = Preferences.loadString(context, "status", FILENAME);
        return new UserProfile(emoji, name, status);
    }

    public static void deleteProfile(Context context){
        UserProfile profile = getProfile(context);
        Preferences.deleteFile(context, "UserProfile", "xml");
        if (profileListener != null){
            profileListener.onProfileDeleted(profile.name);
        }
    }

    public static void setProfileListener(ProfileListener profileListener){
        ProfileManager.profileListener = profileListener;
    }
}
