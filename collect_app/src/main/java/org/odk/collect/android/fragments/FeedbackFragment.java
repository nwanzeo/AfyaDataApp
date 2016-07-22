package org.odk.collect.android.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.activities.ChatListActivity;
import org.odk.collect.android.adapters.FeedbackListAdapter;
import org.odk.collect.android.database.AfyaDataDB;
import org.odk.collect.android.models.Feedback;
import org.odk.collect.android.preferences.PreferencesActivity;
import org.odk.collect.android.prefs.Preferences;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import web.BackgroundClient;
import web.RestClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackFragment extends Fragment {

    private static String TAG = "Feedback Fragment";
    private View rootView;

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
    private static final String TAG_DATE_CREATED = "date_created";
    private static final String TAG_STATUS = "status";
    private static final String TAG_REPLY_BY = "reply_by";

    private ProgressDialog pDialog;

    public FeedbackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_feedback, container, false);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        username = mSharedPreferences.getString(PreferencesActivity.KEY_USERNAME, null);

        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        //TODO language request
        language = mSharedPreferences.getString(Preferences.DEFAULT_LOCALE, null);

        listFeedback = (ListView) rootView.findViewById(R.id.list_feedback);

        db = new AfyaDataDB(getActivity());

        feedbackList = db.getAllFeedback();

        if (feedbackList.size() > 0) {
            refreshDisplay();
        }

        //check network connectivity
        if (ni == null || !ni.isConnected())
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();
        else
            new FetchFeedbackTask().execute();


        listFeedback.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Feedback fb = feedbackList.get(position);
                String formTitle = ((TextView) view.findViewById(R.id.form_name))
                        .getText().toString();

                String formId = ((TextView) view.findViewById(R.id.form_id))
                        .getText().toString();

                String instanceId = ((TextView) view.findViewById(R.id.instance_id))
                        .getText().toString();

                Intent feedbackIntent = new Intent(getActivity(), ChatListActivity.class);
                //feedbackIntent.putExtra(".models.Feedback", fb);
                feedbackIntent.putExtra("title", formTitle);
                feedbackIntent.putExtra("form_id", formId);
                feedbackIntent.putExtra("instance_id", instanceId);
                startActivity(feedbackIntent);
            }
        });
        return rootView;
    }

    //refresh display
    private void refreshDisplay() {
        feedbackAdapter = new FeedbackListAdapter(getActivity(), feedbackList);
        listFeedback.setAdapter(feedbackAdapter);
        feedbackAdapter.notifyDataSetChanged();
    }

    class FetchFeedbackTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // Progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setCancelable(true);
            pDialog.setMessage(getResources().getString(R.string.lbl_login_message));
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Feedback lastFeedback = db.getLastFeedback(username);
            String dateCreated;

            if (lastFeedback != null) {
                dateCreated = lastFeedback.getDateCreated();
            } else {
                dateCreated = null;
            }

            RequestParams param = new RequestParams();
            param.add("username", username);
            param.add("date_created", dateCreated);
            param.add("language", language);

            String feedbackURL = serverUrl + "/api/v1/feedback/get_feedback";

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
