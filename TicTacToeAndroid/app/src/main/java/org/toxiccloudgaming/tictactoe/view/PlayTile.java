package org.toxiccloudgaming.tictactoe.view;

import android.content.Context;
import android.widget.TextView;

public class PlayTile extends TextView {

    public PlayTile(Context context) {
        super(context);

        this.setText("LOCKED");
        this.setEnabled(false);
    }

    public void enable() {
        this.setEnabled(true);
    }

    public void disable() {
        this.setEnabled(false);
    }

    public void lockText() {
        this.setText("LOCKED");
    }
}
