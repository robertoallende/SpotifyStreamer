package edu.nanodegree.spotify;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;


public class ArtistsActivity extends AppCompatActivity implements
        ArtistsFragment.OnDestroyArtistsFragmentListener,
        TracksFragment.OnDestroyTracksFragmentListener{
    private String FRAGMENT_TAG = "SpotifyStreamerArtistData";
    private RetainedFragment dataFragment;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);

        // find the retained fragment on activity restarts
        FragmentManager fm = getSupportFragmentManager();
        dataFragment = (RetainedFragment) fm.findFragmentByTag(FRAGMENT_TAG);

        // create the fragment and data the first time
        if (dataFragment == null) {
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, FRAGMENT_TAG).commit();
        } else {
            List<Artist> artists = (List<Artist>) dataFragment.getData();
            ArtistsFragment fragment = (ArtistsFragment)fm.findFragmentById(R.id.artists_fragment);
            fragment.updateListView(artists);
        }

        if (findViewById(R.id.tracks_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.tracks_container, new TracksFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onArtistsFragmentDestroyed(List<Artist> artists) {
        dataFragment.setData(artists);
    }

    @Override
    public void onTracksFragmentDestroyed(List<Track> tracks) {
        dataFragment.setData(tracks);
    }
}
