package com.github.bettehem.messenger.tools.viewholders;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.bettehem.messenger.R;
import com.github.bettehem.messenger.tools.adapters.ChatsRecyclerAdapter;

public class ChatsRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public RelativeLayout item;
    public AppCompatTextView nameTextView;
    public AppCompatTextView messageTextView;
    public AppCompatTextView timeTextView;

    public ChatsRecyclerViewHolder(View itemView) {
        super(itemView);
        nameTextView = (AppCompatTextView) itemView.findViewById(R.id.chatNameTextView);
        messageTextView = (AppCompatTextView) itemView.findViewById(R.id.chatMessageTextView);
        timeTextView = (AppCompatTextView) itemView.findViewById(R.id.chatTimeTextView);
        item = (RelativeLayout) itemView.findViewById(R.id.mainRecyclerChatItem);
        item.setOnClickListener(this);
        item.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainRecyclerChatItem:
                if (ChatsRecyclerAdapter.chatItemListener != null){
                   ChatsRecyclerAdapter.chatItemListener.onItemClicked(v, getAdapterPosition());
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        boolean returnValue;

        if (ChatsRecyclerAdapter.chatItemListener != null){
            switch (v.getId()){
                case R.id.mainRecyclerChatItem:
                    returnValue = ChatsRecyclerAdapter.chatItemListener.onItemLongCLicked(v, getAdapterPosition());
                    break;

                default:
                    returnValue = false;
                    break;
            }
        }else{
            returnValue = false;
        }

        return returnValue;
    }
}
