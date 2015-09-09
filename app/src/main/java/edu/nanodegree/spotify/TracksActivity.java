package edu.nanodegree.spotify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

public class TracksActivity extends AppCompatActivity implements
        TracksFragment.OnDestroyTracksFragmentListener {
    protected static final String ARTIST_NAME = "artistName";
    protected static final String FRAGMENT_TAG = "SpotifyStreamerTracksData";
    private RetainedFragment dataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        Intent intent = getIntent();
        String artistId = intent.getStringExtra(TracksFragment.ARTIST_ID);
        String artistName = intent.getStringExtra(ARTIST_NAME);

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        TracksFragment tracksFragment = (TracksFragment) fm.findFragmentById(R.id.fragment_tracks);
        dataFragment = (RetainedFragment) fm.findFragmentByTag(FRAGMENT_TAG);

        if (dataFragment == null) {
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, FRAGMENT_TAG).commit();
            tracksFragment.searchTracks(artistId);
        } else {
            List<Track> tracks = (List<Track>) dataFragment.getData();
            tracksFragment = (TracksFragment) fm.findFragmentById(R.id.fragment_tracks);
            tracksFragment.updateListView(tracks);
        }

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (artistName != null) {
            ab.setSubtitle(artistName);
        }
    }

    public static Intent makeIntent(Context context, String artistId, String artistName) {
        Intent intent = new Intent(context, TracksActivity.class);
        intent.putExtra(TracksFragment.ARTIST_ID, artistId);
        intent.putExtra(ARTIST_NAME, artistName);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentDestroyed(List<Track> tracks) {
        dataFragment.setData(tracks);
    }
}
