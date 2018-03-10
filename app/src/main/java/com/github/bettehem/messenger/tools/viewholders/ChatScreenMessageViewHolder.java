package com.github.bettehem.messenger.tools.viewholders;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.bettehem.androidtools.misc.Time;
import com.github.bettehem.messenger.R;
import com.github.bettehem.messenger.tools.managers.ChatsManager;
import com.rockerhieu.emojicon.EmojiconTextView;

import java.util.Calendar;

public class ChatScreenMessageViewHolder extends RecyclerView.ViewHolder{

    public LinearLayout layout;
    public EmojiconTextView messageTextView;
    private AppCompatTextView timeTextView;

    public ChatScreenMessageViewHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.messageItemLayout);
        messageTextView = itemView.findViewById(R.id.messageItemTextView);
        timeTextView = itemView.findViewById(R.id.messageItemTime);
    }

    public void setTime(Time time){
        //get current time
        timeTextView.setText(ChatsManager.formatTime(time));
    }

    public void setStatus(boolean delivered, boolean isOwnMessage){
        if (delivered && isOwnMessage){
            timeTextView.setText(timeTextView.getText().toString() + " ✔️");
        }
    }

    public void configureLayout(Context context, boolean isOwnMessage){
        int margin_dp = 50;
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) layout.getLayoutParams();
        //check if you sent the message
        if (isOwnMessage){
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin_dp, context.getResources().getDisplayMetrics());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.setMarginStart(px);
                layoutParams.setMarginEnd(0);
            }else {
                layoutParams.leftMargin = px;
                layoutParams.rightMargin = 0;
            }
            layout.setLayoutParams(layoutParams);
            layout.setGravity(Gravity.END);
            layout.setHorizontalGravity(Gravity.END);

            //set item background
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                layout.setBackground(context.getDrawable(R.drawable.message_bubble_sent));
            }else {
                layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.message_bubble_sent));
            }
        }else{
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin_dp, context.getResources().getDisplayMetrics());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.setMarginEnd(px);
                layoutParams.setMarginStart(0);
            }else {
                layoutParams.rightMargin = px;
                layoutParams.leftMargin = 0;
            }
            layout.setLayoutParams(layoutParams);
            layout.setGravity(Gravity.START);
            layout.setHorizontalGravity(Gravity.START);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                layout.setBackground(context.getDrawable(R.drawable.message_bubble_received));
            }else {
                layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.message_bubble_received));
            }
        }
    }
}
