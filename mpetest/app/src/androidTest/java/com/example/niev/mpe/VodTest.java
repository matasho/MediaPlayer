package com.example.niev.mpe;


import android.media.MediaPlayer;
import android.net.Uri;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.SurfaceHolder;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.io.File;
import java.io.FileInputStream;
import java.util.Random;


import static org.junit.Assert.*;



/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class VodTest {
    private static final String TAG = VodTest.class.getSimpleName();
    private static int TEST_SETUP = 0;  //1 = emulator,tablet 0 = rack
    private static String URI_MID = "1001";
    private static String URI_VOD = "pac_rtsp://mid:" + URI_MID;


    private MainActivity mMainActivity;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void initMainActivity(){
        mMainActivity = mActivityTestRule.getActivity();
    }

    @After
    public void releasePlayer(){
        mediaPlayer.release();
    }

    @Test
    public void playVODSeekTest() throws Exception {
        SurfaceHolder mSurfaceHolder;
        VodFragment vodFragment = new VodFragment();
        mMainActivity.setFragment(vodFragment, "nav_vod");
        Thread.sleep(1000);
        mSurfaceHolder = vodFragment.surfaceView.getHolder();
        int random_play_time;
        Random random = new Random(System.currentTimeMillis());

        try {

            mediaPlayer.setDisplay(mSurfaceHolder);

            if (TEST_SETUP == 1) {
                setupPlayer("test");
            } else {
                setupPlayer("VOD");
            }

            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            assertTrue(mediaPlayer.isPlaying());


            for(int i=0; i < 10 ; i++){
                if(TEST_SETUP == 1)
                    random_play_time = random.nextInt(15);
                else
                    random_play_time = random.nextInt(mediaPlayer.getDuration()/1000);
                Log.v(TAG, "Random Number Generator:" + random_play_time);
                mediaPlayer.seekTo(random_play_time*1000);
                Thread.sleep(5000);
                assertTrue("Assertion Error: seekTo with current position: " + mediaPlayer.getCurrentPosition() + " random play time: " + (random_play_time*1000), mediaPlayer.getCurrentPosition() >= (random_play_time*1000));
            }

            Log.v(TAG, "Video Height: " + Integer.toString(mediaPlayer.getVideoHeight()));
            Log.v(TAG, "Video Width: " + Integer.toString(mediaPlayer.getVideoWidth()));
            assertTrue("Assertion Error: Wrong video height", mediaPlayer.getVideoHeight() >= 240);
            assertTrue("Assertion Error: Wrong video width", mediaPlayer.getVideoWidth() >= 320);

        } catch(Exception e){
            Log.v(TAG, e.toString());
        }

    }

    @Test
    public void playVODTrackTest() throws Exception{
        SurfaceHolder mSurfaceHolder;
        MediaPlayer.TrackInfo[] trackinfo;

        VodFragment vodFragment = new VodFragment();
        mMainActivity.setFragment(vodFragment, "nav_vod");
        Thread.sleep(1000);
        mSurfaceHolder = vodFragment.surfaceView.getHolder();
        try {

            mediaPlayer.setDisplay(mSurfaceHolder);

            if (TEST_SETUP == 1) {
                setupPlayer("test");
            } else {
                setupPlayer("VOD");
            }

            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            trackinfo = mediaPlayer.getTrackInfo();
            assertTrue("Assertion Error: Number of tracks 0", trackinfo.length > 0);

            for(int i = 0; i < trackinfo.length; i++){
                Log.v(TAG, "TrackInfo: " + Integer.toString(trackinfo[i].getTrackType()));
                if(trackinfo[i].getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO){
                    mediaPlayer.selectTrack(i);
                }
                else if(trackinfo[i].getTrackType() == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_SUBTITLE){
                    mediaPlayer.deselectTrack(i);
                    mediaPlayer.selectTrack(i);
                }
                Thread.sleep(5000);
            }

        } catch(Exception e){
            Log.v(TAG, e.toString());
        }

    }

    @Test
    public void playVODPauseTest() throws Exception{
        SurfaceHolder mSurfaceHolder;
        MediaPlayer.TrackInfo[] trackinfo;
        int seek_time = 5000;

        VodFragment vodFragment = new VodFragment();
        mMainActivity.setFragment(vodFragment, "nav_vod");
        Thread.sleep(1000);
        mSurfaceHolder = vodFragment.surfaceView.getHolder();
        try {

            mediaPlayer.setDisplay(mSurfaceHolder);

            if (TEST_SETUP == 1) {
                setupPlayer("test");
            } else {
                setupPlayer("VOD");
            }

            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.seekTo(seek_time);
            mediaPlayer.pause();
            int currentPosition = mediaPlayer.getCurrentPosition();
            Thread.sleep(2000);
            Log.v(TAG, "Current Position: " + mediaPlayer.getCurrentPosition());
            assertTrue("Assertion Error: Pause issue old currentPosition: " + currentPosition + " new currentPosition: " + mediaPlayer.getCurrentPosition(), Math.abs(mediaPlayer.getCurrentPosition()-currentPosition) < 1000);

        } catch(Exception e){
            Log.v(TAG, e.toString());
        }

    }

    private void setupPlayer(String state){
        switch(state) {
            case "VOD":
                try {
                    mediaPlayer.setDataSource(mMainActivity.getApplicationContext(), Uri.parse(URI_VOD));
                }
                catch(Exception e){
                    Log.v(TAG, e.toString());
                }
                break;
            case "test":
                try {
                    String path = "/sdcard/Download/test_320_audio.mp4";
                    File file = new File(path);
                    file.setReadable(true, false);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    mediaPlayer.setDataSource(fileInputStream.getFD());
                    fileInputStream.close();
                } catch(Exception e){
                    Log.v(TAG, e.toString());
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid state selection for VOD player");
        }
    }


}