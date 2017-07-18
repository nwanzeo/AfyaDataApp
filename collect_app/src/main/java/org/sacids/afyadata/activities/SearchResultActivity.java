package org.sacids.afyadata.activities;

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
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sacids.afyadata.R;
import org.sacids.afyadata.adapters.FormDetailsAdapter;
import org.sacids.afyadata.models.FormDetails;
import org.sacids.afyadata.preferences.PreferencesActivity;
import org.sacids.afyadata.web.RestClient;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends Activity {

    private static String TAG = "search";
    private Context context = this;

    private List<FormDetails> formList = new ArrayList<FormDetails>();
    private ListView listView;
    private FormDetailsAdapter adapter;

    private SharedPreferences mSharedPreferences;
    private String serverUrl;
    private String language;

    private static final String TAG_LABEL = "label";
    private static final String TAG_VALUE = "value";

    private long formId;
    private String searchFor;
    private String field;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //set Title
        setTitle(getString(R.string.app_name) + " > " + "Search Results");

        Intent searchIntent = getIntent();
        formId = searchIntent.getLongExtra("form_id", 0);
        searchFor = searchIntent.getStringExtra("search_for");
        field = searchIntent.getStringExtra("field");

        listView = (ListView) findViewById(R.id.list_search);
        adapter = new FormDetailsAdapter(this, formList);
        listView.setAdapter(adapter);

        fetchFormDetails();
    }

    //get form details from server
    private void fetchFormDetails() {
        // Progress dialog
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(true);
        pDialog.setMessage(getResources().getString(R.string.lbl_login_message));
        pDialog.show();

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        //search Url
        String searchUrl = serverUrl + "/api/v3/search/form";

        RequestParams params = new RequestParams();
        params.add("form_id", formId + "");
        params.add("search_for", searchFor);
        params.add("field", field);

        RestClient.get(searchUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {
                        JSONArray formArray = response.getJSONArray("form_details");

                        for (int i = 0; i < formArray.length(); i++) {
                            JSONObject obj = formArray.getJSONObject(i);
                            FormDetails formDetails = new FormDetails();
                            formDetails.setLabel(obj.getString(TAG_LABEL));
                            formDetails.setValue(obj.getString(TAG_VALUE));
                            //add to list
                            formList.add(formDetails);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, getString(R.string.no_content), Toast.LENGTH_SHORT).show();
                    }
                    //progress visibility
                    pDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d(TAG, "Failed " + responseString);
                //progress visibility
                pDialog.dismiss();
            }
        });
    }
}

