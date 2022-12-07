package com.example.gps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

/**
 * @author Oliver Brottare
 * @version 1.0
 * @since   2022-12-07
 * Class for displaying the welcome screen.
 */
public class SplashActivity extends Activity {
    Handler handler;

    /**
     * On creation the screen shows up for three seconds before switching to the StartActivity screen.
     * @param savedInstanceState A saved instance if needed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this,StartActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);

    }
}