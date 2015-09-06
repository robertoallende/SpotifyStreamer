package edu.nanodegree.spotify;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

    private Boolean isFirst = false;
    private String songUrl = "";
    private Boolean isPlaying = false;

    public static Intent makeIntent(Context context, String songId, String songName,
                                    String artistName, long duration, String artwork,
                                    String albumName, String url, Boolean isFirst) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(SONG_NAME, songName);
        intent.putExtra(SONG_ID, songId);
        intent.putExtra(ARTIST_NAME, artistName);
        intent.putExtra(DURATION, duration);
        intent.putExtra(ARTWORK, artwork);
        intent.putExtra(ALBUM, albumName);
        intent.putExtra(IS_FIRST, isFirst);
        intent.putExtra(URL, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                duration, artwork, album);
        fm.beginTransaction().add(R.id.activity_player, playerFragment).commit();

        playOrPause(playerFragment.getView());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void playPrevious(View v) {
        if (isFirst) {
            player(PlayerService.ACTION_STOP);
        } else {
            player(PlayerService.ACTION_STOP);
            setResult(TracksFragment.PLAY_PREVIOUS);
            finish();
        }
    }

    public void playNext(View v) {
        setResult(TracksFragment.PLAY_NEXT);
        player(PlayerService.ACTION_STOP);
        finish();
    }

    public void playOrPause(View v) {
        if (! isPlaying) {
            isPlaying = true;
            player(PlayerService.ACTION_PLAY);
        } else {
            isPlaying = false;
            player(PlayerService.ACTION_PAUSE);
        }
    }

    private void player(String action) {
        Intent intent = new Intent(this, PlayerService.class);
        intent.putExtra(PlayerService.SONG_URL, songUrl);
        intent.setAction(action);
        this.startService(intent);

        if (PlayerService.ACTION_PLAY == action) {
            updateButton(true);
        } else {
            updateButton(false);
        }
    }

    private void updateButton(Boolean showPlay) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.Fragment fragment = fm.findFragmentById(R.id.activity_player);
        if (fragment instanceof PlayerFragment)
            ((PlayerFragment) fragment).changePlayButton(showPlay);

    }

}