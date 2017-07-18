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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
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

import org.sacids.afyadata.web.RestClient;

/**
 * Responsible for registration
 * directory.
 *
 * @author Renfrid Ngolongolo (renfrid.ngolongolo@sacids.org)
 * @author Godluck Akyoo (godluck.akyoo@sacids.org)
 */

public class RegisterActivity extends Activity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private Context context = this;
    //variables
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFirstName, inputLastName, inputCode, inputMobile;
    private EditText inputPassword, inputPasswordConfirm;

    private String firstName;
    private String lastName;
    private String code;
    private String mobile;
    private String password;
    private String passwordConfirm;

    private ProgressDialog pDialog;

    private PrefManager prefManager;

    private SharedPreferences mSharedPreferences;
    private String serverUrl;

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        prefManager = new PrefManager(this);

        //setup view
        setUpViews();
    }

    //set up views
    private void setUpViews() {
        inputFirstName = (EditText) findViewById(R.id.first_name);
        inputLastName = (EditText) findViewById(R.id.last_name);
        inputMobile = (EditText) findViewById(R.id.mobile);
        inputCode = (EditText) findViewById(R.id.country_code);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPasswordConfirm = (EditText) findViewById(R.id.password_confirm);

        //set CountryCode
        inputCode.setText(getCountryCode());
        inputCode.setEnabled(false);

        //button
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                firstName = inputFirstName.getText().toString();
                lastName = inputLastName.getText().toString();
                code = inputCode.getText().toString();
                mobile = inputMobile.getText().toString();
                password = inputPassword.getText().toString();
                passwordConfirm = inputPasswordConfirm.getText().toString();

                if (firstName == null || firstName.length() == 0) {
                    inputFirstName.setError(getString(R.string.first_name_required));
                } else if (lastName == null || lastName.length() == 0) {
                    inputLastName.setError(getString(R.string.last_name_required));
                } else if (mobile == null || mobile.length() == 0) {
                    inputLastName.setError(getString(R.string.phone_required));
                } else if (password == null || password.length() == 0) {
                    inputPassword.setError(getString(R.string.password_required));
                } else if (passwordConfirm == null || passwordConfirm.length() == 0) {
                    inputPasswordConfirm.setError(getString(R.string.password_required));
                } else {
                    //Register now if condition accepts
                    registerUser();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(context, LoginActivity.class));
                finish();
            }
        });
    }

    //Function to post details to the server
    private void registerUser() {
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        pDialog.setMessage(getResources().getString(R.string.lbl_login_message));
        pDialog.show();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        final RequestParams params = new RequestParams();
        params.add("first_name", firstName);
        params.add("last_name", lastName);
        params.add("phone", code + mobile);
        params.add("password", password);
        params.add("password_confirm", passwordConfirm);


        String registerURL = serverUrl + "/api/v3/auth/register";

        RestClient.post(registerURL, params, new JsonHttpResponseHandler() {

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
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                pDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
            }
        });
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
