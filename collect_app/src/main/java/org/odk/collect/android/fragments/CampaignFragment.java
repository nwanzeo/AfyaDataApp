package org.odk.collect.android.fragments;


import java.text.ParseException;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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

import java.util.ArrayList;
import java.util.List;

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

    private String username;
    private String password;
    private String serverUrl;

    private ProgressDialog progressDialog;
    private SharedPreferences mSharedPreferences;

    //AfyaData database
    private AfyaDataDB db;


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
        username = mSharedPreferences.getString(PreferencesActivity.KEY_USERNAME, getResources().getString(R.string.default_sacids_username));
        password = mSharedPreferences.getString(PreferencesActivity.KEY_PASSWORD, getResources().getString(R.string.default_sacids_password));
        serverUrl = mSharedPreferences.getString(PreferencesActivity.KEY_SERVER_URL, getString(R.string.default_server_url));

        gridView = (GridView) rootView.findViewById(R.id.gridView);

        //show progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Wait while loading campaigns ....");
        progressDialog.show();

        db = new AfyaDataDB(getActivity());

        campaignList = db.getAllCampaign();

        if (campaignList.size() > 0) {
            refreshDisplay();
        }

        //check network connectivity
        if (ni == null || !ni.isConnected())
            Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_SHORT).show();
        else
            loadCampaign();

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




    private List<Campaign> getCampaignList(JSONArray campaign) throws JSONException, ParseException {
        for (int i = 0; i < campaign.length(); i++) {
            JSONObject obj = campaign.getJSONObject(i);

            //campaign object
            Campaign cmp = new Campaign();
            cmp.setId(obj.getInt("id"));
            cmp.setTitle(obj.getString("title"));
            cmp.setType(obj.getString("type"));
            cmp.setIcon(obj.getString("icon"));
            cmp.setFormId(obj.getString("form_id"));
            cmp.setDescription(obj.getString("description"));
            cmp.setDateCreated(obj.getString("date_created"));

            //add campaign to a list
            campaignList.add(cmp);

            if (!db.isCampaignExist(cmp)) {
                db.addCampaign(cmp);

            } else {
                db.updateCampaign(cmp);
            }
        }
        return campaignList;
    }

    //refresh display
    private void refreshDisplay() {
        campaignAdapter = new CampaignListAdapter(getActivity(), campaignList);
        gridView.setAdapter(campaignAdapter);
        progressDialog.dismiss();
    }


    //get data from server
    public void loadCampaign() {

        RequestParams params = new RequestParams();

        String campaignURL = serverUrl + "/campaign/get_campaign";

        RestClient.get(username, password, campaignURL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressDialog.dismiss();
                Log.d(TAG, "Server Response:" + response.toString());

                try {
                    if (response.getString("status").equalsIgnoreCase("success")) {
                        JSONArray campaignArray = response.getJSONArray("campaign");
                        campaignList = getCampaignList(campaignArray);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d(TAG, "on Failure " + responseString);
                Toast.makeText(getActivity(), "Unauthorized", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                super.onCancel();
                progressDialog.dismiss();
            }
        });
    }


}
