package com.example.niev.mpe;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


public class AodFragment extends Fragment implements View.OnClickListener {
    private final String TAG = "AodFragment";
    private static final int TEST_SETUP = 0;
    private static String URI_BCA_IP_PORT = "239.192.1.9:51128";
    private String mPlayType;
    private Player player;
    private TextView channelText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.aod_fragment, container, false);
        channelText = (TextView) v.findViewById(R.id.channel);
        MainActivity mainActivity = (MainActivity) getActivity();
        mPlayType = mainActivity.getType();

        int midStart = 21001;
        int midEnd = 21006;

        if(mPlayType.equals("bcaChan")){
            midStart = 0;
            midEnd = 6;
        }

        player = new Player(getActivity().getApplicationContext(), midStart, midEnd, URI_BCA_IP_PORT, mPlayType, null);

        ViewGroup viewGroup = (ViewGroup) v.findViewById(R.id.linearLayout);
        setButtonListeners(viewGroup);

        return v;
    }

    public Player getPlayer() {
        return player;
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
            case R.id.audioUp:
                player.channelUp();
                break;
            case R.id.audioDown:
                player.channelDown();
                break;
        }
        channelText.setText(Integer.toString(player.getMid()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null){
            Log.v(TAG, "in onResume");
            player.play();
            channelText.setText(Integer.toString(player.getMid()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mPlayType.equals("aod"))
            player.pause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        player.release();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }
}
