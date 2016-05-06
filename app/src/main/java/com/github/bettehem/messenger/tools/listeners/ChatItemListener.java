package com.github.bettehem.messenger.tools.listeners;

import android.view.View;

public interface ChatItemListener {
    void onItemClicked(View v, int position);
    boolean onItemLongCLicked(View v, int position);
}
