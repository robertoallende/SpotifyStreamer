package edu.nanodegree.spotify;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistsFragment extends Fragment {

    private final String LOG_TAG = ArtistsFragment.class.getSimpleName();

    private ArtistsFragment context;
    private ArtistAdapter artistAdapter;
    private View view;

    public ArtistsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artists, container, false);
        context = this;

        EditText searchArtist = (EditText) rootView.findViewById(R.id.editText_searchArtist);

        searchArtist.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String mText = v.getText().toString();
                    Log.v(LOG_TAG, "Search Pressed: " + mText);
                    removePhoneKeypad();
                    searchArtist(mText);
                    return true;
                }
                return false;
            }
        });
        view = rootView;
        return rootView;
    }

    public void updateListView(ArtistsPager artistsPager) {
        if ((artistsPager.artists.total == 0) || (artistsPager == null)) {
            CharSequence text = "No artists found.";
            Toast toast = Toast.makeText(context.getActivity(), text, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        artistAdapter = new ArtistAdapter(context.getActivity(), artistsPager.artists.items);
        final ListView listView = (ListView) context.getActivity()
                .findViewById(R.id.listview_artistsFound);
        listView.setAdapter(artistAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Artist artist = (Artist) listView.getItemAtPosition(position);
                Log.v(LOG_TAG, artist.name);
                showTopTracks(artist.id, artist.name);
            }
        });
    }

    public void removePhoneKeypad() {

        InputMethodManager inputManager = (InputMethodManager) view
                .getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        IBinder binder = view.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showTopTracks(String artistId, String artistName) {
            Intent intent = TracksActivity.makeIntent(this.getActivity(), artistId, artistName);
        startActivity(intent);
    }

    public void searchArtist(String mText) {
        FetchArtistsTask artistsTask = new  FetchArtistsTask();
        artistsTask.execute(mText);
    }

    public class FetchArtistsTask extends AsyncTask<String, Void, ArtistsPager>{

        private final String LOG_TAG = FetchArtistsTask.class.getSimpleName();

        @Override
        protected ArtistsPager doInBackground(String... params) {
            int count = params.length;
            if (count == 0) {
                return null;
            }
            String artistName = params[0];
            SpotifyApi api = new SpotifyApi();

            SpotifyService spotify = api.getService();
            ArtistsPager result = spotify.searchArtists(artistName,
                    ImmutableMap.<String, Object>of("type", "artist"));
            Log.v(LOG_TAG, result.toString());
            return result;
        }

        @Override
        protected void onPostExecute(ArtistsPager artistsPager) {
            updateListView(artistsPager);
        }
    }
}