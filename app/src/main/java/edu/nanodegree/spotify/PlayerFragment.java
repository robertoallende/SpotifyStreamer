package edu.nanodegree.spotify;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class PlayerFragment extends Fragment {

    // private MediaPlayer mp;
    String songUrl;
    ImageButton playButton;
    ImageButton pauseButton;
    static final String MSG_TAG = "messenger";

    static TextView songDuration;
    static TextView songZero;
    static SeekBar seekBar;

    private Handler handler;
    public final static int SET_SEEKBAR = 1;
    public final static int SET_DURATION = 2;
    public final static int SET_ZERO = 3;

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


            songDuration = (TextView) rootView.findViewById(R.id.song_duration);
            songZero = (TextView) rootView.findViewById(R.id.zero);
            seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        }
        return rootView;
    }

    public void shiftButton() {
        /* Thanks to Udacity reviewer who suggested the following implementation:
        */
        boolean bool = playButton.isEnabled();
        playButton.setEnabled(!bool);
        pauseButton.setEnabled(bool);
        playButton.setVisibility(bool ? View.GONE : View.VISIBLE);
        pauseButton.setVisibility(bool ? View.VISIBLE : View.GONE);
    }

    public void playSong() {
        Activity activity = getActivity();
        handler = new UIHandler(new WeakReference<PlayerFragment>(this));
        final Messenger messenger = new Messenger(handler);

        Intent intent = PlayerService.makeIntent(activity, songUrl, PlayerService.ACTION_PLAY, messenger);
        activity.startService(intent);
        shiftButton();
    }

    public void stopSong() {
        Activity activity = getActivity();
        Intent intent = PlayerService.makeIntent(activity, songUrl, PlayerService.ACTION_STOP, null);
        activity.startService(intent);
        shiftButton();
    }

    static class UIHandler extends Handler {
        WeakReference<PlayerFragment> mParent;

        public UIHandler(WeakReference<PlayerFragment> parent) {
            mParent = parent;
        }

        @Override
        public void handleMessage(Message msg) {
            PlayerFragment parent = mParent.get();
            if (null != parent) {
                int valor = (Integer) msg.obj;
                switch (msg.what) {
                    case SET_SEEKBAR: {
                        if (seekBar != null) seekBar.setProgress(valor);
                        break;
                    }
                    case SET_DURATION: {
                        if (songDuration != null) songDuration.setText(Utils.secToMin(valor));
                        break;
                    }
                    case SET_ZERO: {
                        if (songZero != null) songZero.setText(Utils.secToMin(valor));
                        break;
                    }
                }
            }
        }

    }
}