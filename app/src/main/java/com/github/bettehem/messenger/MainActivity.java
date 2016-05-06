package com.github.bettehem.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.ViewFlipper;

import com.github.bettehem.androidtools.Preferences;
import com.github.bettehem.messenger.fragments.NewChatAuthFragment;
import com.github.bettehem.messenger.fragments.NewProfileFragment;
import com.github.bettehem.messenger.gcm.RegistrationIntentService;
import com.github.bettehem.messenger.objects.ChatPreparerInfo;
import com.github.bettehem.messenger.objects.ChatRequestResponseInfo;
import com.github.bettehem.messenger.tools.adapters.ChatsRecyclerAdapter;
import com.github.bettehem.messenger.tools.background.RequestResponse;
import com.github.bettehem.messenger.tools.items.ChatItem;
import com.github.bettehem.messenger.tools.listeners.ChatItemListener;
import com.github.bettehem.messenger.tools.listeners.ChatRequestListener;
import com.github.bettehem.messenger.tools.listeners.ProfileListener;
import com.github.bettehem.messenger.tools.managers.ChatsManager;
import com.github.bettehem.messenger.tools.managers.ProfileManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ChatRequestListener, ChatItemListener, ProfileListener{

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    public static final int MAIN_VIEW = 0;
    public static final int MAIN_FRAGMENT = 1;

    public static Toolbar toolbar;
    private FloatingActionButton newChatButton;
    private RecyclerView chatsRecyclerView;
    private ArrayList<ChatItem> chatItems = new ArrayList<ChatItem>();
    public static FragmentManager fragmentManager;
    public static Fragment currentFragment;
    public static ViewFlipper mainViewFlipper;
    private ChatsRecyclerAdapter chatsRecyclerAdapter;
    public static ChatRequestListener chatRequestListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareGCM();

        setup();

        checkExtras();
    }

    private void setup(){
        toolbars();
        buttons();
        navDrawer();
        recyclers();
        viewFlippers();
        listeners();
    }

    private void toolbars(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        MainActivity.toolbar.setSubtitle("Chats");
        setSupportActionBar(toolbar);
    }

    private void buttons(){
        newChatButton = (FloatingActionButton) findViewById(R.id.chatsNewMessageFab);
        newChatButton.setOnClickListener(this);
    }

    private void navDrawer(){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
            }else{
                newChatButton.show();
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
                newChat();
                break;

        }
    }



    private void newChat(){
        newChatButton.hide();

        if (!Preferences.fileExists(this, "UserProfile", "xml")){

            //Open a profile addition screen, and let the user add a new profile
            mainViewFlipper.setDisplayedChild(MAIN_FRAGMENT);
            fragmentManager = getSupportFragmentManager();
            currentFragment = new NewProfileFragment();
            fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, currentFragment).commit();
        }else{
            //open new chat addition screen
            mainViewFlipper.setDisplayedChild(MAIN_FRAGMENT);
            fragmentManager = getSupportFragmentManager();
            currentFragment = new NewChatAuthFragment();
            fragmentManager.beginTransaction().replace(R.id.mainFrameLayout, currentFragment).commit();
        }

    }

    private void prepareGCM(){
        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            intent.putExtra("topics", ChatsManager.getGcmTopics(this));
            // Start IntentService to register this application with GCM.
            startService(intent);
        }
    }
















    //Called when the app is resumed.
    @Override
    protected void onResume() {
        super.onResume();

        //check for play services, and if everything is ok, subscribe to the needed topics
        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            intent.putExtra("topics", ChatsManager.getGcmTopics(this));
            // Start IntentService to register this application with GCM.
            startService(intent);
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
        //update the chat items list
        chatsRecyclerAdapter.setChatItems(ChatsManager.getChatItems(this));

        //open Chat screen
        ChatsManager.openChatScreen(this, chatPreparerInfo.username, chatPreparerInfo.status, chatPreparerInfo.frameId, chatPreparerInfo.fragmentManager);
    }

    @Override
    public void onChatRequestResponse(ChatRequestResponseInfo responseInfo) {
        if (responseInfo.requestAccepted){
            //If other user accepted the chat request, start the chat if password was also correct

            //Start the chat with the other user
            ChatsManager.startChat(this, responseInfo.correctPassword, responseInfo.username, R.id.mainFrameLayout, getSupportFragmentManager());

        }else{

        }
    }

    @Override
    public void onItemClicked(View v, int position) {
        newChatButton.hide();
        ChatsManager.openChatScreen(this, v.getTag().toString(), Preferences.loadString(this, "status", v.getTag().toString()), R.id.mainFrameLayout, getSupportFragmentManager());
    }

    @Override
    public boolean onItemLongCLicked(View v, int position) {
        //TODO: Ask user if they want to delete the chat with $USER
        return false;
    }




    private void checkExtras(){
        if (getIntent().hasExtra("type")){
            switch (getIntent().getExtras().getString("type")){
                case "chatRequest":
                    ChatsManager.openChatScreen(this, getIntent().getExtras().getString("username"), "chatRequest", R.id.mainFrameLayout, getSupportFragmentManager());
                    break;
            }
        }
    }

    @Override
    public void onProfileSaved() {
        prepareGCM();
    }
}
