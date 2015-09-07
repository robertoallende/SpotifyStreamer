package edu.nanodegree.spotify;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    protected static String SONG_NAME = "songName";
    protected static String SONG_ID = "songId";
    protected static String ARTIST_NAME = "artistName";
    protected static String DURATION = "duration";
    protected static String ARTWORK = "artwork";
    protected static String ALBUM = "album";
    protected static String IS_FIRST = "isFirst";
    protected static String URL = "url";

    private String songUrl;
    private Boolean isFirst = false;

    protected static final String FRAGMENT_TAG = "SpotifyStreamerPlayerActivity";

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
        boolean runtimeChange = false;
        super.onCreate(savedInstanceState);
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

        PlayerFragment playerFragment = PlayerFragment.newInstance(songId, songName, artistName,
                artwork, album, songUrl);
        fm.beginTransaction().add(R.id.activity_player, playerFragment, FRAGMENT_TAG).commit();
    }

    public void playPrevious(View v) {
        if (isFirst) {
            // player(PlayerService.ACTION_STOP);
        } else {
            // player(PlayerService.ACTION_STOP);
            setResult(TracksFragment.PLAY_PREVIOUS);
            finish();
        }
    }

    public void playNext(View v) {
        setResult(TracksFragment.PLAY_NEXT);
        // player(PlayerService.ACTION_STOP);
        finish();
    }

    public void play(View v) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        PlayerFragment fragment = (PlayerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        fragment.playSong();
    }

    public void pause(View v) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        PlayerFragment fragment = (PlayerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        fragment.stopSong();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

}