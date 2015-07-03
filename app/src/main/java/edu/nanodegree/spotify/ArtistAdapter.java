package edu.nanodegree.spotify;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.squareup.picasso.Picasso;

import java.util.List;
import kaaes.spotify.webapi.android.models.Artist;

public class ArtistAdapter extends ArrayAdapter<Artist> {
    private static final String LOG_TAG = ArtistAdapter.class.getSimpleName();

    public ArtistAdapter(Context context, List<Artist> artists) {
        super(context, 0, artists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_row, parent, false);
        }

        ViewHolder holder=(ViewHolder)convertView.getTag();

        if (holder==null) {
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        Artist artist = getItem(position);
        holder.title.setText(artist.name);
        if (artist.images.size() > 0) {
            int indice = Utils.getSizeIndex(artist.images);
            String imageUrl = artist.images.get(indice).url;
            Picasso.with(this.getContext())
                    .load(imageUrl)
                    .fit()
                    .centerCrop()
                    .into(holder.icon);
        }
        return convertView;
    }
}
