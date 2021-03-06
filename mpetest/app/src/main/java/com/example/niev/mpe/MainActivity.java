package com.example.niev.mpe;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.util.Log;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private int selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("ONCREATE", "APPSTARTING");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawerLayout != null;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        drawerLayout.setScrimColor(Color.TRANSPARENT);

        // Drawer selection listener
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        setTitle("MPEngine Test");
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (selected = menuItem.getItemId()) {
            case R.id.nav_vod:
                setFragment(new VodFragment(), Integer.toString(selected));
                break;
            case R.id.nav_aod:
                setFragment(new AodFragment(), Integer.toString(selected));
                break;
            default:
        }
        menuItem.setChecked(true);
        return true;
    }

    private void setFragment(Fragment fragment, String tag) {
        // Insert fragment and replace any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame, fragment, tag).commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("e", Integer.toString(keyCode));
        Player player;
        if (selected == R.id.nav_vod)
            player = ((VodFragment) getSupportFragmentManager().findFragmentByTag(Integer.toString(selected))).getPlayer();
        else
            player = ((AodFragment) getSupportFragmentManager().findFragmentByTag(Integer.toString(selected))).getPlayer();

        switch (keyCode) {
            case 85: // Pause/play
                if (player.isPlaying())
                    player.pause();
                else
                    player.play();
                break;
            case 166: // Channel up
                player.channelUp();
                break;
            case 167: // Channel down
                player.channelDown();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}