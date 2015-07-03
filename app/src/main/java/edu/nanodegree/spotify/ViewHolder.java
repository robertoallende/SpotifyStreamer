package edu.nanodegree.spotify;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolder {
    ImageView icon=null;
    TextView title=null;
    TextView subtitle=null;

    ViewHolder(View row) {
        this.icon=(ImageView)row.findViewById(R.id.list_item_icon);
        this.title=(TextView)row.findViewById(R.id.list_item_title);
        this.subtitle=(TextView)row.findViewById(R.id.list_item_subtitle);
    }

    ViewHolder(View row, Boolean subtitleVisible) {
        this.icon=(ImageView)row.findViewById(R.id.list_item_icon);
        this.title=(TextView)row.findViewById(R.id.list_item_title);
        if (subtitleVisible) {
            this.subtitle=(TextView)row.findViewById(R.id.list_item_subtitle);
            this.subtitle.setVisibility(View.VISIBLE);
        }
    }
}