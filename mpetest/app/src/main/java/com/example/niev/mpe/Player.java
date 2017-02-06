package com.example.niev.mpe;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by niev on 10/6/2016.
 */

public class Player extends MediaPlayer implements MediaPlayer.OnPreparedListener {
    private final String TAG = "Player";
    private final String URI = "pac_rtsp://mid:";
    private MediaPlayer mediaPlayer;
    private int mid;
    private int start;
    private int end;
    private int track = 0;
    private int totalTracks;
    private MediaPlayer.TrackInfo trackInfo[];
    private Context context;
    private boolean stopped;
    /*subtitle test check*/
    private boolean subFlag = false;

    /*ffwd / rewind flag */
    private String seekState;


    /*ffwd / rewind check*/
    private Handler mHandler = new Handler();
    private Runnable mSeekRunnable = new Runnable() {
        @Override
        public void run() {
            if(seekState.equals("fwd")) {
                Log.v(TAG, "forwarding!");
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 1000);
            }
            else if(seekState.equals("rew")){
                Log.v(TAG, "rewinding!");
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 1000);
            }
            Log.v(TAG, "***CurrentPosition: " + Integer.toString(mediaPlayer.getCurrentPosition()));
            mHandler.postDelayed(this, 2000);
        }
    };


    Player(Context context, int start, int end) {
        this.start = start;
        this.end = end;
        mid = start;
        track = 0;
        stopped = true;
        this.context = context;
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setOnPreparedListener(this);
            //FOR RACK USE
            //mediaPlayer.setDataSource(context, Uri.parse(URI + mid));

            //FOR EMULATOR USE
            String path = "/sdcard/Download/test_320_audio.mp4";
            File file = new File(path);
            file.setReadable(true, false);
            FileInputStream fileInputStream = new FileInputStream(file);
            mediaPlayer.setDataSource(fileInputStream.getFD());
            fileInputStream.close();
        } catch (Exception e) {
            Log.d(TAG, "Set Data Source Failed" + e.toString());
        }


    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.v(TAG, "in on prepared");
        if (mediaPlayer == this.mediaPlayer) {
            try {
                mediaPlayer.start();
                setTotalTracks();
                stopped = false;
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    public void setHolder(SurfaceHolder surfaceHolder) {
        mediaPlayer.setDisplay(surfaceHolder);
        mediaPlayer.setScreenOnWhilePlaying(true);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void play() {
        mHandler.removeCallbacks(mSeekRunnable);
        if (stopped) {
            try {
                mediaPlayer.prepare();
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        } else {
            mediaPlayer.start();
        }
    }

    public void stop() {
        if (!stopped) {
            mediaPlayer.stop();
            stopped = true;
        }
    }

    public void release() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void fastForward() {
        seekState = "fwd";
        mHandler.postDelayed(mSeekRunnable, 2000);
    }

    public void rewind() {
        seekState = "rew";
        mHandler.postDelayed(mSeekRunnable, 2000);
    }

    public void channelUp() {
        if (mid < end) {
            mid++;
            changeSource();
        }
    }

    public void channelDown() {
        if (mid > start) {
            mid--;
            changeSource();
        }
    }

    public void trackUp() {

        Log.v(TAG, "Lenght of track info:" + Integer.toString(getTotalTracks()));
        if(trackInfo[track].getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_SUBTITLE){
            mediaPlayer.deselectTrack(track);
        }
        track++;
        if (track < getTotalTracks()) {
            try {
                mediaPlayer.selectTrack(track);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
        else {
            track = 0;
            mediaPlayer.selectTrack(track);
        }



    }

    public void trackDown() {

    }

    public void changeSource() {
        mediaPlayer.reset();
        track = 0;
        stopped = true;
        try {
            mediaPlayer.setDataSource(context, Uri.parse(URI+mid));
            play();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public int getMid() {
        return mid;
    }

    public int getTrack() {
        return track;
    }

    private void setTotalTracks() {
        trackInfo = mediaPlayer.getTrackInfo();
        totalTracks = trackInfo.length;
    }

    private int getTotalTracks() { return totalTracks; }

    public String getLanguage() {
        if (mediaPlayer.getTrackInfo().length == 0)
            return "NA";
        return mediaPlayer.getTrackInfo()[track].getLanguage();
    }

    public void pause() {
        mediaPlayer.pause();
    }

}