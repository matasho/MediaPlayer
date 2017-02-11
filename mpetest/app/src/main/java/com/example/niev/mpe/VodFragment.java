package com.example.niev.mpe;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageButton;
import android.widget.TextView;


public class VodFragment extends Fragment implements SurfaceHolder.Callback, View.OnClickListener {
    private static final int TEST_SETUP = 0;
    private static String URI_BCV_IP_PORT = "239.192.3.82:52226";
    private final String TAG = "VodFragment";
    private String mPlayType;
    private Player player;
    public SurfaceView surfaceView;
    public SurfaceHolder surfaceHolder;
    private TextView trackText;
    private TextView channelText;
    private TextView language;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.vod_fragment, container, false);
        channelText = (TextView) v.findViewById(R.id.channel);
        trackText = (TextView) v.findViewById(R.id.track);
        language = (TextView) v.findViewById(R.id.lan);
        MainActivity mainActivity = (MainActivity) getActivity();
        surfaceView = (SurfaceView) v.findViewById(R.id.surfaceView);


        int midStart = 1001;
        int midEnd = 1006;

        mPlayType = mainActivity.getType();
        if (mPlayType.equals("bcvChan")) {
            midStart = 0;
            midEnd = 6;
        }

        if(TEST_SETUP == 0) {
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
        }

        player = new Player(getActivity().getApplicationContext(), midStart, midEnd, URI_BCV_IP_PORT, mPlayType);

        ViewGroup viewGroup = (ViewGroup) v.findViewById(R.id.linearLayout);
        setButtonListeners(viewGroup);


        return v;
    }

    // Recursively gather all buttons and set their listeners
    private void setButtonListeners(ViewGroup viewGroup) {
        View view;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                setButtonListeners((ViewGroup) view);
            } else if (view instanceof ImageButton) {
                view.setOnClickListener(this);
            }
        }
    }

    public Player getPlayer() {
        return player;
    }


    public void onClick(View v) {
        Log.d(TAG, "onClick");
        switch (v.getId()) {
            case R.id.play:
                player.play();
                break;
            case R.id.stop:
                player.stop();
                break;
            case R.id.pause:
                player.pause();
                break;
            case R.id.videoUp:
                player.channelUp();
                break;
            case R.id.videoDown:
                player.channelDown();
                break;
            case R.id.trackUp:
                player.trackUp();
                break;
            case R.id.trackDown:
                player.trackDown();
                break;
            case R.id.fastForward:
                player.fastForward();
                break;
            case R.id.rewind:
                player.rewind();
                break;
        }
        setInfo();
    }

    private void setInfo() {
        channelText.setText(Integer.toString(player.getMid()));
        trackText.setText(Integer.toString(player.getTrack()));
        language.setText(player.getLanguage());
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "Surface created");

        if(TEST_SETUP == 0) {
            player.setHolder(surfaceHolder);
            player.play();
            setInfo();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d(TAG, "Surface changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "Surfaced destroyed");
    }


    @Override
    public void onPause() {
        super.onPause();
        if(mPlayType.equals("vod"))
            player.pause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        player.release();
        player = null;
    }

}