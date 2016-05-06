package com.github.bettehem.messenger.tools.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.bettehem.messenger.R;
import com.github.bettehem.messenger.tools.items.ChatItem;
import com.github.bettehem.messenger.tools.listeners.ChatItemListener;
import com.github.bettehem.messenger.tools.managers.ChatsManager;
import com.github.bettehem.messenger.tools.viewholders.ChatsRecyclerViewHolder;

import java.util.ArrayList;

public class ChatsRecyclerAdapter extends RecyclerView.Adapter<ChatsRecyclerViewHolder>{

    private Context context;
    private ArrayList<ChatItem> chatItems = new ArrayList<ChatItem>();
    public static ChatItemListener chatItemListener;

    public ChatsRecyclerAdapter(Context context){
        this.context = context;
    }

    public ChatsRecyclerAdapter(Context context, ArrayList<ChatItem> chatItems) {
        this.context = context;
        this.chatItems = chatItems;
        notifyDataSetChanged();
    }

    @Override
    public ChatsRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatsRecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.main_recycler_chat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ChatsRecyclerViewHolder holder, int position) {
        holder.nameTextView.setText(chatItems.get(position).name);
        holder.messageTextView.setText(chatItems.get(position).message);
        holder.timeTextView.setText(ChatsManager.formatTime(chatItems.get(position).time));
        holder.item.setTag(chatItems.get(position).name);
    }

    public void setChatItems(ArrayList<ChatItem> chatItems){
        this.chatItems = chatItems;
        notifyDataSetChanged();
    }

    public void setChatItemListener(ChatItemListener chatItemListener) {
        ChatsRecyclerAdapter.chatItemListener = chatItemListener;
    }

    @Override
    public int getItemCount() {
        return chatItems.size();
    }
}
