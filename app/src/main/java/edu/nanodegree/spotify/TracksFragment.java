package edu.nanodegree.spotify;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;

import com.google.common.collect.ImmutableMap;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
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
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = this;
        String artistId = "";

        if (getArguments() != null) {
            artistId = getArguments().getString(ARTIST_ID);
            searchTracks(artistId);
        }
    }

    public void searchTracks(String mText) {
        FetchTrackListTask trackListTask = new  FetchTrackListTask();
        trackListTask.execute(mText);
    }

    public void updateListView(Tracks tracks) {

        if (tracks != null) {
            trackAdapter = new TrackAdapter(context.getActivity(), tracks.tracks);
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
            updateListView(tracks);
        }
    }

}
