package edu.nanodegree.spotify;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Bundle;
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

import java.util.List;

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

    private boolean mDualPane;

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
                    Log.v(LOG_TAG, "Searching " + mText);
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View detailsFrame = getActivity().findViewById(R.id.tracks_container);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
    }

    public void updateArtistsPager(ArtistsPager artistsPager) {
        updateListView(artistsPager.artists.items);
    }

    public void updateListView(List<Artist> artistsList) {
        if (artistsList == null ) return;

        if ((artistsList.size() == 0) || (artistsList == null)) {
            if (artistAdapter != null) {
                artistAdapter.clear();
            }
            Toast toast = Toast.makeText(
                    context.getActivity(), R.string.no_artist_found, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        artistAdapter = new ArtistAdapter(context.getActivity(), artistsList);
        final ListView listView = (ListView) context.getActivity()
                .findViewById(R.id.listview_artistsFound);
        listView.setAdapter(artistAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        InputMethodManager inputManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        IBinder binder = view.getWindowToken();
        inputManager.hideSoftInputFromWindow(binder,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showTopTracks(String artistId, String artistName) {
        if (mDualPane) {
            TracksFragment tracksFragment = (TracksFragment)
                    getFragmentManager().findFragmentById(R.id.tracks_container);
            tracksFragment.searchTracks(artistId);

        } else {
            Intent intent = TracksActivity.makeIntent(this.getActivity(), artistId, artistName);
            startActivity(intent);
        }
    }

    public void searchArtist(String mText) {

        if (!Utils.isNetworkAvailable(getActivity())) {
            Toast toast = Toast.makeText(
                    context.getActivity(), R.string.no_connection_error, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (mDualPane) {
            TracksFragment tracksFragment = (TracksFragment)
                    getFragmentManager().findFragmentById(R.id.tracks_container);
            if (tracksFragment != null) {
                tracksFragment.clearTracks();
            }
        }
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
            if (artistsPager == null) return;
            updateArtistsPager(artistsPager);
        }
    }

    /*
        Following
        http://developer.android.com/training/basics/fragments/communicating.html
     */
    OnDestroyArtistsFragmentListener mCallback;

    // Container Activity must implement this interface
    public interface OnDestroyArtistsFragmentListener {
        public void onArtistsFragmentDestroyed(List<Artist> artists);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnDestroyArtistsFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDestroyListener");
        }
    }

    /* Using onPause because of this:
        Note: do not count on this method being called as a place for saving data!
        For example, if an activity is editing data in a content provider, those edits
        should be committed in either onPause() or onSaveInstanceState(Bundle), not here.
        http://developer.android.com/reference/android/app/Activity.html#onDestroy%28%29
        I reported a bug on Runtime Changes docs: http://goo.gl/TnBBv5
    */
    @Override
    public void onPause() {
        super.onPause();
        if (artistAdapter == null) return;
        List<Artist> artists = artistAdapter.getArtists();
        if (artists != null) {
            mCallback.onArtistsFragmentDestroyed(artists);
        }
    }
}