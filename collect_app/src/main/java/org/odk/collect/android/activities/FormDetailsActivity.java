package org.odk.collect.android.activities;

import android.app.Activity;
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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import org.odk.collect.android.adapters.FormDetailsAdapter;
import org.odk.collect.android.database.AfyaDataDB;
import org.odk.collect.android.models.FormDetails;
import org.odk.collect.android.preferences.PreferencesActivity;
import org.odk.collect.android.prefs.Preferences;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import web.BackgroundClient;
import web.RestClient;

public class FormDetailsActivity extends Activity {

    private static String TAG = "Form Details";

    private List<FormDetails> formList = new ArrayList<FormDetails>();
    private ListView listView;
    private FormDetailsAdapter formAdapter;

    private SharedPreferences mSharedPreferences;
    private String serverUrl;
    private String language;

    private String instanceId;
    private String formTitle;
    private String formId;

    //AfyaData database
    private AfyaDataDB db;

    private static final String TAG_ID = "id";
    private static final String TAG_LABEL = "label";
    private static final String TAG_TYPE = "type";
    private static final String TAG_VALUE = "value";

    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_details);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        //TODO language request
        language = mSharedPreferences.getString(Preferences.DEFAULT_LOCALE, null);

        // Get the message from the intent
        Intent intent = getIntent();
        instanceId = intent.getStringExtra("instance_id");
        formTitle = intent.getStringExtra("title");
        formId = intent.getStringExtra("form_id");

        //set Title
        setTitle(getString(R.string.app_name) + " > " + formTitle);

        listView = (ListView) findViewById(R.id.list_forms);

        //initialize database
        db = new AfyaDataDB(this);

        formList = db.getFormDetails(instanceId);

        if (formList.size() > 0) {
            refreshDisplay();
        }

        //check network connectivity
        if (ni == null || !ni.isConnected())
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        else
            new FetchFormDetailsTask().execute();
    }

    //refresh display
    private void refreshDisplay() {
        formAdapter = new FormDetailsAdapter(this, formList);
        listView.setAdapter(formAdapter);
        formAdapter.notifyDataSetChanged();
    }

    class FetchFormDetailsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // Progress dialog
            pDialog = new ProgressDialog(FormDetailsActivity.this);
            pDialog.setCancelable(true);
            pDialog.setMessage(getResources().getString(R.string.lbl_login_message));
            pDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            final RequestParams param = new RequestParams();
            param.add("table_name", formId);
            param.add("instance_id", instanceId);
            param.add("language", language);

            String formURL = serverUrl + "/api/v1/feedback/get_form_details";

            BackgroundClient.get(formURL, param, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    try {
                        if (response.getString("status").equalsIgnoreCase("success")) {
                            JSONArray feedbackArray = response.getJSONArray("form_details");

                            for (int i = 0; i < feedbackArray.length(); i++) {
                                JSONObject obj = feedbackArray.getJSONObject(i);

                                FormDetails formDetails = new FormDetails();
                                formDetails.setId(obj.getLong(TAG_ID));
                                formDetails.setLabel(obj.getString(TAG_LABEL));
                                formDetails.setType(obj.getString(TAG_TYPE));
                                formDetails.setValue(obj.getString(TAG_VALUE));
                                formDetails.setInstanceId(instanceId);

                                //check if form details exists
                                if (!db.isFormDetailsExist(formDetails)) {
                                    db.addFormDetails(formDetails);
                                } else {
                                    db.updateFormDetails(formDetails);
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
            formList = db.getFormDetails(instanceId);

            if (formList != null) {
                refreshDisplay();
            }
            pDialog.dismiss();
        }
    }

}
