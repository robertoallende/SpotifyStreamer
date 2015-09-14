package edu.nanodegree.spotify;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;


public class PlayerActivity extends AppCompatActivity {
    protected static String SONG_NAME = "songName";
    protected static String SONG_ID = "songId";
    protected static String ARTIST_NAME = "artistName";
    protected static String DURATION = "duration";
    protected static String ARTWORK = "artwork";
    protected static String ALBUM = "album";
    protected static String IS_FIRST = "isFirst";
    protected static String URL = "url";
    protected static String POSITION = "position";
    protected static String POSITION_MAX = "positionMax";


    private String songUrl;
    private Boolean isFirst = false;

    protected static final String FRAGMENT_TAG = "SpotifyStreamerPlayerActivity";
    protected static final String DIALOG_TAG = FRAGMENT_TAG + "DIALOG";

    public static Intent makeIntent(Context context, String songId, String songName,
                                    String artistName, String artwork,
                                    String albumName, String url, Boolean isFirst) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(SONG_NAME, songName);
        intent.putExtra(SONG_ID, songId);
        intent.putExtra(ARTIST_NAME, artistName);
        intent.putExtra(ARTWORK, artwork);
        intent.putExtra(ALBUM, albumName);
        intent.putExtra(IS_FIRST, isFirst);
        intent.putExtra(URL, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Utils.isNetworkAvailable(this)) {
            Toast toast = Toast.makeText(
                    this, R.string.no_connection_error, Toast.LENGTH_SHORT);
            toast.show();
            finish();
            return;
        }

        setContentView(R.layout.activity_player);
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();

        Intent intent = getIntent();
        String songName = intent.getStringExtra(SONG_NAME);
        String songId = intent.getStringExtra(SONG_ID);
        String artistName = intent.getStringExtra(ARTIST_NAME);
        long duration = intent.getLongExtra(DURATION, 0);
        String artwork = intent.getStringExtra(ARTWORK);
        String album = intent.getStringExtra(ALBUM);
        isFirst = intent.getBooleanExtra(IS_FIRST, false);
        songUrl = intent.getStringExtra(URL);

        Integer position = 0;
        Integer position_max = 0;
        HashMap savedConf = (HashMap) getLastCustomNonConfigurationInstance();
        if (savedConf != null) {
            position = (Integer) savedConf.get(POSITION);
            position_max = (Integer) savedConf.get(POSITION_MAX);

            if (position == null) position = 0;
            if (position_max == null) position = 0;
        }

        PlayerFragment playerFragment = PlayerFragment.newInstance(songId, songName, artistName,
                artwork, album, songUrl, position, position_max);
        fm.beginTransaction().add(R.id.activity_player, playerFragment, FRAGMENT_TAG).commit();
   }

    public void playPrevious(View v) {
        stopSong();
        setResult(TracksFragment.PLAY_PREVIOUS);
        finish();
    }

    public void playNext(View v) {
        stopSong();
        setResult(TracksFragment.PLAY_NEXT);
        finish();
    }

    public void playSong(View v) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        PlayerFragment fragment = (PlayerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        fragment.playSong();
    }

    public void pauseSong(View v) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        PlayerFragment fragment = (PlayerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        fragment.pauseSong();
    }

    public void stopSong() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        PlayerFragment fragment = (PlayerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        fragment.stopSong();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        HashMap result = null;
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        PlayerFragment fragment = (PlayerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) result = fragment.getPosition();
        return result;
    }
}