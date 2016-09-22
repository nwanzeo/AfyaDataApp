package org.sacids.afyadata.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.preference.PreferenceManager;
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
import org.parceler.Parcels;
import org.sacids.afyadata.R;
import org.sacids.afyadata.adapters.ChatListAdapter;
import org.sacids.afyadata.database.AfyaDataDB;
import org.sacids.afyadata.models.Feedback;
import org.sacids.afyadata.preferences.PreferencesActivity;

import java.util.ArrayList;
import java.util.List;

import org.sacids.afyadata.web.RestClient;

import javax.sql.StatementEvent;


public class ChatListActivity extends Activity {

    private static String TAG = "Survey Feedback";

    private Feedback feedback = null;
    private AfyaDataDB db;

    private List<Feedback> chatList = new ArrayList<Feedback>();
    private ChatListAdapter chatAdapter;
    private ListView listFeedback;

    private SharedPreferences mSharedPreferences;
    private String username;
    private String serverUrl;
    private String message;

    private Button btnFeedback;
    private EditText editFeedback;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_feedback);

        feedback = (Feedback) Parcels.unwrap(getIntent().getParcelableExtra("feedback"));

        //set Title
        setTitle(getString(R.string.app_name) + " > " + feedback.getTitle());

        db = new AfyaDataDB(this);

        listFeedback = (ListView) findViewById(R.id.list_feedback);
        chatList = db.getFeedbackByInstance(feedback.getInstanceId());

        if (chatList.size() > 0) {
            refreshDisplay();
        }

        //For submitting feedback to server
        editFeedback = (EditText) findViewById(R.id.edit_feedback);
        btnFeedback = (Button) findViewById(R.id.btn_submit_feedback);

        //if submit feedback
        btnFeedback.setOnClickListener(new View.OnClickListener() {
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
        chatAdapter.notifyDataSetChanged();
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
        username = mSharedPreferences.getString(PreferencesActivity.KEY_USERNAME, null);
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.lbl_login_message));
        progressDialog.show();

        final RequestParams params = new RequestParams();
        params.add("form_id", feedback.getFormId());
        params.add("username", username);
        params.add("message", message);
        params.add("instance_id", feedback.getInstanceId());
        params.add("sender", "user");
        params.add("status", "pending");

        //append chat at last
        feedback.setFormId(feedback.getFormId());
        feedback.setUserName(username);
        feedback.setMessage(message);
        feedback.setSender("user");
        feedback.setInstanceId(feedback.getInstanceId());
        feedback.setReplyBy(String.valueOf(0));
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
                Toast.makeText(ChatListActivity.this, getResources().getString(R.string.error_feedback),
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
        feedbackIntent.putExtra("feedback", Parcels.wrap(feedback));
        startActivity(feedbackIntent);
    }


}
