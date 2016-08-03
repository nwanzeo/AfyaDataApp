/*
 * Copyright (C) 2011 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.sacids.afyadata.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;

import org.sacids.afyadata.R;
import org.sacids.afyadata.preferences.PrefManager;
import org.sacids.afyadata.prefs.Preferences;

import java.util.Locale;


public class SplashScreenActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private SharedPreferences settings;
    private ProgressBar progressBar;

    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        getActionBar().hide();

        settings = getSharedPreferences(Preferences.AFYA_DATA, MODE_PRIVATE);
        String defaultLocale = settings.getString(Preferences.DEFAULT_LOCALE, "sw");

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(defaultLocale);
        res.updateConfiguration(conf, dm);

        //pref Manager
        prefManager = new PrefManager(this);

        //progressBar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                //check login
                if (prefManager.isLoggedIn()) {
                    Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                } else {
                    //Direct to Login Activity
                    Intent loginIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
                //progressBar Gone
                progressBar.setProgress(View.GONE);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}

