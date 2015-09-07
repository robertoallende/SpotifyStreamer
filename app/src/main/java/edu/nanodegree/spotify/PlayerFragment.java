package edu.nanodegree.spotify;

import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
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

import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 *  Since requirements didn' say anything about Handling audio focus and services
 *  I kept the player very simple.
 */
public class PlayerFragment extends Fragment implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mp;
    String songUrl;

    ImageButton playButton;
    ImageButton pauseButton;

    public PlayerFragment() {
    }

    public static PlayerFragment newInstance(String songId, String songName, String artistName,
                                             String artwork, String album, String songUrl) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString(PlayerActivity.SONG_NAME, songName);
        args.putString(PlayerActivity.SONG_ID, songId);
        args.putString(PlayerActivity.ARTIST_NAME, artistName);
        args.putString(PlayerActivity.ARTWORK, artwork);
        args.putString(PlayerActivity.ALBUM, album);
        args.putString(PlayerActivity.URL, songUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        playButton = (ImageButton) rootView.findViewById(R.id.player_play);
        pauseButton = (ImageButton) rootView.findViewById(R.id.player_pause);

        if (arguments != null) {
            String songName = arguments.getString(PlayerActivity.SONG_NAME);
            String songId = arguments.getString(PlayerActivity.SONG_ID);
            String artistName = arguments.getString(PlayerActivity.ARTIST_NAME);
            String artwork = arguments.getString(PlayerActivity.ARTWORK);
            String albumName = arguments.getString(PlayerActivity.ALBUM);
            songUrl = arguments.getString(PlayerActivity.URL);

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

            playSong();
        }

        return rootView;
    }

    public void shiftButton() {
        /* I know there's a nicer way to do this, i thought
           for a minute and i didn't came with anything simpler
           than this.
        */
        if (playButton.isEnabled()) {
            playButton.setEnabled(false);
            playButton.setVisibility(View.GONE);
            pauseButton.setEnabled(true);
            pauseButton.setVisibility(View.VISIBLE);
        } else {
            playButton.setEnabled(true);
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setEnabled(false);
            pauseButton.setVisibility(View.GONE);
        }
    }

    public void playSong() {
        Uri songUri = Uri.parse(songUrl);
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mp.setDataSource(songUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.setOnPreparedListener(this);
        mp.prepareAsync();
        mp.setOnCompletionListener(this);
        shiftButton();
    }

    public void stopSong() {
        shiftButton();
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopSong();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onDestroy () {
        stopSong();
        super.onDestroy();
    }
}
