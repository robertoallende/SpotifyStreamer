package edu.nanodegree.spotify;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends Fragment {

    public PlayerFragment() {
    }

    public static PlayerFragment newInstance(String songId, String songName, String artistName,
                                             long duration, String artwork, String album) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString(PlayerActivity.SONG_NAME, songName);
        args.putString(PlayerActivity.SONG_ID, songId);
        args.putString(PlayerActivity.ARTIST_NAME, artistName);
        args.putLong(PlayerActivity.DURATION, duration);
        args.putString(PlayerActivity.ARTWORK, artwork);
        args.putString(PlayerActivity.ALBUM, album);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        if (arguments != null) {
            String songName = arguments.getString(PlayerActivity.SONG_NAME);
            String songId = arguments.getString(PlayerActivity.SONG_ID);
            String artistName = arguments.getString(PlayerActivity.ARTIST_NAME);
            long duration = arguments.getLong(PlayerActivity.DURATION);
            String artwork = arguments.getString(PlayerActivity.ARTWORK);
            String albumName = arguments.getString(PlayerActivity.ALBUM);

            TextView songView = (TextView) rootView.findViewById(R.id.song);
            songView.setText(songName);
            TextView artistView = (TextView) rootView.findViewById(R.id.artist);
            artistView.setText(artistName);
            TextView albumView = (TextView) rootView.findViewById(R.id.album);
            albumView.setText(albumName);
            ImageView artworkView = (ImageView) rootView.findViewById(R.id.artwork_icon);
            int screenWidth = Utils.getScreenWidth(this.getActivity());
            Picasso.with(this.getActivity())
                    .load(artwork)
                    .resize(screenWidth, screenWidth)
                    .centerInside()
                    .into(artworkView);
        }
        return rootView;
    }

    public void changePlayButton(Boolean showPlay) {
        View rootView = getActivity().getWindow().getDecorView().getRootView();
        ImageButton button = (ImageButton) rootView.findViewById(R.id.player_play);
        if (! showPlay) {
            button.setImageResource(android.R.drawable.ic_media_play);
        } else {
            button.setImageResource(android.R.drawable.ic_media_pause);
        }
    }


}
