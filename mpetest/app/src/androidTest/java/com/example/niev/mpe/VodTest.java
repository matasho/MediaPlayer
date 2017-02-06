package com.example.niev.mpe;


import android.media.MediaPlayer;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.io.File;
import java.io.FileInputStream;

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
        private MainActivity mMainActivity;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void initMainActivity(){
        Log.v(TAG, "in before");
        mMainActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void playVODTest() throws Exception {
        SurfaceHolder mSurfaceHolder;


        VodFragment vodFragment = new VodFragment();
        mMainActivity.setFragment(vodFragment, "nav_vod");

        Thread.sleep(1000);
        mSurfaceHolder = vodFragment.surfaceView.getHolder();

        try {
            MediaPlayer mediaPlayer = new MediaPlayer();

            String path = "/sdcard/Download/test_320_audio.mp4";
            File file = new File(path);
            file.setReadable(true, false);
            FileInputStream fileInputStream = new FileInputStream(file);
            mediaPlayer.setDisplay(mSurfaceHolder);
            mediaPlayer.setDataSource(fileInputStream.getFD());
            fileInputStream.close();


            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.seekTo(1000);
            Thread.sleep(50000);


        } catch(Exception e){
            Log.v(TAG, e.toString());
        }

    }

    private void setupVODPlayer(MediaPlayer mp){

    }


}