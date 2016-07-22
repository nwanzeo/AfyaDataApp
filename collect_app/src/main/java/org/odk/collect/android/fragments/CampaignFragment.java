package org.odk.collect.android.fragments;


import java.text.ParseException;

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
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odk.collect.android.R;
import org.odk.collect.android.activities.CampaignActivity;
import org.odk.collect.android.adapters.CampaignListAdapter;
import org.odk.collect.android.database.AfyaDataDB;
import org.odk.collect.android.models.Campaign;
import org.odk.collect.android.preferences.PreferencesActivity;
import org.odk.collect.android.prefs.Preferences;

import java.util.ArrayList;
import java.util.List;

import web.BackgroundClient;
import web.RestClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class CampaignFragment extends Fragment {

    private static String TAG = "Campaign Fragment";
    private View rootView;

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
    private static final String TAG_ICON = "icon";
    private static final String TAG_FORM_ID = "form_id";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_DATE_CREATED = "date_created";


    public CampaignFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_campaign, container, false);

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL,
                getString(R.string.default_server_url));

        //TODO language request
        language = mSharedPreferences.getString(Preferences.DEFAULT_LOCALE, null);

        gridView = (GridView) rootView.findViewById(R.id.gridView);

        db = new AfyaDataDB(getActivity());

        campaignList = db.getAllCampaign();

        if (campaignList.size() > 0) {
            refreshDisplay();
        }

        //check network connectivity
        if (ni == null || !ni.isConnected())
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();
        else
            new FetchCampaignTask().execute();


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Campaign campaign = campaignList.get(position);
                Intent intent = new Intent(getActivity(), CampaignActivity.class);
                intent.putExtra(".models.Campaign", campaign);
                startActivity(intent);
            }
        });

        return rootView;
    }

    //refresh display
    private void refreshDisplay() {
        campaignAdapter = new CampaignListAdapter(getActivity(), campaignList);
        gridView.setAdapter(campaignAdapter);
        campaignAdapter.notifyDataSetChanged(); //TODO: check this issue
    }


    class FetchCampaignTask extends AsyncTask<Void, Void, Void> {

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

            RequestParams param = new RequestParams();
            param.add("language", language);

            String campaignURL = serverUrl + "/api/v1/campaign/get_campaign";

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
                                cmp.setIcon(obj.getString(TAG_ICON));
                                cmp.setFormId(obj.getString(TAG_FORM_ID));
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
                refreshDisplay();
            }
            pDialog.dismiss();
        }
    }


}
