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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.adapters.FeedbackListAdapter;
import org.odk.collect.android.models.Feedback;
import org.odk.collect.android.models.SurveyForm;
import org.odk.collect.android.preferences.PreferencesActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import web.RestClient;


public class SurveyFeedbackActivity extends Activity {
    private static String TAG = "SurveyForm";


    private List<Feedback> feedbackList = new ArrayList<Feedback>();
    private FeedbackListAdapter adapter;
    private ListView listFeedback;

    // initially offset will be 0, later will be updated while parsing the json
    private int offSet = 0;

    private Button btnFeeedback;
    private EditText editFeedback;
    private ProgressDialog progressDialog;
    private SharedPreferences mSharedPreferences;
    private String username;
    private String password;
    private SurveyForm mForm;
    private String instanceId;
    private String formId;
    private String serverUrl;
    private String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_feedback);

        // Get the message from the intent
        Intent intent = getIntent();
        instanceId = intent.getStringExtra("instance_id");
        formId = intent.getStringExtra("form_id");

        //sharedPreference data
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = mSharedPreferences.getString(PreferencesActivity.KEY_USERNAME, getResources().getString(R.string.default_sacids_username));
        password = mSharedPreferences.getString(PreferencesActivity.KEY_PASSWORD, getResources().getString(R.string.default_sacids_password));
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL, getString(R.string.default_server_url));

        listFeedback = (ListView) findViewById(R.id.list_feedback);

        //show progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait while loading feedback ....");
        progressDialog.show();

        getFeedbackFromServer(); //load data

        //For submitting feedback to server
        editFeedback = (EditText) findViewById(R.id.edit_feedback);
        btnFeeedback = (Button) findViewById(R.id.btn_submit_feedback);

        //if submit feedback
        btnFeeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = editFeedback.getText().toString();

                if (editFeedback.getText().length() < 1) {
                    editFeedback.setError("Your feedback is required");
                } else {
                    //post feedback to the server
                    postFeedbackToServer();
                }
            }
        });
    }


    private void getFeedbackFromServer() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }

        final RequestParams params = new RequestParams();
        params.add("username", username);
        params.add("instance_id", instanceId);

        String feedbackURL = serverUrl + "/feedback/get_feedback";

        // research/feedback/get_feedback
        RestClient.get(username, password, feedbackURL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();

                Log.d(TAG, response.toString());
                Log.d(TAG, headers.toString());

                if (statusCode == 200) {
                    try {
                        feedbackList.removeAll(feedbackList);
                        feedbackList = getMessagesFromJsonResponse(response.getJSONArray("feedback"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "Found " + feedbackList.size() + " feedback");

                    //feedbackList adapter
                    adapter = new FeedbackListAdapter(SurveyFeedbackActivity.this, feedbackList);
                    listFeedback.setAdapter(adapter);


                } else if (statusCode == 204) {
                    Log.d(TAG, "No feedback at the moment");
                    Toast.makeText(SurveyFeedbackActivity.this, "No Feedback at the moment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressDialog.dismiss();

                if (statusCode == 401) {
                    //TODO apply authentication here
                    Toast.makeText(SurveyFeedbackActivity.this, "Unauthorized " + responseString, Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, headers.toString());
                Log.d(TAG, "Failed " + responseString);
            }
        });
    }


    //Function to post details to the server
    private void postFeedbackToServer() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }

        //progress Dialog
        progressDialog.setMessage("Posting feedback...");
        progressDialog.show();

        final RequestParams params = new RequestParams();
        params.add("username", username);
        params.add("message", message);
        params.add("form_id", formId);
        params.add("instance_id", instanceId);
        params.add("sender", "user");

        final Feedback postFeedback = new Feedback();
        postFeedback.setMessage(message);

        //URL
        String post_feedbackURL = serverUrl + "/feedback/post_feedback";

        // research/feedback/get_feedback
        RestClient.post(post_feedbackURL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
                Log.d(TAG, response.toString());
                Log.d(TAG, headers.toString());

                if (statusCode == 200) {
                    //append new message
                    feedbackList.add(postFeedback);
                    adapter.notifyDataSetChanged();

                    editFeedback.setText("");//clear feedback posted

                    Log.d(TAG, "Saving feedback success");
                    Toast.makeText(SurveyFeedbackActivity.this, "Feedback saved, will get back to you soon", Toast.LENGTH_SHORT).show();

                } else if (statusCode == 400) {
                    //Failed to post
                    editFeedback.setText("");
                    Log.d(TAG, "Saving Feedback failed");
                    Toast.makeText(SurveyFeedbackActivity.this, "Failed to send feedback", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressDialog.dismiss();

                Toast.makeText(SurveyFeedbackActivity.this, responseString, Toast.LENGTH_SHORT);

                if (statusCode == 400) {
                    //Failed to post
                    Toast.makeText(SurveyFeedbackActivity.this, "Failed to send feedback " + responseString, Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, headers.toString());
                Log.d(TAG, "Failed " + responseString);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_survey_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (item.getItemId()) {

            case R.id.action_refresh:
                getFeedbackFromServer();
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<Feedback> getMessagesFromJsonResponse(JSONArray jsonArray) throws JSONException {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("dd-MM-yyyy HH:mm:ss");
        gsonBuilder.setFieldNamingStrategy(new FieldNamingStrategy() {

            @Override
            public String translateName(Field field) {
                if (field.getName().equals("userId"))
                    return "user_id";

                if (field.getName().equals("formId"))
                    return "form_id";

                if (field.getName().equals("instanceId"))
                    return "instance_id";

                if (field.getName().equals("dateCreated"))
                    return "date_created";

                return field.getName();
            }
        });
        Gson gson = gsonBuilder.create();
        Type listType = new TypeToken<List<Feedback>>() {
        }.getType();
        return gson.fromJson(jsonArray.toString(), listType);
    }
}
