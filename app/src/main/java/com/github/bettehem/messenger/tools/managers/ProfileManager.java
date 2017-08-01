package com.github.bettehem.messenger.tools.managers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.github.bettehem.androidtools.Preferences;
import com.github.bettehem.messenger.tools.listeners.ProfileListener;
import com.github.bettehem.messenger.tools.users.UserProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

public abstract class ProfileManager {

    public static final String FILENAME = "UserProfile";
    private static ProfileListener profileListener;

    public static void saveProfile(final Context context, final UserProfile userProfile){
        Preferences.saveString(context, "emoji", userProfile.emoji, FILENAME);
        Preferences.saveString(context, "name", userProfile.name, FILENAME);
        Preferences.saveString(context, "status", userProfile.status, FILENAME);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference profiles = database.getReference("users");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DatabaseReference profiles = FirebaseDatabase.getInstance().getReference("users");
                if (dataSnapshot.hasChild(userProfile.name) && dataSnapshot.child(userProfile.name).hasChild(userProfile.emoji) && dataSnapshot.child(userProfile.name).hasChild(userProfile.status)){
                    //TODO: inform user of existing username
                    Toast.makeText(context, "Username Exists!", Toast.LENGTH_LONG).show();
                }else{
                    profiles.child(userProfile.name).child("status").setValue(userProfile.status);
                    profiles.child(userProfile.name).child("emoji").setValue(userProfile.emoji);
                    if (profileListener != null){
                        profileListener.onProfileSaved();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        profiles.addValueEventListener(postListener);
    }

    public static UserProfile getProfile(Context context){
        String emoji = Preferences.loadString(context, "emoji", FILENAME);
        String name = Preferences.loadString(context, "name", FILENAME);
        String status = Preferences.loadString(context, "status", FILENAME);
        return new UserProfile(emoji, name, status);
    }

    public static void setProfileListener(ProfileListener profileListener){
        ProfileManager.profileListener = profileListener;
    }
}
