package edu.nanodegree.spotify;

/* PlayerService is based on:
    http://developer.android.com/guide/topics/media/mediaplayer.html#audiofocus
    https://github.com/commonsguy/cw-omnibus/blob/master/Service/FakePlayer/src/com/commonsware/android/fakeplayer/PlayerService.java
    https://github.com/PaulTR/AndroidDemoProjects/tree/master/MediaSessionwithMediaStyleNotification
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import java.io.IOException;

public class PlayerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener  {

    public static final String ACTION_PLAY = "SpotifyStreamerPlayerService.PLAY";
    public static final String ACTION_PAUSE = "SpotifyStreamerPlayerService.PAUSE";
    public static final String ACTION_STOP= "SpotifyStreamerPlayerService.STOP";
    private static final String SONG_URL = "songUrl";
    private Messenger messenger;

    private enum PlayerStates {
        STOPPED, PAUSED, STARTED
    }
    private MediaPlayer mMediaPlayer;
    private PlayerStates currentStatus = PlayerStates.STOPPED;
    String songUrl = "";

    public PlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return(null);
    }

    public static Intent makeIntent(Context context, String songUrl, String action,
                                    Messenger messenger) {

        Intent intent = new Intent(context, PlayerService.class);
        intent.putExtra(PlayerFragment.MSG_TAG, messenger);
        intent.putExtra(SONG_URL, songUrl);
        intent.setAction(action);
        return intent;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String newSong = intent.getStringExtra(SONG_URL);
        if (newSong.equals(songUrl)) return(START_NOT_STICKY);

        Messenger newMessenger = (Messenger) intent.getParcelableExtra(PlayerFragment.MSG_TAG);
        if (newMessenger != null) messenger = newMessenger ;

        songUrl = newSong;
        if (intent.getAction().equals(ACTION_PLAY)) {
            if (currentStatus == PlayerStates.STARTED) {
                stop();
            }
            initMediaPlayer();
        } else if (intent.getAction().equals(ACTION_STOP)){
            stop();
        }
        return(START_NOT_STICKY);
    }

    @Override
    public void onDestroy() {
        stop();
        super.onDestroy();
    }

    public void initMediaPlayer() {
        Uri songUri = Uri.parse(songUrl);

        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }

        if (mMediaPlayer.isPlaying()) {
            stop();
        }

        mMediaPlayer.reset();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), songUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        mMediaPlayer.prepareAsync();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            currentStatus = PlayerStates.STARTED;
            initSeekBar();
        }
    }

    public void stop() {
        if (mMediaPlayer == null) {
            return;
        }
        if (mMediaPlayer.isPlaying()) {
            currentStatus = PlayerStates.STOPPED;
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stop();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    private void initSeekBar() {
        if (messenger == null) {
            return;
        }
        final Message durationMsg = Message.obtain(
                null, PlayerFragment.SET_DURATION, mMediaPlayer.getDuration());
        final Message zeroMsg = Message.obtain(
                null, PlayerFragment.SET_DURATION, mMediaPlayer.getCurrentPosition());
        try {
            messenger.send(durationMsg);
            messenger.send(zeroMsg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mMediaPlayer == null) initMediaPlayer();
                else if (!mMediaPlayer.isPlaying()) mMediaPlayer.start();
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) stop();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, we just pause it.
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) mMediaPlayer.pause();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mMediaPlayer != null) {
                    if (mMediaPlayer.isPlaying()) mMediaPlayer.setVolume(0.1f, 0.1f);
                }
                break;
        }
    }

    public int getDuration(){
        if (mMediaPlayer == null) return 0;
        return mMediaPlayer.getDuration();
    }

    public int getPosition() {
        int pos = 0;
        if (mMediaPlayer == null) return pos;
        if (mMediaPlayer.isPlaying()) {
            pos = mMediaPlayer.getCurrentPosition();
        }
        return pos;
    }
}
