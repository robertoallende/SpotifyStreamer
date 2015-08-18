package edu.nanodegree.spotify;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerFragment extends Fragment {

    public PlayerFragment() {
    }

    public static PlayerFragment newInstance(String songId, String songName) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString(PlayerActivity.SONG_NAME, songName);
        args.putString(PlayerActivity.SONG_ID, songId);
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

            TextView helloWorld = (TextView) rootView.findViewById(R.id.helloWorld);
            helloWorld.setText(songName);
        }
        return rootView;
    }
}
