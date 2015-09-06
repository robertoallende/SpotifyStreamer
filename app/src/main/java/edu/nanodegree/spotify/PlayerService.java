package edu.nanodegree.spotify;

/*  Following
    http://developer.android.com/guide/topics/media/mediaplayer.html
    https://github.com/commonsguy/cw-omnibus/tree/master/Service/FakePlayer
*/

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener {
    private boolean isPlaying=false;
    protected static final String SONG_URL = "songUrl";
    protected static final String ACTION_PLAY = "edu.nanodegree.spotifystreamer.action.PLAY";
    protected static final String ACTION_PAUSE = "edu.nanodegree.spotifystreamer.action.PAUSE";
    protected static final String ACTION_STOP = "edu.nanodegree.spotifystreamer.action.STOP";
    MediaPlayer mMediaPlayer = null;

    public PlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String songUrl = intent.getStringExtra(SONG_URL);

        if (intent.getAction().equals(ACTION_PLAY)) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mMediaPlayer.setDataSource(songUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
        } else if (intent.getAction().equals(ACTION_STOP)) {
            stop();
        } else if (intent.getAction().equals(ACTION_PAUSE)) {
            pause();
        }

        return(START_NOT_STICKY);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void stop() {
        mMediaPlayer.stop();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
}

