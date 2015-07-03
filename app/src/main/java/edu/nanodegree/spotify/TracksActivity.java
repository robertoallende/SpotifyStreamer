package edu.nanodegree.spotify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class TracksActivity extends AppCompatActivity {
    protected static final String ARTIST_NAME = "artistName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String artistId = intent.getStringExtra(TracksFragment.ARTIST_ID);
        String artistName = intent.getStringExtra(ARTIST_NAME);
        TracksFragment tracksFragment = TracksFragment.newInstance(artistId, artistName);

        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, tracksFragment).commit();
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
        return super.onOptionsItemSelected(item);
    }
}
