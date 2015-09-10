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
        TracksFragment.OnDestroyTracksFragmentListener {
    private String ARTIST_FRAGMENT_TAG = "SpotifyStreamerArtistData";
    private String TRACK_FRAGMENT_TAG = "SpotifyStreamerTrackData";
    private RetainedFragment artistDataFragment;
    private RetainedFragment trackDataFragment;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);

        // find the retained fragment on activity restarts
        FragmentManager fm = getSupportFragmentManager();
        artistDataFragment = (RetainedFragment) fm.findFragmentByTag(ARTIST_FRAGMENT_TAG);
        trackDataFragment = (RetainedFragment) fm.findFragmentByTag(TRACK_FRAGMENT_TAG);

        // create the fragment and data the first time
        if (artistDataFragment == null) {
            artistDataFragment = new RetainedFragment();
            fm.beginTransaction().add(artistDataFragment, ARTIST_FRAGMENT_TAG).commit();

        } else {
            List<Artist> artists = (List<Artist>) artistDataFragment.getData();
            ArtistsFragment artistFragment =
                    (ArtistsFragment)fm.findFragmentById(R.id.artists_fragment);
            artistFragment.updateListView(artists);
        }

        if (findViewById(R.id.tracks_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.tracks_container, new TracksFragment())
                        .commit();
                if (trackDataFragment != null) {
                    TracksFragment tracksFragment =
                            (TracksFragment) fm.findFragmentById(R.id.tracks_container);
                    List<Track> tracks = (List<Track>) trackDataFragment.getData();
                    tracksFragment.setTracks(tracks);
                } else {
                    trackDataFragment = new RetainedFragment();
                    fm.beginTransaction().add(trackDataFragment, TRACK_FRAGMENT_TAG).commit();
                }
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
        artistDataFragment.setData(artists);
    }

    @Override
    public void onTracksFragmentDestroyed(List<Track> tracks) {
        trackDataFragment.setData(tracks);
    }
}
