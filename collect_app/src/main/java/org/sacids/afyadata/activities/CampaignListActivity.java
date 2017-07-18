package org.sacids.afyadata.activities;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.sacids.afyadata.R;
import org.sacids.afyadata.adapters.CampaignListAdapter;
import org.sacids.afyadata.database.AfyaDataDB;
import org.sacids.afyadata.models.Campaign;
import org.sacids.afyadata.preferences.PreferencesActivity;
import org.sacids.afyadata.prefs.Preferences;
import org.sacids.afyadata.web.BackgroundClient;

import java.util.ArrayList;
import java.util.List;

public class CampaignListActivity extends Activity {

    private static String TAG = "Campaign Activity";
    private Context context = this;

    private List<Campaign> campaignList = new ArrayList<Campaign>();
    private GridView gridView;
    private CampaignListAdapter campaignAdapter;

    private SharedPreferences mSharedPreferences;
    private String serverUrl;
    private String language;

    //AfyaData database
    private AfyaDataDB db;

    private ProgressDialog pDialog;

    //variable Tag
    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_TYPE = "type";
    private static final String TAG_FEATURED = "featured";
    private static final String TAG_ICON = "icon";
    private static final String TAG_JR_FORM_ID = "jr_form_id";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_DATE_CREATED = "date_created";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_list);

        //Set Activity Title
        setTitle(getString(R.string.app_name) + " > " + getString(R.string.nav_item_forms));

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        //TODO language request
        language = mSharedPreferences.getString(Preferences.DEFAULT_LOCALE, null);

        db = new AfyaDataDB(this);

        gridView = (GridView) findViewById(R.id.gridView);

        fetchCampaign();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Campaign campaign = campaignList.get(position);

                Intent intent = new Intent(context, CampaignActivity.class);
                intent.putExtra("campaign", Parcels.wrap(campaign));
                startActivity(intent);
            }
        });

        //check network connectivity
        if (ni == null || !ni.isConnected())
            Toast.makeText(context, R.string.no_connection, Toast.LENGTH_SHORT).show();
        else
            new FetchCampaignTask().execute();
    }

    //Fetch All Campaign
    private void fetchCampaign() {
        campaignList = db.getAllCampaign();

        if (campaignList.size() > 0) {
            campaignAdapter = new CampaignListAdapter(this, campaignList);
            gridView.setAdapter(campaignAdapter);
            campaignAdapter.notifyDataSetChanged();
        }else {
            Toast.makeText(context, getString(R.string.no_campaign), Toast.LENGTH_LONG).show();
        }
    }


    class FetchCampaignTask extends AsyncTask<Void, Void, Void> {

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

            RequestParams param = new RequestParams();
            param.add("language", language);

            String campaignURL = serverUrl + "/api/v3/campaign/get_campaign";

            BackgroundClient.get(campaignURL, param, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // If the response is JSONObject instead of expected JSONArray
                    try {
                        if (response.getString("status").equalsIgnoreCase("success")) {
                            JSONArray campaignArray = response.getJSONArray("campaign");

                            for (int i = 0; i < campaignArray.length(); i++) {
                                JSONObject obj = campaignArray.getJSONObject(i);
                                Campaign cmp = new Campaign();
                                cmp.setId(obj.getInt(TAG_ID));
                                cmp.setTitle(obj.getString(TAG_TITLE));
                                cmp.setType(obj.getString(TAG_TYPE));
                                cmp.setFeatured(obj.getString(TAG_FEATURED));
                                cmp.setIcon(obj.getString(TAG_ICON));
                                cmp.setJrFormId(obj.getString(TAG_JR_FORM_ID));
                                cmp.setDescription(obj.getString(TAG_DESCRIPTION));
                                cmp.setDateCreated(obj.getString(TAG_DATE_CREATED));

                                if (!db.isCampaignExist(cmp)) {
                                    db.addCampaign(cmp);
                                } else {
                                    db.updateCampaign(cmp);
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
            campaignList = db.getAllCampaign();

            if (campaignList.size() > 0) {
                fetchCampaign();
            }
            pDialog.dismiss();
        }
    }
}
