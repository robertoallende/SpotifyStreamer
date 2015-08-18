package edu.nanodegree.spotify;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;

import java.util.List;
import kaaes.spotify.webapi.android.models.Track;


public class TrackAdapter extends ArrayAdapter<Track> {

    private static final String LOG_TAG = TrackAdapter.class.getSimpleName();
    private List<Track> tracks;

    public TrackAdapter(Context context, List<Track> tracks) {
        super(context, 0, tracks);
        this.tracks = tracks;
    }

    public List<Track> getTracks() { return this.tracks; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_row, parent, false);
        }

        ViewHolder holder=(ViewHolder)convertView.getTag();

        if (holder==null) {
            holder=new ViewHolder(convertView, true);
            convertView.setTag(holder);
        }

        Track track = getItem(position);
        holder.title.setText(track.name);
        holder.subtitle.setText(track.album.name);

        if (track.album.images.size() > 0) {
            int indice = Utils.getSizeIndex(track.album.images);
            String imageUrl = track.album.images.get(indice).url;
            Picasso.with(this.getContext())
                    .load(imageUrl)
                    .fit()
                    .centerCrop()
                    .into(holder.icon);
        }
        return convertView;
    }
}
