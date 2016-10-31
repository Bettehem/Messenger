package com.github.bettehem.messenger.tools.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.bettehem.messenger.R;
import com.rockerhieu.emojicon.EmojiconTextView;

public class ChatScreenMessageViewHolder extends RecyclerView.ViewHolder{

    public EmojiconTextView messageTextView;

    public ChatScreenMessageViewHolder(View itemView) {
        super(itemView);
        messageTextView = (EmojiconTextView) itemView.findViewById(R.id.messageItemTextView);
    }
}
