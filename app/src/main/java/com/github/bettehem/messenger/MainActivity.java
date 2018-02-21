package com.github.bettehem.messenger;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.github.bettehem.androidtools.Preferences;
import com.github.bettehem.androidtools.dialog.CustomAlertDialog;
import com.github.bettehem.messenger.fragments.NewChatAuthFragment;
import com.github.bettehem.messenger.fragments.NewProfileFragment;
import com.github.bettehem.messenger.fragments.SettingsFragment;
import com.github.bettehem.messenger.objects.ChatPreparerInfo;
import com.github.bettehem.messenger.objects.ChatRequestResponseInfo;
import com.github.bettehem.messenger.tools.adapters.ChatsRecyclerAdapter;
import com.github.bettehem.messenger.tools.background.RequestResponse;
import com.github.bettehem.messenger.tools.items.ChatItem;
import com.github.bettehem.messenger.tools.listeners.ChatItemListener;
import com.github.bettehem.messenger.tools.listeners.ChatRequestListener;
import com.github.bettehem.messenger.tools.listeners.ProfileListener;
import com.github.bettehem.messenger.tools.listeners.TopicListener;
import com.github.bettehem.messenger.tools.listeners.SettingsListener;
import com.github.bettehem.messenger.tools.managers.ChatsManager;
import com.github.bettehem.messenger.tools.managers.EncryptionManager;
import com.github.bettehem.messenger.tools.managers.ProfileManager;
import com.github.bettehem.messenger.tools.managers.TopicManager;
import com.github.bettehem.messenger.tools.users.UserProfile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rockerhieu.emojicon.EmojiconTextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ChatRequestListener, ChatItemListener, ProfileListener, View.OnLongClickListener, TopicListener, SettingsListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    public static final int MAIN_VIEW = 0;
    public static final int MAIN_FRAGMENT = 1;

    public static Toolbar toolbar;
    public static FloatingActionButton newChatButton;
    private RecyclerView chatsRecyclerView;
    private ArrayList<ChatItem> chatItems = new ArrayList<ChatItem>();
    public static FragmentManager fragmentManager;
    public static Fragment currentFragment;
    public static ViewFlipper mainViewFlipper;
    public static ChatsRecyclerAdapter chatsRecyclerAdapter;
    public static ChatRequestListener chatRequestListener;
    public static RelativeLayout mainRelativeLayout;
    private NavigationView navigationView;
    private EmojiconTextView emojiTextView;
    private AppCompatTextView usernameTextView, statusTextView;

    private boolean isFabPressed = false;


    private TopicManager topicManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topicManager = new TopicManager(this, this);

        prepareGCM();

        setup();

        checkIfProfileExists();

        checkExtras();
    }

    private void setup(){
        toolbars();
        buttons();
        navDrawer();
        recyclers();
        viewFlippers();
        listeners();
        layouts();
    }

    private void toolbars(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        MainActivity.toolbar.setSubtitle("Chats");
        setSupportActionBar(toolbar);
    }

    private void buttons(){
        newChatButton = (FloatingActionButton) findViewById(R.id.chatsNewMessageFab);
        newChatButton.setOnClickListener(this);
        newChatButton.setOnLongClickListener(this);
    }

    private void navDrawer(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        updateNavHeader();
    }

    private void recyclers(){
        chatsRecyclerAdapter = new ChatsRecyclerAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        chatsRecyclerView = (RecyclerView) findViewById(R.id.chatsRecyclerView);
        chatsRecyclerView.setLayoutManager(layoutManager);
        chatsRecyclerView.setHasFixedSize(true);
        chatsRecyclerView.setAdapter(chatsRecyclerAdapter);
        chatsRecyclerAdapter.setChatItemListener(this);


        chatsRecyclerAdapter.setChatItems(ChatsManager.getChatItems(this));

    }

    private void viewFlippers(){
        mainViewFlipper = (ViewFlipper) findViewById(R.id.mainViewFlipper);
        mainViewFlipper.setDisplayedChild(MAIN_VIEW);
    }

    private void listeners(){
        chatRequestListener = this;
        ProfileManager.setProfileListener(this);
        RequestResponse.setRequestListener(this);
    }

    private void layouts(){
        mainRelativeLayout = (RelativeLayout) findViewById(R.id.mainVewRelativeLayout);
    }

    private void updateNavHeader(){
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //get user profile
        UserProfile userProfile = ProfileManager.getProfile(this);

        //add default data to the user profile if empty
        if (!userProfile.hasData()){
            userProfile.emoji = "😀";
            userProfile.name = "Default user";
            userProfile.status = "Default status";
        }

        //get headerView
        View headerView = navigationView.getHeaderView(0);

        //set user details in header
        emojiTextView = (EmojiconTextView) headerView.findViewById(R.id.navDrawerUserEmoji);
        usernameTextView = (AppCompatTextView) headerView.findViewById(R.id.navDrawerUsernameText);
        statusTextView = (AppCompatTextView) headerView.findViewById(R.id.navDrawerStatusText);

        emojiTextView.setText(userProfile.emoji);
        usernameTextView.setText(userProfile.name);
        statusTextView.setText(userProfile.status);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //Close fragment if visible
            if (!Preferences.loadString(this, "currentFragment").contentEquals("")){
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(currentFragment).commit();
                mainViewFlipper.setDisplayedChild(MAIN_VIEW);

                MainActivity.toolbar.setTitle(Preferences.loadString(this, "currentFragment").contentEquals("ChatScreen") ? Preferences.loadString(this, "defaultToolbarText") : Preferences.loadString(this, "defaultToolbarText"));
                Preferences.deleteIndividualValue(this, "currentFragment");
                newChatButton.show();

                toolbar.setTitle("Messenger");
                toolbar.setSubtitle("Chats");

                chatsRecyclerAdapter.setChatItems(ChatsManager.getChatItems(this));
                OvershootInterpolator interpolator = new OvershootInterpolator();
                ViewCompat.animate(newChatButton).rotation(0).withLayer().setDuration(400).setInterpolator(interpolator).start();
            }else{
                newChatButton.show();
                OvershootInterpolator interpolator = new OvershootInterpolator();
                ViewCompat.animate(newChatButton).rotation(0).withLayer().setDuration(400).setInterpolator(interpolator).start();
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mainViewFlipper.setDisplayedChild(MAIN_FRAGMENT);
            fragmentManager = getSupportFragmentManager();
            SettingsFragment settingsFragment = new SettingsFragment();
            settingsFragment.setListener(this);
            fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, settingsFragment).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.chatsNewMessageFab:
                if (isFabPressed){
                    OvershootInterpolator interpolator = new OvershootInterpolator();
                    ViewCompat.animate(newChatButton).rotation(0).withLayer().setDuration(450).setInterpolator(interpolator).start();
                    isFabPressed = false;
                    // TODO: 8/26/17 hide checkboxes
                }else {
                    newChat();
                }
                break;

        }
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()){
            case R.id.chatsNewMessageFab:
                if (isFabPressed){
                    OvershootInterpolator interpolator = new OvershootInterpolator();
                    ViewCompat.animate(newChatButton).rotation(0).withLayer().setDuration(450).setInterpolator(interpolator).start();
                    isFabPressed = false;
                }else{
                    OvershootInterpolator interpolator = new OvershootInterpolator();
                    ViewCompat.animate(newChatButton).rotation(45).withLayer().setDuration(450).setInterpolator(interpolator).start();
                    isFabPressed = true;

                    // TODO: 8/25/17 set chat list items' checkboxes to be visible
                }
                return true;

            default:
                return false;
        }
    }


    private void newChat(){
        if (checkIfProfileExists()){
            newChatButton.hide();
            //open new chat addition screen
            mainViewFlipper.setDisplayedChild(MAIN_FRAGMENT);
            fragmentManager = getSupportFragmentManager();
            currentFragment = new NewChatAuthFragment();
            fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, currentFragment).commit();
        }

    }

    /**
     *
     * Subscribe to FCM topics.
     */
    private void prepareGCM(){
        if (checkPlayServices()){
            updateTopics(this);
        }
    }

    private boolean checkIfProfileExists(){
        if (!Preferences.fileExists(this, "UserProfile", "xml")){
            //Open a profile addition screen, and let the user add a new profile
            newChatButton.hide();
            mainViewFlipper.setDisplayedChild(MAIN_FRAGMENT);
            fragmentManager = getSupportFragmentManager();
            currentFragment = new NewProfileFragment();
            fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, currentFragment).commit();

            return false;
        }else {
            return true;
        }
    }

    public void  updateTopics(Context context, String... topics){

        ArrayList<String> topicList = new ArrayList<>();

        for (String s : topics){
            if (!s.contentEquals(""))
            topicList.add(s);
        }
        topicList.addAll(topicManager.getTopics());

        for (String topic : topicList) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
        }
    }












    //Called when the app is resumed.
    @Override
    protected void onResume() {
        super.onResume();

        //check for play services, and if everything is ok, subscribe to the needed topics
        if (checkPlayServices()) {
           prepareGCM();
        }

        Preferences.saveBoolean(this, "appVisible", true);

        //set the fragmentManager
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void onPause() {
        super.onPause();

        Preferences.saveBoolean(this, "appVisible", false);
    }


    //gcm stuff
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onChatPrepared(ChatPreparerInfo chatPreparerInfo) {
        //update subscribed topics to listen for the encrypted username
        topicManager.addTopic(EncryptionManager.createHash(chatPreparerInfo.encryptedUsername));

        //update the chat items list
        chatsRecyclerAdapter.setChatItems(ChatsManager.getChatItems(this));

        //open Chat screen
        ChatsManager.openChatScreen(this, chatPreparerInfo.username, chatPreparerInfo.status, chatPreparerInfo.frameId, chatPreparerInfo.fragmentManager, this);
    }

    @Override
    public void onChatRequestResponse(ChatRequestResponseInfo responseInfo) {
        if (responseInfo.requestAccepted){
            //If other user accepted the chat request, start the chat if password was also correct

            //Start the chat with the other user
            ChatsManager.startChat(this, responseInfo.correctPassword, responseInfo.username, R.id.mainFrameLayout, getSupportFragmentManager(), this);

        }else{
            //TODO: Request rejected
        }
    }

    @Override
    public void onItemClicked(View v, int position) {
        newChatButton.hide();
        String status = Preferences.loadString(this, "chatStatus", v.getTag().toString());
        ChatsManager.openChatScreen(this, v.getTag().toString(), status, R.id.mainFrameLayout, getSupportFragmentManager(), this);
    }

    @Override
    public boolean onItemLongCLicked(View v, int position) {
        //TODO: Ask user if they want to delete the chat with $USER
        return false;
    }

    @Override
    public void onRequestAccepted(final String username, final String key) {
        final Context context = this;
        new Thread(){
            @Override
            public void run() {
                String topic = EncryptionManager.createHash(EncryptionManager.encrypt(Preferences.loadString(context, "iv", username), key, EncryptionManager.scramble(ProfileManager.getProfile(context).name)).get(1));
                topicManager.addTopic(topic);
            }
        }.run();
    }

    private void checkExtras(){
        if (getIntent().hasExtra("type")){
            switch (getIntent().getExtras().getString("type")){
                case "chatRequest":
                    ChatsManager.openChatScreen(this, getIntent().getExtras().getString("username"), "chatRequest", R.id.mainFrameLayout, getSupportFragmentManager(), this);
                    break;
            }
        }
    }

    @Override
    public void onProfileSaved(UserProfile userProfile) {
        fragmentManager.beginTransaction().remove(currentFragment);
        fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, new NewChatAuthFragment()).commit();
        topicManager.addTopic(userProfile.name);
        updateNavHeader();
    }

    @Override
    public void onProfileDeleted(String profileName) {
        updateNavHeader();

        //remove subscription from FirebaseMessaging
        //delete topic
        topicManager.deleteTopic(profileName);
    }

    @Override
    public void onTopicAdded(@NonNull String topicName) {
        updateTopics(this);
    }

    @Override
    public void onTopicDeleted(@NotNull String topicName) {
        updateTopics(this);
    }
}
