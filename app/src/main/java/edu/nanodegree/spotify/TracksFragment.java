package edu.nanodegree.spotify;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;

import com.google.common.collect.ImmutableMap;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class TracksFragment extends ListFragment {
    protected static final String ARTIST_ID = "artistId";

    private TracksFragment context;
    private TrackAdapter trackAdapter;

    public static TracksFragment newInstance(String artistId, String artistName) {
        TracksFragment fragment = new TracksFragment();
        Bundle args = new Bundle();
        args.putString(ARTIST_ID, artistId);
        fragment.setArguments(args);
        return fragment;
    }

    public TracksFragment() {
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);
        context = this;
        String artistId = "";

        if (savedInstanceState == null && getArguments() != null) {
            artistId = getArguments().getString(ARTIST_ID);
            searchTracks(artistId);
        }

    }

    public void searchTracks(String mText) {
        FetchTrackListTask trackListTask = new  FetchTrackListTask();
        trackListTask.execute(mText);
    }

    public void updateListView(List<Track> tracks) {

        if (tracks != null) {
            trackAdapter = new TrackAdapter(context.getActivity(), tracks);
            setListAdapter(trackAdapter);
        }
    }

    public class FetchTrackListTask extends AsyncTask<String, Void, Tracks> {

        private final String LOG_TAG = FetchTrackListTask.class.getSimpleName();

        @Override
        protected Tracks doInBackground(String... params) {
            int count = params.length;
            if (count == 0) {
                return null;
            }
            String artistId = params[0];
            SpotifyApi api = new SpotifyApi();

            SpotifyService spotify = api.getService();
            Tracks result = spotify.getArtistTopTrack(
                    artistId,
                    ImmutableMap.<String, Object>of("market", "US", "country", "US"));
            Log.v(LOG_TAG, result.toString());
            return result;
        }

        @Override
        protected void onPostExecute(Tracks tracks) {
            updateListView(tracks.tracks);
        }
    }

    /*
    Following
    http://developer.android.com/training/basics/fragments/communicating.html
 */
    OnDestroyTracksFragmentListener mCallback;

    // Container Activity must implement this interface
    public interface OnDestroyTracksFragmentListener {
        public void onFragmentDestroyed(List<Track> tracks);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnDestroyTracksFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDestroyListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (trackAdapter == null) return;
        List<Track> tracks = trackAdapter.getTracks();
        if (tracks != null) {
            mCallback.onFragmentDestroyed(tracks);
        }
    }
}
