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
    private static final int TEST_SETUP = 0;
    private final String TAG = "Player";
    private final String URI = "pac_rtsp://mid:";
    private static String URI_BCV = "pac_rtp://";
    private static String URI_BCA = "pac_rtp://audio:";
    private static String URI_BCV_CHANNEL = "pac_rtp://video:";
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
    private String mPlayType;

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


    Player(Context context, int start, int end, String broadcast, String type) {
        this.start = start;
        this.end = end;
        mid = start;
        mPlayType = type;
        track = 0;
        stopped = true;
        this.context = context;
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setOnPreparedListener(this);
            if(TEST_SETUP == 1) {
                String path = "/sdcard/Download/test_320_audio.mp4";
                File file = new File(path);
                file.setReadable(true, false);
                FileInputStream fileInputStream = new FileInputStream(file);
                mediaPlayer.setDataSource(fileInputStream.getFD());
                fileInputStream.close();
            } else{
                if(type.equals("vod") || type.equals("aod"))
                    mediaPlayer.setDataSource(context, Uri.parse(URI + mid));
                else if(type.equals("bcv"))
                    mediaPlayer.setDataSource(context, Uri.parse(URI_BCV + broadcast));
                else if(type.equals("bca"))
                    mediaPlayer.setDataSource(context, Uri.parse(URI_BCA + broadcast));
                else if(type.equals("bcvChan"))
                    mediaPlayer.setDataSource(context, Uri.parse(URI_BCV_CHANNEL + mid));
                else if(type.equals("bcaChan"))
                    mediaPlayer.setDataSource(context, Uri.parse(URI_BCA + mid));

            }
        } catch (Exception e) {
            Log.d(TAG, "Set Data Source Failed" + e.toString());
        }


    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
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
            if(mPlayType.equals("vod") || mPlayType.equals("aod"))
                mediaPlayer.setDataSource(context, Uri.parse(URI + mid));
            else if(mPlayType.equals("bcvChan"))
                mediaPlayer.setDataSource(context, Uri.parse(URI_BCV_CHANNEL + mid));
            else if(mPlayType.equals("bcaChan"))
                mediaPlayer.setDataSource(context, Uri.parse(URI_BCA + mid));
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