package com.routetracking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.orm.SugarContext;

public class Splash extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private boolean Islogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        SugarContext.init(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Splash.this);
        Islogin = prefs.getBoolean("Islogin", false);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (Islogin) {
                    Intent goToLogin = new Intent(Splash.this, Home.class);
                    startActivity(goToLogin);
                    finish();
                }else{
                    Intent goToLogin = new Intent(Splash.this, Login.class);
                    startActivity(goToLogin);
                    finish();
                }


            }
        }, SPLASH_TIME_OUT);
    }
}
