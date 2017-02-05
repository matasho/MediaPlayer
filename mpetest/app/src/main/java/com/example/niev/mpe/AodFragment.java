package com.example.niev.mpe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


public class AodFragment extends Fragment implements View.OnClickListener {
    private final String TAG = "AodFragment";
    private Player player;
    private TextView channelText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.aod_fragment, container, false);
        channelText = (TextView) v.findViewById(R.id.channel);

        //player = new Player(getActivity().getApplicationContext(), 6583, 6587);
        player = new Player(getActivity().getApplicationContext(), 21001, 21005);

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
            player.play();
            channelText.setText(Integer.toString(player.getMid()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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
