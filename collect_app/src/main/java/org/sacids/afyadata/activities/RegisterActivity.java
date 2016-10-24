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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputName, inputUsername;
    private EditText inputPassword, inputPasswordConfirm;

    private String username;
    private String full_name;
    private String password;
    private String passwordConfirm;

    private ProgressDialog pDialog;

    private PrefManager prefManager;

    private SharedPreferences mSharedPreferences;
    private String serverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        prefManager = new PrefManager(this);

        //setup view
        setUpViews();

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                full_name = inputName.getText().toString();
                username = inputUsername.getText().toString();
                password = inputPassword.getText().toString();
                passwordConfirm = inputPasswordConfirm.getText().toString();

                if (full_name == null || full_name.length() == 0) {
                    inputName.setError(getResources().getString(R.string.name_required));
                } else if (username == null || username.length() == 0) {
                    inputUsername.setError(getResources().getString(R.string.username_required));
                } else if (password == null || password.length() == 0) {
                    inputPassword.setError(getResources().getString(R.string.password_required));
                } else if (passwordConfirm == null || passwordConfirm.length() == 0) {
                    inputPassword.setError(getResources().getString(R.string.password_required));
                } else {
                    //Register now if condition accepts
                    registerUser();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

    }

    //set up views
    private void setUpViews() {
        inputName = (EditText) findViewById(R.id.full_name);
        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPasswordConfirm = (EditText) findViewById(R.id.password_confirm);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
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
        params.add("full_name", full_name);
        params.add("username", username);
        params.add("password", password);
        params.add("password_confirm", passwordConfirm);


        String registerURL = serverUrl + "/api/v2/auth/register";

        RestClient.post(registerURL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                pDialog.dismiss();

                try {

                    boolean error = response.getBoolean("error");

                    if (!error) {
                        String message = response.getString("success_msg");
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

                        //Redirect to Login Activity
                        Intent mainIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(mainIntent);
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
}
