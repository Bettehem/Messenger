package com.github.bettehem.messenger.tools.managers;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.MediaMetadataCompat;

import com.github.bettehem.androidtools.Preferences;
import com.github.bettehem.androidtools.misc.Time;
import com.github.bettehem.androidtools.notification.CustomNotification;
import com.github.bettehem.messenger.MainActivity;
import com.github.bettehem.messenger.R;
import com.github.bettehem.messenger.fragments.ChatScreen;
import com.github.bettehem.messenger.objects.ChatPreparerInfo;
import com.github.bettehem.messenger.objects.ChatRequestResponseInfo;
import com.github.bettehem.messenger.tools.adapters.ChatsRecyclerAdapter;
import com.github.bettehem.messenger.tools.items.ChatItem;
import com.github.bettehem.messenger.tools.users.Sender;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class ChatsManager {
    public static final String SPLITTER = "_kjhas3ng7vb3b3a-XYZYX-di8x888xgwbkwv0vaw3pxds22_";

    public static ArrayList<ChatItem> getChatItems(Context context){

        //Get current chat items
        ArrayList<ChatItem> chatItems = new ArrayList<ChatItem>();
        int chatAmount = Preferences.loadInt(context, "chatsAmount", "ChatDetails");
        for (int i = 0; i < chatAmount; i++){
            String[] item = Preferences.loadStringArray(context, "chatItem_" + i, "ChatDetails");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(item[2]));
            chatItems.add(new ChatItem(item[0], item[1], new Time(calendar)));
        }
        return chatItems;
    }

    /**
     * Used to format the custom time object to a string that shows the hour and minute, date, or if the time was yesterday, it shows yesterday.
     * @param time The time object that should be formatted
     * @return Returns a String that can be used for the chat items.
     */
    public static String formatTime(Time time){
        //TODO: Remove hard-coded strings;

        String formatted;

        Time currentTime = new Time(Calendar.getInstance());

        //compare given time to the current time on the device.

        //do if the current time is newer than the formatted time
        if (Integer.valueOf(currentTime.date) > Integer.valueOf(time.date) && Integer.valueOf(currentTime.year) >= Integer.valueOf(time.year)){
            //If the formatted time was yesterday
            if (Integer.valueOf(currentTime.date) - 1 == Integer.valueOf(time.date) && (int)Integer.valueOf(currentTime.year) == Integer.valueOf(time.year)){
                formatted = "Yesterday";

                //if formatted time is older than yesterday
            }else{
                formatted = time.date + "." + time.month + "." + time.year.substring(2);
            }

         //do if current time is same as the formatted time
        }else{
            formatted = time.hour + ":" + time.minute;
        }

        return formatted;
    }

    /**
     * Gets the topics that the user will be subscribed to
     * @param context Context is used to get the needed information from SharedPreferences
     * @return Returns a String array that contains the topics that the users need to subscribe to
     */
    public static String[] getGcmTopics(Context context){
        if (Preferences.loadString(context, "name", ProfileManager.FILENAME).contentEquals("")){
            return new String[]{"global"};
        }else{
            return new String[]{"global", Preferences.loadString(context, "name", ProfileManager.FILENAME).replace(" ", SPLITTER)};
        }
    }

    public static Sender getSenderData(Context context, String senderData){
        String userName = getUserName(context, senderData);
        boolean isSecretMessage = Boolean.valueOf(senderData.split(SPLITTER)[1]);
        return new Sender(userName, isSecretMessage);
    }

    public static String getMessage(Sender senderData, String rawMessage){
        String unscrambled = EncryptionManager.unscramble(rawMessage);
        String message = EncryptionManager.decrypt(EncryptionManager.scramble(SPLITTER.split("-X&X-")[0] + EncryptionManager.scramble(senderData.userName)), unscrambled);
        return message;
    }

    public static boolean usernameExists(Context context, String username){
        boolean usernameExists = false;

        ArrayList<ChatItem> chatItems = getChatItems(context);
        for (ChatItem c : chatItems){
            if (c.name.contentEquals(username)){
                usernameExists = true;
            }
        }

        return usernameExists;
    }

    /**
     * WARNING!
     * Running this method is not recommended in the main thread!
     */
    public static ChatPreparerInfo prepareChat(Context context, FragmentManager fragmentManager, String username, String password){

        //generate key from password
        String key = EncryptionManager.createKey(password);

        //encrypt username
        String scrambledUsername = EncryptionManager.scramble(username);
        String encryptedUsername = EncryptionManager.encrypt(key, scrambledUsername);
        String readyUsername = EncryptionManager.scramble(encryptedUsername);


        //save username
        Preferences.saveString(context, "encryptedUsername", readyUsername, username);

        //save a chat item
        if (usernameExists(context, username)){
            editChatItem(context, username, "Pending...", new Time(Calendar.getInstance()));
        }else{
            saveChatItem(context, username, "Pending...", new Time(Calendar.getInstance()));
        }

        sendRequest(context, username);

        return new ChatPreparerInfo(username, "pending", R.id.mainFrameLayout, fragmentManager);
    }

    public static void openChatScreen(Context context, String name, String chatStatus, int fragmentId, FragmentManager fragmentManager){
        //Save settings for current chat
        Preferences.saveString(context, "username", name, "CurrentChat");

        //Save chat status
        setChatStatus(context, chatStatus, name);

        //open chat screen
        fragmentManager.beginTransaction().replace(fragmentId, new ChatScreen()).commit();

        //change viewFlipper to show fragments
        MainActivity.mainViewFlipper.setDisplayedChild(MainActivity.MAIN_FRAGMENT);
    }


    /**
     * Sends a chat request to the wanted person
     * @param context Used to get needed information from SharedPreferences.
     * @param username The chat request is sent to the given username.
     */
    private static void sendRequest(final Context context, final String username){
        final String receiver = username.replace(" ", SPLITTER);
        Thread thread = new Thread(){
            public void run(){
                HttpPost post = new HttpPost("https://fcm.googleapis.com/fcm/send");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("to", "/topics/" + receiver);
                    JSONObject data = new JSONObject();
                    data.put("type", "chatRequest");
                    data.put("sender", Preferences.loadString(context, "name", ProfileManager.FILENAME));
                    data.put("key", Preferences.loadString(context, "encryptedUsername", username));
                    jsonObject.put("data", data);
                    StringEntity se = new StringEntity(jsonObject.toString());
                    se.setContentType(new BasicHeader("Content-Type", "application/json"));
                    post.setEntity(se);
                    post.setHeader("Authorization", "key=" + "AIzaSyD8C9exPq2SWMkJUcGc8ZNT8MA9b18rF4I");
                    HttpClient client = new DefaultHttpClient();
                    client.execute(post);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }


    public static void handleChatRequest(Context context, String sender, String key ) {
        if(Preferences.loadString(context, "currentFragment").contentEquals("ChatScreen") && Preferences.loadString(context, "username", "CurrentChat").contentEquals(sender)){
            //User has the chat open


        }else {
            //User doesn't have the chat open, so make a notification

            //save status
            Preferences.saveString(context, "chatStatus", "chatRequest", sender);

            //save key
            Preferences.saveString(context, "requestKey", key, sender);


            if (usernameExists(context, sender)){
                //edit the existing chat item
                editChatItem(context, sender, "New Chat Request", new Time(Calendar.getInstance()));
            }else{
                //Save a chat item for this chat
                saveChatItem(context, sender, "New Chat Request", new Time(Calendar.getInstance()));
            }



            if (Preferences.loadBoolean(context, "appVisible")){
                MainActivity.chatsRecyclerAdapter.setChatItems(getChatItems(context));
            }else{
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("type", "chatRequest");
                intent.putExtra("username", sender);
                //TODO: Remove hard-coded strings
                CustomNotification.make(context, R.mipmap.ic_launcher, "Messenger", "New chat request from " + sender, intent, true, true).show();
            }
        }
    }

    public static void responseToRequest(final Context context, final boolean acceptRequest, final String username, final String password){
        //generate key from password
        String key = EncryptionManager.createKey(password);

        //encrypt username
        String scrambledUsername = EncryptionManager.scramble(username);
        String encryptedUsername = EncryptionManager.encrypt(key, scrambledUsername);
        final String readyUsername = EncryptionManager.scramble(encryptedUsername);

        //Send response to the chat request
        final String receiver = username.replace(" ", SPLITTER);
        Thread thread = new Thread(){
            public void run(){
                HttpPost post = new HttpPost("https://fcm.googleapis.com/fcm/send");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("to", "/topics/" + receiver);
                    JSONObject data = new JSONObject();
                    data.put("type", "requestResponse");
                    data.put("sender", Preferences.loadString(context, "name", ProfileManager.FILENAME));
                    data.put("requestAccepted", String.valueOf(acceptRequest));
                    data.put("password", readyUsername);
                    jsonObject.put("data", data);
                    StringEntity se = new StringEntity(jsonObject.toString());
                    se.setContentType(new BasicHeader("Content-Type", "application/json"));
                    post.setEntity(se);
                    post.setHeader("Authorization", "key=" + "AIzaSyD8C9exPq2SWMkJUcGc8ZNT8MA9b18rF4I");
                    HttpClient client = new DefaultHttpClient();
                    client.execute(post);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        Snackbar.make(MainActivity.mainRelativeLayout, "Response Sent!, Please wait...", Snackbar.LENGTH_LONG).show();
    }

    public static ChatRequestResponseInfo handleChatRequestResponse(Context context, boolean requestAccepted, String username, String password){
        if (requestAccepted){
            //get local encrypted username
            String localEncryptedUsername = Preferences.loadString(context, "encryptedUsername", username);

            //encrypt username
            String scrambledUsername = EncryptionManager.scramble(username);
            String encryptedUsername = EncryptionManager.encrypt(password, scrambledUsername);
            String readyUsername = EncryptionManager.scramble(encryptedUsername);

            if (localEncryptedUsername.contentEquals(readyUsername)){
                return new ChatRequestResponseInfo(requestAccepted, true, username);
            }else {
                return new ChatRequestResponseInfo(requestAccepted, false, username);
            }

        }else {
            return new ChatRequestResponseInfo(requestAccepted, false, username);
        }
    }

    public static void startChat(final Context context, final boolean correctPassword, final String username, int fragmentId, FragmentManager fragmentManager){

        //Send info to other user on starting the chat
        final String receiver = username.replace(" ", SPLITTER);
        Thread thread = new Thread(){
            public void run(){
                HttpPost post = new HttpPost("https://fcm.googleapis.com/fcm/send");
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("to", "/topics/" + receiver);
                    JSONObject data = new JSONObject();
                    data.put("type", "startChat");
                    data.put("correctPassword", String.valueOf(correctPassword));
                    data.put("sender", Preferences.loadString(context, "name", ProfileManager.FILENAME));
                    data.put("key", Preferences.loadString(context, "encryptedUsername", username));
                    jsonObject.put("data", data);
                    StringEntity se = new StringEntity(jsonObject.toString());
                    se.setContentType(new BasicHeader("Content-Type", "application/json"));
                    post.setEntity(se);
                    post.setHeader("Authorization", "key=" + "AIzaSyD8C9exPq2SWMkJUcGc8ZNT8MA9b18rF4I");
                    HttpClient client = new DefaultHttpClient();
                    client.execute(post);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        if (correctPassword){
            //Edit current chatItem
            //TODO: Remove hard-coded strings
            editChatItem(context, username, "Chat Started", new Time(Calendar.getInstance()));

            //open the chat screen if user is not in chat list
            if (!Preferences.loadString(context, "currentFragment").contentEquals("")){
                openChatScreen(context, username, "normal", fragmentId, fragmentManager);
            }
        }else{
            //TODO: Ask user if they want to delete the chat [ALERT_DIALOG]
        }
    }

    public static void startNormalChat(Context context, String username){
        //TODO: Remove hard-coded strings
        //Edit the chatItem
        editChatItem(context, username, "Chat Started", new Time(Calendar.getInstance()));

        //TODO: set the chat status

        //update chat item's list
        MainActivity.chatsRecyclerAdapter.setChatItems(getChatItems(context));

        MainActivity.toolbar.setSubtitle("New Chat");
        MainActivity.newChatButton.hide();
        openChatScreen(context, username, "normal", R.id.mainFrameLayout, MainActivity.fragmentManager);
    }









    //Private methods
    //----------------------------------------------------------------------------------------------

    private static Time getMessageTime(String messageTime){
        if (messageTime.contentEquals("")){
            return new Time(Calendar.getInstance());
        }else{
            String[] messageTimeArray = messageTime.split(SPLITTER);
            return new Time(Integer.valueOf(messageTimeArray[0]), Integer.valueOf(messageTimeArray[1]), Integer.valueOf(messageTimeArray[2]), Integer.valueOf(messageTimeArray[3]), Integer.valueOf(messageTimeArray[4]), Integer.valueOf(messageTimeArray[5]));
        }
    }

    private static String getUserName(Context context, String senderData){
        String user_raw = senderData.split(SPLITTER)[0];
        String user_unscrambled = EncryptionManager.unscramble(user_raw);
        String userName = EncryptionManager.unscramble(EncryptionManager.decrypt(Preferences.loadString(context, user_raw, "ChatsConfig"), user_unscrambled));

        return userName;
    }

    private static void setChatStatus(Context context, String status, String username){
        Preferences.saveString(context, "chatStatus", status, username);
    }

    private static void saveChatItem(Context context, String username, String message, Time time){

        //Get current chat items
        ArrayList<ChatItem> chatItems = new ArrayList<ChatItem>();
        int chatAmount = Preferences.loadInt(context, "chatsAmount", "ChatDetails");
        for (int i = 0; i < chatAmount; i++){
            String[] item = Preferences.loadStringArray(context, "chatItem_" + i, "ChatDetails");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(item[2]));
            chatItems.add(new ChatItem(item[0], item[1], new Time(calendar)));
        }


        //Add new item to chatItems
        chatItems.add(new ChatItem(username, message, time));



        //save amount of chats
        Preferences.saveInt(context, "chatsAmount", chatItems.size(), "ChatDetails");

        //Save chat items
        for (int i = 0; i < chatItems.size(); i++){
            String[] item = new String[]{chatItems.get(i).name, chatItems.get(i).message, String.valueOf(chatItems.get(i).time.getTimeInMillis())};
            Preferences.saveStringArray(context, "chatItem_" + i, item, "ChatDetails");
        }
    }

    private static void editChatItem(Context context, String username, String newMessage, Time newTime){
        //Get current chat items
        ArrayList<ChatItem> chatItems = new ArrayList<ChatItem>();
        int chatAmount = Preferences.loadInt(context, "chatsAmount", "ChatDetails");
        for (int i = 0; i < chatAmount; i++){
            String[] item = Preferences.loadStringArray(context, "chatItem_" + i, "ChatDetails");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(item[2]));
            chatItems.add(new ChatItem(item[0], item[1], new Time(calendar)));
        }

        //Edit the items
        for (int i = 0; i < chatItems.size(); i++){
            if (chatItems.get(i).name.contentEquals(username)){
                chatItems.set(i, new ChatItem(username, newMessage, newTime));
            }
        }

        //delete the old items
        Preferences.deleteAllValues(context, "ChatDetails");

        //save the items
        for (ChatItem c : chatItems){
            saveChatItem(context, c.name, c.message, c.time);
        }

        //update list
        MainActivity.chatsRecyclerAdapter.setChatItems(getChatItems(context));
    }
}
