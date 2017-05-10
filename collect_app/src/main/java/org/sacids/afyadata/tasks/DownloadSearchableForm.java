package org.sacids.afyadata.tasks;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sacids.afyadata.R;
import org.sacids.afyadata.database.AfyaDataDB;
import org.sacids.afyadata.models.SearchableData;
import org.sacids.afyadata.models.SearchableForm;
import org.sacids.afyadata.preferences.PreferencesActivity;
import org.sacids.afyadata.prefs.Preferences;
import org.sacids.afyadata.web.BackgroundClient;


public class DownloadSearchableForm extends IntentService {

    private static String TAG = "Download Searchable Forms";

    private SharedPreferences mSharedPreferences;
    private String username;
    private String serverUrl;
    private String language;

    private AfyaDataDB db;

    //variables
    private static final String TAG_FORM_ID = "form_id";
    private static final String TAG_JR_FORM_ID = "jr_form_id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_SEARCH_FIELD = "search_fields";
    private static final String TAG_LABEL = "label";
    private static final String TAG_VALUE = "value";


    public DownloadSearchableForm() {
        super("DownloadSearchableForm");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            username = mSharedPreferences.getString(PreferencesActivity.KEY_USERNAME, null);
            serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                    getString(R.string.default_server_url));

            //TODO language request
            language = mSharedPreferences.getString(Preferences.DEFAULT_LOCALE, null);

            db = new AfyaDataDB(getApplicationContext());

            //params
            RequestParams param = new RequestParams();
            param.add("username", username);
            param.add("language", language);

            String formUrl = serverUrl + "/api/v2/search/init";

            BackgroundClient.get(formUrl, param, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    try {
                        if (response.getString("status").equalsIgnoreCase("success")) {
                            JSONArray formArray = response.getJSONArray("searchable_form");

                            Log.d(TAG, formArray.toString());

                            for (int i = 0; i < formArray.length(); i++) {
                                JSONObject obj = formArray.getJSONObject(i);

                                SearchableForm form = new SearchableForm();
                                form.setId(obj.getLong(TAG_FORM_ID));
                                form.setTitle(obj.getString(TAG_TITLE));
                                form.setJrFormId(obj.getString(TAG_JR_FORM_ID));
                                //check if form exists
                                if (!db.isSearchableExist(form)) {
                                    db.addSearchableForm(form);
                                } else {
                                    db.updateSearchableForm(form);
                                }

                                //json array search field
                                JSONArray searchArray = obj.getJSONArray(TAG_SEARCH_FIELD);
                                for (int j = 0; j < searchArray.length(); j++) {
                                    JSONObject jsonObject = searchArray.getJSONObject(j);

                                    SearchableData data = new SearchableData();
                                    data.setFormId(obj.getLong(TAG_FORM_ID));
                                    data.setLabel(jsonObject.getString(TAG_LABEL));
                                    data.setValue(jsonObject.getString(TAG_VALUE));
                                    //check if searchable data exist
                                    if (!db.isSearchableDataExist(data)) {
                                        db.addSearchableData(data);
                                    } else {
                                        db.updateSearchableData(data);
                                    }
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

        }
    }
}
