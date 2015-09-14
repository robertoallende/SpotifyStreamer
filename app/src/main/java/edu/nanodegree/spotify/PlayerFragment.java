package edu.nanodegree.spotify;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class PlayerFragment extends Fragment {

    String songUrl;
    ImageButton playButton;
    ImageButton pauseButton;
    static final String MSG_TAG = "messenger";

    private TextView songDuration;
    private TextView songZero;
    private SeekBar seekBar;

    private Handler handler;
    public final static int BAR_UPDATE = 1;
    public final static int BAR_SET_DURATION = 2;
    public final static int BAR_SET_COMPLETED = 3;

    int positionBeforePause = 0;

    public PlayerFragment() {
    }

    public static PlayerFragment newInstance(String songId, String songName, String artistName,
                                             String artwork, String album, String songUrl,
                                             Integer position, Integer positionMax) {

        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString(PlayerActivity.SONG_NAME, songName);
        args.putString(PlayerActivity.SONG_ID, songId);
        args.putString(PlayerActivity.ARTIST_NAME, artistName);
        args.putString(PlayerActivity.ARTWORK, artwork);
        args.putString(PlayerActivity.ALBUM, album);
        args.putString(PlayerActivity.URL, songUrl);
        args.putString(PlayerActivity.POSITION, position.toString());
        args.putString(PlayerActivity.POSITION_MAX, positionMax.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        boolean isPaused = false;
        Bundle arguments = getArguments();
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        positionBeforePause = Integer.valueOf(arguments.getString(PlayerActivity.POSITION));

        // http://stackoverflow.com/questions/10983396/fragment-oncreateview-and-onactivitycreated-called-twice
        if (savedInstanceState != null) {
            boolean isRecovered = savedInstanceState.getBoolean("recovered", false);
            if (isRecovered && positionBeforePause == 0) {
                return rootView;
            }
            isPaused = savedInstanceState.getBoolean("paused", false);
        }

        playButton = (ImageButton) rootView.findViewById(R.id.player_play);
        pauseButton = (ImageButton) rootView.findViewById(R.id.player_pause);

        if (arguments != null) {
            String songName = arguments.getString(PlayerActivity.SONG_NAME);
            String songId = arguments.getString(PlayerActivity.SONG_ID);
            String artistName = arguments.getString(PlayerActivity.ARTIST_NAME);
            String artwork = arguments.getString(PlayerActivity.ARTWORK);
            String albumName = arguments.getString(PlayerActivity.ALBUM);
            int  positionMax = Integer.valueOf(arguments.getString(PlayerActivity.POSITION_MAX));
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


            songDuration = (TextView) rootView.findViewById(R.id.song_duration);
            songZero = (TextView) rootView.findViewById(R.id.zero);
            seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);

            boolean algo = isPaused;
            if (positionBeforePause > 0) {
                getPrepared(positionMax);
            } else {
                playSong();
                seekBar.setEnabled(false);
            }

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){


                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (seekBar == null) return;
                    int position = seekBar.getProgress();
                    seek(position);
                    if (playButton.isEnabled()) positionBeforePause = position;
                }
            });
        }
        return rootView;
    }

    public void getPrepared(int positionMax){
        seekBar.setMax(positionMax);
        seekBar.setProgress(positionBeforePause);
        songZero.setText(Utils.secToMin(positionBeforePause));
    }

    public void shiftButton(boolean isPlaying) {
        /* Thanks to Udacity reviewer who suggested the following implementation:
        */
        boolean playEnabled = playButton.isEnabled();

        if (isPlaying && !playEnabled)  return;
        if (!isPlaying && playEnabled)  return;

        playButton.setEnabled(! playEnabled);
        pauseButton.setEnabled(playEnabled);
        playButton.setVisibility(playEnabled ? View.GONE : View.VISIBLE);
        pauseButton.setVisibility(playEnabled ? View.VISIBLE : View.GONE);
    }

    public void playSong() {
        Activity activity = getActivity();
        handler = new UIHandler(new WeakReference<PlayerFragment>(this));
        final Messenger messenger = new Messenger(handler);

        Intent intent = PlayerService.makeIntent(activity, songUrl,
                PlayerService.ACTION_PLAY, messenger, positionBeforePause);
        activity.startService(intent);
        positionBeforePause = 0;
        shiftButton(true);
    }

    public void stopSong() {
        Activity activity = getActivity();
        Intent intent = PlayerService.makeIntent(activity, songUrl,
                PlayerService.ACTION_STOP, null, positionBeforePause);
        activity.startService(intent);
        activity.stopService(intent);
        shiftButton(false);
    }

    public void pauseSong() {
        if (seekBar != null) {
            positionBeforePause = seekBar.getProgress();
        }
        Activity activity = getActivity();
        Intent intent = PlayerService.makeIntent(activity, songUrl,
                PlayerService.ACTION_PAUSE, null, positionBeforePause);
        activity.startService(intent);
        activity.stopService(intent);
        shiftButton(false);
    }

    public void seek(int valor) {
        Activity activity = getActivity();
        Intent intent = PlayerService.makeIntent(activity, songUrl,
                PlayerService.ACTION_SEEK, null, valor);
        activity.startService(intent);
    }

    public void setSeekbarMax(int valor){
        if (seekBar == null) return;
        seekBar.setMax(valor);
        seekBar.setEnabled(true);
    }

    public void setSeekbarProgress(int valor){
        if (positionBeforePause != 0) return;
        if (seekBar != null) seekBar.setProgress(valor);
    }

    public void setDuration(int valor) {
        if (songDuration != null) songDuration.setText(Utils.secToMin(valor));
    }

    public void setZero(int valor) {
        if (positionBeforePause != 0) return;
        if (songZero != null) songZero.setText(Utils.secToMin(valor));
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
                    case BAR_UPDATE: {
                        parent.setSeekbarProgress(valor);
                        parent.setZero(valor);
                        break;
                    }
                    case BAR_SET_DURATION: {
                        parent.setDuration(valor);
                        parent.setSeekbarMax(valor);
                        break;
                    }
                    case BAR_SET_COMPLETED: {
                        parent.setSeekbarProgress(0);
                        parent.setZero(0);
                        parent.shiftButton(false);
                        break;
                    }
                }
            }
        }
    }

    public HashMap getPosition() {
        HashMap result = new HashMap();
        if (playButton.isEnabled())
            result.put(PlayerActivity.POSITION, positionBeforePause);
            result.put(PlayerActivity.POSITION_MAX, seekBar.getMax());
        return result;
    }

    public void setPosition(int position) {
        positionBeforePause = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putBoolean("recovered", true);
        if (playButton != null) outState.putBoolean("paused", playButton.isEnabled());
        super.onSaveInstanceState(outState);
    }
}