package com.vlim.puebla;

import android.view.View;

public class CustomOnClickListener implements View.OnClickListener {
    private int position;
    private OnCustomClickListener callback;

    public CustomOnClickListener(Object callback, int pos) {
        position = pos;
        this.callback = (OnCustomClickListener) callback;
    }

    @Override
    public void onClick(View v) {
        // Let's call our custom callback with the position we added in the constructor
        callback.OnCustomClick(v, position);
    }
}