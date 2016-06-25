package org.odk.collect.android.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.preferences.PrefManager;
import org.odk.collect.android.preferences.PreferencesActivity;
import org.odk.collect.android.utilities.AfyaDataUtils;

import web.RestClient;

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
                    inputName.setError("Full name is required");
                } else if (username == null || username.length() == 0) {
                    inputUsername.setError("Username is required");
                } else if (password == null || password.length() == 0) {
                    inputPassword.setError("Password is required");
                } else if (passwordConfirm == null || passwordConfirm.length() == 0) {
                    inputPassword.setError("Password Confirmation is required");
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
        pDialog.setCancelable(false);
        pDialog.setMessage("Please Wait while registering ....");
        pDialog.show();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        final RequestParams params = new RequestParams();
        params.add("full_name", full_name);
        params.add("username", username);
        params.add("password", password);
        params.add("password_confirm", passwordConfirm);


        String registerURL = serverUrl + "/api/v1/auth/register";

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
                Toast.makeText(RegisterActivity.this, "Failed to create account ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
