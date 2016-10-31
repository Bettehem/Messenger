package com.github.bettehem.messenger.tools.adapters;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.bettehem.messenger.R;
import com.github.bettehem.messenger.tools.items.MessageItem;
import com.github.bettehem.messenger.tools.viewholders.ChatScreenMessageViewHolder;

import java.util.ArrayList;

public class ChatsScreenMessageAdapter extends RecyclerView.Adapter<ChatScreenMessageViewHolder>{

    private Context mContext;
    private ArrayList<MessageItem> mMessageItems = new ArrayList<>();


    public ChatsScreenMessageAdapter(Context context){
        mContext = context;
    }


    public void setMessageItems(ArrayList<MessageItem> messageItems){
        mMessageItems = messageItems;
        notifyDataSetChanged();
    }


    @Override
    public ChatScreenMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatScreenMessageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.message_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatScreenMessageViewHolder holder, int position) {
        holder.messageTextView.setText(mMessageItems.get(position).mMessage);
        if (mMessageItems.get(position).mIsOwnMessage){
            holder.messageTextView.setGravity(GravityCompat.END);
        }else{
            holder.messageTextView.setGravity(GravityCompat.START);
        }
    }

    @Override
    public int getItemCount() {
        return mMessageItems.size();
    }
}
