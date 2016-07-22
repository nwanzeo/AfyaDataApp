package org.odk.collect.android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.adapters.ChatListAdapter;
import org.odk.collect.android.database.AfyaDataDB;
import org.odk.collect.android.models.Feedback;
import org.odk.collect.android.preferences.PreferencesActivity;

import java.util.ArrayList;
import java.util.List;

import web.RestClient;


public class ChatListActivity extends Activity {

    private static String TAG = "Survey Feedback";

    //private Bundle bundle;
    private Feedback feedback = null;
    private AfyaDataDB db;

    private List<Feedback> chatList = new ArrayList<Feedback>();
    private ChatListAdapter chatAdapter;
    private ListView listFeedback;

    private String instanceId;
    private String formTitle;
    private String formId;
    private String message;

    private SharedPreferences mSharedPreferences;
    private String username;
    private String serverUrl;

    private Button btnFeeedback;
    private EditText editFeedback;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_feedback);

        // Get the message from the intent
        Intent intent = getIntent();
        instanceId = intent.getStringExtra("instance_id");
        formTitle = intent.getStringExtra("title");
        formId = intent.getStringExtra("form_id");

        //set Title
        setTitle(getString(R.string.app_name) + " > " + formTitle);

        feedback = new Feedback();
        db = new AfyaDataDB(this);

        listFeedback = (ListView) findViewById(R.id.list_feedback);
        chatList = db.getFeedbackByInstance(instanceId);

        if (chatList.size() > 0) {
            refreshDisplay();
        }

        //For submitting feedback to server
        editFeedback = (EditText) findViewById(R.id.edit_feedback);
        btnFeeedback = (Button) findViewById(R.id.btn_submit_feedback);

        //if submit feedback
        btnFeeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = editFeedback.getText().toString();

                if (editFeedback.getText().length() < 1) {
                    editFeedback.setError(getResources().getString(R.string.required_feedback));
                } else {
                    //post to the server
                    postFeedbackToServer();
                }
            }
        });
    }

    //refresh display
    private void refreshDisplay() {
        chatAdapter = new ChatListAdapter(this, chatList);
        listFeedback.setAdapter(chatAdapter);
    }


    //Function to post details to the server
    private void postFeedbackToServer() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        if (ni == null || !ni.isConnected()) {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = mSharedPreferences.getString(PreferencesActivity.KEY_USERNAME,
                getResources().getString(R.string.default_sacids_username));
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.lbl_login_message));
        progressDialog.show();

        final RequestParams params = new RequestParams();
        params.add("form_id", formId);
        params.add("username", username);
        params.add("message", message);
        params.add("instance_id", instanceId);
        params.add("sender", "user");
        params.add("status", "pending");

        //append chat at last
        feedback.setFormId(formId);
        feedback.setUserName(username);
        feedback.setMessage(message);
        feedback.setSender("user");
        feedback.setInstanceId(instanceId);
        feedback.setStatus("pending");

        String postFeedbackURL = serverUrl + "/api/v1/feedback/post_feedback";

        RestClient.post(postFeedbackURL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();

                Log.d(TAG, response.toString());

                chatList.add(feedback);
                chatAdapter.notifyDataSetChanged();
                editFeedback.setText("");//clear feedback posted
                Log.d(TAG, "Saving feedback success");

                Toast.makeText(ChatListActivity.this, getResources().getString(R.string.success_feedback),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressDialog.dismiss();
                Toast.makeText(ChatListActivity.this, getResources().getString(R.string.success_feedback),
                        Toast.LENGTH_SHORT).show();

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

            case R.id.action_form_details:
                showFormDetails();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //show form details
    private void showFormDetails() {
        Intent feedbackIntent = new Intent(ChatListActivity.this, FormDetailsActivity.class);
        feedbackIntent.putExtra("title", formTitle);
        feedbackIntent.putExtra("form_id", formId);
        feedbackIntent.putExtra("instance_id", instanceId);
        startActivity(feedbackIntent);
    }


}
