package com.github.bettehem.messenger.tools.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.bettehem.messenger.R;
import com.github.bettehem.messenger.tools.items.MessageItem;
import com.github.bettehem.messenger.tools.managers.ChatsManager;
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

    public void updateItem(MessageItem item){
        for (int i = 0; i < mMessageItems.size(); i++){
            if (mMessageItems.get(i).mMessageId.contentEquals(item.mMessageId)){
                mMessageItems.set(i, item);
                notifyItemChanged(i);
            }
        }
    }


    @Override
    public ChatScreenMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatScreenMessageViewHolder(LayoutInflater.from(mContext).inflate(R.layout.message_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatScreenMessageViewHolder holder, int position) {
        holder.messageTextView.setText(mMessageItems.get(position).mMessage);

        holder.setTime(mMessageItems.get(position).mTime);

        holder.setStatus(mMessageItems.get(position).mMessageDelivered, mMessageItems.get(position).mIsOwnMessage);

        holder.configureLayout(mContext, mMessageItems.get(position).mIsOwnMessage);
    }

    @Override
    public int getItemCount() {
        return mMessageItems.size();
    }
}
