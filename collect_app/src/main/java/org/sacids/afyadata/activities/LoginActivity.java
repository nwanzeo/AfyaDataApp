/*
 * Copyright (C) 2016 Sacids Tanzania
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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.sacids.afyadata.R;
import org.sacids.afyadata.preferences.PrefManager;
import org.sacids.afyadata.preferences.PreferencesActivity;
import org.sacids.afyadata.prefs.Preferences;

import java.util.Locale;

import org.sacids.afyadata.web.RestClient;

/**
 * Responsible for login
 * directory.
 *
 * @author Renfrid Ngolongolo (renfrid.ngolongolo@sacids.org)
 * @author Godluck Akyoo (godluck.akyoo@sacids.org)
 */

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    //variables
    private Button btnLogin, btnLinkToRegister;
    private EditText inputCode;
    private EditText inputMobile;
    private EditText inputPassword;

    private String code;
    private String mobile;
    private String password;

    private ProgressDialog pDialog;

    private PrefManager prefManager;

    private Context context = this;
    private SharedPreferences mSharedPreferences;
    private String serverUrl;

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSharedPreferences = getSharedPreferences(Preferences.AFYA_DATA, MODE_PRIVATE);
        if (mSharedPreferences.getBoolean(Preferences.FIRST_TIME_APP_OPENED, true)) {
            showChangeLanguageDialog();
        }

        prefManager = new PrefManager(this);

        //setup view
        setUpViews();
    }

    //setUpViews
    private void setUpViews() {
        inputCode = (EditText) findViewById(R.id.country_code);
        inputMobile = (EditText) findViewById(R.id.mobile);
        inputPassword = (EditText) findViewById(R.id.password);

        //set CountryCode
        inputCode.setText(getCountryCode());
        inputCode.setEnabled(false);

        //button
        btnLogin = (Button) findViewById(R.id.button_login);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                code = inputCode.getText().toString();
                mobile = inputMobile.getText().toString();
                password = inputPassword.getText().toString();

                if (mobile == null || mobile.length() == 0) {
                    inputMobile.setError(getResources().getString(R.string.phone_required));
                } else if (password == null || password.length() == 0) {
                    inputPassword.setError(getResources().getString(R.string.password_required));
                } else {
                    // Login now if condition satisfy
                    checkLogin();
                }

            }
        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                startActivity(new Intent(context, RegisterActivity.class));
                finish();
            }
        });
    }


    //check user login
    public void checkLogin() {
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        pDialog.setMessage(getResources().getString(R.string.lbl_login_message));
        pDialog.show();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        RequestParams params = new RequestParams();
        params.add("phone", code + mobile);
        params.add("password", password);

        String loginURL = serverUrl + "/api/v3/auth/login";

        RestClient.post(loginURL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                pDialog.dismiss();

                try {

                    boolean error = response.getBoolean("error");

                    if (!error) {
                        int userId = response.getInt("uid");
                        JSONObject obj = response.getJSONObject("user");

                        String username = obj.getString("username");
                        String first_name = obj.getString("first_name");
                        String last_name = obj.getString("last_name");

                        //login session
                        prefManager.createLogin(userId, username);

                        //save variables to shared Preference
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(KEY_USERNAME, username);
                        editor.putString(KEY_PASSWORD, password);
                        editor.commit();

                        //Redirect to Main activity
                        startActivity(new Intent(context, MainActivity.class));
                        finish();
                    } else {
                        String message = response.getString("error_msg");
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                pDialog.hide();
                Log.d(TAG, "Server response " + responseString);
            }
        });
    }


    private void showChangeLanguageDialog() {

        LayoutInflater li = LayoutInflater.from(context);
        View promptView = li.inflate(R.layout.dialog_change_language, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setView(promptView);

        // set dialog message
        alertDialogBuilder.setTitle(R.string.title_choose_language);
        alertDialogBuilder.setIcon(R.drawable.ic_language_black_48dp);
        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        promptView.findViewById(R.id.btn_swahili).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("sw");
                alertDialog.dismiss();
            }

        });

        promptView.findViewById(R.id.btn_english).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("en");
                alertDialog.dismiss();
            }

        });

        promptView.findViewById(R.id.btn_french).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("fr");
                alertDialog.dismiss();
            }

        });

        promptView.findViewById(R.id.btn_portuguese).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocale("pt");
                alertDialog.dismiss();
            }

        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
                startActivity(getIntent());
            }
        });

        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    private void setLocale(String locale) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(locale);
        res.updateConfiguration(conf, dm);
        mSharedPreferences.edit().putString(Preferences.DEFAULT_LOCALE, locale).commit();
        mSharedPreferences.edit().putBoolean(Preferences.FIRST_TIME_APP_OPENED, false).commit();
    }


    /**
     * Function to get County code
     * country code should be of 3 digits length
     *
     * @return
     */
    public String getCountryCode() {
        String CountryID = "";
        String countryCode = "";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                countryCode = g[0];
                break;
            }
        }
        return countryCode;
    }
}
