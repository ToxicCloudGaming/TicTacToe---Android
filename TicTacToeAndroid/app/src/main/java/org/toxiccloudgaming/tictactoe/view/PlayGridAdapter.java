package org.toxiccloudgaming.tictactoe.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import org.toxiccloudgaming.tictactoe.R;

import java.util.List;

public class PlayGridAdapter extends BaseAdapter {

    private List<PlayTile> playTiles;
    private Context context;

    public PlayGridAdapter(Context context, List<PlayTile> playTiles) {
        this.context = context;
        this.playTiles = playTiles;
    }

    @Override
    public int getCount() {
        return this.playTiles.size();
    }

    @Override
    public Object getItem(int position) {
        return this.playTiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.playTiles.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.play_tile, null);

        GridView playGrid = (GridView)parent.findViewById(R.id.play_grid);
        //'playTile' is the object, 'textView' is the XML item
        PlayTile playTile = this.playTiles.get(position);
        TextView textView = (TextView)view.findViewById(R.id.play_tile);

        /* Graphical settings of 'playTile' */
        textView.setHeight(playGrid.getColumnWidth());
        textView.setText(playTile.getText());

        /* Interactivity settings of 'playTile' */
        if(playTile.isEnabled() != textView.isEnabled()) textView.setEnabled(playTile.isEnabled());
        if(playTile.isClickable() != textView.isClickable()) textView.setClickable(playTile.isClickable());
        if(playTile.getBackground() != textView.getBackground()) textView.setBackground(playTile.getBackground());

        return view;
    }
}
