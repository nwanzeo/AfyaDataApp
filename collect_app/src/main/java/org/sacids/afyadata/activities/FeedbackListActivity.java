package org.sacids.afyadata.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.sacids.afyadata.R;
import org.sacids.afyadata.adapters.FeedbackListAdapter;
import org.sacids.afyadata.database.AfyaDataDB;
import org.sacids.afyadata.models.Feedback;
import org.sacids.afyadata.preferences.PreferencesActivity;
import org.sacids.afyadata.prefs.Preferences;
import org.sacids.afyadata.web.BackgroundClient;

import java.util.ArrayList;
import java.util.List;

public class FeedbackListActivity extends Activity {

    private static String TAG = "Feedback Activity";
    private Context context = this;

    private List<Feedback> feedbackList = new ArrayList<Feedback>();
    private ListView listFeedback;
    private FeedbackListAdapter feedbackAdapter;

    private SharedPreferences mSharedPreferences;
    private String username;
    private String serverUrl;
    private String language;

    //AfyaData database
    private AfyaDataDB db;

    //variable Tag
    private static final String TAG_ID = "id";
    private static final String TAG_FORM_ID = "form_id";
    private static final String TAG_INSTANCE_ID = "instance_id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_SENDER = "sender";
    private static final String TAG_USER = "user";
    private static final String TAG_CHR_NAME = "chr_name";
    private static final String TAG_DATE_CREATED = "date_created";
    private static final String TAG_STATUS = "status";
    private static final String TAG_REPLY_BY = "reply_by";

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        //Set Activity Title
        setTitle(getString(R.string.app_name) + " > " + getString(R.string.nav_item_feedback));

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = mSharedPreferences.getString(PreferencesActivity.KEY_USERNAME, null);

        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        //TODO language request
        language = mSharedPreferences.getString(Preferences.DEFAULT_LOCALE, null);

        listFeedback = (ListView) findViewById(R.id.list_feedback);

        db = new AfyaDataDB(this);

        feedbackList = db.getAllFeedback();

        if (feedbackList.size() > 0) {
            refreshDisplay();
        } else {
            Toast.makeText(this, getString(R.string.no_feedback), Toast.LENGTH_LONG).show();
        }

        //check network connectivity
        if (ni == null || !ni.isConnected())
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        else
            new FetchFeedbackTask().execute();

        //OnLong Press
        listFeedback.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long arg) {
                //set background color
                view.setBackgroundColor(Color.parseColor("#F4F4F4"));

                final Feedback feedback = feedbackList.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getResources().getString(R.string.delete_status))
                        .setCancelable(false)
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db.deleteFeedback(feedback.getInstanceId());
                                feedbackList.remove(position);
                                feedbackAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }

        });

        //Onclick Listener
        listFeedback.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //set background color
                view.setBackgroundColor(Color.parseColor("#F4F4F4"));

                Feedback feedback = feedbackList.get(position);

                Intent feedbackIntent = new Intent(context, ChatListActivity.class);
                feedbackIntent.putExtra("feedback", Parcels.wrap(feedback));
                startActivity(feedbackIntent);
            }
        });
    }

    //refresh display
    private void refreshDisplay() {
        feedbackAdapter = new FeedbackListAdapter(this, feedbackList);
        listFeedback.setAdapter(feedbackAdapter);
        feedbackAdapter.notifyDataSetChanged();
    }

    class FetchFeedbackTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // Progress dialog
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(true);
            pDialog.setMessage(getResources().getString(R.string.lbl_login_message));
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Feedback lastFeedback = db.getLastFeedback();
            String dateCreated;
            long lastId;

            if (lastFeedback != null) {
                dateCreated = lastFeedback.getDateCreated();
                lastId = lastFeedback.getId();
            } else {
                dateCreated = null;
                lastId = 0;
            }

            RequestParams param = new RequestParams();
            param.add("username", username);
            param.add("lastId", String.valueOf(lastId));
            param.add("date_created", dateCreated);
            param.add("language", language);

            String feedbackURL = serverUrl + "/api/v3/feedback";

            BackgroundClient.get(feedbackURL, param, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    try {
                        if (response.getString("status").equalsIgnoreCase("success")) {
                            JSONArray feedbackArray = response.getJSONArray("feedback");

                            for (int i = 0; i < feedbackArray.length(); i++) {
                                JSONObject obj = feedbackArray.getJSONObject(i);
                                Feedback fb = new Feedback();
                                fb.setId(obj.getInt(TAG_ID));
                                fb.setFormId(obj.getString(TAG_FORM_ID));
                                fb.setInstanceId(obj.getString(TAG_INSTANCE_ID));
                                fb.setTitle(obj.getString(TAG_TITLE));
                                fb.setMessage(obj.getString(TAG_MESSAGE));
                                fb.setSender(obj.getString(TAG_SENDER));
                                fb.setUserName(obj.getString(TAG_USER));
                                fb.setChrName(obj.getString(TAG_CHR_NAME));
                                fb.setDateCreated(obj.getString(TAG_DATE_CREATED));
                                fb.setStatus(obj.getString(TAG_STATUS));
                                fb.setReplyBy(obj.getString(TAG_REPLY_BY));

                                if (!db.isFeedbackExist(fb)) {
                                    db.addFeedback(fb);
                                } else {
                                    db.updateFeedback(fb);
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d(TAG, "on Failure " + responseString);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            feedbackList = db.getAllFeedback();

            if (feedbackList.size() > 0) {
                refreshDisplay();
            }
            pDialog.dismiss();
        }
    }

}
