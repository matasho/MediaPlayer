package com.example.niev.mpe;


import android.media.MediaPlayer;
import android.net.Uri;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

import static android.os.SystemClock.sleep;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class VodTest {
    private static final String TAG = VodTest.class.getSimpleName();
    private static int TEST_SETUP = 1;  //1 = emulator,tablet 0 = rack
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
    public void playVODTest() throws Exception {
        SurfaceHolder mSurfaceHolder;
        int random_play_time;
        VodFragment vodFragment = new VodFragment();
        mMainActivity.setFragment(vodFragment, "nav_vod");
        Thread.sleep(1000);
        mSurfaceHolder = vodFragment.surfaceView.getHolder();
        Random random = new Random(System.currentTimeMillis());

        try {

            mediaPlayer.setDisplay(mSurfaceHolder);

            if(TEST_SETUP == 1) {
                String path = "/sdcard/Download/test_320_audio.mp4";
                File file = new File(path);
                file.setReadable(true, false);
                FileInputStream fileInputStream = new FileInputStream(file);
                mediaPlayer.setDataSource(fileInputStream.getFD());
                fileInputStream.close();
            } else {
                setupPlayer("VOD");
            }

            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            assertTrue(mediaPlayer.isPlaying());

            for(int i=0; i < 10 ; i++){
                random_play_time = random.nextInt(70);
                Log.v(TAG, "Random Number Generator:" + random_play_time);
                mediaPlayer.seekTo(random_play_time*1000);
                assert
            }
            mediaPlayer.seekTo(1000);
            Thread.sleep(50000);


        } catch(Exception e){
            Log.v(TAG, e.toString());
        }

    }

    private void setupPlayer(String state){
        switch(state) {
            case "VOD":
                try {
                    mediaPlayer.setDataSource(mMainActivity.getApplicationContext(), Uri.parse(URI_VOD));
                    break;
                }
                catch(Exception e){
                    Log.v(TAG, e.toString());
                }
            default:
                throw new IllegalArgumentException("Invalid state selection for VOD player");
        }
    }


}