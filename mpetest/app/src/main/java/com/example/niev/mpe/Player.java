package com.example.niev.mpe;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by niev on 10/6/2016.
 */

public class Player implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener{
    private static final int TEST_SETUP = 1;
    private final String TAG = "Player";
    private final String URI = "pac_rtsp://mid:";
    private static String URI_BCV = "pac_rtp://";
    private static String URI_BCA = "pac_rtp://audio:";
    private static String URI_BCV_CHANNEL = "pac_rtp://video:";
    private static final String URI_FILEPLAY = "pac_rtp://file:test.mpg";
    private static final String URI_HDMI = "pac_rtp://hdmi";
    private MediaPlayer mediaPlayer;

    private MediaPlayer nextMediaPlayer;
    private boolean mNextFlag = false;

    private VodFragment mVodFragment;
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
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
            }
            else if(seekState.equals("rew")){
                Log.v(TAG, "rewinding!");
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
            }
            Log.v(TAG, "***CurrentPosition: " + Integer.toString(mediaPlayer.getCurrentPosition()));
            mHandler.postDelayed(this, 3000);
        }
    };


    Player(Context context, int start, int end, String broadcast, String type, @Nullable Activity parentActivity) {
        this.start = start;
        this.end = end;
        mid = start;
        mPlayType = type;
        track = 0;
        stopped = true;
        this.context = context;
        Fragment currentFragment;

        switch(type){
            case "next":
                currentFragment = parentActivity.getFragmentManager().findFragmentByTag("next");
                if(currentFragment instanceof VodFragment)
                    mVodFragment = (VodFragment)currentFragment;
                break;
            case "vod":
                currentFragment = parentActivity.getFragmentManager().findFragmentByTag("vod");
                if(currentFragment instanceof VodFragment)
                    mVodFragment = (VodFragment)currentFragment;
                break;

        }


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);

        if(type.equals("next")) {
            nextMediaPlayer = new MediaPlayer();
            mNextFlag = true;
        }

        try {
            mediaPlayer.setOnPreparedListener(this);
            if(TEST_SETUP == 1) {
                String path = "/sdcard/Download/test_short_320.mp4";
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
                else if(type.equals("file"))
                    mediaPlayer.setDataSource(context, Uri.parse(URI_FILEPLAY));
                else if(type.equals("hdmi"))
                    mediaPlayer.setDataSource(context, Uri.parse(URI_HDMI));

            }
            mediaPlayer.setNextMediaPlayer(nextMediaPlayer);
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

        if(mNextFlag){
            try {
                nextMediaPlayer.setOnCompletionListener(this);

                if(TEST_SETUP == 1) {
                    String path = "/sdcard/Download/1.mp3";
                    File file = new File(path);
                    file.setReadable(true, false);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    nextMediaPlayer.setDataSource(fileInputStream.getFD());
                    fileInputStream.close();
                } else {
                    nextMediaPlayer.setDataSource(context, Uri.parse(URI + "62862"));
                }
                nextMediaPlayer.prepare();
                mNextFlag = false;
            } catch(Exception e) {
                Log.d(TAG, "Set Data Source Failed" + e.toString());
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

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
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
        if (track < getTotalTracks()) {
            track++;
            try {
                Log.v(TAG, "trackUp: selectTrack " + Integer.toString(track));
                mediaPlayer.selectTrack(track);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }

    }

    public void trackDown() {
        Log.v(TAG, "Lenght of track info:" + Integer.toString(getTotalTracks()));
        if(trackInfo[track].getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_SUBTITLE){
            mediaPlayer.deselectTrack(track);
        }
        if (track > 0) {
            track--;
            try {
                Log.v(TAG, "trackUp: selectTrack " + Integer.toString(track));
                mediaPlayer.selectTrack(track);
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
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

    public void seekBarSeekTo(int percent) {
        mediaPlayer.seekTo((percent * mediaPlayer.getDuration())/100);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayerComplete) {
        mVodFragment.setTextIntoView("EOS");
        Log.v(TAG, "mediaPlayerCompleteDuration: " + Integer.toString(mediaPlayerComplete.getDuration()));

        if(mPlayType.equals("next")){
            nextMediaPlayer.start();
        } else{
            mediaPlayer.start();
        }

    }
}